package puzzles.tilt.model;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a model for the game called tilt.
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
public class TiltModel {
    /** the collection of observers of this model */
    private final List<Observer<TiltModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private TiltConfig currentConfig;
    private final Solver solver;


    /**
     * Create initial game configuration using given filename and a solver.
     *
     * @param filename the filename to pull game data from.
     */
    public TiltModel(String filename) throws IOException {
        currentConfig = new TiltConfig(filename);
        solver = new Solver();
    }

    /**
     * Solves the puzzle and obtains the next move. Sets current configuration
     * to that move.
     */
    public void getHint(){
        List<Configuration> path = solver.buildPathBFS(currentConfig);

        if (path == null){
            alertObservers("No Solution!");
        }else if (path.size() > 1) {
            currentConfig = (TiltConfig) path.get(1);
            alertObservers("Next Step!");
        }else{
            alertObservers("Already Solved!");
        }
    }

    /**
     * Returns size of the board or length/width of board as they are the same.
     *
     * @return the size of the board.
     */
    public int getSize(){
        return currentConfig.getSize();
    }

    /**
     * Returns specific value in the grid.
     *
     * @return value at a specific board point in the puzzle.
     */
    public char getVal(int row, int col){
        return currentConfig.getVal(row, col);
    }

    /**
     * Attempts to create new configuration from given filename.
     *
     * @param filename the file to load game from.
     * @exception IOException if the file cannot be loaded
     */
    public void loadFromFile(String filename) throws IOException {
        try {
            currentConfig = new TiltConfig(filename);
            alertObservers("Loaded: " + filename);
        }catch(Exception e){
            alertObservers("Could not load: " + filename);
        }
    }

    /**
     * Attempts to make the specified move.
     *
     * @param direction the direction to tilt the board/make move.
     */
    public void makeMove(String direction) {
        if (!currentConfig.probeSpecificMove(direction)){
            alertObservers("Not a valid move!");
        }else if (currentConfig.isSolution()){
            alertObservers("You Solved It!");
        }else {
            alertObservers("Move Made!");
        }


    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<TiltModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    /**
     * Returns a visual representation of the current configurations board.
     *
     * @return the board in string format.
     */
    @Override
    public String toString(){
        return currentConfig.toString();
    }

}
