package puzzles.strings;

import puzzles.common.solver.Configuration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Configuration for solving the strings puzzle.
 *
 * @author Ethan Hartman (theeman05)
 */
public class StringsConfig implements Configuration {
    private final int CHAR_MIN = 65; // Uppercase 'A' char value
    private final int CHAR_MAX = 90; // Uppercase 'Z' char value
    private final String start, end; // Start string and end string to get to

    /**
     * Default constructor for creating a StringsConfig object.
     *
     * @param start The string we start the puzzle at
     * @param end The string we end the puzzle at
     */
    public StringsConfig(String start, String end){
        this.start = start;
        this.end = end;
    }

    /**
     * Grab a substring of the initial string with the replacement character at the given index.
     *
     * @param initial string
     * @param replacement character
     * @param idx replacement index location
     * @return a substring with the character at idx replaced by replacement
     */
    private String replaceCharAt(String initial, char replacement, int idx){
        return initial.substring(0, idx) + replacement + initial.substring(idx+1);
    }

    /**
     * Shift the character at the index of start down one within [A, Z]
     *
     * @param idx specified to shift down one character
     * @return the shifted string
     */
    private String shiftCharAtDown(int idx){
        return replaceCharAt(start, (char) (start.charAt(idx) > CHAR_MIN ? start.charAt(idx) - 1 : CHAR_MAX), idx);
    }

    /**
     * Shift the character at the index of start up one within [A, Z]
     *
     * @param idx specified to shift up one character
     * @return the shifted string
     */
    private String shiftCharAtUp(int idx){
        return replaceCharAt(start, (char)(start.charAt(idx) < CHAR_MAX ? start.charAt(idx) + 1 : CHAR_MIN), idx);
    }

    /**
     * @return the start String for this object
     */
    public String getStart(){
        return start;
    }

    /**
     * @return whether the start equals the end
     */
    @Override
    public boolean isSolution() {
        return start.equals(end);
    }

    /**
     * @return the neighbors of this configuration. In this case, the left and right chars of the start at each letter
     * in start.
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new LinkedList<>();
        for (int at = 0; at < start.length(); at++) {
            neighbors.add(new StringsConfig(shiftCharAtDown(at), end));
            neighbors.add(new StringsConfig(shiftCharAtUp(at), end));
        }
        return neighbors;
    }

    /**
     * Check if the current object equals the given object.
     *
     * @param other to test if both objects are equal.
     * @return whether the two objects are equal.
     */
    @Override
    public boolean equals(Object other){
        if (other instanceof StringsConfig casted)
            return start.equals(casted.start) && end.equals(casted.end);
        return false;
    }

    /**
     * @return the hash code for this object.
     */
    @Override
    public int hashCode(){
        return start.hashCode() + end.hashCode();
    }
}
