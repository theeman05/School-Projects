package magnets;

import backtracking.Configuration;
import test.IMagnetTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The representation of a magnet configuration, including the ability
 * to backtrack and also give information to the JUnit tester.
 *
 * This implements a more optimal pruning strategy in isValid():
 * - Pair checked each time a new cell is populated
 * - Polarity checked each time a new cell is populated
 * - When last column or row is populated, the pos/neg counts are checked
 *
 * @author RIT CS + Ethan Hartman (theeman05)
 */
public class MagnetsConfig implements Configuration, IMagnetTest {
    /** a cell that has not been assigned a value yet */
    private final static char EMPTY = '.';
    /** a blank cell */
    private final static char BLANK = 'X';
    /** a positive cell */
    private final static char POS = '+';
    /** a negative cell */
    private final static char NEG = '-';
    /** left pair value */
    private final static char LEFT = 'L';
    /** right pair value */
    private final static char RIGHT = 'R';
    /** top pair value */
    private final static char TOP = 'T';
    /** bottom pair value */
    private final static char BOTTOM = 'B';
    /** and ignored count for pos/neg row/col */
    private final static int IGNORED = -1;

    // add private state here
    /** The total row and column values of the rectangle */
    private static int ROWS, COLS;
    /** The number of positive and negative values which are in the row/column */
    private static int[] POS_ROW, NEG_ROW, POS_COL, NEG_COL;
    /** The direction of each magnet pair */
    private static char[][] MAG_PAIRS;
    /** The current values of our magnets in each row and column */
    private char[][] magValues;
    /** The current pointer location */
    private int curRow, curCol;
    /** The current count of positives and negatives in the current row */
    private int curPosCt, curNegCt;

    /**
     * Read in the magnet puzzle from the filename.  After reading in, it should display:
     * - the filename
     * - the number of rows and columns
     * - the grid of pairs
     * - the initial config with all empty cells
     *
     * @param filename the name of the file
     * @throws IOException thrown if there is a problem opening or reading the file
     */
    public MagnetsConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            // read first line: rows cols
            String[] fields = in.readLine().split("\\s+");
            String field;
            ROWS = Integer.parseInt(fields[0]);
            COLS = Integer.parseInt(fields[1]);
            POS_ROW = new int[ROWS];
            NEG_ROW = new int[ROWS];
            POS_COL = new int[COLS];
            NEG_COL = new int[COLS];
            MAG_PAIRS = new char[ROWS][COLS];
            magValues = new char[ROWS][COLS];
            curPosCt = curNegCt = 0;

            System.out.println("File: " + filename);
            System.out.println("Rows: " + ROWS + ", Columns: " + COLS);

            // Build the number of positive values
            fields = in.readLine().split("\\s+");
            for (curRow = 0; curRow < ROWS; curRow++)
                POS_ROW[curRow] = Integer.parseInt(fields[curRow]);

            fields = in.readLine().split("\\s+");
            for (curCol = 0; curCol < COLS; curCol++)
                POS_COL[curCol] = Integer.parseInt(fields[curCol]);

            // Build the number of negative values
            fields = in.readLine().split("\\s+");
            for (curRow = 0; curRow < ROWS; curRow++)
                NEG_ROW[curRow] = Integer.parseInt(fields[curRow]);

            fields = in.readLine().split("\\s+");
            for (curCol = 0; curCol < COLS; curCol++)
                NEG_COL[curCol] = Integer.parseInt(fields[curCol]);

            System.out.println("Pairs:");
            // Populate magnet pairs
            for (curRow = 0; curRow < ROWS; curRow++) {
                field = in.readLine();
                fields = field.split("\\s+");
                System.out.println(field);
                for (curCol = 0; curCol < COLS; curCol++)
                    MAG_PAIRS[curRow][curCol] = fields[curCol].charAt(0);
            }

            System.out.println("Initial config:");
            // Fill magValues with empty characters
            for (curRow = 0; curRow < ROWS; curRow++)
                for (curCol = 0; curCol < COLS; curCol++)
                    magValues[curRow][curCol] = EMPTY;

            System.out.println(this);

