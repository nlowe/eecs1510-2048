package eecs1510.Game;

import java.util.Scanner;

/**
 * Created by nathan on 2/12/15
 *
 * A clone of the game 2048 (which is itself a clone of the game "3's") for EECS 1510
 */
public class Game {

    /* ======= Special control keys ======= */
    public static final char QUIT = 'q';
    public static final char HELP = 'h';
    public static final char HELP_ALT = '?';
    public static final char RESTART = 'r';
    public static final char UNDO = 'z';
    public static final char REDO = 'y';
    /*===================================== */

    /** The game board associated with the current game */
    private Board gameBoard;

    private boolean clearScreenEachTurn = false;
    private boolean displayStats = true;
    private boolean lost = false;
    private boolean notifiedWon = false;

    private int score = 0;
    private int totalMoves = 0;
    private int totalMerged = 0;
    private int totalMergedThisTurn = 0;

    /**
     * Main entry point for the program. Parses command line arguments and starts a game.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Game g = new Game();

            new OptionsParser().add("seed", "Start the game with the specified seed", ((s) -> {
                try {
                    // Remove leading and trailing quotes and strip whitespace
                    g.changeSeed(s.replaceAll("^[\"\']+", "").replaceAll("[\"\']+$", "").replaceAll("\\s", ""));
                } catch(Randomizer.InvalidSeedException e) {
                    e.printStackTrace();
                }
            })).add("size", "Initialize the board with the specified size", (i) -> {
                try {
                    g.resize(Integer.parseInt(i));
                } catch(Randomizer.InvalidSeedException e) {
                    e.printStackTrace();
                }
            }).addSwitch("endless", "Start the game in endless mode", (() -> g.notifiedWon = true))
              .addSwitch("WASD", "Use WASD/IJKL instead of ULDR/8462 for movement", () -> Direction.useLegacyInput(false))
              .addSwitch("noStats", "Don't display stats", () -> g.displayStats = false)
              .addSwitch("clear", "Attempt to clear the display each turn", () -> g.clearScreenEachTurn = true)
              .parse(args);

            g.run();
        } catch(Randomizer.InvalidSeedException e) {
            System.err.println("Unable to set seed");
            e.printStackTrace();
        }
    }

    public Game() throws Randomizer.InvalidSeedException {
        gameBoard = new Board();
    }

    public Game(String seed) throws Randomizer.InvalidSeedException {
        gameBoard = new Board(seed);
    }

    /**
     * Re-initializes the game board with the specified size
     *
     * @param size the new size of the game board
     * @throws Randomizer.InvalidSeedException
     */
    private void resize(int size) throws Randomizer.InvalidSeedException {
        gameBoard = new Board(size, gameBoard.getSeed());

        resetStats();
    }

    /**
     * Re-initializes the game board with the specified seed
     * @param seed
     * @throws Randomizer.InvalidSeedException
     */
    private void changeSeed(String seed) throws Randomizer.InvalidSeedException {
        gameBoard = new Board(gameBoard.getSize(), seed);

        resetStats();
    }

    /**
     * Resets all tracked statistics
     */
    private void resetStats() {
        totalMoves = totalMerged = 0;
        lost = false;
        notifiedWon = false;
    }

