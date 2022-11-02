package parkingoffice;

import java.util.Comparator;

/**
 * Comparator which compares two cars.
 * Compares cars originally by tickets (descending)
 * Then by plate if tickets are equal (ascending)
 *
 * @author Ethan Hartman
 */
public class CarDataComparator implements Comparator<CarData> {
    /**
     * Compare two cars
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return 1: o1 is bigger than o2 | -1: o1 is smaller than o2 | 0: o1 and o2 are equally weighed
     */
    @Override
    public int compare(CarData o1, CarData o2) {
        int result = o2.compareTo(o1);
        if (result == 0)
            result = o1.plate.compareTo(o2.plate);
        return result;
    }
}
