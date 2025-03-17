"""
Class: Intro to Computer Vision
Assignment: A4
Task: 1
Author: Ethan Hartman
"""

import cv2
import matplotlib.pyplot as plt
import numpy as np

MIN_GOOD_MATCHES = 4

SIFT = cv2.SIFT.create()
ORB = cv2.ORB.create()
BFMatcher = cv2.BFMatcher.create()


def featureDetect(grayscale_img, detection_method):
    """
    Detect and compute features using the given detection method.

    :param grayscale_img: Grayscale Image
    :param detection_method: cv2 Method to detect features with.
    :return: Keypoints and descriptors array.
    """
    return detection_method.detectAndCompute(grayscale_img, np.ones_like(grayscale_img, dtype=np.uint8) * 255)


def bruteForceKPDS(query, reference, ref_kps, ref_descriptors, query_kps, query_descriptors):
    """
    Brute forces the query and reference image given keypoints and descriptors to stitch a result together.

    :param query: Query image
    :param reference: Reference Image
    :param ref_kps: Reference Keypoints
    :param ref_descriptors: Reference Descriptors
    :param query_kps: Query Keypoints
    :param query_descriptors: Query Descriptors
    :return: A stitched BGR image if there were a certain number of good matches or None.
    """
    # Use a Brute-Force Matcher to find potential matches between keypoints in the two images
    matches = BFMatcher.knnMatch(ref_descriptors, query_descriptors, k=2)

    # Ratio test
    good_matches = []
    for m, n in matches:
        if m.distance < .75 * n.distance:
            good_matches.append(m)

    if len(good_matches) >= MIN_GOOD_MATCHES:
        # If there are at least 4 good matches, estimate a homography matrix to align the images using RANSAC.
        src_pts = np.float32([ref_kps[m.queryIdx].pt for m in good_matches]).reshape(-1, 1, 2)
        dst_pts = np.float32([query_kps[m.trainIdx].pt for m in good_matches]).reshape(-1, 1, 2)

        homography_matrix, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC, 5.0)

        # Warp the second image using the estimated homography to align it with the first image.
        aligned_image = cv2.warpPerspective(reference, homography_matrix, (query.shape[1] + reference.shape[1], query.shape[0]))

        # Add the query image over the aligned image.
        aligned_image[:query.shape[0], :query.shape[1]] = query[:, :]

        return aligned_image


def tryPanoramaImages(img_name):
    """
    Tries to create a panorama out the images with the path: query_<img_name>.jpg and reference_<img_name>.jpg

    :param img_name: Name of the image to panoramize.
    """
    query, reference = cv2.imread("query_" + img_name + ".jpg"), cv2.imread("reference_" + img_name + ".jpg")
    gray_query = cv2.cvtColor(query, cv2.COLOR_BGR2GRAY)
    gray_ref = cv2.cvtColor(reference, cv2.COLOR_BGR2GRAY)

    # Detect keypoints and descriptors for both images, then bruteforce them using first sift, then orb
    sift_stitched = bruteForceKPDS(query, reference, *featureDetect(gray_ref, SIFT), *featureDetect(gray_query, SIFT))
    orb_stitched = bruteForceKPDS(query, reference, *featureDetect(gray_ref, ORB), *featureDetect(gray_query, ORB))

    fig = plt.figure(figsize=(10, 9))
    fig.add_subplot(2, 1, 1)

    if sift_stitched is not None:
        plt.imshow(cv2.cvtColor(sift_stitched, cv2.COLOR_BGR2RGB))
        plt.title("Sift stitch")
        cv2.imwrite(f"task_1_sift_{img_name}.jpg", sift_stitched)

    fig.add_subplot(2, 1, 2)
    if orb_stitched is not None:
        plt.imshow(cv2.cvtColor(orb_stitched, cv2.COLOR_BGR2RGB))
        plt.title("Orb stitch")
        cv2.imwrite(f"task_1_orb_{img_name}.jpg", orb_stitched)

    plt.show()


def main():
    """Main method which will run our helpers"""
    tryPanoramaImages("gym")
    tryPanoramaImages("parking")


if __name__ == '__main__':
    main()
