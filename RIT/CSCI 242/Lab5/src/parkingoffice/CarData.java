package parkingoffice;

/**
 * A class that represent a car's data. All cars will have a license plate, allowed parking lot, and a ticket count.
 *
 * @author Ethan Hartman
 */
public class CarData implements Comparable<CarData> {
    protected final String plate;   // The license plate of the car
    protected final int allowedLot; // The parking lot this car may park in
    protected int tickets;          // The number of tickets this car has

    /**
     * Create a new car.
     * @param plate license plate for the car
     * @param allowedLot allowed parking lot the car may park in
     */
    public CarData(String plate, int allowedLot){
        this.plate = plate;
        this.allowedLot = allowedLot;
        tickets = 0;
    }

    /**
     * Check if the car is parked in the correct lot
     * @param lotNumber lot number to check
     * @return True if given lot is equal to the car's allowedLot
     */
    public boolean isOk(int lotNumber){
        return allowedLot == lotNumber;
    }

    /**
     * Give this car a ticket
     */
    public void giveTicket(){
        tickets++;
    }

    /**
     * Pay off this car's tickets
     */
    public void payTickets(){
        tickets = 0;
    }

    /**
     * Hash function for this car.
     * @return a hash code for the car based on its license plate and allowed lot
     */
    @Override
    public int hashCode(){
        return plate.hashCode() + allowedLot;
    }

    /**
     * Compare this car to another based on tickets
     * @param other the object to be compared
     * @return -1: This car is smaller than the other | 1: This car is larger than the other | 0: The two have equal tickets
     */
    @Override
    public int compareTo(CarData other){
        return Integer.compare(tickets, other.tickets);
    }

    /**
     * Check if this car is equal to another object
     * @param other compare object.
     * @return True if the two have the same data or are the same Object.
     */
    @Override
    public boolean equals(Object other){
        if (other instanceof CarData otherCar)
            return otherCar == this ||
                    plate.equals(otherCar.plate) && allowedLot == otherCar.allowedLot && tickets == otherCar.tickets;
        return false;
    }

    /**
     * @return this car's data in the form "{plate} + (lot {allowedLot}) : {tickets} ticket(s)"
     */
    @Override
    public String toString(){
        return plate +
                " (lot " + allowedLot + ") : " +
                tickets + " ticket(s)";
    }
}