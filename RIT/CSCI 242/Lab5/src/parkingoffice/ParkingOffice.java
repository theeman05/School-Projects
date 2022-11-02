package parkingoffice;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * A class that represent a parking office. Every office will store their registered cars, a map of it's parking
 * lots, a tree of sorted cars who received tickets.
 * Can process days given an event file.
 *
 * @author Ethan Hartman
 */
public class ParkingOffice {
    private final HashMap<String, CarData> CAR_MAP;     // Map of registered cars
    private final HashMap<Integer, LotData> LOT_MAP;    // Map of lots controlled by this office
    private final TreeSet<CarData> TOP_TICKET_CAR_TREE; // Tree of cars sorted based on CarDataComparator

    /**
     * Create a new ParkingOffice, create the given amount of lots, add cars to the car map.
     * @param totalLots Number of lots to create
     * @param registryFileName Name of car registry file.
     * @throws FileNotFoundException Car registry file not found
     */
    public ParkingOffice(int totalLots, String registryFileName) throws FileNotFoundException{
        Scanner in = new Scanner(new File(registryFileName));
        String[] lineDat;

        CAR_MAP = new HashMap<>();
        LOT_MAP = new HashMap<>();
        TOP_TICKET_CAR_TREE = new TreeSet<>(new CarDataComparator());

        for (int i = 0; i < totalLots; i++)
            LOT_MAP.put(i, new LotData(i));

        while (in.hasNextLine()){
            lineDat = in.nextLine().split(" ");
            CAR_MAP.put(lineDat[0], new CarData(lineDat[0], Integer.parseInt(lineDat[1])));
        }
    }

    /**
     * Begin the day by resetting lots.
     */
    private void beginDay(){
        for (Map.Entry<Integer, LotData> entry : LOT_MAP.entrySet())
            entry.getValue().newDay();  // Reset lots
        System.out.println("------------");
    }

    /**
     * End the day by displaying the worst offenders (up to 10)
     */
    private void endDay(){
        System.out.println("End of day. Worst offenders are:");
        int cars = 0;
        for (CarData car : TOP_TICKET_CAR_TREE) {
            if (cars++ == 10) break;
            System.out.println(car);
        }

        System.out.println("Lot usage was:");
        for (Map.Entry<Integer, LotData> entry : LOT_MAP.entrySet())
            System.out.println(entry.getValue().report());
    }

    /**
     * Process the days inside an events file
     * @param eventsFileName Mame of events file.
     * @throws FileNotFoundException Events file not found.
     */
    public void processDays(String eventsFileName) throws FileNotFoundException{
        Scanner in = new Scanner(new File(eventsFileName));
        int lastLot = -1;
        String line;

        while (in.hasNextLine()){
            line = in.nextLine();
            if (line.equals("BeginDay"))
                beginDay();
            else if (line.equals("EndDay"))
                endDay();
            else if (line.equals("P"))
                lastLot = -1;
            else if(line.matches("\\d*"))
                lastLot = Integer.parseInt(line);
            else if (CAR_MAP.containsKey(line)) {
                    CarData seen = CAR_MAP.get(line);
                    if (lastLot >= 0) {
                        if (LOT_MAP.get(lastLot).sawCar(line) && !seen.isOk(lastLot)) {
                            TOP_TICKET_CAR_TREE.remove(seen); // Remove so tree can be updated
                            seen.giveTicket();
                            TOP_TICKET_CAR_TREE.add(seen); // Add new car into tree
                        }
                    }else {
                        seen.payTickets();
                        TOP_TICKET_CAR_TREE.remove(seen); // Remove because the car paid their tickets.
                    }
            }else System.out.println(line + " not found in cars");
        }
    }
}