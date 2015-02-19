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
    public static final char RESTART = 'r';

    /** The game board associated with the current game */
    private Board gameBoard;

    private boolean displayStats = true;
    private boolean lost = false;
    private boolean notifiedWon = false;

    private int score = 0;
    private int totalMoves = 0;
    private int totalMerged = 0;
    private int totalMergedThisTurn = 0;

    public static void main(String[] args){
        try{
            Game g = new Game();

            // TODO: --WASD for WASD/IJKL controls, otherwise ULDR
            new OptionsParser().add("seed", ((s) -> {
                try{
                   g.changeSeed(s.replaceAll("^[\"\']+", "").replaceAll("[\"\']+$", "").replaceAll("\\s",""));
                } catch(Randomizer.InvalidSeedException e) {
                    e.printStackTrace();
                }
            })).add("size", (i) -> {
                try{
                    g.resize(Integer.parseInt(i));
                } catch(Randomizer.InvalidSeedException e) {
                    e.printStackTrace();
                }
            }).addSwitch("endless", (() -> g.notifiedWon = true)).parse(args);

            g.run();
        } catch(Randomizer.InvalidSeedException e) {
            System.err.println("Unable to set seed");
            e.printStackTrace();
        }
    }



    public Game() throws Randomizer.InvalidSeedException{
        gameBoard = new Board();
    }

    public Game(String seed) throws Randomizer.InvalidSeedException{
        gameBoard = new Board(seed);
    }

    private void resize(int size) throws Randomizer.InvalidSeedException{
        gameBoard = new Board(size, gameBoard.getSeed());
    }

    private void changeSeed(String seed) throws Randomizer.InvalidSeedException{
        gameBoard = new Board(gameBoard.getSize(), seed);

        resetStats();
    }

    private void resetStats(){
        totalMoves = totalMerged = 0;
        lost = false;
        notifiedWon = false;
    }

    public void run(){
        try(Scanner s = new Scanner(System.in)){

            // If we run into a problem, store the message here and warn the user the next cycle
            String warning = "";

            while(!lost){
                // Clear Screen on compatible terminals
                clearScreen();

                printBoard();
                System.out.println("");

                // Print a warning if we have one
                if(!warning.isEmpty()){
                    System.out.println(warning);
                    warning = "";
                }

                // Prompt for and read the next key
                System.out.print((notifiedWon ? "[ENDLESS] " : "") + "2048 (h for help)> ");
                String input = s.next().toLowerCase();
                char code = input.charAt(0);

                if(input.length() > 1) {
                    warning += "WARNING: More than one character entered. Ignoring everything except the first\n";
                }

                if(code == QUIT){
                    break;
                }else if(code==HELP){
                    clearScreen();
                    printHelp();
                    s.next();
                    continue;
                }else if(code==RESTART){
                    clearScreen();
                    gameBoard = new Board();
                    resetStats();
                    continue;
                }

                try{
                    Direction d = Direction.parse(code);
                    MoveResult turn = gameBoard.squash(d);

                    totalMergedThisTurn = turn.mergeCount;

                    if(totalMergedThisTurn < 0){
                        //We've tried to move in an invalid direction
                        totalMergedThisTurn = 0;
                        warning += "Invalid Move, try again!";
                        continue;
                    }

                    totalMerged += totalMergedThisTurn;
                    score += turn.mergeValue;

                    totalMoves++;

                    if(!gameBoard.placeRandom()){
                        lost = true;
                        doGameLost();
                    }

                    if(!notifiedWon){
                        if(gameBoard.isWon()){
                            doVictory();
                            s.next();
                            notifiedWon = true;
                        }
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

    private void doVictory(){
        System.out.println("You've won after " + totalMoves + "turns!\n" +
                "The game is now in \"Endless Mode\", Try and get to 4096! Thank you for playing!");
    }

    private void doGameLost(){
        if(!lost){
            throw new IllegalStateException("Game is still going!");
        }

        System.out.print("You Lost the Game after " + totalMoves + " turns!");
        System.exit(-1);

    }

    /**
     * Clears the screen on most compatible terminals
     */
    private void clearScreen(){
        System.out.print("\u001b[2J");
        System.out.flush();
    }

    /**
     * Prints the help menu
     */
    public void printHelp(){
        System.out.println("2048 (A clone of \"3's\"), Developed by Nathan Lowe for EECS 1510\n");
        System.out.println("Squash tiles in one of four directions. Tiles that match will be merged, tiles closest to the destination will be merged first!");
        System.out.println("Keys:");
        System.out.println("\th: This Help Menu");
        System.out.println("\tr: Restart the Game");
        System.out.println("\tq: Quit\n");
        System.out.println("\t\t\t\tUP (i or w)");
        System.out.println("\tLEFT (a or h)\t\t\tRIGHT(d or l)");
        System.out.println("\t\t\t\tDOWN (s or k)\n");

        System.out.println("Press Any Key and then Enter to return to the game...");
    }

    /**
     * Prints the game board
     */
    public void printBoard(){
        int width = gameBoard.getSize();
        
        for(int row=0; row<width; row++){
            if(row == 0){
                System.out.println(buildRowDivider(RowDividerType.TOP));
            }else{
                System.out.println(buildRowDivider(RowDividerType.INTERMEDIATE));
            }

            System.out.print('\u2551');
            for(int column=0; column < width; column++){
                int element = gameBoard.getElement(row,column);
                //TODO: Don't do fixed column sizes. Once the game goes into endless mode
                //TODO: some columns have the potential to be more than 4 digits
                System.out.print(element > 0 ? String.format(" %4d ", element) : "      ");
                if(column < width-1){
                    System.out.print('\u2551');
                }
            }
            System.out.print('\u2551');

            if(displayStats){
                switch(row){
                    case 0:  System.out.println("\t\tScore: " + score + "\tTotal Moves: " + totalMoves); break;
                    case 1:  System.out.println("\t\tTotal Merged Cells: " + totalMerged); break;
                    case 2:  System.out.println("\t\tTotal Merged This Turn: " + totalMergedThisTurn); break;
                    case 3:  System.out.println("\t\tSeed: " + gameBoard.getSeed().substring(0, 4) + " " + gameBoard.getSeed().substring(4, 8)); break;
                    default: System.out.println();
                }
            }else{
                System.out.println();
            }

            if(row+1 == width){
                System.out.println(buildRowDivider(RowDividerType.BOTTOM));
            }
        }
    }
    
    public enum RowDividerType{
        TOP,
        INTERMEDIATE,
        BOTTOM
    }

    /**
     * Creates the string that represents a given row divider
     *
     * @param t the Type of divider to generate
     * @return
     */
    public String buildRowDivider(RowDividerType t){
        StringBuilder topRow = new StringBuilder();
        topRow.append(t == RowDividerType.TOP ? '\u2554' : t == RowDividerType.INTERMEDIATE ? '\u2560' : '\u255A');
        
        int width = gameBoard.getSize();
        
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
