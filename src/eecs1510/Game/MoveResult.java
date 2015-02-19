package eecs1510.Game;

/**
 * Created by nathan on 2/19/15
 */
public class MoveResult {

    public final int mergeCount;
    public final int mergeValue;

    public MoveResult(int mergeCount, int mergeValue) {
        this.mergeCount = mergeCount;
        this.mergeValue = mergeValue;
    }

    public static MoveResult invalid() {
        return new MoveResult(-1, -1);
    }

    public boolean isInvalid() {
        return mergeCount == -1 && mergeValue == -1;
    }

}
