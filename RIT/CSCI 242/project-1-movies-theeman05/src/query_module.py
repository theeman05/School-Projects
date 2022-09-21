"""
Module containing query methods for printing different sets of data from the Rating or Movie dictionary.

Author: Ethan Hartman (ehh4525)
"""

from timeit import default_timer as timer
from binary_module import binary_insert
from movie_dc_module import Movie
from rating_dc_module import Rating


def sort_year_genre(movie: Movie) -> str:
    """
    Sort key for the year_and_genre method based on a movie's primary title (ascending)
    :param movie: The movie we are grabbing the title from
    :return: The movies primary title.
    """
    return movie.primary_title


def sort_runtime(movie: Movie) -> tuple[int, str]:
    """
    Sort key for the runtime method based on a movie's runtime(descending) and it's primary title(ascending).
    :param movie: The movie we are grabbing the title from
    :return: (The movie's negative runtime, the movie's primary title)
    """
    return -movie.runtime_minutes, movie.primary_title


def sort_most_votes(tup: tuple[int, Movie]) -> tuple[int, str]:
    """
    Sort key for the most_votes method based on a movie's votes(descending) and it's primary title(ascending).
    :param tup: tup[0] - The rating number of votes | tup[1] - The movie we are grabbing the title from
    :return: (The movie's negative votes, the movie's primary title)
    """
    return -tup[0], tup[1].primary_title


def sort_top(tup: tuple[Rating, Movie]) -> tuple[float, int, str]:
    """
    Sort key for the top method based on a movie's average(descending), votes(descending) and it's primary
    title(ascending).
    :param tup: tup[0] - The movie's Rating | tup[1] - The movie we are grabbing the title from
    :return: (The movie's negative average, the movie's negative votes, the movie's primary title)
    """
    return -tup[0].average, -tup[0].votes, tup[1].primary_title


def combine_list_to_str(items: list[str], separator: str = "") -> str:
    """
    Combines a list's items into a string separated by "separator"
    :param items: The list of items
    :param separator: The separator between each item in the combined string
    :return: a string of items separated by the separator
    """
    combined = ""
    for i in range(len(items)):
        if i != 0:
            combined += separator + items[i]
        else:
            combined += items[i]
    return combined


def lookup(movie_dict: dict, rating_dict: dict, t_const: str):
    """
    Look up and display a movie and its rating based on the identifier.
    :param movie_dict: Dictionary of movies
    :param rating_dict: Dictionary of ratings
    :param t_const: Identifier to represent the Movie and Rating.
    """
    found_movie = movie_dict.get(t_const)
    found_rating = rating_dict.get(t_const)

    print(("\tMOVIE: " + str(found_movie)) if found_movie else "\tMovie not found!")
    print(("\tRATING: " + str(found_rating)) if found_rating else "\tRating not found!")


def contains(movie_dict: dict, title_type: str, words: str):
    """
    Find and display all movies of a certain type which contain the sequence of words.
    :param movie_dict: Dictionary of movies to search in
    :param title_type: The type of movies to look for
    :param words: The sequence of words to look for
    """
    no_match = True
    for movie in movie_dict.values():
        if movie.title_type == title_type and words in movie.primary_title:
            print("\t" + str(movie))
            no_match = False
    if no_match:
        print("\tNo match found!")


def year_and_genre(movie_dict: dict, title_type: str, year: int, genre: str):
    """
    Find and display all movies of a certain type from a particular year with the given genre.
    :param movie_dict: Dictionary of movies to search in
    :param title_type: The type of movies to look for
    :param year: The start year of the desired movies
    :param genre: The genre of the desired movies
    """
    matches = []
    for movie in movie_dict.values():  # insert matches
        if movie.title_type == title_type and movie.start_year == year and genre in movie.genres:
            binary_insert(matches, movie, key=sort_year_genre)

    if len(matches) == 0:  # print matches
        print("\tNo match found!")
    else:
        for movie in matches:
            print("\t" + str(movie))


def runtime(movie_dict: dict, title_type: str, min_minutes: int, max_minutes: int):
    """
    Find and display all movies of a certain type that are within a range of minutes.
    :param movie_dict: Dictionary of movies to search in
    :param title_type: The type of movies to look for
    :param min_minutes: The minimum search minutes
    :param max_minutes: The maximum search minutes
    """
    matches = []
    for movie in movie_dict.values():  # insert matches
        if movie.title_type == title_type and min_minutes <= movie.runtime_minutes <= max_minutes:
            binary_insert(matches, movie, key=sort_runtime)

    if len(matches) == 0:  # print matches
        print("\tNo match found!")
    else:
        for movie in matches:
            print("\t" + str(movie))


