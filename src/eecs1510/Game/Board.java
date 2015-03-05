package eecs1510.Game;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nathan on 2/12/15
 *
 * Holds the elements for the game. All game boards are square.
 */
public class Board
{

    /** The default size of the game board */
    public static final int DEFAULT_SIZE = 4;
    /** What value determines when the game has been "won" */
    public static final int WIN_CONDITION_VALUE = 2048;
    /** Any random number above this will generate a four */
    public static final double FOUR_THRESHOLD = 0.9;

    /** The seeded random number generator for this game board */
    private final Randomizer rng;

    private final int size;
    private final int[][] data;

    public Board() throws Randomizer.InvalidSeedException
    {
        this(DEFAULT_SIZE, Randomizer.randomSeed());
    }

    public Board(String seed) throws Randomizer.InvalidSeedException
    {
        this(DEFAULT_SIZE, seed);
    }

    public Board(int size, String seed) throws Randomizer.InvalidSeedException
    {
        this.size = size;

        data = new int[size][size];

        rng = new Randomizer(seed.trim().replaceAll("\\s", ""));

        placeRandom();
        placeRandom();
    }

    /**
     * @return the Size of the game board (which is square)
     */
    public int getSize()
    {
        return size;
    }

    /**
     * @return the Seed of the random number generator
     */
    public String getSeed()
    {
        return rng.seed;
    }

    /**
     * Gets the element at the specified row and column
     *
     * @param row
     * @param column
     * @return
     */
    public int getElement(int row, int column)
    {
        return data[row][column];
    }

    /**
     * Squashes all elements in the specified direction. Note that this does NOT
     * generate a random tile and add it to the game board. For that, you need
     * to call <code>placeRandom()</code>
     *
     * A move is considered invalid if all cells are aligned against the border
     * in which you are trying to merge AND no cells can be merged.
     *
     * @param d The direction to squash elements in
     * @return The total number of squashed tiles or -1 if the move was invalid
     */
    public MoveResult squash(Direction d)
    {
        switch(d)
        {
            case NORTH: return squashNorth();
            case SOUTH: return squashSouth();
            case  EAST: return squashEast();
            case  WEST: return squashWest();
            default: return MoveResult.invalid();
        }
    }

    /**
     * Merges like items in the specified direction
     *
     * @param source The row or column-slice to merge
     * @param LTR    The direction to merge in
     * @return a <code>MoveResult</code> object containing the total number
     *         of merged cells as well as the score gained by those merged
     *         cells
     */
    private MoveResult merge(int[] source, boolean LTR)
    {
        int totalMerged = 0;
        int totalMergedValue = 0;

        if (LTR)
        {
            for (int i = 0; i < source.length - 1; i++)
            {
                if (source[i] == source[i + 1])
                {
                    source[i] *= 2;
                    source[i + 1] = 0;
                    totalMerged++;
                    totalMergedValue += source[i];
                }
            }
        } else {
            for (int i = source.length - 1; i >= 1; i--)
            {
                if (source[i] == source[i - 1])
                {
                    source[i] *= 2;
                    source[i - 1] = 0;
                    totalMerged++;
                    totalMergedValue += source[i];
                }
            }
        }

        return new MoveResult(totalMerged, totalMergedValue);
    }

    /**
     * @param arr
     * @return a filtered array of the source <code>arr</code> with all zeros removed
     */
    private int[] stripZeros(int[] arr)
    {
        return Arrays.stream(arr).filter((v) -> v > 0).toArray();
    }

    private MoveResult squashNorth()
    {
        int totalMerged = 0;
        int totalMergedValue = 0;
        int[][] newState = new int[size][size];

        for (int column = 0; column < size; column++)
        {
            int[] filteredColumn = stripZeros(slice(column));
            if (filteredColumn.length == 0) continue;

            MoveResult partial = merge(filteredColumn, true);
            totalMerged += partial.mergeCount;
            totalMergedValue += partial.mergeValue;

            filteredColumn = stripZeros(filteredColumn);
            for (int row = 0; row < size; row++)
            {
                newState[row][column] = row < filteredColumn.length ? filteredColumn[row] : 0;
            }
        }

        // if the new state is the same as the current state, then the move is invalid
        if (totalMerged == 0 && Arrays.deepEquals(data, newState))
        {
            return MoveResult.invalid();
        } else {
            setState(newState);
        }

        return new MoveResult(totalMerged, totalMergedValue);
    }

