package eecs1510.Game;

import java.util.ArrayList;

/**
 * Created by nathan on 2/12/15
 *
 * Holds the elements for the game. All game boards are square.
 */
public class Board {

    /** The default size of the game board */
    public static final int DEFAULT_SIZE = 4;

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

    public int getSize(){
        return size;
    }

    public int getElement(int row, int column){
        return data[row][column];
    }

    /**
     * Squases all elements in the specified direction. Note that this does NOT
     * generate a random tile and add it to the game board. For that, you need
     * to call <code>placeRandom()</code>
     *
     * @param d The direction to squash elements in
     */
    public void squash(Direction d){
        System.out.println("Squashing " + d);
        switch(d){
            case NORTH:{ squashNorth(); break; }
            case SOUTH:{ squashSouth(); break; }
            case EAST: { squashEast();  break; }
            case WEST: { squashWest();  break; }
        }
    }

    /**
     * For each column, combines like elements top down once and then
     * aligns the row with the top of the board
     */
    private void squashNorth(){
        for(int column = 0, pointer=0; column < size; column++){
            for(int row = 0; row < size; row++){
                if(data[row][column] > 0){
                    if(row > 0 && data[row][column] == data[pointer][column]){
                        data[pointer][column] *= 2;
                    } else {
                        data[pointer++][column] = data[row][column];
                    }

                    if(row > 0){
                        data[row][column] = 0;
                    }
                }
            }

            pointer = 0;
        }
    }

    private void squashEast(){
        for(int row = 0, pointer=size-1; row < size; row++){
            for(int column = size-1; column >= 0; column--){
                if(data[row][column] > 0){
                    if(column > 0 && data[row][column] == data[column][pointer]){
                        data[column][pointer] *= 2;
                    } else {
                        data[column][pointer--] = data[row][column];
                    }

                    if(column < size-1){
                        data[row][column] = 0;
                    }
                }
            }

            pointer = size-1;
        }
    }

    private void squashSouth(){
        for(int column = 0, pointer=size-1; column < size; column++){
            for(int row = size-1; row >= 0; row--){
                if(data[row][column] > 0){
                    if(row > 0 && data[row][column] == data[pointer][column]){
                        data[pointer][column] *= 2;
                    } else {
                        data[pointer--][column] = data[row][column];
                    }

                    if(row < size-1){
                        data[row][column] = 0;
                    }
                }
            }

            pointer = size-1;
        }
    }

    private void squashWest(){
        for(int row = 0, pointer=0; row < size; row++){
            for(int column = 0; column < size; column++){
                if(data[row][column] > 0){
                    if(column > 0 && data[row][column] == data[row][pointer]){
                        data[row][pointer] *= 2;
                    } else {
                        data[row][pointer++] = data[row][column];
                    }

                    if(column > 0){
                        data[row][column] = 0;
                    }
                }
            }

            pointer = 0;
        }
    }

    public void placeRandom(){
        int initialValue = Math.random() >= FOUR_THRESHOLD ? 4 : 2;

        int[] freeRows = getFreeRows();
        int freeRow = freeRows[(int)(Math.random() * freeRows.length)];

        int[] freeColumns = getFreeColumns(freeRow);
        int freeColumn = getFreeColumns(freeRow)[(int)(Math.random() * freeColumns.length)];

        System.out.println("Placing " + initialValue + " in " + freeRow + "," + freeColumn);

        data[freeRow][freeColumn] = initialValue;
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

}
