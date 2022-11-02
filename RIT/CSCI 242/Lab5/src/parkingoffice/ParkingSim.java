package parkingoffice;

import java.io.FileNotFoundException;

/**
 * Main parking simulation class.
 * Creates a parking office given the number of lots, car file name then processes the days based on the
 * events file name.
 *
 * @author Ethan Hartman
 */
public class ParkingSim {
    /**
     * Main testing method
     * @param args given arguments (expected 3)
     * @throws FileNotFoundException either the car file or events file wasn't found
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 3){
            int numLots = Integer.parseInt(args[0]);
            String carFileName = args[1], eventsFileName = args[2];
            ParkingOffice parkingOffice = new ParkingOffice(numLots, carFileName);
            parkingOffice.processDays(eventsFileName);
        }else System.out.println("Usage: java ParkingOffice <num-lots> <car-filename> <days-filename>");
    }
}