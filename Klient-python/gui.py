#!/usr/bin/env python3
import json
from multiprocessing import Process, Value, Queue
import os
from queue import Empty, Full
import socket
import subprocess
from threading import Thread
from tkinter import N, W, E, S, Text, END, Tk, StringVar, DISABLED
from tkinter.ttk import Frame, Button, Label, Entry, Combobox
from urllib.error import HTTPError, URLError
from urllib.request import urlopen
import time
import rfid_reader
from server_api import AddPlaceRequest, AddPalletRequest, GetArticlesRequest


class StartPage(Frame):
    """The start page of the application."""

    def __init__(self, parent, controller):
        """
        Args:
            parent: Where this frame should be put inside.
            controller: Controller of the application.
        """
        Frame.__init__(self, parent)

        Label(self, text='Scan pallets moving between pallet slots').grid(
            column=0, row=0, sticky=(E, S))
        Button(self, text='Enter mode',
               command=lambda: controller.show_frame(MoveMode)).grid(
                   column=1, row=0, sticky=(W, S))

        Label(self, text='Add new pallets into the system').grid(
            column=0, row=1, sticky=(E,))
        Button(self, text='Enter mode',
               command=lambda: controller.show_frame(AddPalletsMode)).grid(
                   column=1, row=1, sticky=(W,))

        Label(self, text='Add new places into the system').grid(
            column=0, row=2, sticky=(E,))
        Button(self, text='Enter mode',
               command=lambda: controller.show_frame(AddPlacesMode)).grid(
                   column=1, row=2, sticky=(W,))

        Label(self, text='Delete tags from the system').grid(
            column=0, row=3, sticky=(E, N))
        Button(self, text='Enter mode',
               command=lambda: controller.show_frame(DeleteTagsMode)).grid(
                   column=1, row=3, sticky=(W, N))

        self.columnconfigure(0, weight=1)
        self.columnconfigure(1, weight=1)
        self.rowconfigure(0, weight=1)
        self.rowconfigure(3, weight=1)


class ThreadSafeConsole(Text):
    """
    Taken from http://effbot.org/zone/tkinter-threads.htm
    """

    def __init__(self, master, **options):
        Text.__init__(self, master, **options)
        self.queue = Queue()
        self.update_me()

    def write(self, line):
        self.queue.put(line)

    def clear(self):
        self.queue.put(None)

    def update_me(self):
        try:
            while 1:
                line = self.queue.get_nowait()
                if line is None:
                    self.delete(1.0, END)
                else:
                    self.insert(END, str(line))
                self.see(END)
                self.update_idletasks()
        except Empty:
            pass
        self.after(100, self.update_me)


class ThreadSafeCombobox(Combobox):

    def __init__(self, master, **options):
        Combobox.__init__(self, master, **options)
        self.queue = Queue()
        self.update_me()

    def set_values(self, values):
        self.queue.put(values)

    def set_text(self, text):
        self.queue.put(text)

    def update_me(self):
        try:
            while 1:
                item = self.queue.get_nowait()
                if isinstance(item, list):
                    self['values'] = item
                else:
                    self.set(item)
                self.update_idletasks()
        except Empty:
            pass
        self.after(100, self.update_me)


