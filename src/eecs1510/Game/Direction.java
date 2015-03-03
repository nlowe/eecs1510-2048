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

    private static final char[][] NORTH_CHARS = {{'u', '8'}, {'w', 'i'}};
    private static final char[][] SOUTH_CHARS = {{'d', '2'}, {'s', 'k'}};
    private static final char[][] EAST_CHARS = {{'r', '6'}, {'d', 'l'}};
    private static final char[][] WEST_CHARS = {{'l', '4'}, {'a', 'j'}};

    /**
     * For a given character, returns a direction vector corresponding to that character
     *
     * @param c
     * @return
     * @throws IllegalArgumentException if the specified character is not in the lookup list
     */
    public static Direction parse(char c) throws IllegalArgumentException {

        for (char d : NORTH_CHARS[legacyInput ? 0 : 1]) {
            if (c == d) {
                return NORTH;
            }
        }

        for (char d : SOUTH_CHARS[legacyInput ? 0 : 1]) {
            if (c == d) {
                return SOUTH;
            }
        }

        for (char d : EAST_CHARS[legacyInput ? 0 : 1]) {
            if (c == d) {
                return EAST;
            }
        }

        for (char d : WEST_CHARS[legacyInput ? 0 : 1]) {
            if (c == d) {
                return WEST;
            }
        }

        throw new IllegalArgumentException("Unknown Direction for code " + c);
    }

    public static char[] getCharactersFor(Direction d){
        switch(d){
            case NORTH: return NORTH_CHARS[legacyInput ? 0: 1];
            case SOUTH: return SOUTH_CHARS[legacyInput ? 0 : 1];
            case EAST: return EAST_CHARS[legacyInput ? 0 : 1];
            case WEST: return WEST_CHARS[legacyInput ? 0 : 1];
            default: return new char[]{};
        }
    }

    /**
     * The original assignment called for UDLR instead of WSAD or IKJL for movement keys.
     * This method is called to override the original keys with more sensible ones if specified.
     *
     * @param b whether or not to use legacy input keys
     */
    public static void useLegacyInput(boolean b) {
        legacyInput = b;
    }
}
