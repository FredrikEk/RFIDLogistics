class Forklift():
    """Model of a forklift.

    Attributes:
        state: If it's lifting a pallet or not.
        tags: The scanned pallet and pallet slot tags.
    """

    def __init__(self, moves):
        """Initialize the forklift.

        Args:
            moves: A MovesQueue where new moves are put.
        """
        self.moves = moves

    def submit_tag(self, tag):
        """Submit a newly scanned tag.

        Args:
            tag: String of the newly scanned tag.
        """
        self.moves.put(tag)
