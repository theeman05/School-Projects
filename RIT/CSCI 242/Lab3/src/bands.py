"""
CSAPX Lab 3: Battle of the Bands
Given a list of bands and the number of votes they received, find the most mediocre band (i.e. the band with the median amount of votes)

$ python3 bands.py [slow|fast] input-file

Author: RIT CS
Author: Ethan Hartman
"""
from dataclasses import dataclass
from os import getcwd
import sys  # argv
import time  # clock
import random  # random


@dataclass
class Band:
    name: str
    votes: int


def _partition(data: list[Band], pivot: Band) \
        -> tuple[list[Band], list[Band], list[Band]]:
    """
    Three way partition the data into smaller, equal and greater lists,
    in relationship to the pivot
    :param data: The data to be sorted (a list)
    :param pivot: The value to partition the data on
    :return: Three list: smaller, equal and greater
    """
    less, equal, greater = [], [], []
    for element in data:
        if element.votes < pivot.votes:
            less.append(element)
        elif element.votes > pivot.votes:
            greater.append(element)
        else:
            equal.append(element)
    return less, equal, greater


def quick_sort(data: list[Band]) -> list[Band]:
    """
    Performs a quick sort and returns a newly sorted list (ascending)
    :param data: The data to be sorted (a list)
    :return: A sorted list
    """
    if len(data) == 0:
        return []
    else:
        pivot = data[0]
        less, equal, greater = _partition(data, pivot)
        return quick_sort(less) + equal + quick_sort(greater)


def quick_select(data: list[Band], k: int) -> Band:
    """
    Finds the k'th smallest Band in the given list based on votes.
    :param data: The data to pull the k'th smallest element from
    :param k: The k'th smallest element
    :return: The k'th smallest Band
    """
    pivot = data[random.randint(0, len(data) - 1)]
    smaller, equal, larger = _partition(data, pivot)

    m = len(smaller)
    count = len(equal)

    if m <= k < m + count:
        return pivot
    elif m > k:
        return quick_select(smaller, k)
    else:
        return quick_select(larger, k - m - count)


def load_bands(filename: str) -> list[Band]:
    """
    Reads bands from the given file and returns them.
    :param filename: The name of the file holding the band data
    :return: A list with a child type, 'Band'
    """
    bands = []
    with open(filename, encoding='utf-8') as f:
        for line in f:
            info = line.split("\t")
            bands.append(Band(name=info[0], votes=int(info[1])))
        return bands


def main() -> None:
    """
    The main function.
    :return: None
    """
    # Ensure input arguments == 3; if not, display error message and return
    if len(sys.argv) != 3:
        print("Usage: " + sys.argv[0] + " [slow|fast] input-file")
        return

    search_type, filename = sys.argv[1], sys.argv[2]
    outfile = "out-" + search_type + "-" + filename
    if getcwd().endswith("\\src"):
        filename = "../data/" + filename
        outfile = "../output/" + outfile

    bands = load_bands(filename)
    mediocre = None

    start = time.perf_counter()
    if search_type == "slow":
        mediocre = quick_sort(bands)[len(bands) // 2]
    elif search_type == "fast":
        mediocre = quick_select(bands, len(bands) // 2)
    elapsed = time.perf_counter() - start

    # Write stuff to the out file
    with open(outfile, "w", encoding='utf-8') as f:
        f.write("Search type: " + search_type + "\n" +
                "Number of band:" + str(len(bands)) + "\n" +
                "Elapsed time:" + str(elapsed) + "\n" +
                "Most Mediocre Band:" + str(mediocre)
                )


if __name__ == '__main__':
    main()
