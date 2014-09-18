#!/usr/bin/env python3
from optparse import OptionParser
import socket
import time
import sys
from urllib.error import HTTPError, URLError
from urllib.request import urlopen

from forklift import Forklift
from move_submitter import MoveSubmitter
from moves_queue import MovesQueue
import rfid_reader
from server_api import DeleteTagRequest


def rfid_loop(function):
    """Polling the RFID reader for scanned tags.

    Args:
        function: A function taking one argument that should be called once the
            RFID reader has scanned a tag.
    """
    while True:
        # Slow down polling, to not consume 100 % CPU
        time.sleep(0.01)
        # Read data from the RFID reader
        data = rfid_reader.call()
        if data == 'usb not connected':
            print('RFID reader not connected to the computer')
            break
        if not data:
            continue
        function(data)
        sys.stdout.flush()


def delete_tags():
    """The delete pallets mode."""
    def loop_function(tag):
        try:
            urlopen(DeleteTagRequest(tag))
            print('Deleted tag %s from the server' % tag)
        except socket.timeout:
            print('Connection timed out.')
        except HTTPError as e:
            response = e.read().decode('utf-8')
            if response == "The tag isn't in the system.":
                print("The tag %s isn't in the system." % tag)
            else:
                raise e
        except URLError as e:
            print('Can\'t connect to the server.')
    rfid_loop(loop_function)


def move_pallets():
    """The move pallets mode."""
    # Create a queue where the moves are buffered if the server isn't reachable
    moves = MovesQueue()
    # Create a forklift model
    forklift = Forklift(moves)
    # Start a separate thread that handles submitting the moves to the server
    running = [True, ]
    move_submitter = MoveSubmitter(moves, running)
    move_submitter.start()
    # tags = ['010886B011', '01010172C6']
    # forklift.submit_tag(tags[0])
    # forklift.submit_tag(tags[1])

    def loop_function(tag):
        forklift.submit_tag(tag)
    rfid_loop(loop_function)
    # When the rfid_loop ends, set running[0] to false. This will stop the
    # move_submitter thread.
    running[0] = False


def main():
    description = 'A client with two modes. Either to register moved pallets ' \
                  'or to delete tags (belonging to pallets or pallet slots) ' \
                  'from the server.'
    parser = OptionParser(description=description)
    parser.add_option('-m', '--mode', dest='mode', default='move',
                      help='set the mode the program should run in',
                      metavar='(delete|move)')
    (options, args) = parser.parse_args()
    modes = {
        'delete': delete_tags,
        'move': move_pallets,
    }
    # Remove an initial whitespace that gets added when the GUI tries to start
    # the delete mode in a subprocess.
    options.mode = options.mode.strip()
    print('Mode: %s' % options.mode, flush=True)
    modes[options.mode]()

if __name__ == '__main__':
    main()