            // Set our start point off the board.
            curRow = 0;
            curCol = -1;
        } // <3 Jim
    }

    /**
     * The copy constructor which advances the cursor, creates a new grid,
     * and populates the grid at the cursor location with val
     * @param other the board to copy
     * @param val the value to store at new cursor location
     */
    private MagnetsConfig(MagnetsConfig other, char val) {
        curCol = other.curCol + 1;
        curRow = other.curRow;
        if (curCol == COLS) {
            curRow++;
            curCol = curPosCt = curNegCt = 0;
            if (curRow == ROWS) return;
        }else{
            curPosCt = other.curPosCt;
            curNegCt = other.curNegCt;
        }

        magValues = new char[ROWS][COLS];
        for (int row=0; row < ROWS; row++)
            magValues[row] = other.magValues[row].clone();

        magValues[curRow][curCol] = val;

        if (val == POS) curPosCt++;
        else if (val == NEG) curNegCt++;
    }


    /**
     * Generate the successor configs.  For minimal pruning, this should be
     * done in the order: +, - and X.
     *
     * @return the collection of successors
     */
    @Override
    public List<Configuration> getSuccessors() {
        List<Configuration> successors = new ArrayList<>();
        successors.add(new MagnetsConfig(this, POS));
        successors.add(new MagnetsConfig(this, NEG));
        successors.add(new MagnetsConfig(this, BLANK));

        return successors;
    }

    /**
     * Checks to make sure a successor is valid or not.  For minimal pruning,
     * each newly placed cell at the cursor needs to make sure its pair
     * is valid, and there is no polarity violation.  When the last cell is
     * populated, all row/col pos/negative counts are checked.
     *
     * @return whether this config is valid or not
     */
    @Override
    public boolean isValid() {
        char otherVal, curVal;
        if (MAG_PAIRS[curRow][curCol] == RIGHT)
            otherVal = magValues[curRow][curCol-1]; // Other must be left
        else if (MAG_PAIRS[curRow][curCol] == BOTTOM)
            otherVal = magValues[curRow-1][curCol]; // Other must be top
        else if (MAG_PAIRS[curRow][curCol] == LEFT || MAG_PAIRS[curRow][curCol] == TOP)
            otherVal = EMPTY; // Since we go left->right, top->bottom, we can't have values right or below us (yet).
        else return false;

        curVal = magValues[curRow][curCol];
        // Check if pairs are correct first
        if ((curVal != BLANK && otherVal == EMPTY) ||
                ((curVal == NEG && otherVal == POS) || (curVal == POS && otherVal == NEG))) {
            // At this point, we must ensure the surrounding area isn't equal
            if ((curCol > 0 && magValues[curRow][curCol-1] == curVal)           ||  // Check left
                    (curCol < COLS-1 && magValues[curRow][curCol+1] == curVal)  ||  // Check right
                    (curRow > 0 && magValues[curRow-1][curCol] == curVal)       ||  // Check top
                    (curRow < ROWS-1 && magValues[curRow+1][curCol] == curVal))     // Check bottom
                return false;
        } else if (curVal != BLANK || (otherVal != BLANK && otherVal != EMPTY))
            return false; // curVal is invalid, or it is blank and the other value isn't empty

        // Assuming the previous configurations are valid, we need to check rows and columns
        // First ensure we aren't over our negative and positive row limits, or equal the limits if we're in the last col
        if ((curCol != COLS - 1 && ((POS_ROW[curRow] != -1 && curPosCt > POS_ROW[curRow]) ||
                (NEG_ROW[curRow] != -1 && curNegCt > NEG_ROW[curRow]))) ||
                (curCol == COLS - 1 && ((POS_ROW[curRow] != -1 && curPosCt != POS_ROW[curRow]) ||
                        (NEG_ROW[curRow] != -1 && curNegCt != NEG_ROW[curRow]))))
            return false;

        int colPosCt=0, colNegCt=0;
        // Count the positive and negative columns
        for (int row=0; row <= curRow ; row++) {
            if (magValues[row][curCol] == POS) colPosCt++;
            else if (magValues[row][curCol] == NEG) colNegCt++;
        }
        // Ensure the column values
        return ((POS_COL[curCol] == -1 || colPosCt <= POS_COL[curCol]) && // are <= the desired amount
                (NEG_COL[curCol] == -1 || colNegCt <= NEG_COL[curCol])) &&
                (curRow != ROWS - 1 || ((POS_COL[curCol] == -1 || colPosCt == POS_COL[curCol]) &&
                        (NEG_COL[curCol] == -1 || colNegCt == NEG_COL[curCol]))); // and equal desired if they must.
    }

    @Override
    public boolean isGoal() {
        // Last cell of our grid.
        return curCol == COLS - 1 && curRow == ROWS - 1;
    }

    /**
     * Returns a string representation of the puzzle including all necessary info.
     *
     * @return the string
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        // top row
        result.append("+ ");
        for (int col = 0; col < getCols(); ++col) {
            result.append(getPosColCount(col) != IGNORED ? getPosColCount(col) : " ");
            if (col < getCols() - 1) {
                result.append(" ");
            }
        }
        result.append(System.lineSeparator());
        result.append("  ");
        for (int col = 0; col < getCols(); ++col) {
            if (col != getCols() - 1) {
                result.append("--");
            } else {
                result.append("-");
            }
        }
        result.append(System.lineSeparator());

        // middle rows
        for (int row = 0; row < getRows(); ++row) {
            result.append(getPosRowCount(row) != IGNORED ? getPosRowCount(row) : " ").append("|");
            for (int col = 0; col < getCols(); ++col) {
                result.append(getVal(row, col));
                if (col < getCols() - 1) {
                    result.append(" ");
                }
            }
            result.append("|").append(getNegRowCount(row) != IGNORED ? getNegRowCount(row) : " ");
            result.append(System.lineSeparator());
        }

        // bottom row
        result.append("  ");
        for (int col = 0; col < getCols(); ++col) {
            if (col != getCols() - 1) {
                result.append("--");
            } else {
                result.append("-");
            }
        }
        result.append(System.lineSeparator());

        result.append("  ");
        for (int col = 0; col < getCols(); ++col) {
            result.append(getNegColCount(col) != IGNORED ? getNegColCount(col) : " ").append(" ");
        }
        result.append(" -").append(System.lineSeparator());
        return result.toString();
    }

    // IMagnetTest

    @Override
    public int getRows() {
        return ROWS;
    }

    @Override
    public int getCols() {
        return COLS;
    }

    @Override
    public int getPosRowCount(int row) {
        return POS_ROW[row];
    }

    @Override
    public int getPosColCount(int col) {
        return POS_COL[col];
    }

    @Override
    public int getNegRowCount(int row) {
        return NEG_ROW[row];
    }

    @Override
    public int getNegColCount(int col) {
        return NEG_COL[col];
    }

    @Override
    public char getPair(int row, int col) {
        return MAG_PAIRS[row][col];
    }

    @Override
    public char getVal(int row, int col) {
        return magValues[row][col];
    }

    @Override
    public int getCursorRow() {
        return curRow;
    }

    @Override
    public int getCursorCol() {
        return curCol;
    }
}