class ExternalProgramOutputFrame(Frame):
    """A frame that's able to run an external program in a new thread and output
    the program's standard output inside a text area.

    Attributes:
        program_path: Path to an external program whose standard output should
            be printed inside this frame's text area.
        text_area: The text area where the external program's standard output
            will be printed.
        subprocess: A Popen object with the running external process.
        subprocess_running: If the external process has been started.
    """

    def __init__(self, parent, controller, program_path):
        """
        Args:
            parent: Where this frame should be put inside.
            controller: Controller of the application.
            program_path: Path to the external program whose standard output
                should be printed inside this frame's text area.
        """
        Frame.__init__(self, parent)
        self.program_path = program_path
        self.text_area = ThreadSafeConsole(self, wrap=None)
        self.text_area.grid(column=0, row=0, columnspan=3)
        Button(self, text='Run', command=self.run_program_in_new_thread).grid(
            column=0, row=1)
        Button(self, text='Stop', command=self.stop_program).grid(column=1,
                                                                  row=1)
        Button(self, text='Back',
               command=lambda: self.leave(controller)).grid(column=2, row=1)

        self.columnconfigure(0, weight=1)
        self.columnconfigure(1, weight=1)
        self.columnconfigure(2, weight=1)
        self.rowconfigure(0, weight=1)

        self.subprocess = None
        self.subprocess_running = False

    def leave(self, controller):
        """Stop the program running in this frame and return to the start page.
        """
        self.stop()
        controller.show_frame(StartPage)

    def stop(self):
        self.stop_program()

    def run_program(self):
        """Run this object's program_path in a subprocess and block the calling
        thread while printing the subprocess' standard output inside
        text_area."""
        process = subprocess.Popen(self.program_path, stdout=subprocess.PIPE,
                                   stderr=subprocess.STDOUT,
                                   universal_newlines=True)
        self.subprocess = process
        self.subprocess_running = True
        while True:
            line = process.stdout.readline()
            if not line:
                break
            self.text_area.write(line)
        self.subprocess_running = False

    def run_program_in_new_thread(self):
        """Run this object's program_path in a new thread and print its output
        inside text_area."""
        Thread(target=self.run_program).start()

    def stop_program(self):
        """Stop the program running in this frame."""
        if self.subprocess_running:
            self.subprocess.kill()
            self.subprocess_running = False


class MoveMode(ExternalProgramOutputFrame):
    """Frame where the forklift program is running."""

    def __init__(self, parent, controller):
        """
        Args:
            parent: Where this frame should be put inside.
            controller: Controller of the application.
        """
        path = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                            'run.py')
        ExternalProgramOutputFrame.__init__(self, parent, controller, path)


class RfidReader(Process):
    """A process that polls the RFID reader and puts its return value into a
    queue.

    Using this solution because running the polling in only a Thread resulted in
    segfault.
    """

    def __init__(self, running, queue):
        """
        Args:
            running: A Value instance set to True if the process should run and
                False if it should return.
            queue: A reference to a Queue instance where scanned tags will be
                put.
        """
        super().__init__()
        self.running = running
        self.queue = queue

    def run(self):
        while self.running.value:
            # Slow down polling, to not consume 100 % CPU
            time.sleep(0.1)
            # Read data from the RFID reader
            data = rfid_reader.call()
            if not data:
                continue
            else:
                try:
                    self.queue.put(data, block=True, timeout=5)
                except Full:
                    pass


class EntryUpdater(Thread):
    """Thread that polls items from a queue and sets them in `StringVar`s."""

    def __init__(self, running, queue, string_vars):
        """
        Args:
            running: A Value instance set to True if the thread should run and
                False if it should return.
            queue: A reference to a Queue instance where scanned tags can be
                found.
            string_vars: A list of references to a StringVar where the tags
                from the queue should be put.
        """
        super().__init__()
        self.running = running
        self.queue = queue
        self.string_vars = string_vars

    def run(self):
        string_var_index = 0
        while self.running.value:
            try:
                item = self.queue.get(True, 0.05)
                if item:
                    self.string_vars[string_var_index].set(item)
                    string_var_index += 1
                    string_var_index %= len(self.string_vars)
            except Empty:
                # If the queue is empty
                time.sleep(0.05)


