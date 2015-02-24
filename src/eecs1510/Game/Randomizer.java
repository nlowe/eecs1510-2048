package eecs1510.Game;

import java.util.Random;

/**
 * Created by nathan on 2/17/15
 *
 * A seeded random-number generator. Seeds consist of the numbers 0-9 and capital letters,
 * are 8 characters long, and may or may not have a space between the 4th and 5th characters.
 *
 * For simplicities sake, certain numbers are counted as letters. See <code>asciiSimplify()</code>
 * for details
 */
public class Randomizer {

    /** The valid characters that may make up a seed */
    public static final String VALID_SEED_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public class InvalidSeedException extends Exception {
        public InvalidSeedException(String msg) {
            super(msg);
        }
    }

    public final String seed;
    private final Random source;

    public Randomizer(String seed) throws InvalidSeedException {
        if (!validSeed(seed)) {
            throw new InvalidSeedException("'" + seed + "' is not a valid seed!");
        }

        long iv = 0L;
        this.seed = seed;
        for (char c : seed.toCharArray()) {
            iv += asciiSimplify(c);
            iv <<= 8;
        }

        this.source = new Random(iv);
    }

    /**
     * Determines whether a seed is valid. All whitespace characters should
     * be stripped from the seed prior to calling this method
     *
     * @param seed
     * @return
     */
    public static boolean validSeed(String seed) {
        if (seed.length() != 8) {
            return false;
        }

        for (char c : seed.toCharArray()) {
            if (VALID_SEED_CHARS.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a random seed
     *
     * @return
     */
    public static String randomSeed() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(VALID_SEED_CHARS.charAt((int) (Math.random() * VALID_SEED_CHARS.length())));
        }
        return sb.toString();
    }


    /**
     * Simplifies a seed. '0' is the same as 'O', '3' is the same as 'E', and '1' is the same as 'L'
     * @param c
     * @return
     */
    private static char asciiSimplify(char c) {
        if (c == '0' || c == 'O') {
            return '0';
        } else if (c == '3' || c == 'E') {
            return '3';
        } else if (c == '1' || c == 'L') {
            return '1';
        }

        return c;
    }

    /**
     * @return a random number between 0 and 1 exclusive
     */
    public double next() {
        return source.nextDouble();
    }

}
