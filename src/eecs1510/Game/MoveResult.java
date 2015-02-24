package eecs1510.Game;

/**
 * Created by nathan on 2/19/15
 *
 * A data structure created for the result of a move for a given row,
 * column, or direction in general.
 *
 * Keeps track of the total number of merged cells and the value that they represent.
 */
public class MoveResult {

    /** The total number of merged cells for a given move */
    public final int mergeCount;
    /** The total value of all merged cells for a given move */
    public final int mergeValue;

    public MoveResult(int mergeCount, int mergeValue) {
        this.mergeCount = mergeCount;
        this.mergeValue = mergeValue;
    }

    /**
     * @return a new MoveResult for an invalid move
     */
    public static MoveResult invalid() {
        return new MoveResult(-1, -1);
    }

    /**
     * A move is invalid if all cells are already aligned in the direction they
     * are trying to be merged and no two adjacent cells have the same value
     *
     * @return whether or not the result of a move was an invalid move
     */
    public boolean isInvalid() {
        return mergeCount == -1 && mergeValue == -1;
    }

}
