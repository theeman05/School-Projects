package toys;

/**
 * Abstract Toy class which will eventually be played with. Has a product code, name, happiness level, and wear.
 *
 * @author Ethan Hartman(ehh4525)
 */
public abstract class Toy implements IToy {
    private final static int MAX_HAPPINESS = 100;   // Max Happiness a Toy can have
    private final static int INITIAL_HAPPINESS = 0; // Initial Happiness of a Toy
    private final static double INITIAL_WEAR = 0;   // Initial Wear of a Toy
    private final int code;                         // Toy's given product code
    private final String name;                      // Toy's name
    private int happiness;                          // How happy the toy is
    private double wear;                            // How worn down the toy is

    /**
     * Creates a Toy given a product code, and a name.
     * Happiness and wear are set to their default values
     * @param code the product code of this Toy
     * @param name the name of this Toy
     */
    protected Toy(int code, String name){
        this.code = code;
        this.name = name;
        happiness = INITIAL_HAPPINESS;
        wear = INITIAL_WEAR;
    }

    /**
     * All toys can be played with for a certain amount of time.
     * Displays the toy is playing for however long, and it's state.
     * Calls the toy's specialPlay method.
     * Increases its happiness by the minutes.
     * Will display if the toy has retired.
     * @param minutes how long the Toy is active for.
     */
    public void play(int minutes){
        System.out.println("PLAYING("+minutes+"): "+ this);
        this.specialPlay(minutes);
        happiness += minutes;
        if (isRetired())
            System.out.println("RETIRED: "+ this);
    }

    /**
     * Increases the wear of the toy by the given amount
     * @param amount amount given
     */
    public void increaseWear(double amount){wear += amount;}

    /**
     * Toy special play method. This is expected to be implemented from the subclass.
     * @param minutes how long the toy is active for
     */
    protected abstract void specialPlay(int minutes);

    /**
     * @return the Toy's product code
     */
    public int getProductCode(){return code;}

    /**
     * @return the Toy's name
     */
    public String getName(){return name;}

    /**
     * @return the Toy's current happiness
     */
    public int getHappiness(){return happiness;}

    /**
     * @return the Toy's current wear
     */
    public double getWear(){return wear;}

    /**
     * @return if the toy has retired yet
     */
    public boolean isRetired(){return happiness >= MAX_HAPPINESS;}

    /**
     * @return the state for this Toy. PC: Product Code, N: Name, H: Happiness Level, R: If toy Retired, W: Wear level
     */
    @Override
    public String toString(){
        return "Toy{PC:" + code + ", N:" + name + ", H:" + happiness + ", R:" + isRetired() + ", W:" + wear + "}";
    }
}
