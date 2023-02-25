package puzzles.jam.model;

import puzzles.common.solver.Configuration;
import puzzles.common.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Configuration for solving the jam puzzle.
 * There should not be multiple cars with the same ID.
 *
 * @author Ethan Hartman (theeman05)
 */
public class JamConfig implements Configuration {
    /** The ID of the solver car */
    private static final String SOLVER_ID = "X";

    /** The character for an empty space */
    private static final char EMPTY_SPACE = '.';

    /** The dimensions of the board (rows, columns) */
    private final Coordinate BOARD_DIMENSIONS;

    /** The map of cars on the board. We expect the key to be the ID of the car. */
    private final HashMap<String, Car> cars;

    /** The characters of the visual board */
    private final String[][] visualBoard;

    /** The car which was moved last */
    private Car lastMoved;

    /**
     * Default constructor which creates a JamConfig by reading the given file.
     * @param filePath the path to the file we want to read.
     * @throws IOException could not read the given file.
     */
    public JamConfig(String filePath) throws IOException {
        System.out.println("File: " + filePath);
        cars = new HashMap<>();
        try (BufferedReader in = new BufferedReader(new FileReader(filePath))){
            Car curCar;
            int numCars;
            String[] fields = in.readLine().split("\\s+");
            BOARD_DIMENSIONS = new Coordinate(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]));
            visualBoard = new String[BOARD_DIMENSIONS.getRow()][BOARD_DIMENSIONS.getColumn()];
            numCars = Integer.parseInt(in.readLine().strip());
            for (int i = 0; i < numCars; i++){ // Iterate through cars
                fields = in.readLine().split("\\s+");
                curCar = new Car(fields[0], new Coordinate(fields[1], fields[2]), new Coordinate(fields[3], fields[4]));
                cars.put(curCar.getID(), curCar);
                visualizeCar(curCar);
            }
        }
    }

    /**
     * Creates a new JamConfig given a list of cars and a board dimension.
     * @param cars list of cars included in the config.
     * @param boardDimensions dimensions of the board.
     * @param lastMoved the car moved last
     */
    private JamConfig(HashMap<String, Car> cars, Coordinate boardDimensions, Car lastMoved){
        this.cars = cars;
        this.lastMoved = lastMoved;
        BOARD_DIMENSIONS = boardDimensions;
        visualBoard = new String[BOARD_DIMENSIONS.getRow()][BOARD_DIMENSIONS.getColumn()];
        for (Map.Entry<String, Car> entry : cars.entrySet()) visualizeCar(entry.getValue());
    }

    /**
     * Adds the given car to the visual list.
     * @param car to add to the visual list
     */
    private void visualizeCar(Car car){
        for (int r = car.getBackCoordinate().getRow(); r <= car.getFrontCoordinate().getRow(); r++)
            for (int c = car.getBackCoordinate().getColumn(); c <= car.getFrontCoordinate().getColumn(); c++)
                visualBoard[r][c] = car.getID();
    }

    /**
     * Removes a car from the visual list.
     * @param car to remove from the visual list
     */
    private void unVisualizeCar(Car car){
        for (int r = car.getBackCoordinate().getRow(); r <= car.getFrontCoordinate().getRow(); r++)
            for (int c = car.getBackCoordinate().getColumn(); c <= car.getFrontCoordinate().getColumn(); c++)
                visualBoard[r][c] = null;
    }

    /** @return true: the given coordinate is off the board */
    private boolean isOffBoard(Coordinate coordinate){
        return coordinate.getRow() < 0 || coordinate.getRow() >= BOARD_DIMENSIONS.getRow() ||
                coordinate.getColumn() < 0 || coordinate.getColumn() >= BOARD_DIMENSIONS.getColumn();
    }

    /**
     * Returns a new config with the given car shifted the given amount.
     * If the shifted car is out of bounds, null is returned.
     * @param car to be shifted.
     * @param moveAmount to shift the car by.
     * @return a new configuration with the car shifted or null if the car couldn't be shifted.
     */
    private Configuration newShift(Car car, int moveAmount){
        Car newCar = new Car(car, moveAmount);
        if (newCar.isInBounds(BOARD_DIMENSIONS) &&
                (moveAmount > 0 || visualBoard[newCar.getBackCoordinate().getRow()][newCar.getBackCoordinate().getColumn()] == null) &&
                (moveAmount < 0 || visualBoard[newCar.getFrontCoordinate().getRow()][newCar.getFrontCoordinate().getColumn()] == null)){
            // Checks if the car is in bounds, then if there's already a car in our new position
            // Copy and replace the old car with the new, moved car
            HashMap<String, Car> newCarList = new HashMap<>(cars);
            newCarList.put(newCar.getID(), newCar);
            return new JamConfig(newCarList, BOARD_DIMENSIONS, newCar);
        }
        return null;
    }

    /**
     * Shifts the given car to the given coordinate and updates the visual list of cars.
     * @param car to move to the coordinate.
     * @param to  coordinate to move the car to.
     * @return true: Car shifted successfully
     */
    public boolean shiftCar(Car car, Coordinate to){
        if (car == null || isOffBoard(to)) return false;
        int moveAmount = car.getMoveAmountTo(to);
        if (moveAmount == 0) return false;

        String match;
        int startRow, startCol;
        // Verify there are no cars leading up to the move location
        if (moveAmount > 0) {
            startRow = car.getFrontCoordinate().getRow();
            startCol = car.getFrontCoordinate().getColumn();
            for (int i = 1; i < moveAmount + 1; i++) {
                match = car.isHorizontal() ? visualBoard[startRow][startCol + i] : visualBoard[startRow + i][startCol];
                if (match != null && !match.equals(car.getID()))
                    return false; // there's another car here
            }
        }else{
            startRow = car.getBackCoordinate().getRow();
            startCol = car.getBackCoordinate().getColumn();
            for (int i = 1; i < Math.abs(moveAmount) + 1; i++) {
                match = car.isHorizontal() ? visualBoard[startRow][startCol - i] : visualBoard[startRow - i][startCol];
                if (match != null && !match.equals(car.getID()))
                    return false; // there's another car here
            }
        }

        unVisualizeCar(car);
        car.moveBy(moveAmount);
        visualizeCar(car);
        lastMoved = car;
        return true;
    }

    /** Returns the car at the given coordinate. Will be null if there is no car or the coordinates are out of bounds */
    public Car getCarAt(Coordinate at){
        if (isOffBoard(at)) return null;
        return cars.get(visualBoard[at.getRow()][at.getColumn()]);
    }

    /** @return the last moved car */
    public Car getLastMoved(){
        return lastMoved;
    }

    /** @return the dimensions of the board */
    public Coordinate getDimensions(){
        return BOARD_DIMENSIONS;
    }

    /**
     * @return true if the solver is in the last column
     */
    @Override
    public boolean isSolution() {
        return cars.get(SOLVER_ID).getFrontCoordinate().getColumn() == BOARD_DIMENSIONS.getColumn() - 1;
    }

    /**
     * Neighbors will be all cars moved forward and backward (if possible)
     * @return the neighbors of the current configuration.
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new LinkedList<>();
        Configuration testConfig;
        for (Map.Entry<String, Car> entry : cars.entrySet()){
            if ((testConfig = newShift(entry.getValue(), 1)) != null)
                neighbors.add(testConfig);
            if ((testConfig = newShift(entry.getValue(), -1)) != null)
                neighbors.add(testConfig);
        }
        return neighbors;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof JamConfig compare)
            return cars.equals(compare.cars);
        return false;
    }

    @Override
    public int hashCode() {
        return cars.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int r = 0; r < BOARD_DIMENSIONS.getRow(); r++) {
            if (r != 0) result.append("\n");
            for (int c = 0; c < BOARD_DIMENSIONS.getColumn(); c++) {
                if (c != 0) result.append(" ");
                if (visualBoard[r][c] == null)
                    result.append(EMPTY_SPACE);
                else
                    result.append(visualBoard[r][c]);
            }
        }
        return result.toString();
    }
}
