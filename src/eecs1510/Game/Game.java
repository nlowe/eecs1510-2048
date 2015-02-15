package eecs1510.Game;

import java.util.Scanner;

/**
 * Created by nathan on 2/12/15
 *
 * A clone of the game 2048 (which is itself a clone of the game "3's" for EECS 1510
 */
public class Game {

    public static final char QUIT = 'q';
    public static final char HELP = 'h';

    private final Board gameBoard;

    private boolean lost = false;

    public static void main(String[] args){
        new Game().run();
    }

    public Game(){
        gameBoard = new Board();
    }

    public void run(){
        try(Scanner s = new Scanner(System.in)){

            String warning = "";

            while(!lost){
                // Clear Screen on compatible terminals
                clearScreen();

                printBoard(gameBoard);
                System.out.println("");

                if(!warning.isEmpty()){
                    System.out.println(warning);
                    warning = "";
                }

                System.out.print("2048 (h for help)> ");
                String input = s.next().toLowerCase();
                char code = input.charAt(0);

                if(input.length() > 1) {
                    warning += "WARNING: More than one character entered. Ignoring everything except the first\n";
                }

                if(code == QUIT){
                    break;
                } else if(code==HELP){
                    clearScreen();
                    printHelp();
                    s.next();
                    continue;
                }

                try{
                    Direction d = Direction.parse(code);
                    gameBoard.squash(d);
                    if(!gameBoard.placeRandom()){
                        lost = true;
                        doGameLost();
                    }
                }catch(IllegalArgumentException e){
                    warning += "WARNING: " + e.getMessage() + "\n";
                }
            }
        }catch(Exception e){
            System.err.println("Something went wrong!: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void doGameLost(){
        if(!lost){
            throw new IllegalStateException("Game is still going!");
        }

        System.out.print("You Lost the Game!");
        System.exit(-1);

    }

    private void clearScreen(){
        System.out.print("\u001b[2J");
        System.out.flush();
    }

    public void printHelp(){
        System.out.println("2048 (A clone of \"3's\"), Developed by Nathan Lowe for EECS 1510\n");
        System.out.println("Squash tiles in one of four directions. Tiles that match will be merged, tiles closest to the destination will be merged first!");
        System.out.println("Keys:");
        System.out.println("\th: This Help Menu");
        System.out.println("\tq: Quit\n");
        System.out.println("\t\t\t\tUP (i or w)");
        System.out.println("\tLEFT (a or h)\t\t\tRIGHT(d or l)");
        System.out.println("\t\t\t\tDOWN (s or k)\n");

        System.out.println("Press Any Key to return to the game...");
    }

    public void printBoard(Board b){
        
        //TODO: Stats / Help menu?
        
        int width = b.getSize();
        
        for(int row=0; row<width; row++){
            if(row == 0){
                System.out.println(buildRowDivider(b, RowDividerType.TOP));
            }else{
                System.out.println(buildRowDivider(b, RowDividerType.INTERMEDIATE));
            }

            System.out.print('\u2551');
            for(int column=0; column < width; column++){
                int element = b.getElement(row,column);
                System.out.print(element > 0 ? String.format(" %4d ", element) : "      ");
                if(column < width-1){
                    System.out.print('\u2551');
                }
            }
            System.out.println('\u2551');

            if(row+1 == width){
                System.out.println(buildRowDivider(b, RowDividerType.BOTTOM));
            }
        }
    }
    
    public enum RowDividerType{
        TOP,
        INTERMEDIATE,
        BOTTOM
    }
    
    public String buildRowDivider(Board b, RowDividerType t){
        StringBuilder topRow = new StringBuilder();
        topRow.append(t == RowDividerType.TOP ? '\u2554' : t == RowDividerType.INTERMEDIATE ? '\u2560' : '\u255A');
        
        int width = b.getSize();
        
        for(int i=0; i<width; i++){
            topRow.append("\u2550\u2550\u2550\u2550\u2550\u2550");
            if(i<width-1){
                topRow.append(t == RowDividerType.TOP ? '\u2566' : t == RowDividerType.INTERMEDIATE ? '\u256C' : '\u2569');
            }
        }

        topRow.append(t == RowDividerType.TOP ? '\u2557' : t == RowDividerType.INTERMEDIATE ? '\u2563' : '\u255D');
        
        return topRow.toString();
    }
    

}
