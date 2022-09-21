"""
Loader module which does the reading of movies and ratings from a specified filename into dictionaries.

Author: Ethan Hartman (ehh4525)
"""

from timeit import default_timer as timer
from movie_dc_module import Movie
from rating_dc_module import Rating
from query_module import combine_list_to_str


EMPTY_STRING = "\\N"  # Identifier for an empty string. Used in "none_if_empty"


def none_if_empty(test_str: str, alt_return=None):
    """
    Returns the given alr_return if the given string is equal to "EMPTY_STRING"
    If not empty, returns a cast value of test_str with the type being the alternate return.
    :param test_str: The string tested for an empty sequence.
    :param alt_return: What will be returned if "test_str" is considered empty.
    :return: alt_return (test_str empty) or cast test_str with the type alt_return
    """
    return alt_return if test_str == EMPTY_STRING else test_str if alt_return is None else type(alt_return)(test_str)


def load_movies(filename: str) -> dict:
    """
    Reads bands from the given file and returns them.
    :param filename: The name of the file holding the band data
    :return: A list with a child type, 'Band'
    """
    movies = dict()
    not_first = False
    with open(filename, encoding='utf-8') as f:
        for line in f:
            if not_first:
                info = line.strip().split("\t")
                if info[4] == "0":
                    movies[info[0]] = Movie(t_const=info[0], title_type=info[1], primary_title=info[2],
                                            original_title=info[3], start_year=none_if_empty(info[5], 0),
                                            runtime_minutes=none_if_empty(info[7], 0),
                                            genres=none_if_empty(combine_list_to_str(info[8].split(","), ", "), "None"))
            else:
                not_first = True
        return movies


def load_ratings(filename: str, check_dict: dict) -> dict:
    """
    Reads ratings from the given file and returns them if they're in the check_dictionary.
    :param filename: The name of the file holding the band data
    :param check_dict: The dictionary in which we search if the rating is present.
    :return: A list with a child type, 'Rating'
    """
    ratings = dict()
    not_first = False
    with open(filename, encoding='utf-8') as f:
        for line in f:
            if not_first:
                info = line.strip().split("\t")
                if info[0] in check_dict:
                    ratings[info[0]] = Rating(t_const=info[0], average=float(info[1]), votes=int(info[2]))
            else:
                not_first = True
        return ratings


def get_movies_ratings_display_results(is_large: bool) -> tuple[dict[Movie], dict[Rating]]:
    """
    Times the loading and stores both ratings and movies into dictionaries.
    :param is_large: If the files are large, we will search title.
    :return: Movie dictionary, Rating dictionary
    """
    size_path = "title" if is_large else "small"
    print("reading data/" + size_path + ".basics.tsv into dict...")
    start = timer()
    movies = load_movies("data/" + size_path + ".basics.tsv")
    print("elapsed time (s): " + str(timer() - start))

    print("\nreading data/" + size_path + ".ratings.tsv into dict...")
    start = timer()
    ratings = load_ratings("data/" + size_path + ".ratings.tsv", movies)
    print("elapsed time (s): " + str(timer() - start))

    print("\nTotal movies:", len(movies))
    print("Total ratings: " + str(len(ratings)) + "\n")

    return movies, ratings
