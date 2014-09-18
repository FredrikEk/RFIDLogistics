from datetime import datetime
import json
from urllib.request import Request


class SubmitMoveRequest(Request):
    """
    Attributes:
        move_json: The move object given to the constructor as a JSON object.
    """
    __URL = 'http://smartrfid.se:9000/api/moves'

    def __init__(self, move):
        """Make a Request that will submit a move to the server.

        Args:
            move: A Python object with the following format:
                {'tag': 'tag1',
                 'reader': 'Python client',
                 'date': '2014-04-30 21:54:20'}
        """
        self.move_json = json.dumps(move).encode('utf-8')
        super().__init__(
            url=self.__URL,
            data=self.move_json,
            headers={'Content-Type': 'application/json'},
            method='POST',
        )


class DeleteTagRequest(Request):
    __URL = 'http://smartrfid.se:9000/api/tags/%s'

    def __init__(self, tag):
        """Make an instance of a Request that will delete a tag from the server.

        Args:
            tag: A string.
        """
        super().__init__(
            url=self.__URL % tag,
            method='DELETE'
        )


class AddPalletRequest(Request):
    """
    Attributes:
        pallet_json: The article object given to the constructor as a JSON
            object.
    """
    __URL = 'http://smartrfid.se:9000/api/pallets'

    def __init__(self, article, amount, tag1, tag2):
        """Make an instance of a Request that will add a pallet to the server.

        Args:
            article: The article's ID as a string.
            amount: The number of units of the the article.
            tag1: The pallet's first tag.
            tag2: The pallet's second tag.
        """
        self.pallet_json = json.dumps({
            'tag1': tag1,
            'tag2': tag2,
            'time_entrance': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            'products': [
                {
                    'article': article,
                    'amount': amount,
                }
            ],
        }).encode('utf-8')

        super().__init__(
            url=self.__URL,
            data=self.pallet_json,
            headers={'Content-Type': 'application/json'},
            method='POST',
        )


class AddPlaceRequest(Request):
    """
    Attributes:
        place_json: The article object given to the constructor as a JSON
            object.
    """
    __URL = 'http://smartrfid.se:9000/api/palletslots'

    def __init__(self, position, tag):
        """Make an instance of a Request that will add a pallet to the server.

        Args:
            position: A string.
            tag: A string.
        """
        self.place_json = json.dumps({
            'position': position,
            'tag': tag,
        }).encode('utf-8')

        super().__init__(
            url=self.__URL,
            data=self.place_json,
            headers={'Content-Type': 'application/json'},
            method='POST',
        )


class GetArticlesRequest(Request):
    __URL = 'http://smartrfid.se:9000/api/articles'

    def __init__(self):
        """Make an instance of a Request that will get the articles from the
        server."""

        super().__init__(
            url=self.__URL,
            method='GET',
        )
