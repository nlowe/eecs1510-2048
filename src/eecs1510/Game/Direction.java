package eecs1510.Game;

/**
 * Created by nathan on 2/12/15
 *
 * A direction vector. Use the convenience function <code>parse</code>
 * to obtain a direction vector for a given key
 */
public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    private static boolean legacyInput = true;

    private static final char[][] NORTH_CHARS = {{'u'},{'w', 'i'}};
    private static final char[][] SOUTH_CHARS = {{'d'},{'s', 'k'}};
    private static final char[][] EAST_CHARS  = {{'r'},{'d', 'l'}};
    private static final char[][] WEST_CHARS  = {{'l'},{'a', 'j'}};

    /**
     * For a given character, returns a direction vector corresponding to that character
     *
     * @param c
     * @return
     * @throws IllegalArgumentException if the specified character is not in the lookup list
     */
    public static Direction parse(char c) throws IllegalArgumentException{

        for(char d : NORTH_CHARS[legacyInput ? 0 : 1]){
            if(c == d){
                return NORTH;
            }
        }

        for(char d : SOUTH_CHARS[legacyInput ? 0 : 1]){
            if(c == d){
                return SOUTH;
            }
        }

        for(char d : EAST_CHARS[legacyInput ? 0 : 1]){
            if(c == d){
                return EAST;
            }
        }

        for(char d : WEST_CHARS[legacyInput ? 0 : 1]){
            if(c == d){
                return WEST;
            }
        }

        throw new IllegalArgumentException("Unknown Direction for code " + c);
    }

    public static void useLegacyInput(boolean b){
        legacyInput = b;
    }
}
