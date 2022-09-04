"""
CSAPX Lab 2: Fractal Antenna

A program that recursively creates and displays a fractal antenna two different ways with the requested
    length, 'length' and level, 'level'. The level will determine the antenna pattern. The total antenna length
    will be printed after both methods.
The program displays the antenna using python's turtle library.

author: Ethan Hartman
"""
import math
import turtle
import re

# RegEx patterns for the given class type
TYPE_PATTERS = {
    int: r"[0-9]+",
    float: r"[0-9]+.[0-9]+"
}


def draw_perimeter(length: float, level: int, is_base_call: bool = True):
    """
    (Method 1) A recursive function who draws a fractal antenna by drawing it's perimeter.
    :param length: The overall length of the antenna.
    :param level: The depth of recursion which overall shapes the antenna.
    :param is_base_call: If the call is a base or recursive call. To get four sides, this param must be True.
    :return: The total length of the lines.
    """
    iterations = 1
    total_length = 0

    if is_base_call:
        # Base call loops 4 times vs. 1, also places turtle in correct position and places pen down.
        turtle.penup()
        iterations = 4
        turtle.right(90)
        turtle.forward(length / math.sqrt(2))
        turtle.left(135)
        turtle.pendown()

    for _ in range(iterations):
        if level == 1:
            total_length += length
            turtle.forward(length)
        else:
            total_length += draw_perimeter(length / 3, level - 1, False)
            turtle.left(90)
            total_length += draw_perimeter(length / 3, level - 1, False)
            turtle.right(90)
            total_length += draw_perimeter(length / 3, level - 1, False)
            turtle.right(90)
            total_length += draw_perimeter(length / 3, level - 1, False)
            turtle.left(90)
            total_length += draw_perimeter(length / 3, level - 1, False)
        if is_base_call:
            turtle.left(90)
    return total_length


def draw_squares(length: float, level: int):
    """
    (Method 2) A recursive function who draws a fractal antenna by drawing it's individual squares.
    :post-condition: turtle must be penup.
    :param length: The overall length of the antenna.
    :param level: The depth of recursion which overall shapes the antenna.
    :return: The total length of the lines.
    """
    if level == 1:
        # Move to the center based on the given length and create a square & move back to initial position.
        turtle.back(length / math.sqrt(2))
        turtle.pendown()
        turtle.left(45)
        for _ in range(4):
            turtle.forward(length)
            turtle.right(90)
        turtle.penup()
        turtle.right(45)
        turtle.forward(length / math.sqrt(2))
        return length * 4
    # Create 5 squares in the antenna pattern
    length /= 3
    total_len = draw_squares(length, level - 1)
    for _ in range(4):
        turtle.forward(length / math.sqrt(2) * 2)
        total_len += draw_squares(length, level - 1)
        turtle.back(length / math.sqrt(2) * 2)
        turtle.left(90)
    return total_len


def type_cast(uncast_str: str, desired_type: type, alt_type: type = None):
    """
    Casts the given string to the desired_type or alt_type ( if desired fails)
        The global variable 'TYPE_PATTERS' must define the RegEx patterns.
    :param uncast_str: The string being casted.
    :param desired_type: The main type we would like to cast 'uncast_str' to.
    :param alt_type: (Optional) The secondary type we would like to cast 'uncast_str' to if desired fails.
    :return: IF successful cast: A casted object of 'desired_type' ELSE: None
    """
    if re.fullmatch(TYPE_PATTERS[desired_type], uncast_str) or alt_type and re.fullmatch(TYPE_PATTERS[alt_type], uncast_str):
        return desired_type(uncast_str)

    print("Value must be a " + desired_type.__name__ + ". You entered '" + uncast_str + "'.")
    return None


def wait_for_given_type(input_str: str, desired_type: type, alt_type: type = None):
    """
    Waits for the user to type a valid string which may be casted to 'desired_type' or 'alt_type' (if applicable).
    :param input_str: The string we would like to display before an input request.
    :param desired_type: The main type we would like to cast the user input to.
    :param alt_type: (Optional) The secondary type we would like to cast the user input to if desired fails.
    :return: A casted object of 'desired_type'
    """
    typed_object = None
    while not typed_object:
        typed_object = type_cast(input(input_str), desired_type, alt_type)
    return typed_object


def wait_for_enter():
    """
    Waits for the user to press "Enter" on their keyboard
    """
    print("Hit enter to continue...", end="")
    while input() != "":
        pass


def turtle_init():
    """
    Clears and places the turtle in the center of the screen facing East, sets turtle speed to 0, and lifts the pen.
    """
    turtle.reset()
    turtle.speed(0)
    turtle.penup()


def main():
    """
    Main method of the program which executes both antenna methods with the validated user-input values.
    """

    desired_len = wait_for_given_type("Length of initial side: ", float, int)
    desired_levels = wait_for_given_type("Number of levels: ", int)
    turtle_init()
    print("Strategy 1 - Antenna's length is " + str(draw_squares(desired_len, desired_levels)) + " units.")
    wait_for_enter()
    turtle_init()
    print("Strategy 2 - Antenna's length is " + str(draw_perimeter(desired_len, desired_levels)) + " units.")
    turtle.done()
    print("Bye!")


if __name__ == '__main__':
    main()
