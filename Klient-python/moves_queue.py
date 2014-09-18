from datetime import datetime
from queue import Queue


class MovesQueue():
    """Wraps Queue with a better put method.

    Class variables:
        __READER: The name of the reader.

    Attributes:
        __queue: The wrapped Queue object.
    """

    __READER = 'Python client'

    def __init__(self):
        super().__init__()
        self.__queue = Queue()

    def get(self, block=True, timeout=None):
        """Get a move object from the queue.

        Returns:
            An object with the following format:
            {'tag': 'tag1',
             'reader': 'Python client',
             'date': '2014-04-30 21:54:20'}
        """
        return self.__queue.get(block, timeout)

    def put(self, tag, block=True, timeout=None):
        """Create a move object and put it into the queue.

        Args:
            tag: A tag as a string.
        """
        if not isinstance(tag, str):
            raise ValueError('tags must be a string')
        self.__queue.put({
            'tag': tag,
            'reader': self.__READER,
            'date': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
        }, block, timeout)
