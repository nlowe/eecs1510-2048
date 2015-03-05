package eecs1510.Game;

/**
 * Created by nathan on 3/5/15
 *
 * Represents a snapshot of the game at a point in time. Contains a copy
 * of the game board and the score, total number of moves, total number
 * of merged cells, and total number of cells merged for the most recent
 * turn before this snapshot was generated.
 */
public class GameState {

    public final int[][] board;
    public final int score;
    public final int totalMoves;
    public final int totalMerged;
    public final int totalMergedThisTurn;

    public GameState(int[][] board, int score, int totalMoves, int totalMerged, int totalMergedThisTurn){
        this.board = board;
        this.score = score;
        this.totalMoves = totalMoves;
        this.totalMerged = totalMerged;
        this.totalMergedThisTurn = totalMergedThisTurn;
    }

}