def most_votes(movie_dict: dict, rating_dict: dict, title_type: str, num: int):
    """
    Find and display a specified number of movies with a certain type and the most votes
    :param movie_dict: Dictionary of movies to search in
    :param rating_dict: Dictionary of ratings
    :param title_type: The type of movies to look for
    :param num: Number movies to be displayed
    """
    matches = []  # (rating.votes, movie)
    for rating in rating_dict.values():   # insert matches
        if movie_dict[rating.t_const].title_type == title_type:
            binary_insert(matches, (rating.votes, movie_dict[rating.t_const]), max_size=num, key=sort_most_votes)

    if len(matches) == 0:  # print matches
        print("\tNo match found!")
    else:
        for i in range(len(matches) if len(matches) < num else num):
            print("\t{0}. VOTES: {1}, MOVIE: {2}".format(i + 1, matches[i][0], matches[i][1]))


def top(movie_dict: dict, rating_dict: dict, title_type: str, num: int, start_year: int, end_year: int):
    """
    Find and display the number of movies of a certain type by a range of years (inclusive) that are the highest rated,
    and have at least 1000 votes.
    :param movie_dict: Dictionary of movies to search in
    :param rating_dict: Dictionary of ratings
    :param title_type: The type of movies to look for
    :param num: Number movies to be displayed in each year
    :param start_year: The year to begin the search at
    :param end_year: The year to end the search at
    """
    matches = dict()  # year = list[(rating, movie)]
    for i in range(start_year, end_year + 1):
        matches[i] = []

    for rating in rating_dict.values():  # insert matches
        if rating.votes >= 1000:
            movie = movie_dict[rating.t_const]
            if movie.title_type == title_type and start_year <= movie.start_year <= end_year:
                binary_insert(matches[movie.start_year], (rating, movie), max_size=num, key=sort_top)

    for year in matches:  # print matches
        arr_len = len(matches[year])
        print("\tYEAR: {0}".format(year))
        if arr_len == 0:
            print("\t\tNo match found!")
        else:
            for i in range(arr_len if arr_len < num else num):
                print("\t\t{0}. RATING: {1}, VOTES: {2}, MOVIE: {3}".
                      format(i + 1, matches[year][i][0].average, matches[year][i][0].votes, matches[year][i][1]))


def get_query_data(data_line: str) -> tuple[str]:
    """
    Separates the data into a tuple based on spaces
    :param data_line: data line to be separated
    :return: split data pieces
    """
    split_data = data_line.split(" ")
    tuple_data = tuple(split_data)

    if tuple_data[0] == "CONTAINS":  # Contains has annoying spaces
        tuple_data = tuple([split_data[0], split_data[1], combine_list_to_str(split_data[2:], " ")])

    return tuple_data


def execute_query_from_data_str(movies: dict, ratings: dict, data_line: str):
    """
    Times and executes a query based on the query type and dictionaries passed.
    :param movies: Movie dictionary
    :param ratings: Rating dictionary
    :param data_line: data line
    """
    query_data = get_query_data(data_line)
    start = timer()
    if query_data[0] == "LOOKUP":
        print("processing:", query_data[0], query_data[1])
        lookup(movies, ratings, query_data[1])
    elif query_data[0] == "CONTAINS":
        print("processing:", query_data[0], query_data[1], query_data[2])
        contains(movies, query_data[1], query_data[2])
    elif query_data[0] == "YEAR_AND_GENRE":
        print("processing:", query_data[0], query_data[1], query_data[2], query_data[3])
        year_and_genre(movies, query_data[1], int(query_data[2]), query_data[3])
    elif query_data[0] == "RUNTIME":
        print("processing:", query_data[0], query_data[1], query_data[2], query_data[3])
        runtime(movies, query_data[1], int(query_data[2]), int(query_data[3]))
    elif query_data[0] == "MOST_VOTES":
        print("processing:", query_data[0], query_data[1], query_data[2])
        most_votes(movies, ratings, query_data[1], int(query_data[2]))
    elif query_data[0] == "TOP":
        print("processing:", query_data[0], query_data[1], query_data[2], query_data[3], query_data[4])
        top(movies, ratings, query_data[1], int(query_data[2]), int(query_data[3]), int(query_data[4]))
    else:
        print("No valid query could be found for '" + query_data[0] + "'.")
    print("elapsed time (s): " + str(timer() - start), "\n")
