"""
This project reads in different movie and rating datasets into dictionaries and utilizes query_module to provide
different lookup queries to print that data out, and how long they take.

Author: Ethan Hartman (ehh4525)
"""

import sys
import query_module as query
import loader_module as loader


def main():
    """
    Main function of the program. Creates the movies and ratings dictionary.
    Iterates through sys.stdin to execute certain queries based on the line.
    """
    movies, ratings = loader.get_movies_ratings_display_results(len(sys.argv) == 1)
    for line in sys.stdin:
        query.execute_query_from_data_str(movies, ratings, line.strip())


if __name__ == '__main__':
    main()
