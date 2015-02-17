package eecs1510.Game;

import java.util.Random;

/**
 * Created by nathan on 2/17/15
 */
public class Randomizer {

    public static final String VALID_SEED_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public class InvalidSeedException extends Exception{
        public InvalidSeedException(String msg){
            super(msg);
        }
    }

    public final String seed;
    private final Random source;

    public Randomizer(String seed) throws InvalidSeedException{
        if(!validSeed(seed)){
            throw new InvalidSeedException("'" + seed + "' is not a valid seed!");
        }

        long iv = 0L;
        this.seed = seed;
        for(char c : seed.toCharArray()){
            iv += asciiSimplify(c);
            iv <<= 8;
        }

        this.source = new Random(iv);
    }

    public static boolean validSeed(String seed){
        if(seed.length() != 8){
            return false;
        }

        for(char c : seed.toCharArray()){
            if(VALID_SEED_CHARS.indexOf(c) == -1){
                return false;
            }
        }
        return true;
    }

    public static String randomSeed(){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<8; i++){
            sb.append(VALID_SEED_CHARS.charAt((int)(Math.random()*VALID_SEED_CHARS.length())));
        }
        return sb.toString();
    }


    private static char asciiSimplify(char c){
        if(c == '0' || c == 'O'){
            return '0';
        }else if(c == '3' || c == 'E'){
            return '3';
        }else if(c == '1' || c == 'L'){
            return '1';
        }

        return c;
    }

    public double next(){
        return source.nextDouble();
    }

}
