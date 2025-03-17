"""
Class: Intro to Computer Vision
Assignment: A2
Author: Ethan Hartman
"""
import math

import cv2
import numpy as np
import matplotlib.pyplot as plt


IMAGE_NAME = "a.png"
MAX_INTENSITY_VALUE = 255
NEGATIVE_MITIGATOR = 128
EXAMPLE_KERNEL = np.asarray([[-1, 0], [0, 1]])


def display_img_wait(img_name, img):
    """
    Shows the given image then waits for input to close the image.
    :param img_name: The name to give the display window.
    :param img: The image array.
    """
    cv2.imshow(img_name, img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


def Task1(img):
    """
    Calculates the difference between vertical neighboring pixel values
    :param img: The cv2 image array.
    """
    m, n = img.shape
    diff_image = np.zeros(img.shape, dtype='uint8')  # Default type is 'float64'
    for r in range(1, m):  # From (including) to (not including) M
        for c in range(n):  # Implicit from 0 to (not including) N
            diff_image[r, c] = img[r - 1, c] - img[r, c] + NEGATIVE_MITIGATOR

    display_img_wait("Task 1", diff_image)


# When sampling in one dimension, the image will become thinner in either height or width. If we do both, we can see
# a major pixelation and quality reduction.
def ImageSampling(grayscale_img, sr_x=10, sr_y=10):
    """
    Displays a sampled digital image given a grayscale image, and sr pixel skip intervals.
    :param grayscale_img: The image array.
    :param sr_x: Skip interval for the x-axis. Default: 10
    :param sr_y: Skip interval for the y-axis. Default: 10
    :return: Returns the sampled digital image
    """
    m, n = grayscale_img.shape
    # Divide img shape by the skip intervals and round down
    m //= sr_y
    n //= sr_x
    digital_img = np.zeros((m, n), dtype='uint8')  # Create empty array for our digital image.
    for r in range(m):
        for c in range(n):
            digital_img[r, c] = grayscale_img[r * sr_y, c * sr_x]
    plt.imshow(grayscale_img, cmap='gray', vmin=0, vmax=255)
    plt.title("Continuous Image")
    plt.show()
    plt.title("Digital Image (Sampled " + str(sr_x) + "x" + str(sr_y) + ")")
    plt.imshow(digital_img, cmap='gray', vmin=0, vmax=255)
    plt.show()
    return digital_img


# As quantization level increases, the image becomes "higher quality" and lighter, as well.
def ImageQuantization(img, bit_depth=2):
    """
    Displays an image with the defined level of bit depth.
    :param img: The image array.
    :param bit_depth: The quantization / bit depth
    """
    local_intensity_max = 2 ** bit_depth - 1
    m, n, _ = img.shape
    quantized_img = np.zeros_like(img)  # Create empty array for our quantized image.
    # Images are normally 8 bit, so we divide the current value by 255 and multiply it by 2^level - 1
    for r in range(m):
        for c in range(n):
            # After normalizing, we round each BGR value, then scale it back to the [0, 255] range
            quantized_img[r, c] = np.round(img[r, c] / MAX_INTENSITY_VALUE * local_intensity_max) * MAX_INTENSITY_VALUE / local_intensity_max

    display_img_wait("Quantized", quantized_img)


def SampleAndQuantizeImage(grayscale_img, sr_x=10, sr_y=10, quantization_levels=2):
    """
    Samples then quantiles the image and displays it with matplot.
    :param grayscale_img: The image to perform sampling and quantization on.
    :param sr_x: Skip interval for the x-axis. Default: 10
    :param sr_y: Skip interval for the y-axis. Default: 10
    :param quantization_levels: The quantization / bit depth.
    :return:
    """
    local_intensity_max = 2 ** quantization_levels - 1
    # Sample the image and display the first two images
    sampled_img = ImageSampling(grayscale_img, sr_x, sr_y)
    for r in range(sampled_img.shape[0]):
        for c in range(sampled_img.shape[1]):
            # After normalizing, we round each BGR value, then scale it back to the [0, 255] range
            sampled_img[r, c] = np.round(sampled_img[r, c] / MAX_INTENSITY_VALUE * local_intensity_max) * MAX_INTENSITY_VALUE / local_intensity_max

    plt.title("Quantized Image (" + str(quantization_levels) + " Levels)")
    plt.imshow(sampled_img, cmap='gray', vmin=0, vmax=255)
    plt.show()


# The image has been made a lot lighter. The brightness increase seems to make the image look lower quality since
# there isn't as much color variety. We can see this most in his face.
def EqualizeHistogram(grayscale_img):
    """
    Equalizes an image based on its histogram cdf.
    :param grayscale_img: The image to equalize.
    """
    hist, bins = np.histogram(grayscale_img.flatten(), bins=MAX_INTENSITY_VALUE + 1, density=True)
    # Calculate the cdf
    cdf = hist.cumsum()
    cdf = MAX_INTENSITY_VALUE * cdf / cdf[-1]  # Normalize it

    equalized_img = np.interp(grayscale_img.flatten(), bins[:-1], cdf)
    cv2.imshow("Original Image", grayscale_img)
    display_img_wait("Equalized Image", equalized_img.reshape(grayscale_img.shape).astype(np.uint8))


def ApplyKernelToImg(img, kernel):
    """
    Applies the kernel to an image.
    :param img: Image to apply kernel to.
    :param kernel: Kernel to apply to image.
    :return: The resultant image.
    """
    m, n = img.shape
    kernel_height, kernel_width = kernel.shape
    result_img = np.zeros_like(img)

    # Apply the kernel
    for i in range(m):
        for j in range(n):
            result_img[i, j] = np.sum(img[i:i + kernel_height, j:j + kernel_width] * kernel)

    return result_img


def Convolution2D(grayscale_image, kernel):
    """
    Convolves an image with a kernel.
    :param grayscale_image: Image to convolve.
    :param kernel: Kernel to apply to the image.
    :return: The convolved image.
    """
    # Flip the kernel horizontally and vertically for convolution
    kernel = np.flipud(np.fliplr(kernel))
    return ApplyKernelToImg(grayscale_image, kernel)


def Correlation2D(grayscale_image, kernel):
    """
    Correlates an image with a kernel.
    :param grayscale_image: Image to correlate.
    :param kernel: Kernel to apply to the image.
    :return: The correlated image.
    """
    return ApplyKernelToImg(grayscale_image, kernel)


def mean_smooth_pixel(img, x, y, width, height):
    """
    Smooths the pixel at location x, y using the mean within the given extents.
    :param img: Image to grab pixel from.
    :param x: x-location of the pixel to smooth.
    :param y: y-location of the pixel to smooth.
    :param width: Width of pixels to include in the x
    :param height: Height of pixels to include in the y
    :return: The mean-smoothed pixel
    """
    extent_x, extent_y = width // 2, height // 2
    # Return the average of the pixels by summing all x's and y's then dividing them by the dimensions
    return np.sum(np.sum(img[y-extent_y: y+extent_y + 1, x-extent_x: x+extent_x + 1], axis=0), axis=0) / width / height


def median_smooth_pixel(img, x, y, width, height):
    """
    Smooths the pixel at location x, y using the median within the given extents.
    :param img: Image to grab pixel from.
    :param x: x-location of the pixel to smooth.
    :param y: y-location of the pixel to smooth.
    :param width: Width of pixels to include in the x
    :param height: Height of pixels to include in the y
    :return: The median-smoothed pixel
    """
    extent_x, extent_y = width // 2, height // 2
    # Return the median of the pixels
    return np.median(np.median(img[y-extent_y: y+extent_y + 1, x-extent_x: x+extent_x + 1], axis=0), axis=0)


def Task7(img, width=9, height=9):
    """
    Applies a median box filter, and a mean box filter to the given image and displays all images.
    :param img: Image to apply filters to
    :param width: Width of the filter
    :param height: Height of the filter
    """
    mean_smoothed_image = np.zeros_like(img)
    median_smoothed_image = np.zeros_like(img)
    m, n, _ = img.shape
    extent_x, extent_y = width // 2, height // 2
    for r in range(m):
        for c in range(n):
            # Boundary check
            if c < extent_x or c >= n - extent_x or r < extent_y or r >= m - extent_y:
                mean_smoothed_image[r, c] = img[r, c]
                median_smoothed_image[r, c] = img[r, c]
            else:
                mean_smoothed_image[r, c] = mean_smooth_pixel(img, c, r, width, height)
                median_smoothed_image[r, c] = median_smooth_pixel(img, c, r, width, height)

    cv2.imshow("Normal", img)
    cv2.imshow("Mean Filtered", mean_smoothed_image)
    display_img_wait("Median Filtered", median_smoothed_image)


def get_gaussian_value_at(x, y, sigma_value):
    """
    Returns the gaussian value at the specified coordinates
    :param x: X coordinate.
    :param y: Y coordinate
    :param sigma_value: The deviation of the filter.
    :return: The associated gaussian value.
    """
    return 1/(2*math.pi*(sigma_value**2)) * (math.e ** (-(x ** 2 + y ** 2)/(2*(sigma_value ** 2))))


def gaussian_kernel(kernel_size, sigma_value):
    """
    Returns a gaussian kernel with the given size and sigma value.
    :param kernel_size: Size of the kernel
    :param sigma_value: Deviation of the gaussian filter.
    :return: A gaussian kernel with the given size and sigma value.
    """
    kernel = np.zeros((kernel_size, kernel_size))
    # Build the kernel
    for r in range(kernel_size):
        for c in range(kernel_size):
            kernel[r, c] = get_gaussian_value_at(c - kernel_size//2, r - kernel_size//2, sigma_value)

    # Normalize and return the kernel
    return kernel / np.sum(kernel)


def Task8(img, kernel_size, sigma_value):
    """
    Designs a gaussian filter for a given kernel and sigma value, then applies the kernel to the image.
    :param img: The image to apply filter to.
    :param kernel_size: Size of the kernel.
    :param sigma_value: Deviation of the gaussian filter,
    """
    m, n, _ = img.shape
    filtered_image = np.zeros_like(img)
    extent = kernel_size // 2
    kernel = gaussian_kernel(kernel_size, sigma_value)
    for r in range(m):
        for c in range(n):
            # Boundary check
            if c < extent or c >= n - extent or r < extent or r >= m - extent:
                filtered_image[r, c] = img[r, c]
            else:
                # Sum up the elements using the kernel then apply them
                summed_elements = [0.0, 0.0, 0.0]
                for x in range(-extent, extent+1):
                    for y in range(-extent, extent+1):
                        summed_elements += img[y + r, x + c] * kernel[y, x]
                filtered_image[r, c] = summed_elements
    cv2.imshow("Original", img)
    display_img_wait("Gaussian Filtered", filtered_image.astype(np.uint8))


def Task9(img, width=3, height=3):
    """
    Median smooths the given image with the given smoothing width and height then displays the four different
    methods with different edges.
    :param img: Image to work on.
    :param width: Width of the filter.
    :param height: Height of the filter.
    :return:
    """
    images = {
        "Clipped": np.zeros_like(img),
        "Wrapped": np.zeros_like(img),
        "CopiedEdge": np.zeros_like(img),
        "ReflectedEdge": np.zeros_like(img),
    }
    m, n, _ = img.shape
    extent_x, extent_y = width // 2, height // 2
    for r in range(m):
        for c in range(n):
            if c < extent_x or c >= n - extent_x or r < extent_y or r >= m - extent_y:
                images["CopiedEdge"][r, c] = img[r, c]  # Copy edges from original
            else:
                smoothed_pixel = median_smooth_pixel(img, c, r, width, height)
                for img_key in images:
                    images[img_key][r, c] = smoothed_pixel

    # Wrap images
    images["Wrapped"][:, :extent_x] = img[:, -extent_x:]
    images["Wrapped"][:, -extent_x:] = img[:, :extent_x]
    images["Wrapped"][:extent_y, :] = img[-extent_y:, :]
    images["Wrapped"][-extent_y:, :] = img[:extent_y, :]

    # Copy opposite corners
    images["Wrapped"][:extent_y, :extent_x] = img[-extent_y:, -extent_x:]   # Top Left
    images["Wrapped"][-extent_y:, -extent_x:] = img[:extent_y, :extent_x]   # Bottom Right
    images["Wrapped"][:extent_y, -extent_x:] = img[-extent_y:, :extent_x]   # Top Right
    images["Wrapped"][-extent_y:, :extent_x] = img[:extent_y, -extent_x:]   # Bottom Left

    # Reflect the top and bottom
    images["ReflectedEdge"][:extent_y, :] = np.flipud(img[extent_y:2 * extent_y, :])
    images["ReflectedEdge"][-extent_y:, :] = np.flipud(img[-2 * extent_y:-extent_y, :])

    # Reflect the left and right
    images["ReflectedEdge"][:, :extent_x, :] = np.fliplr(img[:, extent_x:2 * extent_x, :])
    images["ReflectedEdge"][:, -extent_x:] = np.fliplr(img[:, -2 * extent_x:-extent_x])

    # Show all images
    for img_key in images:
        cv2.imshow(img_key, images[img_key])

    cv2.waitKey(0)
    cv2.destroyAllWindows()


def main():
    """Main method which will run our helpers"""
    grayscale_img = cv2.imread(IMAGE_NAME, cv2.IMREAD_GRAYSCALE)
    normal_img = cv2.imread(IMAGE_NAME)
    Task1(grayscale_img)
    ImageSampling(grayscale_img)
    ImageQuantization(normal_img, 1)
    SampleAndQuantizeImage(grayscale_img, 6, 6, 6)
    EqualizeHistogram(grayscale_img)
    cv2.imshow("Convolved", Convolution2D(grayscale_img, EXAMPLE_KERNEL))
    display_img_wait("Correlated", Correlation2D(grayscale_img, EXAMPLE_KERNEL))
    Task7(normal_img, 9, 9)
    Task8(normal_img, 9, 5)
    Task9(normal_img)


if __name__ == '__main__':
    main()
