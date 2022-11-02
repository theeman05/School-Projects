package toys;

/**
 * Abstract BatteryPowered class which has batteries and a battery level. Will eventually be played with.
 *
 * @author Ethan Hartman(ehh4525)
 */
public abstract class BatteryPowered extends Toy{
    public final static int FULLY_CHARGED = 100;    // Default charge level of BatteryPowered
    public final static int DEPLETED = 0;           // Amount which we consider depleted
    private final int numBatteries;                 // Number of batteries this object uses
    private int batteryLevel;                       // Battery level

    /**
     * Creates a BatteryPowered given a product code, name and number of batteries.
     * @param productCode the product code of this object
     * @param name the name of this object
     * @param numBatteries how many batteries this object has
     */
    protected BatteryPowered(int productCode, String name, int numBatteries){
        super(productCode, name);
        this.numBatteries = numBatteries;
        batteryLevel = FULLY_CHARGED;
    }

    /**
     * @return the battery level of this object.
     */
    public int getBatteryLevel(){return batteryLevel;}

    /**
     * @return the number of batteries for this object.
     */
    public int getNumBatteries(){return numBatteries;}

    /**
     * Reduces battery level by the given time, as well as this object's number of batteries required.
     * Displays if the battery is depleted, and when it is recharged again.
     * @param time how long the object is used for
     */
    public void useBatteries(int time){
        batteryLevel -= time + numBatteries;
        if (batteryLevel <= DEPLETED){
            batteryLevel = 0;
            System.out.println("\tDEPLETED:"+ this);
            batteryLevel = FULLY_CHARGED;
            System.out.println("\tRECHARGED:"+ this);
        }
    }

    /**
     * @return the state for this object. BL: Battery Level, NB: Number of Batteries
     */
    @Override
    public String toString(){
        return super.toString() + ", BatteryPowered{BL:" + batteryLevel + ", NB:" + numBatteries + "}";
    }
}
