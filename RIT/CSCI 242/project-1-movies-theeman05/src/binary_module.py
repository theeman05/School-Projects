"""
The binary module mainly for its binary_insert method to insert into a list with a certain sorting criteria.

Author: Ethan Hartman (ehh4525)
"""


def table_shift(table: list, fro: int):
    """
    Shifts the items at and after the given index, 'fro' right 1.
    The last value of the list will be replaced.
    :param table: List of items
    :param fro: The index identifying the first location to be shifted.
    """
    prev_val = table[fro]
    for i in range(fro, len(table) - 1):
        table[i + 1], prev_val = prev_val, table[i + 1]


def insert_or_shift(table: list, value, desired_idx: int, max_size: int):
    """
    Either inserts into or shifts the given list to insert the given value at the desired index.
    If the list is the max size and the desired index is inside the size, the list will be shifted and the value will be
    inserted at desired index.
    If the list has not reached its max size or max size is -1, the value will be inserted at the desired index.
    If desired index is >= the max size, it will not be added.
    :param table: Table that value is expected to insert into
    :param value: Value being inserted into the table
    :param desired_idx: Desired index location in the table for the value to be placed at
    :param max_size: The maximum size of the list.
    """
    if max_size == -1 or len(table) < max_size:
        table.insert(desired_idx, value)
    elif desired_idx < max_size:
        table_shift(table, desired_idx)
        table[desired_idx] = value


def binary_insert(search_list: list, value, max_size: int = -1, key=lambda val: val):
    """
    Binary search method to insert value into a certain location in list based on the key function.
    if a max size is given and the value is outside the range, it will not be inserted.
    NOTE - If the located index is outside the specified max size, the item will not be inserted (unless max_size = -1.)
    :param search_list: The sorted list to insert into
    :param value: The value we are inserting
    :param max_size: The max size of the array. Default: -1 means no limit
    :param key: key function to identify the comparison value
    """
    start = 0
    end = len(search_list) - 1

    if end >= 0:  # if we have at least 1 item, we can speed things up by checking edge cases first.
        if key(value) >= key(search_list[-1]):  # Insert after the last item since larger
            insert_or_shift(search_list, value, end + 1, max_size)
            return
        elif key(value) <= key(search_list[0]):  # Insert before the last item since smaller
            insert_or_shift(search_list, value, 0, max_size)
            return

    while start <= end:
        mid_index = (start + end) // 2
        mid_val = key(search_list[mid_index])
        # if values match, insert the item into this location and shift the rest of the list over
        if key(value) == mid_val:
            insert_or_shift(search_list, value, mid_index + 1, max_size)
            return
        # if value is left of middle, search there
        elif key(value) < mid_val:
            end = mid_index - 1
        # value is right of middle, search there
        else:
            start = mid_index + 1

    insert_or_shift(search_list, value, start, max_size)  # start > end. Try to insert
