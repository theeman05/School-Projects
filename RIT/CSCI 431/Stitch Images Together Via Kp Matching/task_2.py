"""
Class: Intro to Computer Vision
Assignment: A4
Task: 2
Author: Ethan Hartman
"""
import cv2
import numpy as np
from matplotlib import pyplot as plt

ORB_1 = cv2.ORB.create(nfeatures=1000)
ORB_2 = cv2.ORB.create(nfeatures=10000)
BFMatcher = cv2.BFMatcher.create(cv2.NORM_HAMMING, crossCheck=True)


def featureDetect(grayscale_img, detection_method):
    """
    Detect and compute features using the given detection method.

    :param grayscale_img: Grayscale Image
    :param detection_method: cv2 Method to detect features with.
    :return: Key points and descriptors array.
    """
    return detection_method.detectAndCompute(grayscale_img, np.ones_like(grayscale_img, dtype=np.uint8) * 255)


def drawKeyPoints(img, keypoints):
    """
    Draws the given keypoints to two separate images, a non-detailed and detailed.

    :param img: Image to draw on
    :param keypoints: Key points to display
    :return: non-detailed, detailed image with keypoints drawn
    """
    non_detailed = np.zeros_like(img)
    detailed = np.zeros_like(img)
    cv2.drawKeypoints(img, keypoints, non_detailed)
    cv2.drawKeypoints(img, keypoints, detailed, flags=cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)

    return non_detailed, detailed


def displayWithKeypoints(non_detailed, detailed):
    """
    Displays the two images side-by-side

    :param non_detailed: Image with non-detailed key points
    :param detailed: Image with detailed key points
    """
    fig = plt.figure(figsize=(10, 9))

    fig.add_subplot(1, 2, 1)
    plt.imshow(non_detailed)
    plt.title("Base Key Points")

    fig.add_subplot(1, 2, 2)
    plt.imshow(detailed)
    plt.title("Detailed Key points")

    plt.show()


def matchFeatures(gray_reference, gray_query, reference_descriptors, query_descriptors, reference_keypoints, query_keypoints, match_count):
    """
    Matches feature descriptors, then displays the matched result.

    :param gray_reference: Grayscale reference
    :param gray_query: Grayscale query
    :param reference_descriptors: Reference descriptors
    :param query_descriptors: Query descriptors
    :param reference_keypoints: Reference Key points
    :param query_keypoints: Query key points
    :param match_count: How many matches to display
    """

    # Match descriptors
    matches = BFMatcher.match(reference_descriptors, query_descriptors)

    # Sort based on hamming distance
    matches = sorted(matches, key=lambda x: x.distance)

    matched_image = cv2.drawMatches(gray_reference, reference_keypoints, gray_query, query_keypoints, matches[:match_count], outImg=None, flags=cv2.DRAW_MATCHES_FLAGS_NOT_DRAW_SINGLE_POINTS)
    plt.title("Feature Matching")
    plt.imshow(matched_image)
    plt.show()


def main():
    """Main method which will run our helpers"""
    face = cv2.cvtColor(cv2.imread("face.jpg"), cv2.COLOR_BGR2RGB)
    gray_face = cv2.cvtColor(face, cv2.COLOR_RGB2GRAY)

    keypoints, descriptors = featureDetect(gray_face, ORB_1)

    displayWithKeypoints(*drawKeyPoints(face, keypoints))

    rotated_gray_face = cv2.cvtColor(cv2.imread("rotated_img.jpg"), cv2.COLOR_BGR2GRAY)
    rotated_keypoints, rotated_descriptors = featureDetect(rotated_gray_face, ORB_1)
    matchFeatures(gray_face, rotated_gray_face, descriptors, rotated_descriptors, keypoints, rotated_keypoints, 300)

    full_image = cv2.cvtColor(cv2.imread("full_image.jpg"), cv2.COLOR_BGR2GRAY)
    full_keypoints, full_descriptors = featureDetect(full_image, ORB_2)
    matchFeatures(gray_face, full_image, descriptors, full_descriptors, keypoints, full_keypoints, 50)


if __name__ == '__main__':
    main()
