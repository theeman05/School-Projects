package puzzles.tilt.ptui;
import puzzles.common.Observer;
import puzzles.tilt.model.TiltModel;
import java.io.IOException;
import java.util.Scanner;

/**
 * This is a console text based implementation for the game called tilt.
 * The rules for tilt:
 * Tilt is a sliding logic maze puzzle.
 * The board gets populated with gray blockers that do not move and green and blue sliders.
 * You can only move the sliders by tilting the board up, down, left or right.
 * When you do that, all the sliders will move all the way across the board in that direction until they reach the end of the board or a blocker.
 * Moving them just half way is not allowed. The goal is to get all the green sliders to fall through the hole while
 * all the blue ones remain on the board. This video gives an explanation of how the puzzle is played.
 *
 * @author Yaroslav Khalitov
 */
public class TiltPTUI implements Observer<TiltModel, String> {
    private final TiltModel model;
    private final Scanner in;
    private String currentFile;
    private boolean running;



    /**
     * The constructor for Text UI for tilt.
     *
     * @param filename the file for the game.
     * @exception IOException if the game cannot be loaded.
     */
    public TiltPTUI (String filename) throws IOException {
        currentFile = filename;
        model = new TiltModel(currentFile);
        model.addObserver(this);
        in = new Scanner(System.in);
        running = true;
    }

    /**
     * Every time something happens print out the message.
     *
     * @param model model for the PTUI.
     * @param message message returned from model.
     */
    @Override
    public void update(TiltModel model, String message) {
        System.out.println(message);
        if (!message.startsWith("Loaded: ") && !message.startsWith("Could not load: ")) { //it just works okay
            System.out.println(model.toString());
        }
    }

    /**
     * Runs the main game loop until it is stopped. Also prints game starting information.
     */
    public void run(){
        while(running) {
            System.out.println(model.toString());
            System.out.println("h(int)              -- hint next move\n" +
                               "l(oad) filename     -- load new puzzle file\n" +
                               "t(ilt) {N|S|E|W}    -- tilt the board in the given direction\n" +
                               "q(uit)              -- quit the game\n" +
                               "r(eset)             -- reset the current game");
            gameLoop();
        }

    }

    /**
     * The main game loop where user input is entered and used.
     */
    private void gameLoop(){
        while (true){
            System.out.print(">>> ");
            String choice = in.nextLine().strip().toLowerCase();

            if (choice.equals("h") || choice.equals("hint")){
                model.getHint();
            }else if (choice.startsWith("l ") || choice.startsWith("load ")){
                String filename = choice.substring(2);
                try {
                    model.loadFromFile(filename);
                    currentFile = filename;
                    break;
                } catch (IOException e) {
                    System.out.println("**Can't load/find file**");
                }

            }else if (choice.startsWith("t ") || choice.startsWith("tilt ")){
                String direction = choice.substring(2);
                if (direction.equals("n") || direction.equals("e") || direction.equals("s") || direction.equals("w")){
                    model.makeMove(direction);
                }else{
                    System.out.println("**Not a valid direction!**");
                }
            }else if (choice.equals("q") || choice.equals("quit")){
                System.out.println("**Thank you for playing**");
                running = false;
                break;
            }else if (choice.equals("r") || choice.equals("reset")){
                try {
                    model.loadFromFile(currentFile);
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                System.out.println("**Please enter valid input**");

            }


        }
    }

    /**
     * Runs the Text UI for tilt.
     *
     * @param args cmd line args
     * @exception IOException if the game could not be loaded.
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java TiltPTUI filename");
        }else{
            TiltPTUI pui = new TiltPTUI(args[0]);
            pui.run();
        }
    }
}
