package eecs1510.Game;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nathan on 2/12/15
 *
 * Holds the elements for the game. All game boards are square.
 */
public class Board {

    /** The default size of the game board */
    public static final int DEFAULT_SIZE = 4;
    /** What value determines when the game has been "won" */
    public static final int WIN_CONDITION_VALUE = 2048;
    /** Any random number above this will generate a four */
    public static final double FOUR_THRESHOLD = 0.4;

    private final int size;
    private final int[][] data;

    public Board(){
        this(DEFAULT_SIZE);
    }

    public Board(int size){
        this.size = size;

        data = new int[size][size];
        placeRandom();
    }

    /**
     * @return the Size of the game board (which is square)
     */
    public int getSize(){
        return size;
    }

    /**
     * Gets the element at the specified row and column
     *
     * @param row
     * @param column
     * @return
     */
    public int getElement(int row, int column){
        return data[row][column];
    }

    /**
     * Squashes all elements in the specified direction. Note that this does NOT
     * generate a random tile and add it to the game board. For that, you need
     * to call <code>placeRandom()</code>
     *
     * @param d The direction to squash elements in
     * @return The total number of squashed tiles
     */
    public int squash(Direction d){
        System.out.println("Squashing " + d);
        switch(d){
            case NORTH:{ return squashNorth(); }
            case SOUTH:{ return squashSouth(); }
            case EAST: { return squashEast();  }
            case WEST: { return squashWest();  }
            default: return 0;
        }
    }

    /**
     * Merges like items in the specified direction
     *
     * @param source
     * @param LTR
     * @return the total number of merged elements
     */
    private int merge(int[] source, boolean LTR){
        int totalMerged = 0;

        if(LTR){
            for(int i=0; i<source.length-1; i++){
                if(source[i] == source[i+1]){
                    source[i] *= 2;
                    source[i+1] = 0;
                    totalMerged++;
                }
            }
        }else{
            for(int i=source.length-1; i >= 1; i--){
                if(source[i] == source[i-1]){
                    source[i] *= 2;
                    source[i-1] = 0;
                    totalMerged++;
                }
            }
        }

        return totalMerged;
    }

    private int[] stripZeros(int[] arr){
        return Arrays.stream(arr).filter((v) -> v > 0).toArray();
    }

    private int squashNorth(){
        int totalMerged = 0;

        for(int column = 0; column < size; column++){
            int[] filteredColumn = stripZeros(slice(column));
            if(filteredColumn.length == 0) continue;

            totalMerged += merge(filteredColumn, true);
            filteredColumn = stripZeros(filteredColumn);
            for(int row = 0; row < size; row++){
                data[row][column] = row < filteredColumn.length ? filteredColumn[row] : 0;
            }
        }

        return totalMerged;
    }

    private int squashEast(){
        int totalMerged = 0;

        for(int row = 0; row < size; row++){
            // Strip zeros
            int[] filteredColumn = stripZeros(data[row]);
            if(filteredColumn.length == 0) continue;

            totalMerged += merge(filteredColumn, false);
            filteredColumn = stripZeros(filteredColumn);
            for(int column = size-1, i=filteredColumn.length-1; column >= 0; column--, i--){
                data[row][column] = i >= 0 ? filteredColumn[i] : 0;
            }
        }

        return totalMerged;
    }

    private int squashSouth(){
        int totalMerged = 0;

        for(int column = 0; column < size; column++){
            // Strip zeros
            int[] filteredColumn = stripZeros(slice(column));
            if(filteredColumn.length == 0) continue;

            totalMerged += merge(filteredColumn, false);
            filteredColumn = stripZeros(filteredColumn);
            for(int row = size-1, i = filteredColumn.length-1; row >= 0; row--, i--){
                data[row][column] = i >= 0 ? filteredColumn[i] : 0;
            }
        }

        return totalMerged;
    }

    private int squashWest(){
        int totalMerged = 0;

        for(int row = 0; row < size; row++){
            // Strip zeros
            int[] filteredColumn = stripZeros(data[row]);
            if(filteredColumn.length == 0) continue;

            totalMerged += merge(filteredColumn, true);
            filteredColumn = stripZeros(filteredColumn);
            for(int column = 0; column < size; column++){
                data[row][column] = column < filteredColumn.length ? filteredColumn[column] : 0;
            }
        }

        return totalMerged;
    }

    /**
     * Places a random 2 or 4 on the game board at a free space.
     * If there are no more free spaces, this method returns false.
     *
     * @return true if a value was able to be placed
     */
    public boolean placeRandom(){
        int initialValue = Math.random() >= FOUR_THRESHOLD ? 4 : 2;

        int[] freeRows = getFreeRows();

        if(freeRows.length == 0){
            return false;
        }

        int freeRow = freeRows[(int)(Math.random() * freeRows.length)];

        int[] freeColumns = getFreeColumns(freeRow);
        int freeColumn = getFreeColumns(freeRow)[(int)(Math.random() * freeColumns.length)];

        System.out.println("Placing " + initialValue + " in " + freeRow + "," + freeColumn);

        assert(data[freeRow][freeColumn] == 0);
        data[freeRow][freeColumn] = initialValue;

        return true;
    }

    /**
     * Determines what rows have free cells
     *
     * @return An array of indices pointing to rows with free cells
     */
    public int[] getFreeRows(){
        ArrayList<Integer> rows = new ArrayList<>();

        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                if(data[i][j] <= 0){
                    rows.add(i);
                    break;
                }
            }
        }

        int[] results = new int[rows.size()];
        for(int i=0; i<rows.size(); i++){
            results[i] = rows.get(i);
        }

        return results;
    }

    /**
     * Determines what columns in a given row have free cells
     *
     * @param row The index of the row to check
     *
     * @return An array of indices pointing to free columns in a given row
     */
    public int[] getFreeColumns(int row){
        ArrayList<Integer> columns = new ArrayList<>();

        for(int j=0; j<size; j++){
            if(data[row][j] <= 0){
                columns.add(j);
            }
        }

        int[] results = new int[columns.size()];
        for(int i=0; i<columns.size(); i++){
            results[i] = columns.get(i);
        }

        return results;
    }

    /**
     * Extract a vertical slice from the board at a given column
     *
     * @param column The column to slice
     *
     * @return a vertical slice of the board at the specified column
     */
    private int[] slice(int column){
        int[] result = new int[size];

        for(int i=0; i<size; i++){
            result[i] = data[i][column];
        }

        return result;
    }

    /**
     * Determines whether or not the board is a "winning" board
     *
     * @return true iff the board contains a cell with the value <code>WIN_CONDITION_VALUE</code>
     */
    public boolean isWon(){
        //We're using Java 8, might as well make use of it
        return Arrays.stream(data).flatMapToInt(Arrays::stream).max().getAsInt() >= WIN_CONDITION_VALUE;
    }
}
