import numpy as np
import cv2


def subtract_frame_by_frame(video_path):
    """
    Subtracts the video frame by frame and displays it.
    :param video_path: Path to the video.
    """
    video = cv2.VideoCapture(video_path)
    _, prev_frame = video.read()
    while True:
        ret, frame = video.read()
        if not ret:
            break
        # Take the difference from the prev and current frame
        diff = cv2.absdiff(prev_frame, frame)

        cv2.imshow('Frame by Frame Subtraction', diff)
        prev_frame = frame.copy()
        cv2.waitKey(1)
    video.release()
    cv2.destroyAllWindows()


def subtract_first_k_frames(video_path, k=50):
    """
    Uses the first k frames as background, then displays the video
    :param video_path: Path to the video.
    :param k: First k frames to use as background.
    """
    video = cv2.VideoCapture(video_path)

    # Read the first 50 frames and set as background
    frames = [video.read()[1] for _ in range(k)]
    background = np.median(frames, axis=0).astype(dtype=np.uint8)
    while True:
        ret, frame = video.read()
        if not ret:
            break
        # Take the difference from the background and current frame
        diff = cv2.absdiff(background, frame)

        cv2.imshow('First K Frames Subtraction', diff)
        cv2.waitKey(1)
    video.release()
    cv2.destroyAllWindows()


def subtract_last_k_frames(video_path, k=50):
    """
    Uses the last k frames as background, then displays the video
    :param video_path: Path to the video.
    :param k: Last k frames to use as background.
    """
    video = cv2.VideoCapture(video_path)
    frames = []
    while True:
        ret, frame = video.read()
        if not ret:
            break
        # Store each frame and pop if frames > k
        frames.append(frame)
        if len(frames) > k:
            frames.pop(0)

        # Update background
        background = np.median(frames, axis=0).astype(dtype=np.uint8)

        # Take the difference from the background and current frame
        diff = cv2.absdiff(background, frame)
        cv2.imshow('Last K Frames Subtraction', diff)
        cv2.waitKey(1)
    video.release()
    cv2.destroyAllWindows()


def subtract_gmm(video_path, kernel):
    """
    Uses gaussian mixture models to find movement in the video and displays the result.
    :param video_path: Path to the video.
    :param kernel: Kernel to apply to reduce noise
    """
    video = cv2.VideoCapture(video_path)
    subtractor = cv2.createBackgroundSubtractorMOG2()
    while True:
        ret, frame = video.read()
        if not ret:
            break
        foreground_mask = subtractor.apply(frame)
        foreground_mask = cv2.morphologyEx(foreground_mask, cv2.MORPH_OPEN, kernel)
        cv2.imshow('GMM Subtraction', foreground_mask)
        cv2.waitKey(1)
    video.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    try:
        subtract_gmm("pedestrians.mp4", cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (4, 4)))
        subtract_gmm("boat.mp4", cv2.getStructuringElement(cv2.MORPH_RECT, (15, 15)))
        subtract_gmm("vehicles.mp4", cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (7, 7)))
    except KeyboardInterrupt:
        print("Exit")
