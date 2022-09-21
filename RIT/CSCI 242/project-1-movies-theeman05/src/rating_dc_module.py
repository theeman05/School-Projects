"""
Rating class for this project which holds the data for a movie's rating.

Author: Ethan Hartman (ehh4525)
"""

from dataclasses import dataclass


@dataclass(frozen=True)
class Rating:
    t_const: str    # Rating identifier
    average: float  # Rating average out of 10
    votes: int      # Number of votes this rating has

    def __str__(self):
        """
        Overrides str function so we get a nicely formatted Rating string.
        :return: Formatted rating string.
        """
        return "Identifier: {t_const}, Rating: {average}, Votes: {votes}".format(t_const=self.t_const,
                                                                                 average=self.average, votes=self.votes)
