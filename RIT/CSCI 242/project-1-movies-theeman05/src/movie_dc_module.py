"""
Movie class for this project which holds the data for a movie.

Author: Ethan Hartman (ehh4525)
"""

from dataclasses import dataclass


@dataclass(frozen=True)
class Movie:
    t_const: str            # Movie identifier
    title_type: str         # Movie type
    primary_title: str      # Movie primary title
    original_title: str     # Movie original title
    start_year: int         # When the movie was originally released
    runtime_minutes: int    # How long the movie is (minutes)
    genres: str             # Genres associated to the movie

    def __str__(self):
        """
        Overrides str function so we get a nicely formatted Movie string.
        :return: Formatted movie string.
        """
        return "Identifier: {t_const}, Title: {primary_title}, Type: {title_type}, Year: {start_year}, " \
               "Runtime: {runtime_minutes}, Genres: {genres}" \
            .format(t_const=self.t_const, primary_title=self.primary_title, title_type=self.title_type,
                    start_year=self.start_year, runtime_minutes=self.runtime_minutes, genres=self.genres)

