package toys;

/**
 * ActionFigure class which has energy, and can be played with.
 *
 * @author Ethan Hartman(ehh4525)
 */
public class ActionFigure extends Doll{
    public final static int MIN_ENERGY_LEVEL = 1;           // Minimum energy level
    public final static Color HAIR_COLOR = Color.ORANGE;    // Default hair color
    private static int ProductCode = 300;                   // Product code for Action Figures
    private int energyLevel;                                // The current energy level

    /**
     * Creates an ActionFigure given a name, age and a phrase which that action figure says.
     * Increases product code by 1
     * @param name the name of the ActionFigure
     * @param age the age of the ActionFigure
     * @param speak the phrase which the ActionFigure says
     */
    public ActionFigure(String name, int age, String speak) {
        super(ProductCode++, name, HAIR_COLOR, age, speak);
        energyLevel = MIN_ENERGY_LEVEL;
    }

    /**
     * @return the energy level of this action figure.
     */
    public int getEnergyLevel(){return energyLevel;}

    /**
     * ActionFigures kung foo chop when they special play. Displays that this action figure chopped,
     * increases its energy level, and calls Doll's special play as well.
     * @param minutes how long the ActionFigure chops for
     */
    @Override
    public void specialPlay(int minutes) {
        System.out.println("\t"+getName()+" kung foo chops with "+energyLevel*minutes+" energy!");
        energyLevel++;
        super.specialPlay(minutes);
    }

    /**
     * @return the state for this object. E: Energy Level
     */
    @Override
    public String toString(){
        return super.toString() + ", ActionFigure{E:"+energyLevel+"}";
    }
}
