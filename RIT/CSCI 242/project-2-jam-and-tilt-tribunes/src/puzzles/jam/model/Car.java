package puzzles.jam.model;

import puzzles.common.Coordinate;

/**
 * The class for a car in the jam game.
 *
 * @author Ethan Hartman (theeman05)
 */
public class Car {
    /**
     * The letters identifying the car
     */
    private final String ID;

    /**
     * Whether this is a horizontal car.
     */
    private final boolean IS_HORIZONTAL;

    /**
     * The current Coordinates of the vehicle.
     */
    private final Coordinate back, front;

    /**
     * Default Constructor for creating a car
     * @param id the letters identifying the car
     * @param backCoord the back Coordinate of the car
     * @param frontCoord the front Coordinate of the car
     */
    public Car(String id, Coordinate backCoord, Coordinate frontCoord){
        ID = id;
        IS_HORIZONTAL = backCoord.getRow() == frontCoord.getRow();
        back = backCoord;
        front = frontCoord;
    }

    /**
     * Creates a new car, offset by the given amount based on its orientation from the original location.
     * @param original original car model
     * @param moveAmount amount to shift the vehicle
     */
    public Car(Car original, int moveAmount){
        ID = original.ID;
        IS_HORIZONTAL = original.IS_HORIZONTAL;
        back = original.back.clone();
        front = original.front.clone();
        moveBy(moveAmount);
    }

    /**
     * @return the id of this car.
     */
    public String getID(){
        return ID;
    }

    /**
     * @return the back coordinate of this car
     */
    public Coordinate getBackCoordinate(){
        return back;
    }

    /**
     * @return the front coordinate of this car
     */
    public Coordinate getFrontCoordinate(){
        return front;
    }

    /**
     * Move the car by the given move amount
     * @param moveAmount to move the car by.
     */
    public void moveBy(int moveAmount){
        if (IS_HORIZONTAL){
            back.shiftColumn(moveAmount);
            front.shiftColumn(moveAmount);
        }else{
            back.shiftRow(moveAmount);
            front.shiftRow(moveAmount);
        }
    }

    /**
     * Get the move amount it would take to move a car to the given coordinates
     * If the location is not in-line with the car's orientation, 0 will be returned.
     * @param to location to get move amount to.
     * @return 0: Cannot move | any other number means we can move
     */
    public int getMoveAmountTo(Coordinate to){
        int moveAmount = 0;
        if (IS_HORIZONTAL && to.getRow() == front.getRow()){
            moveAmount = to.getColumn() - front.getColumn();
            if (moveAmount < 0)
                moveAmount = to.getColumn() - back.getColumn();
        }else if (!IS_HORIZONTAL && to.getColumn() == front.getColumn()){
            moveAmount = to.getRow() - front.getRow();
            if (moveAmount < 0)
                moveAmount = to.getRow() - back.getRow();
        }
        return moveAmount;
    }

    /** @return true: The cars orientation is horizontal */
    public boolean isHorizontal(){
        return IS_HORIZONTAL;
    }

    /**
     * Tests if the vehicle is within the given coordinate bounds
     * Assumes car is between (0,0) - bounds
     * Assumes back < front
     * @param bounds max bounds that the car must be inside.
     * @return true: The vehicle is within the bounds
     */
    public boolean isInBounds(Coordinate bounds){
        return (back.getRow() >= 0 && front.getRow() < bounds.getRow()) && // Check rows
                (back.getColumn() >= 0 && front.getColumn() < bounds.getColumn()); // Check cols
    }

    /**
     * Cars will be equal if their data are equal.
     * @param obj compare object
     * @return true: cars are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Car compare)
            return ID.equals(compare.ID) && IS_HORIZONTAL == compare.IS_HORIZONTAL && back.equals(compare.back) &&
                    front.equals(compare.front);
        return false;
    }

    @Override
    public int hashCode(){
        return ID.hashCode() + back.hashCode() + front.hashCode();
    }
}
