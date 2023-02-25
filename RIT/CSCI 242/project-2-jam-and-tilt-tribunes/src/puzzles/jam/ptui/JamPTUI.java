package puzzles.jam.ptui;

import puzzles.common.Coordinate;
import puzzles.common.Observer;
import puzzles.jam.model.JamModel;

import java.util.Scanner;

/**
 * Text based UI for Jam.
 *
 * @author Ethan Hartman (theeman05)
 */
public class JamPTUI implements Observer<JamModel, String> {
    /** The commands to display on starting a new game */
    private static final String GAME_COMMANDS =
            """
            Jam Commands:
            h(int)              --hint next move
            l(oad) filename     --load new puzzle file
            s(elect) r c        --select cell at r, c
            q(uit)              --quit the game
            r(eset)             --reset the current game
            """;

    /** The jam model used for storing puzzle data */
    private final JamModel model;

    /** The scanner to request input */
    private final Scanner in;

    public JamPTUI(){
        model = new JamModel();
        model.addObserver(this);
        in = new Scanner(System.in);
    }

    private String[] requestCommandArgs(){
        String command = "";
        System.out.print("Command: ");
        while (command.equals(""))
            command = in.nextLine();
        System.out.println();

        return command.split(" ");
    }

    private void startGameLoop(String originalFileName){
        String lastLoadedFile = originalFileName;
        String[] commandArgs;
        String command;
        boolean selectedCar = false;

        if (!model.loadFromFile(lastLoadedFile))
            return;

        while (true){
            model.printConfig();
            System.out.println(GAME_COMMANDS);
            commandArgs = requestCommandArgs();
            command = commandArgs[0];
            switch (command) {
                case "h", "H":
                    model.announceHint();
                    selectedCar = false;
                    break;
                case "l", "L":
                    if (commandArgs.length == 2)
                        if (model.loadFromFile(commandArgs[1]))
                            lastLoadedFile = commandArgs[1];
                    selectedCar = false;
                    break;
                case "s", "S":
                    if (commandArgs.length == 3) {
                        if (!selectedCar)
                            selectedCar = model.selectCarAt(new Coordinate(commandArgs[1], commandArgs[2]));
                        else {
                            model.moveSelected(new Coordinate(commandArgs[1], commandArgs[2]));
                            selectedCar = false;
                        }
                    }
                    break;
                case "q", "Q":
                    System.out.println("Goodbye.");
                    return;
                case "r", "R":
                    model.loadFromFile(lastLoadedFile);
                    selectedCar = false;
                    break;
            }
        }
    }

    @Override
    public void update(JamModel jamModel, String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("Usage: java JamPTUI filename");
        else
            new JamPTUI().startGameLoop(args[0]);
    }
}
