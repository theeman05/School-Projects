package toys;

/**
 * Custom LaserBlaster class which fires. Can be configured to shoot multiple lasers.
 *
 * @author Ethan Hartman(ehh4525)
 */
public class LaserBlaster extends BatteryPowered{
    private final int repetitions;          // How many lasers this object fires
    private static int ProductCode = 600;   // The current product code of this class
    private int shotsFired;                 // How many lasers this object has fired

    /**
     * Creates a LaserBlaster given a name, number of batteries, and fire repetitions.
     * Increases product code by 1.
     * @param name the name of this LaserBlaster
     * @param numBatteries the number of batteries this LaserBlaster uses
     * @param fireRepetitions how many lasers the object will fire
     */
    protected LaserBlaster(String name, int numBatteries, int fireRepetitions){
        super(ProductCode++, name, numBatteries);
        shotsFired = 0;
        repetitions = fireRepetitions;
    }

    /**
     * @return the total shots this object has fired
     */
    public int getShotsFired(){return shotsFired;}

    /**
     * @return the number of lasers which will be fired
     */
    public int getRepetitions(){return repetitions;}

    /**
     * LaserBlasters fire however many repetitions are given and print their total shots fired.
     * The LaserBlaster uses the given number of minutes for its battery.
     * The LaserBlaster's wear is increased by how many lasers it fires
     * @param minutes how long the LaserBlaster is active for.
     */
    @Override
    protected void specialPlay(int minutes) {
        shotsFired += repetitions;
        System.out.println("\t" + getName() + " fires - "+ "PEW! ".repeat(repetitions) + "and has shot a total of "
                + shotsFired + " times!");
        useBatteries(minutes);
        increaseWear(repetitions);
    }

    /**
     * @return the state for this object. SF: Shots Fired, RS: Laser Repetitions
     */
    @Override
    public String toString(){
        return super.toString() + ", LaserBlaster{SF:" + shotsFired + ", RS:" + repetitions + "}";
    }
}
