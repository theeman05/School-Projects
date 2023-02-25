package puzzles.common;

/**
 * A class for creating row column coordinates.
 * I felt this would be easier than indexes in an array.
 *
 * @author Ethan Hartman (theeman05)
 */
public class Coordinate{
    /** Value for making the difference between rows and columns huge with the hash to reduce collisions */
    private static final int HASH_SEPARATOR = 92568;

    /** The current row of the coordinate */
    private int row;

    /** The current column of the coordinate */
    private int column;

    /**
     * Default constructor requiring a row and a column.
     * @param row the row of the coordinate
     * @param column the column of the coordinate
     */
    public Coordinate(int row, int column){
        set(row, column);
    }

    /**
     * Constructor which parses the given String row and column.
     * Assumes input is valid
     * @param row the String row of the coordinate
     * @param column the String column of the coordinate
     */
    public Coordinate(String row, String column){
        set(Integer.parseInt(row), Integer.parseInt(column));
    }

    /**
     * @return the row.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Set both the row and the column
     * @param row replacing the current row.
     * @param column replacing the current column.
     */
    public void set(int row, int column){
        this.row = row;
        this.column = column;
    }

    /**
     * @param row replaces the current row.
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @param column replaces the current column.
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Shift the row n times.
     * @param amount to shift row by.
     */
    public void shiftRow(int amount){
        row += amount;
    }

    /**
     * Shift the column n times.
     * @param amount to shift column by.
     */
    public void shiftColumn(int amount){
        column += amount;
    }

    /**
     * Check whether the input object has the same row and column as the current object.
     * @param o comparison object.
     * @return true: Objects are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Coordinate comp)
            return row == comp.getRow() && column == comp.getColumn();
        return false ;
    }

    @Override
    public int hashCode(){
        return row * HASH_SEPARATOR + column;
    }

    /**
     * @return (row,column)
     */
    @Override
    public String toString() {
        return "(" + row + "," + column + ")";
    }

    @Override
    public Coordinate clone() {
        return new Coordinate(row, column); // No mutable data, so we can just return the same data.
    }
}
