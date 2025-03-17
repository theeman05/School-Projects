"""
Class: Intro to Computer Vision
Assignment: A3
Author: Ethan Hartman
"""
import os
import threading

import cv2
import numpy as np

MAX_INTENSITY_VALUE = 255
THRESHOLD_VALUE = 190
BLUR_KERNEL = (9, 9)

MIN_DICE_SIZE = 210
MAX_DICE_SIZE = 250
MAX_DOT_ARC_LEN = 280
MIN_DOT_ARC_LEN = 130
MIN_DOT_CIRCULARITY = .56
VALID_CORNER_COUNT = 4

DOTS_ON_DICE = 6

DOT_HIGHLIGHT_COLOR = 0, 255, 0
DIE_HIGHLIGHT_COLOR = 0, 0, 255

# Identity array for a square
SQUARE_POINT_IDENTITY = np.array([
    [-1 / 2, -1 / 2],
    [1 / 2, -1 / 2],
    [1 / 2, 1 / 2],
    [-1 / 2, 1 / 2]
], dtype=np.float32)

# If we want to display output images (probably for debugging)
DISPLAY_OUTPUT_IMG = False

IMAGE_DIR_PATH = "Images_Directory"

OUTPUT_STR = "INPUT Filename: {0}\nNumber of Dice: {1}" + "".join(f"\nNumber of {i_+1}'s: {{{i_ + 2}}}" for i_ in range(DOTS_ON_DICE)) + f"\nNumber of Unknown: {{{DOTS_ON_DICE+2}}}\nTotal of all dots: {{{DOTS_ON_DICE+3}}}"


