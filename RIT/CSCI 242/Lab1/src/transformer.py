"""
CSAPX Lab 1: Secret Messages

A program that encodes/decodes a message by applying a set of transformation operations.
The transformation operations are:
    shift - Sa[,n] changes letter at index a by moving it n letters fwd in the alphabet. A negative
        value for n shifts the letter backward in the alphabet.
    rotate - R[n] rotates the string n positions to the right. A negative value for n rotates the string
        to the left.
    duplicate - Da[,n] follows character at index a with n copies of itself.
    trade - Ti,j trades the letter at index i with the letter at index j.

All indices number (the subscript parameters) are 0-based.

author: Ethan Hartman
"""


def shift(msg: str, idx: int, exponent: int = 1):
    """
    Changes a letter at index 'idx' in the string 'msg' by moving it 'exponent' number of letters fwd in the alphabet.
    Negative values will shift the letter backwards in the alphabet
    :param msg: The message being transformed
    :param idx: The location of the letter being shifted
    :param exponent: The number of letters we are moving the main letter by.
    :return: Transformed string with a shifted letter
    """
    return msg[:idx] + chr((ord(msg[idx]) - ord('A') + 26 + exponent) % 26 + ord('A')) + msg[idx + 1:]


def rotate(msg: str, amount: int = 1):
    """
    Rotates the string 'msg' 'amount' positions to the right. A negative value for 'amount' rotates the string
        to the left
    :param msg: The message being rotated
    :param amount: The amount of characters being rotated
    :return: Transformed string with rotated letters.
    """
    return msg[-amount:] + msg[:-amount]


def duplicate(msg: str, idx: int, copies: int = 1):
    """
    Duplicates a character at 'idx' with 'copies' copies of itself
    If 'copies' < 0, that number of copies will be removed from the string
    :param msg: The message with a duplication
    :param idx: The location of the letter being duplicated
    :param copies: The number of copies we would like made
    :return: Transformed string with duplicated letters.
    """
    if copies >= 0:
        return msg[:idx] + msg[idx] * copies + msg[idx:]
    else:
        return msg[:idx] + msg[idx-copies:]


def trade(msg: str, idx: int, swap_idx: int):
    """
    Trades two characters at index 'idx' and 'swap_idx' with each other. We assume i < j
    :param msg: The message with a desired trade
    :param idx: The index of the first trade character
    :param swap_idx: The index of the second trade character
    :return: Swapped message string
    """
    return msg[:idx] + msg[swap_idx] + msg[idx + 1:swap_idx] + msg[idx] + msg[swap_idx + 1:]


def transform(msg: str, operation_str: str, encrypt: bool):
    """
    Transforms a given string, msg based on the given operations and the boolean, encrypt.
    Operations are parsed and their associated operation will be called with the given arguments
    The resultant string is returned
    :param msg: The message being transformed
    :param operation_str: The operations which will be executed
    :param encrypt: If we are encrypting or decrypting
    :return: The transformed message
    """
    operations = operation_str.split(';')
    encrypt_mult = 1
    if not encrypt:
        # Decrypting requires reversed operations
        operations.reverse()
        encrypt_mult = -1

    for operation in operations:
        operation_name = operation[0]
        comma = operation.find(',')
        operation_method = None
        # assign an operation function
        if operation_name == "S":
            operation_method = shift
        elif operation_name == "R":
            operation_method = rotate
        elif operation_name == "D":
            operation_method = duplicate
        elif operation_name == "T":
            operation_method = trade

        # Determine parameters and call the operation method with those parameters
        if comma != -1:
            # Two parameters
            param1, param2 = int(operation[1:comma]), int(operation[comma + 1:])
            # T will not be changed between encrypting or decrypting
            msg = operation_method(msg, param1, param2 * encrypt_mult if operation_name != "T" else param2)
        elif len(operation) > 1:
            # One parameter
            param = int(operation[1:])
            if encrypt:
                # Normal case
                msg = operation_method(msg, param)
            elif operation_name == "R":
                # Rotate decrypt case. 'param' must be negated
                msg = operation_method(msg, param * encrypt_mult)
            else:
                # Other decrypt cases.
                msg = operation_method(msg, param, encrypt_mult)
        else:
            # No parameters given can only mean 'rotate'.
            msg = rotate(msg, encrypt_mult)
    return msg


def main() -> None:
    """
    The main loop responsible for getting the input details from the user
    and printing in the standard output the results
    of encrypting or decrypting the message applying the transformations
    :return: None
    """
    print("Welcome to Secret Messages!")
    option = input("What do you want to do: (E)ncrypt, (D)ecrypt or (Q)uit? ")

    # Loop until the user chooses "Q"
    while option != "Q":
        if option != "Q":
            msg = input("Enter the message: ")
            operation_str = input("Enter the encrypting transformation operations: ")
            print("Generating output ...")
            print(transform(msg, operation_str, option == "E"))

        option = input("What do you want to do: (E)ncrypt, (D)ecrypt or (Q)uit? ")

    print("Goodbye!")


if __name__ == '__main__':
    main()
