package toys;

/**
 * Doll class which has colored hair, a catchphrase and an age. Can be played with.
 *
 * @author Ethan Hartman(ehh4525)
 */
public class Doll extends Toy {
    private final Color hairColor;          // Hair color of this doll
    private final String catchphrase;       // What the doll says
    private final int age;                  // How old the doll is
    private static int ProductCode = 200;   // Current product code of class

    /**
     * Creates a Doll given a name, hair color, age, and a phrase.
     * Increases product code by 1.
     * @param name the name of this Doll
     * @param hairColor the hair color of this Doll
     * @param age how old the Doll is
     * @param speak what the Doll will say
     */
    protected Doll(String name, Color hairColor, int age, String speak) {
        super(ProductCode++, name);
        this.hairColor = hairColor;
        this.age = age;
        this.catchphrase = speak;
    }

    /**
     * Creates a Doll given a product code, name, hair color, age, and a phrase.
     * @param productCode the product code of this doll
     * @param name the name of this Doll
     * @param hairColor the hair color of this Doll
     * @param age how old the Doll is
     * @param speak what the Doll will say
     */
    protected Doll(int productCode, String name, Color hairColor, int age, String speak){
        super(productCode, name);
        this.hairColor = hairColor;
        this.age = age;
        this.catchphrase = speak;
    }

    /**
     * @return the hair color of this object
     */
    public Color getHairColor(){return hairColor;}

    /**
     * @return the age of this object
     */
    public int getAge() {return age;}

    /**
     * @return the catchphrase of this object
     */
    public String getSpeak(){return catchphrase;}

    /**
     * Doll's brush their hair and speak when they special play. Displays that this Doll brushed its hair and says
     * it's catchphrase.
     * The Doll's wear is increased by its age.
     * @param minutes how long the Doll brushes their hair for
     */
    @Override
    protected void specialPlay(int minutes) {
        System.out.println("\t"+getName()+" brushes their "+hairColor+" hair and says, \""+catchphrase+"\"");
        increaseWear(age);
    }

    /**
     * @return the state for this object. HC: Hair Color, A: Age, S: Catchphrase
     */
    @Override
    public String toString() {
        return super.toString() + ", Doll{HC:"+hairColor+", A:"+age+", S:"+catchphrase+"}";
    }
}
