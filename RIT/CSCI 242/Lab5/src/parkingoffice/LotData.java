package parkingoffice;

import java.util.HashSet;

/**
 * A class that represent a parking lot. Every lot has a unique number and a collection
 * of the cars parked in it.
 *
 * @author Ethan Hartman
 */
public class LotData {
    private final HashSet<String> SEEN_LICENCES;    // Licenses we've seen
    private final int LOT_NUMBER;                   // This Lot's number

    /**
     * Constructor takes in the number of the lot.
     * @param n Lot number
     */
    public LotData(int n) {
        SEEN_LICENCES = new HashSet<>();
        LOT_NUMBER = n;
    }

    /**
     * Returns a string reporting the number of unique cars seen in this lot.
     * The report string is of the form:
     *
     * "Lot {id} was used by {n} car(s) today.", where id is the lot's unique number and n is the number of cars.
     * @return the usage report for the day.
     */
    public String report() {
        return "Lot " + LOT_NUMBER + " was used by " + SEEN_LICENCES.size() + " car(s) today.";
    }

    /**
     * Takes in a license plate, records the information, and returns whether the car
     * is newly-seen in this lot this day.
     * @param plate A license plate
     * @return True if the car had not previously been seen.
     */
    public boolean sawCar(String plate) {
        return SEEN_LICENCES.add(plate);
    }

    /**
     * Resets any data structures for a new day of ticketing.
     */
    public void newDay() {
        SEEN_LICENCES.clear();
    }
}