    private MoveResult squashEast()
    {
        int totalMerged = 0;
        int totalMergedValue = 0;
        int[][] newState = new int[size][size];

        for (int row = 0; row < size; row++)
        {
            // Strip zeros
            int[] filteredColumn = stripZeros(data[row]);
            if (filteredColumn.length == 0) continue;

            MoveResult partial = merge(filteredColumn, false);
            totalMerged += partial.mergeCount;
            totalMergedValue += partial.mergeValue;

            filteredColumn = stripZeros(filteredColumn);
            for (int column = size - 1, i = filteredColumn.length - 1; column >= 0; column--, i--)
            {
                newState[row][column] = i >= 0 ? filteredColumn[i] : 0;
            }
        }

        // if the new state is the same as the current state, then the move is invalid
        if (totalMerged == 0 && Arrays.deepEquals(data, newState))
        {
            return MoveResult.invalid();
        } else {
            setState(newState);
        }

        return new MoveResult(totalMerged, totalMergedValue);
    }

    private MoveResult squashSouth()
    {
        int totalMerged = 0;
        int totalMergedValue = 0;
        int[][] newState = new int[size][size];

        for (int column = 0; column < size; column++)
        {
            // Strip zeros
            int[] filteredColumn = stripZeros(slice(column));
            if (filteredColumn.length == 0) continue;

            MoveResult partial = merge(filteredColumn, false);
            totalMerged += partial.mergeCount;
            totalMergedValue += partial.mergeValue;

            filteredColumn = stripZeros(filteredColumn);
            for (int row = size - 1, i = filteredColumn.length - 1; row >= 0; row--, i--)
            {
                newState[row][column] = i >= 0 ? filteredColumn[i] : 0;
            }
        }

        // if the new state is the same as the current state, then the move is invalid
        if (totalMerged == 0 && Arrays.deepEquals(data, newState))
        {
            return MoveResult.invalid();
        } else {
            setState(newState);
        }

        return new MoveResult(totalMerged, totalMergedValue);
    }

    private MoveResult squashWest()
    {
        int totalMerged = 0;
        int totalMergedValue = 0;
        int[][] newState = new int[size][size];

        for (int row = 0; row < size; row++)
        {
            // Strip zeros
            int[] filteredColumn = stripZeros(data[row]);
            if (filteredColumn.length == 0) continue;

            MoveResult partial = merge(filteredColumn, true);
            totalMerged += partial.mergeCount;
            totalMergedValue += partial.mergeValue;

            filteredColumn = stripZeros(filteredColumn);
            for (int column = 0; column < size; column++)
            {
                newState[row][column] = column < filteredColumn.length ? filteredColumn[column] : 0;
            }
        }

        // if the new state is the same as the current state, then the move is invalid
        if (totalMerged == 0 && Arrays.deepEquals(data, newState))
        {
            return MoveResult.invalid();
        } else {
            setState(newState);
        }

        return new MoveResult(totalMerged, totalMergedValue);
    }

    /**
     * Sets the state of the game board by copying the specified data
     * @param s the Data to copy. Must be a SIZE x SIZE array
     */
    protected void setState(int[][] s)
    {
        if(s.length != size || Arrays.stream(s).filter((r) -> r.length != size).count() > 0)
        {
            throw new IllegalArgumentException("The specified array does not match the game board size");
        }

        for (int i = 0; i < size; i++)
        {
            System.arraycopy(s[i], 0, data[i], 0, size);
        }
    }

    /**
     * Places a random 2 or 4 on the game board at a free space.
     * If there are no more free spaces, this method returns false.
     *
     * @return true if a value was able to be placed
     */
    public boolean placeRandom()
    {
        int initialValue = rng.next() >= FOUR_THRESHOLD ? 4 : 2;

        ArrayList<int[]> freeCells = getFreeCells();

        if (freeCells.isEmpty())
        {
            return false;
        }

        int[] cell = freeCells.get((int) (rng.next() * freeCells.size()));
        int freeRow = cell[0];
        int freeColumn = cell[1];

        data[freeRow][freeColumn] = initialValue;

        return true;
    }

    public ArrayList<int[]> getFreeCells()
    {
        ArrayList<int[]> results = new ArrayList<>();

        for (int row = 0; row < size; row++)
        {
            for (int col=0; col < size; col++)
            {
                if(data[row][col] <= 0)
                {
                    results.add(new int[]{row, col});
                }
            }
        }

        return results;
    }

    /**
     * Extract a vertical slice from the board at a given column
     *
     * @param column The column to slice
     * @return a vertical slice of the board at the specified column
     */
    private int[] slice(int column)
    {
        int[] result = new int[size];

        for (int i = 0; i < size; i++)
        {
            result[i] = data[i][column];
        }

        return result;
    }

    /**
     * Determines whether or not the board is a "winning" board
     *
     * @return true iff the board contains a cell with the value <code>WIN_CONDITION_VALUE</code>
     */
    public boolean isWon()
    {
        // We're using Java 8, might as well make use of streams
        // Basically, this gets the maximum value in a 2d array
        return Arrays.stream(data).flatMapToInt(Arrays::stream).max().getAsInt() >= WIN_CONDITION_VALUE;
    }

    /**
     * @return the raw data held in the game board
     */
    public int[][] getData()
    {
        return data;
    }
}