    /**
     * The main game loop
     */
    public void run() {
        try(Scanner s = new Scanner(System.in)) {

            // If we run into a problem, store the message here and warn the user the next cycle
            String warning = "";

            while(!lost) {
                // Clear Screen on compatible terminals
                clearScreen();

                printBoard();
                System.out.println("");

                // Print a warning if we have one
                if (!warning.isEmpty()) {
                    System.out.println(warning);
                    warning = "";
                }

                // Prompt for and read the next key
                System.out.print((notifiedWon ? "[ENDLESS] " : "") + "2048 (h for help)> ");
                String input = s.next().toLowerCase();
                char code = input.charAt(0);

                if (input.length() > 1) {
                    warning += "WARNING: More than one character entered. Ignoring everything except the first\n";
                }

                if (code == QUIT) {
                    System.out.println("Quitting after " + totalMoves + " moves. You managed to merge " +
                                                          totalMerged + " cells for a score of " + score);
                    break;
                } else if (code == HELP || code == HELP_ALT) {
                    clearScreen();
                    printInGameHelp();
                    s.next();
                    continue;
                } else if (code == RESTART) {
                    clearScreen();
                    gameBoard = new Board();
                    resetStats();
                    continue;
                } else if (code == UNDO) {
                    warning += "Undoing your last move? Cheater";
                    gameBoard.undo();
                    continue;
                } else if (code == REDO) {
                    warning += "Redoing your last move? Cheater";
                    gameBoard.redo();
                    continue;
                }

                try {
                    Direction d = Direction.parse(code);
                    MoveResult turn = gameBoard.squash(d);

                    totalMergedThisTurn = turn.mergeCount;

                    if (turn.isInvalid()) {
                        //We've tried to move in an invalid direction
                        totalMergedThisTurn = 0;
                        warning += "Invalid Move, try again!";
                        continue;
                    }

                    // Update Statistics
                    totalMerged += totalMergedThisTurn;
                    score += turn.mergeValue;
                    totalMoves++;

                    if (!gameBoard.placeRandom()) {
                        lost = true;
                        printLostNotification();
                    }

                    // Check if we've won the game
                    if (!notifiedWon) {
                        if (gameBoard.isWon()) {
                            printVictoryNotification();
                            s.next();
                        }
                    }
                } catch(IllegalArgumentException e) {
                    warning += "WARNING: " + e.getMessage() + "\n";
                }
            }
        } catch(Exception e) {
            System.err.println("Something went wrong!: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Notifies the user that they have won
     */
    private void printVictoryNotification() {
        if(!gameBoard.isWon()) return;

        System.out.println("You've won after " + totalMoves + "turns!\n" +
                "The game is now in \"Endless Mode\", Try and get to 4096! Thank you for playing!");

        notifiedWon = true;
    }

    /**
     * Notifies the user that they have lost
     */
    private void printLostNotification() {
        if (!lost) {
            throw new IllegalStateException("Game is still going!");
        }

        // If the user has already won the game and is in endless mode, they haven't technically "lost"
        if(gameBoard.isWon()){
            System.out.print("The board became full after " + totalMoves + " turns! You managed to merge " +
                                                             totalMerged + " cells for a total score of " + score);
            System.exit(0);
        }else{
            System.out.print("You Lost the Game after " + totalMoves + " turns! You managed to merge " +
                    totalMerged + " cells for a total score of " + score);
            System.exit(-1);
        }
        
    }

    /**
     * Clears the screen on most compatible terminals
     */
    private void clearScreen() {
        if(!clearScreenEachTurn){
            return;
        }
        System.out.print("\u001b[2J");
        System.out.flush();
    }

    /**
     * Prints the in-game help menu
     */
    public void printInGameHelp() {
        System.out.println("2048 (A clone of \"3's\"), Developed by Nathan Lowe for EECS 1510\n");
        System.out.println("Squash tiles in one of four directions. Tiles that match will be merged,\n" +
                "tiles closest to the destination will be merged first! Try to get a tile to 2048!\n");
        System.out.println("Keys:");
        System.out.println("\th: This Help Menu");
        System.out.println("\tr: Restart the Game");
        System.out.println("\tz: Undo the previous move");
        System.out.println("\ty: Redo the previously undone move");
        System.out.println("\tq: Quit\n");
        System.out.println("\t\t\t\tUP " + keyString(Direction.getCharactersFor(Direction.NORTH)));
        System.out.println("\tLEFT " + keyString(Direction.getCharactersFor(Direction.WEST)) +
                                        "\t\t\tRIGHT " + keyString(Direction.getCharactersFor(Direction.EAST)));
        System.out.println("\t\t\t\tDOWN " + keyString(Direction.getCharactersFor(Direction.SOUTH)) + "\n");

        System.out.println("Press Any Key and then Enter to return to the game...");
    }

    /**
     * Converts a collection of characters to a formatted string:
     *
     * ('keys[0]', 'keys[1]', ... or 'keys[keys.length-1]')
     *
     * @param keys
     * @return
     */
    private String keyString(char[] keys){
        StringBuilder sb = new StringBuilder().append("(").append(keys[0]);
        for(int i=1; i<keys.length-1; i++){
            sb.append(", ").append(keys[i]);
        }
        sb.append(" or ").append(keys[keys.length-1]).append(")");

        return sb.toString();
    }

    /**
     * Prints the game board
     */
    public void printBoard() {
        int width = gameBoard.getSize();

        for (int row = 0; row < width; row++) {
            if (row == 0) {
                System.out.println(buildRowDivider(RowDividerType.TOP));
            } else {
                System.out.println(buildRowDivider(RowDividerType.INTERMEDIATE));
            }

            System.out.print('\u2551');
            for (int column = 0; column < width; column++) {
                int element = gameBoard.getElement(row, column);
                //TODO: Don't do fixed column sizes. Once the game goes into endless mode
                //TODO: some columns have the potential to be more than 4 digits
                System.out.print(element > 0 ? String.format(" %4d ", element) : "      ");
                if (column < width - 1) {
                    System.out.print('\u2551');
                }
            }
            System.out.print('\u2551');

            if (displayStats) {
                switch(row) {
                    case 0:
                        System.out.println("\t\tScore: " + score + "\tTotal Moves: " + totalMoves);
                        break;
                    case 1:
                        System.out.println("\t\tTotal Merged Cells: " + totalMerged);
                        break;
                    case 2:
                        System.out.println("\t\tTotal Merged This Turn: " + totalMergedThisTurn);
                        break;
                    case 3:
                        System.out.println("\t\tSeed: " + gameBoard.getSeed().substring(0, 4) + " " + gameBoard.getSeed().substring(4, 8));
                        break;
                    default:
                        System.out.println();
                }
            } else {
                System.out.println();
            }

            if (row + 1 == width) {
                System.out.println(buildRowDivider(RowDividerType.BOTTOM));
            }
        }
    }

    public enum RowDividerType {
            /** The divider for the top of the game board: '╔══════╦══════╦══════╦══════╗' */ TOP,
        /** The divider in-between rows in the game board: '╠══════╬══════╬══════╬══════╣' */ INTERMEDIATE,
         /** The Divider for the bottom of the game board: '╚══════╩══════╩══════╩══════╝' */ BOTTOM
    }

    /**
     * Creates the string that represents a given row divider
     *
     * @param t the Type of divider to generate
     * @return
     */
    public String buildRowDivider(RowDividerType t) {
        StringBuilder topRow = new StringBuilder();
        topRow.append(t == RowDividerType.TOP ? '\u2554' : t == RowDividerType.INTERMEDIATE ? '\u2560' : '\u255A');

        int width = gameBoard.getSize();
        for (int i = 0; i < width; i++) {
            topRow.append("\u2550\u2550\u2550\u2550\u2550\u2550");
            if (i < width - 1) {
                topRow.append(t == RowDividerType.TOP ? '\u2566' : t == RowDividerType.INTERMEDIATE ? '\u256C' : '\u2569');
            }
        }

        topRow.append(t == RowDividerType.TOP ? '\u2557' : t == RowDividerType.INTERMEDIATE ? '\u2563' : '\u255D');

        return topRow.toString();
    }

}
