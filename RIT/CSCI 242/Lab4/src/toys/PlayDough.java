package toys;

/**
 * PlayDough class which can be played with. Has a color.
 *
 * @author Ethan Hartman(ehh4525)
 */
public class PlayDough extends Toy{
    public static final double WEAR_MULTIPLIER = .05;   // How much each PlayDough is worn each minute of use
    private static int ProductCode = 100;               // The product code of this class
    private final Color color;                          // The color of this PlayDough

    /**
     * Creates a PlayDough given a name, and color.
     * Increases product code by 1.
     * @param name the name of this PlayDough
     * @param color the color of this PlayDough
     */
    protected PlayDough(String name, Color color) {
        super(ProductCode++, name);
        this.color = color;
    }

    /**
     * @return the color of this object
     */
    public Color getColor(){return color;}

    /**
     * PlayDough displays a phrase including its name and color when played with.
     * Its wear is increased by the wear multiplier multiplied by the minutes played with.
     * @param minutes how long the PlayDough is played with
     */
    @Override
    protected void specialPlay(int minutes) {
        System.out.println("\tArts and crafting with "+color+" "+getName());
        increaseWear(WEAR_MULTIPLIER * minutes);
    }

    /**
     * @return the state for this object. C: Color
     */
    @Override
    public String toString(){
        return super.toString() + ", PlayDough{C:" + color + "}";
    }
}
