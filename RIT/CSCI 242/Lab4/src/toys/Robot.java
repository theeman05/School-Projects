package toys;

/**
 * Robot class which can be played with and actually moves/flies. Has distance traveled, and if it can fly.
 *
 * @author Ethan Hartman(ehh4525)
 */
public class Robot extends BatteryPowered{
    public final static int FLY_SPEED = 25;     // The fly speed of Robots
    public final static int RUN_SPEED = 10;     // The run speed of Robots
    public final static int INITIAL_SPEED = 0;  // The initial speed of Robots
    private static int ProductCode = 500;       // The product code for the class
    private final boolean flying;               // If this robot flies
    private int distanceTraveled;               // How far this robot has travelled

    /**
     * Creates a Robot given a name, number of batteries, and if it can fly.
     * Increases product code by 1.
     * @param name the name of this Robot
     * @param numBatteries the number of batteries this Robot uses
     * @param flying if this Robot can fly
     */
    protected Robot(String name, int numBatteries, boolean flying){
        super(ProductCode++, name, numBatteries);
        this.flying = flying;
        distanceTraveled = INITIAL_SPEED;
    }

    /**
     * @return if the robot can fly
     */
    public boolean isFlying(){return flying;}

    /**
     * @return the distance travelled
     */
    public int getDistance(){return distanceTraveled;}

    /**
     * Robots either fly or run around and display how far they travelled.
     * The Robot's distance and wear are increased depending on if it can fly or not
     * Batteries are used based on the minutes played.
     * @param minutes how long the Robot is active for.
     */
    @Override
    protected void specialPlay(int minutes){
        if (flying){
            distanceTraveled += minutes * FLY_SPEED;
            System.out.println("\t"+getName()+" is flying around with total distance: "+distanceTraveled);
            increaseWear(FLY_SPEED);
        }else{
            distanceTraveled += minutes * RUN_SPEED;
            System.out.println("\t"+getName()+" is running around with total distance: "+distanceTraveled);
            increaseWear(RUN_SPEED);
        }
        useBatteries(minutes);
    }

    /**
     * @return the state for this object. F: If the Robot can Fly, D: Total Distance Travelled
     */
    @Override
    public String toString() {
        return super.toString() + ", Robot{F:" + flying + ", D:"+distanceTraveled+"}";
    }
}
