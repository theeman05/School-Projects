import cv2
import numpy as np

CLOSEST_THRESHOLD = 60
LINE_THICKNESS = 2
MIN_AREA_THRESHOLD = 300


class Vehicle:
    """
    Class for a vehicle / moving object
    """
    def __init__(self, vehicle_id, position):
        self.vehicle_id = vehicle_id
        self.position = position
        self.start_pos = position
        color = np.random.randint(0, 256, size=3)
        self.line_color = (int(color[0]), int(color[1]), int(color[2]))


class VehicleTracker:
    """
    Class for a vehicle / moving object tracker, which will keep track of moving objects.
    """
    def __init__(self, closest_threshold):
        self.vehicles = {}
        self.next_vehicle_id = 0
        self.closest_threshold = closest_threshold

    def update(self, position):
        """
        Updates the tracker by adding or updating a vehicle, based on the scene
        :param position: Position to track
        """
        closest_vehicle_id = self.get_closest_vehicle(position)
        if closest_vehicle_id is not None:
            self.vehicles[closest_vehicle_id].position = position
        else:
            self.vehicles[self.next_vehicle_id] = Vehicle(self.next_vehicle_id, position)
            self.next_vehicle_id += 1

    def get_closest_vehicle(self, position):
        """
        Gets the closest vehicle id to the given position if it is within the threshold, or returns none.
        :param position: Position to check with.
        :return: The closest vehicle id to the given position if it is within the threshold, or returns none.
        """
        closest_vehicle_id = None
        closest_distance = np.inf
        for vehicle_id, vehicle in self.vehicles.items():
            distance = np.linalg.norm(vehicle.position - position)
            if distance < closest_distance:
                closest_vehicle_id = vehicle_id
                closest_distance = distance

        return closest_vehicle_id if closest_distance < self.closest_threshold else None


def track_objects(video_path, kernel, closest_threshold):
    """
    Uses gaussian mixture models to find movement in the video and displays the result.
    :param video_path: Path to the video.
    :param kernel: Kernel to apply to reduce noise
    :param closest_threshold: Threshold to consider objects the same below
    """
    video = cv2.VideoCapture(video_path)
    subtractor = cv2.createBackgroundSubtractorMOG2(detectShadows=False, varThreshold=40)
    vehicle_tracker = VehicleTracker(closest_threshold)
    while True:
        ret, frame = video.read()
        if not ret:
            break

        # Apply mask and kernel
        foreground_mask = subtractor.apply(frame)
        foreground_mask = cv2.morphologyEx(foreground_mask, cv2.MORPH_OPEN, kernel)

        # Find connected components
        num_labels, labels, stats, centroids = cv2.connectedComponentsWithStats(foreground_mask)

        # Identify labels where the area is above the threshold
        valid_labels = np.where(stats[1:, cv2.CC_STAT_AREA] >= MIN_AREA_THRESHOLD)[0] + 1

        # Create a mask for each cluster and draw bounding boxes
        clustered_mask = np.zeros_like(foreground_mask)

        # Update vehicle tracker by iterating through labels
        for label in valid_labels:
            mask = np.zeros_like(foreground_mask)
            mask[labels == label] = 255
            clustered_mask = cv2.bitwise_or(clustered_mask, mask)

            # Calculate bounding box coordinates
            x, y, w, h = stats[label, cv2.CC_STAT_LEFT], stats[label, cv2.CC_STAT_TOP], stats[label, cv2.CC_STAT_WIDTH], \
                stats[label, cv2.CC_STAT_HEIGHT]

            vehicle_tracker.update(np.array((x+w//2, y+h//2)))

        # Draw lines on frame
        for vehicle in vehicle_tracker.vehicles.values():
            cv2.line(frame, vehicle.start_pos, vehicle.position, vehicle.line_color, LINE_THICKNESS)

        cv2.imshow('GMM Subtraction', frame)
        cv2.waitKey(1)
    video.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    try:
        track_objects("vehicles.mp4", cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (2, 2)), 60)
        track_objects("pedestrians.mp4", cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3)), 60)
        track_objects("boat.mp4", cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (10, 10)), 150)
    except KeyboardInterrupt:
        print("Exit")
