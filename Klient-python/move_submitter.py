from queue import Empty
import socket
from threading import Thread
from urllib.error import URLError, HTTPError
from urllib.request import urlopen
import time

from server_api import SubmitMoveRequest


class MoveSubmitter(Thread):
    """Thread handling submitting moves to the server.

    Class variables:
        __URL: URL to the server API.
    """

    __URL = 'http://smartrfid.se:9000/api/moves'

    def __init__(self, moves, running):
        """Initialize the submitter.

        Args:
            moves: A MovesQueue where moves waiting to be submitted can be
                found.
            running: A list with a single boolean telling if the thread should
                keep running.
        """
        super().__init__()
        self.__moves = moves
        self.__running = running

    def run(self):
        """The thread's loop.

        The method will poll moves from the queue self.__moves. When it gets
        one, it will try to submit it to the server until it succeeds.
        """
        while self.__running[0]:
            try:
                # Try to get a new move to post
                move = self.__moves.get(block=False)
            except Empty:
                time.sleep(0.01)
                continue
            # Prepare the request
            request = SubmitMoveRequest(move)
            # Loop until the request has been successfully posted
            while True:
                try:
                    # Try send the request
                    response = urlopen(request, timeout=5)
                    message = 'Successfully submitted:\n' \
                              '  url: {}\n' \
                              '  data: {}\n' \
                              'Received the response:\n' \
                              '  {}  '
                    message = message.format(self.__URL,
                                             request.move_json.decode('utf-8'),
                                             response.read().decode('utf-8'))
                    print(message, flush=True)
                    break
                except HTTPError as e:
                    # If the request was successful but returned an error
                    print('The HTTP request returned an error:\n{}'.format(
                        e.read().decode('utf-8')), flush=True)
                    break
                except socket.timeout:
                    print('Connection timed out.', flush=True)
                except URLError as e:
                    if e.reason.strerror == 'Network is unreachable':
                        # If the request timed out
                        print("Couldn't access the site", flush=True)
                # Sleep for 10 seconds until retrying sending the request
                time.sleep(10)