def displayImgWait(img_name, img):
    """
    Shows the given image then waits for input to close the image.
    :param img_name: The name to give the display window.
    :param img: The image array.
    """
    cv2.imshow(img_name, img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


def magnitude(arr):
    """
    Get the magnitude of an array.
    :param arr: Array to get magnitude of.
    :return: The magnitude of the array.
    """
    return np.sqrt(np.sum(np.square(arr)))


def computeImageStats(img):
    """
    Computes the stats of the given image, highlights dice, and dots.
    :param img: Image to determine stats on.
    :return: A list of the stats of the image in the following format: [Die Count, Number of 1's, ..., Number of <DOTS_ON_DICE>'s, Number of Unknown, Total Dot count]
    """
    img_stats = [0] * 9
    valid_dice_corners = []

    # Convert color to grayscale
    working_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # Remove any noise, and blur dots so lighter dots look more like full circles.
    working_img = cv2.GaussianBlur(working_img, BLUR_KERNEL, 0)

    # Threshold the image, so it is only between THRESHOLD_VALUE and 255
    _, working_img = cv2.threshold(working_img, THRESHOLD_VALUE, MAX_INTENSITY_VALUE, cv2.THRESH_BINARY)

    # Locate dice contours
    contours, _ = cv2.findContours(working_img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    # Get corners of all dice
    for cnt in contours:
        # Turn contours into a maximum of 4 corners
        approx_corners = cv2.approxPolyDP(cnt, 0.1 * cv2.arcLength(cnt, True), True)
        # Ensure the corners are in range of the correct dice size
        if len(approx_corners) == VALID_CORNER_COUNT:
            offset_y, offset_x = approx_corners[1, 0] - approx_corners[0, 0], approx_corners[2, 0] - approx_corners[
                1, 0]
            height, width = magnitude(offset_y), magnitude(offset_x)
            if height > MIN_DICE_SIZE:
                horiz_dice = round(width / MAX_DICE_SIZE)
                vert_dice = round(height / MAX_DICE_SIZE)
                single_y_offset, single_x_offset = offset_y / vert_dice, offset_x / horiz_dice
                # Separate touching dice with vector calculations
                for i in range(horiz_dice):
                    for j in range(vert_dice):
                        top_right = approx_corners[0, 0] + single_y_offset * j + single_x_offset * i
                        top_left = top_right + single_y_offset
                        bottom_left = top_left + single_x_offset
                        bottom_right = top_right + single_x_offset

                        polygon = np.array([
                            [top_right],
                            [top_left],
                            [bottom_left],
                            [bottom_right]
                        ], dtype=int)

                        rect = cv2.minAreaRect(polygon)

                        # Calculate the side length for the square
                        center_x = int(rect[0][0])
                        center_y = int(rect[0][1])
                        side_length = max(rect[1])

                        # Create a square centered at the polygon's center
                        square_points = SQUARE_POINT_IDENTITY * side_length

                        # Define the rotation angle based on the polygon's orientation
                        angle = rect[2] * np.pi / 180.0

                        # Create a rotation matrix
                        rotation_matrix = np.array([
                            [np.cos(angle), -np.sin(angle)],
                            [np.sin(angle), np.cos(angle)]
                        ], dtype=np.float32)

                        # Apply the rotation matrix to the square points and restore center
                        rotated_square_points = np.dot(square_points, rotation_matrix.T) + np.array([center_x, center_y])

                        # Convert to integer for drawing
                        rotated_square_points = np.intp(rotated_square_points)

                        # Add valid corners to our array
                        valid_dice_corners.append(rotated_square_points)

    img_stats[0] = len(valid_dice_corners)

    # Find contours within each dice
    for corners in valid_dice_corners:
        # Create a mask for the die, where valid pixels are within the corner area.
        mask = np.zeros_like(working_img)
        cv2.fillPoly(mask, [corners], MAX_INTENSITY_VALUE)
        masked_dice = cv2.bitwise_and(working_img, mask)

        # Find contours (potential dots) within the given die
        contours, _ = cv2.findContours(masked_dice, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
        cur_dots = 0
        for cnt in contours:
            area = cv2.contourArea(cnt)
            arc_len = cv2.arcLength(cnt, True)
            circularity = (4 * np.pi * area / (arc_len * arc_len)) if arc_len != 0 else 0
            approx_corners = cv2.approxPolyDP(cnt, .1 * arc_len, True)
            # If the dot is the correct size, circularity, and has 4 corners, we have a dot!
            if MIN_DOT_ARC_LEN < arc_len < MAX_DOT_ARC_LEN and MIN_DOT_CIRCULARITY < circularity and len(approx_corners) == VALID_CORNER_COUNT:
                cv2.drawContours(img, [approx_corners], 0, DOT_HIGHLIGHT_COLOR, 5)
                cur_dots += 1

        if 0 < cur_dots <= DOTS_ON_DICE:
            img_stats[cur_dots] += 1  # Should have an extra dice with that number of dots, so increment the count.
            img_stats[-1] += cur_dots  # Add the current count to the total dot count
        else:
            img_stats[-2] += 1  # The dice either had too many dots or not enough, add to unknowns.

        cv2.drawContours(img, [corners], 0, DIE_HIGHLIGHT_COLOR, 5)

    return img_stats


def computeAndDisplayImgStats(img, filename, display_output_image=False):
    """
    Computes, then displays the stats of the given image.
    Note: Displayed images will be 1/5 their original size.
    :param img: Image to determine stats on.
    :param filename: The name of the file.
    :param display_output_image: Displays output image (False by default)
    """
    img_stats = computeImageStats(img)

    # Print stats
    print(OUTPUT_STR.format(str.upper(filename), *img_stats))

    if display_output_image:
        img_y, img_x, _ = img.shape
        displayImgWait(filename, cv2.resize(img, (img_x // 5, img_y // 5)))


def processImage(img_path, display_output_image):
    """
    Processes the image with the given path.
    :param img_path: Path to the image.
    :param display_output_image: Displays output image if set to true
    """
    computeAndDisplayImgStats(cv2.imread(img_path), str.split(img_path, "/")[-1], display_output_image)


def processImagesAsync(dir_path, display_output_image):
    """
    Processes all images in the given path to the directory, while displaying their stats.
    :param dir_path: The path to the image directory.
    :param display_output_image: Displays output image
    """
    running = []
    # Each image takes time to load, load images on separate threads to speed this process up.
    for filename in os.listdir(dir_path):
        if filename.endswith(".jpg"):
            thread = threading.Thread(target=processImage, args=(dir_path + "/" + filename, display_output_image))
            thread.start()
            running.append(thread)

    # Loop while we still have running threads
    while len(running) > 0:
        running[-1].join()
        del running[-1]


def computeAndDisplayRandomImgStats(dir_path, img_filenames):
    """
    Computes and displays a random image's stats.
    Removes the displayed image from the list.
    :param dir_path: Path to the directory of the image files
    :param img_filenames: List of image file names to choose from.
    """
    filename = np.random.choice(img_filenames)
    img_filenames.remove(filename)
    computeAndDisplayImgStats(cv2.imread(dir_path + "/" + filename), filename, True)


def main():
    """Main method which will run our helpers"""
    processImagesAsync(IMAGE_DIR_PATH, DISPLAY_OUTPUT_IMG)


if __name__ == '__main__':
    main()
