package eecs1510.Game;

/**
 * Created by nathan on 2/12/15
 */
public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    private static final char[] NORTH_CHARS = {'w','i'};
    private static final char[] SOUTH_CHARS = {'s','k'};
    private static final char[] EAST_CHARS = {'d', 'l'};
    private static final char[] WEST_CHARS = {'a', 'j'};

    public static Direction parse(char c) throws IllegalArgumentException{

        for(char d : NORTH_CHARS){
            if(c == d){
                return NORTH;
            }
        }

        for(char d : SOUTH_CHARS){
            if(c == d){
                return SOUTH;
            }
        }

        for(char d : EAST_CHARS){
            if(c == d){
                return EAST;
            }
        }

        for(char d : WEST_CHARS){
            if(c == d){
                return WEST;
            }
        }

        throw new IllegalArgumentException("Unknown Direction for code " + c);
    }
}