class AddPalletsMode(Frame):
    """Frame where it's possible to add new pallets.

    Attributes:
        article: StringVar with the article ID's entry text.
        amount: StringVar with the amount's entry text.
        tag1: StringVar with the first tag's entry text.
        tag2: StringVar with the second tag's entry text.
        status: Value('b') with True or False depending on if it should be
            polling tags from the RFID reader.
    """

    def __init__(self, parent, controller):
        """
        Args:
            parent: Where this frame should be put inside.
            controller: Controller of the application.
        """
        Frame.__init__(self, parent)

        self.article = StringVar()

        Label(self, text='Select an Article ID:').grid(
            column=0, row=0, sticky=(E, S))
        self.article_combobox = ThreadSafeCombobox(self)
        self.article_combobox.grid(column=1, row=0, sticky=(W, S))
        self.article_combobox.bind(
            '<<ComboboxSelected>>',
            lambda _: self.article.set(self.article_combobox.get())
        )
        Thread(target=self.fill_article_combobox).start()

        Label(self, text='Units of article:').grid(
            column=0, row=1, sticky=(E,))
        self.amount = StringVar()
        Entry(self, textvariable=self.amount).grid(
            column=1, row=1, sticky=(W,))

        Label(self, text='Scanned tag #1:').grid(column=0, row=2, sticky=(E,))
        self.tag1 = StringVar()
        Entry(self, textvariable=self.tag1, state=DISABLED).grid(
            column=1, row=2, sticky=(W,))

        Label(self, text='Scanned tag #1:').grid(column=0, row=3, sticky=(E,))
        self.tag2 = StringVar()
        Entry(self, textvariable=self.tag2, state=DISABLED).grid(
            column=1, row=3, sticky=(W,))

        Button(self, text='Create', command=self.create_pallet).grid(
            column=0, row=4, sticky=(N, E))

        Button(self, text='Back', command=lambda: self.leave(controller)).grid(
            column=1, row=4, sticky=(N, W))

        self.columnconfigure(0, weight=1)
        self.columnconfigure(1, weight=1)
        self.rowconfigure(0, weight=1)
        self.rowconfigure(4, weight=1)

        self.status = Value('b')

    def enter(self):
        """Called when the frame is entered."""
        self.start()

    def leave(self, controller):
        """Return to the StartPage."""
        self.stop()
        controller.show_frame(StartPage)

    def start(self):
        self.status.value = True
        queue = Queue(1)
        self.rfid_reader = RfidReader(self.status, queue)
        entry_updater = EntryUpdater(self.status, queue, [self.tag1, self.tag2])
        self.rfid_reader.start()
        entry_updater.start()

    def stop(self):
        self.status.value = False
        self.rfid_reader.terminate()

    def fill_article_combobox(self):
        """Fill the combobox with articles from the server."""
        try:
            articles = json.loads(urlopen(GetArticlesRequest(),
                                          timeout=5).read().decode('utf-8'))
            self.article_combobox.set_values([a['id'] for a in articles])
        except (HTTPError, URLError):
            print('Couldn\'t fill the article ID combobox because couldn\'t ' +
                  'access the server.')
            self.article_combobox.set_text('Can\'t access the server')
        except socket.timeout:
            print('Couldn\'t fill the article ID combobox. Connection timed ' +
                  'out')
            self.article_combobox.set_text('Connection timed out')

    def create_pallet(self):
        """Create a pallet via the API with the entries' position and tag."""
        try:
            urlopen(AddPalletRequest(self.article.get(), self.amount.get(),
                                     self.tag1.get(), self.tag2.get()),
                    timeout=5)
        except socket.timeout:
            print('Couldn\'t create the pallet. Connection timed out.')
        except HTTPError as e:
            response = e.read().decode('utf-8')
            print(response)


