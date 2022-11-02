package toys;

/**
 * RCCar class which can be played with and zooms around. Has its speed.
 *
 * @author Ethan Hartman(ehh4525)
 */
public class RCCar extends BatteryPowered{
    public final static int STARTING_SPEED = 10;    // Starting speed for RCCars
    public final static int SPEED_INCREASE = 5;     // Speed increment for RCCars when played with
    private static int ProductCode = 400;           // Product code for this class
    private int speed;                              // The speed of this RCCar

    /**
     * Creates a RCCar given a name, and number of batteries.
     * Increases product code by 1.
     * @param name the name of this RCCar
     * @param numBatteries the number of batteries this RCCar uses
     */
    protected RCCar(String name, int numBatteries){
        super(ProductCode++, name, numBatteries);
        speed = STARTING_SPEED;
    }

    /**
     * @return the current speed of this object
     */
    public int getSpeed(){return speed;}

    /**
     * RCCars display that they race around at their current speed.
     * Uses batteries for the amount of minutes used.
     * Its wear is increased by the speed of this car.
     * Its speed is increased by SPEED_INCREASE.
     * @param minutes how long the RCCars is used for
     */
    @Override
    protected void specialPlay(int minutes) {
        System.out.println("\t"+getName()+" races around at "+speed+"mph!");
        useBatteries(minutes);
        increaseWear(speed);
        speed += SPEED_INCREASE;
    }

    /**
     * @return the state for this object. S: Speed
     */
    @Override
    public String toString(){
        return super.toString() + ", RCCar{S:"+speed+"}";
    }
}
