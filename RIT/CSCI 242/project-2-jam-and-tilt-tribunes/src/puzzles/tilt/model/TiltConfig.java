package puzzles.tilt.model;
import puzzles.common.solver.Configuration;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * This is a configuration for the game called tilt.
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
public class TiltConfig implements Configuration {
    /** empty space*/
    private final static char EMPTY = '.';
    /** a blocker cell */
    private final static char BLOCKER = '*';
    /** a green slider */
    private final static char GREEN = 'G';
    /** a blue slider */
    private final static char BLUE = 'B';
    /** the goal */
    private final static char GOAL = 'O';

    //possible directional inputs
    private final static String UP = "UP";
    private final static String RIGHT = "RIGHT";
    private final static String DOWN = "DOWN";
    private final static String LEFT = "LEFT";

    //board game variables
    private final char [][] board;
    private static int size;
    private static int blueCount;

    /**
     * Create initial game configuration using given filename.
     *
     * @param filename the filename to pull game data from.
     * @exception IOException if the file cannot be read.
     */
    public TiltConfig (String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))){
            String line = in.readLine();
            size = Integer.parseInt(line);
            int counter = 0;
            blueCount = 0;
            board = new char [size][size];

            //read in filename and create game board
            while ((line = in.readLine()) != null){
                String[] fields = line.split(" ");
                for (int i = 0; i < size; i++){
                    board[counter][i] = fields[i].charAt(0);
                    if (fields[i].charAt(0) == BLUE){
                        blueCount++;
                    }
                }
                counter++;
            }
        }
    }

    /**
     * Create new configuration and shift it by given direction
     *
     * @param other the configuration from which to make a move.
     * @param command the direction which to shift board.
     */
    private TiltConfig (TiltConfig other, String command){
        //copy board values
        board = new char[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                board[i][j] = other.getVal(i, j);
            }
        }

        //shift based on command
        switch (command) {
            case UP -> shiftUp();
            case RIGHT -> shiftRight();
            case DOWN -> shiftDown();
            case LEFT -> shiftLeft();
        }

    }

    /**
     * Shift game board pieces up based on the rules of the Tilt game.
     */
    private void shiftUp(){
        for (int i = 1; i < size; i++){
            for (int j = 0; j < size; j++){
                if (board[i][j] == GREEN || board[i][j] == BLUE){
                    for (int k = i; k > 0 ; k--){
                        if (board[k-1][j] == GOAL){
                            board[k][j] = EMPTY;
                            k = 0;
                        }else if (board[k-1][j] == BLOCKER || board[k-1][j] == BLUE || board[k-1][j] == GREEN){
                            k = 0;
                        }else if (board[k-1][j] == EMPTY){
                            char tempChar = board[k][j];
                            board[k][j] = EMPTY;
                            board[k-1][j] = tempChar;
                        }
                    }
                }
            }
        }
    }

    /**
     * Shift game board pieces right based on the rules of the Tilt game.
     */
    private void shiftRight(){
        for (int j = size - 2; j >= 0; j--){
            for (int i = size - 1; i >= 0; i--){
                if (board[i][j] == GREEN || board[i][j] == BLUE){
                    for (int k = j; k < size - 1 ; k++){
                        if (board[i][k+1] == GOAL){
                            board[i][k] = EMPTY;
                            k = size;
                        }else if (board[i][k+1] == BLOCKER || board[i][k+1] == BLUE || board[i][k+1] == GREEN){
                            k = size;
                        }else if (board[i][k+1] == EMPTY){
                            char tempChar = board[i][k];
                            board[i][k] = EMPTY;
                            board[i][k+1] = tempChar;
                        }
                    }
                }
            }
        }
    }

    /**
     * Shift game board pieces down based on the rules of the Tilt game.
     */
    private void shiftDown(){
        for (int i = size - 2; i >= 0; i--){
            for (int j = size - 1; j >= 0; j--){
                if (board[i][j] == GREEN || board[i][j] == BLUE){
                    for (int k = i; k < size - 1 ; k++){
                        if (board[k+1][j] == GOAL){
                            board[k][j] = EMPTY;
                            k = size;
                        }else if (board[k+1][j] == BLOCKER || board[k+1][j] == BLUE || board[k+1][j] == GREEN){
                            k = size;
                        }else if (board[k+1][j] == EMPTY){
                            char tempChar = board[k][j];
                            board[k][j] = EMPTY;
                            board[k+1][j] = tempChar;
                        }
                    }
                }
            }
        }
    }

    /**
     * Shift game board pieces left based on the rules of the Tilt game.
     */
    private void shiftLeft(){
        for (int j = 1; j < size; j++){
            for (int i = 0; i < size; i++){
                if (board[i][j] == GREEN || board[i][j] == BLUE){
                    for (int k = j; k > 0 ; k--){
                        if (board[i][k-1] == GOAL){
                            board[i][k] = EMPTY;
                            k = 0;
                        }else if (board[i][k-1] == BLOCKER || board[i][k-1] == BLUE || board[i][k-1] == GREEN){
                            k = 0;
                        }else if (board[i][k-1] == EMPTY){
                            char tempChar = board[i][k];
                            board[i][k] = EMPTY;
                            board[i][k-1] = tempChar;
                        }
                    }
                }
            }
        }
    }

    /**
     * Determine if a configuration is valid or not by comparing blue piece count
     * before and after move.
     *
     * @return true if the configuration is valid, false if the configuration is invalid.
     */
    private boolean isValid(){
        int newBlueCount = 0;
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (board[i][j] == BLUE){
                    newBlueCount++;
                }
            }
        }
        return newBlueCount == blueCount;
    }

    /**
     * Returns size of the board or length/width of board as they are the same.
     *
     * @return the size of the board.
     */
    public int getSize(){
        return size;
    }

    /**
     * Creates a configuration based on a given direction and tests it to see if it is a valid move.
     *
     * @param direction the direction of board movement to test if it is valid
     * @return true if the configuration is valid, false if the configuration is invalid
     */
    public boolean probeSpecificMove(String direction){
        switch (direction) {
            case "n" -> {
                TiltConfig testConfiguration = new TiltConfig(this, UP);
                if (testConfiguration.isValid()) {
                    this.shiftUp();
                    return true;
                }

            }
            case "e" -> {
                TiltConfig testConfiguration = new TiltConfig(this, RIGHT);
                if (testConfiguration.isValid()) {
                    this.shiftRight();
                    return true;
                }

            }
            case "s" -> {
                TiltConfig testConfiguration = new TiltConfig(this, DOWN);
                if (testConfiguration.isValid()) {
                    this.shiftDown();
                    return true;
                }

            }
            case "w" -> {
                TiltConfig testConfiguration = new TiltConfig(this, LEFT);
                if (testConfiguration.isValid()) {
                    this.shiftLeft();
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Creates up to 4 additional configurations of each possible move on the current configuration.
     * Does not add configuration if it is invalid.
     *
     * @return the collection of configurations.
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> myConfigs = new ArrayList<>();
        TiltConfig neighbor1 = new TiltConfig(this, UP);
        TiltConfig neighbor2 = new TiltConfig(this, RIGHT);
        TiltConfig neighbor3 = new TiltConfig(this, DOWN);
        TiltConfig neighbor4 = new TiltConfig(this, LEFT);


        if (neighbor1.isValid()){
            myConfigs.add(neighbor1);
        }
        if (neighbor2.isValid()){
            myConfigs.add(neighbor2);
        }
        if (neighbor3.isValid()){
            myConfigs.add(neighbor3);
        }
        if (neighbor4.isValid()){
            myConfigs.add(neighbor4);
        }
        return myConfigs;
    }

    /**
     * Tests to see if the configuration is solved.
     *
     * @return true if there are no more green pieces left, false if there are still green pieces left
     */
    @Override
    public boolean isSolution() {
        return !(toString().contains((String.valueOf(GREEN))));
    }

    /**
     * Tests to see if current and given configurations are equal.
     *
     * @param other the other configuration.
     * @return true if configurations are equal, false if they are not.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof TiltConfig otherConfig){
            return (Arrays.deepEquals(this.getCurrent(), otherConfig.getCurrent()));
        }
        return false;
    }

    /**
     * Hashcode function for configurations. Does hashcode from game board.
     *
     * @return hashcode.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Returns a visual representation of the current configurations board.
     *
     * @return the board in string format.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                result.append(board[i][j]);
                result.append(" ");
                if (j == board.length - 1){
                    result.append("\n");
                }
            }
        }

        return result.toString();
    }

    /**
     * Returns a clone of the current board.
     *
     * @return clone of current board.
     */
    public char[][] getCurrent() {return board.clone();}

    /**
     * Returns specific value in the grid.
     *
     * @return value at a specific board point in the puzzle.
     */
    public char getVal(int row, int col) {
        return board[row][col];
    }

}