class AddPlacesMode(Frame):
    """Frame where it's possible to add new pallet slots.

    Attributes:
        position: StringVar with the position's entry text.
        tag: StringVar with the tag's entry text.
        status: Value('b') with True or False depending on if it should be
            polling tags from the RFID reader.
    """

    def __init__(self, parent, controller):
        """
        Args:
            parent: Where this frame should be put inside.
            controller: Controller of the application.
        """
        Frame.__init__(self, parent)

        Label(self, text='Position ID:').grid(column=0, row=0, sticky=(E, S))
        self.position = StringVar()
        Entry(self, textvariable=self.position).grid(
            column=1, row=0, sticky=(W, S))

        Label(self, text='Scanned tag:').grid(column=0, row=1, sticky=(E,))
        self.tag = StringVar()
        Entry(self, textvariable=self.tag, state=DISABLED).grid(
            column=1, row=1, sticky=(W,))

        Button(self, text='Create', command=self.create_slot).grid(
            column=0, row=2, sticky=(N, E))

        Button(self, text='Back', command=lambda: self.leave(controller)).grid(
            column=1, row=2, sticky=(N, W))

        self.columnconfigure(0, weight=1)
        self.columnconfigure(1, weight=1)
        self.rowconfigure(0, weight=1)
        self.rowconfigure(2, weight=1)

        self.status = Value('b')

    def enter(self):
        """Called when the frame is entered."""
        self.start()

    def leave(self, controller):
        """Return to the StartPage."""
        self.stop()
        controller.show_frame(StartPage)

    def start(self):
        self.status.value = True
        queue = Queue(1)
        self.rfid_reader = RfidReader(self.status, queue)
        entry_updater = EntryUpdater(self.status, queue, [self.tag])
        self.rfid_reader.start()
        entry_updater.start()

    def stop(self):
        self.status.value = False
        self.rfid_reader.terminate()

    def create_slot(self):
        """Create a pallet via the API with the entries' position and tag."""
        try:
            urlopen(AddPlaceRequest(self.position.get(), self.tag.get()),
                    timeout=5)
        except socket.timeout:
            print('Couldn\'t create the slot. Connection timed out.')
        except HTTPError as e:
            response = e.read().decode('utf-8')
            print(response)


class DeleteTagsMode(ExternalProgramOutputFrame):
    """Frame where the program for deleting tags is running."""

    def __init__(self, parent, controller):
        """
        Args:
            parent: Where this frame should be put inside.
            controller: Controller of the application.
        """
        path = [
            os.path.join(os.path.dirname(os.path.realpath(__file__)), 'run.py'),
            '-m delete'
        ]
        ExternalProgramOutputFrame.__init__(self, parent, controller, path)


class App(Tk):
    """The application window."""

    def __init__(self):
        super().__init__()
        self.title('Smart RFID')
        self.container = Frame(self)
        self.container.grid(column=0, row=0, sticky=(N, W, E, S))
        self.container.columnconfigure(0, weight=1)
        self.container.rowconfigure(0, weight=1)

        self.frames = {}
        # Instantiate the frames.
        for frame in (StartPage, MoveMode, AddPalletsMode, DeleteTagsMode,
                      AddPlacesMode):
            frame_instance = frame(self.container, self)
            frame_instance.grid(column=0, row=0, sticky=(N, W, E, S))
            self.frames[frame] = frame_instance
            self.add_padding(frame_instance)
        self.container.pack()
        self.show_frame(StartPage)

        self.protocol('WM_DELETE_WINDOW', self.exit_handler)

    def exit_handler(self):
        """Clean shutdown of the frames."""
        for frame in self.frames.values():
            try:
                frame.stop()
            except AttributeError:
                # The frame doesn't have a stop method
                pass
        self.quit()

    def show_frame(self, frame_class):
        """Switch the currently shown frame.

        Args:
            frame_class: The class of the frame that should be shown in the
                application.
        """
        frame = self.frames[frame_class]
        frame.tkraise()
        try:
            frame.enter()
        except AttributeError:
            # If the frame doesn't have an enter method
            pass

    @staticmethod
    def add_padding(frame):
        """Add padding to the widgets inside a frame.

        Args:
            frame: An instance of a Frame.
        """
        for child in frame.winfo_children():
            child.grid_configure(padx=5, pady=5)


def main():
    # Create the application window
    app = App()
    # Start the main loop
    app.mainloop()

if __name__ == '__main__':
    main()
