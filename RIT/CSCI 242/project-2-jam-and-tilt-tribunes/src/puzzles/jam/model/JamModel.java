package puzzles.jam.model;

import puzzles.common.Coordinate;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Model for Jam
 *
 * @author Ethan Hartman (theeman05)
 */
public class JamModel {
    /** Message sent when a board has successfully loaded. */
    public static final String LOADED = "Game Loaded";

    /** Message sent when a board has failed to load. */
    public static final String LOAD_FAILED = "Load Failed";

    /** The message that will precede a hint. */
    public static final String HINT_PREFIX = "Moved to next step.";

    /** Message sent when a car has been selected */
    public static final String SELECTION_PREFIX = "Selected the car with ID: ";

    /** Message sent when a car has been shifted successfully*/
    public static final String SHIFT_PREFIX = "Moved car to ";

    /** Message sent when a car has been shifted unsuccessfully*/
    public static final String SHIFT_FAIL_PREFIX = "Invalid Movement";

    /** Message sent when an invalid selection has been made */
    public static final String INVALID_SELECTION_PREFIX = "Invalid Selection: There is no car at ";

    /** Message sent when the game is won */
    public static final String WIN_PREFIX = "Yay, you won. Noice.";

    /** Message sent when select or hint is called after the game has completed */
    public static final String GAME_END_PREFIX = "Please reset the current game or load a new game.";

    /** The collection of observers of this model */
    private final List<Observer<JamModel, String>> observers = new LinkedList<>();

    /** the solver object */
    private final Solver solver = new Solver();

    /** The current configuration */
    private JamConfig currentConfig;

    /** If there's a game going on */
    private boolean gameInSession;

    /** The current car selected */
    private Car selectedCar;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<JamModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers)
            observer.update(this, data);
    }

    /**
     * Loads a config based on the specified file and alert observers
     * @param filePath path to generate config from
     * @return true: Config successfully loaded
     */
    public boolean loadFromFile(String filePath) {
        try {
            selectedCar = null;
            currentConfig = new JamConfig(filePath);
            gameInSession = true;
            alertObservers(LOADED);
            checkGameWin();
            return true;
        }catch(IOException e) {
            alertObservers(LOAD_FAILED);
        }
        return false;
    }

    /** Check if the game has been beaten and alert observers if so. */
    private void checkGameWin(){
        if (!currentConfig.isSolution()) return;
        alertObservers(WIN_PREFIX);
        gameInSession = false;
    }

    /** @return true: The game is over and observers have been alerted */
    private boolean alertIfGameOver(){
        if (gameInSession) return false;
        alertObservers(GAME_END_PREFIX);
        return true;
    }

    /** Return the car at the given coordinate or null if no car is present */
    public Car getCarAt(Coordinate at){
        return currentConfig.getCarAt(at);
    }

    /**
     * Select the car at the given location.
     * Will alert observers and return false if the game is over.
     * @param at location the car is within.
     * @return true: There is a valid car at the location
     */
    public boolean selectCarAt(Coordinate at){
        if (alertIfGameOver()) return false;
        if ((selectedCar = getCarAt(at)) != null) {
            alertObservers(SELECTION_PREFIX + selectedCar.getID());
            return true;
        }
        alertObservers(INVALID_SELECTION_PREFIX + at);
        return false;
    }

    /**
     * Move the currently selected car to the given coordinate.
     * Will alert observers and do nothing if the game is over.
     * @param to coordinate to move the car to.
     */
    public void moveSelected(Coordinate to){
        if (alertIfGameOver()) return;
        if (currentConfig.shiftCar(selectedCar, to)) {
            alertObservers(SHIFT_PREFIX + to);
            checkGameWin();
        }else
            alertObservers(SHIFT_FAIL_PREFIX);
    }

    /** Update the current configuration and alert observers of our change */
    public void announceHint(){
        if (alertIfGameOver()) return;

        List<Configuration> path = solver.buildPathBFS(currentConfig);
        currentConfig = (JamConfig) path.get(1);
        alertObservers(HINT_PREFIX);
        checkGameWin();
    }

    /** Return the dimensions of the board */
    public Coordinate getDimensions(){
        return currentConfig.getDimensions();
    }

    /** @return the last moved car */
    public Car getLastMoved() {
        return currentConfig.getLastMoved();
    }

    /** Prints the current Jam Configuration */
    public void printConfig(){
        System.out.println(currentConfig);
    }
}
