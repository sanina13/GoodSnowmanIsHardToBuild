/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 *
 * This class is responsible for loading a level from a text file and converting it
 * into a BoardModel object to be used in the Snowman game.
 */
package pt.ipbeja.estig.po2.snowman.model;

import java.io.File;
import java.util.*;

/**
 * LevelLoader is responsible for loading game levels from a text file. HELP WITH LLMS
 */
public class LevelLoader {

    /**
     * Loads a BoardModel from a given file.
     * The file must contain board size, monster position, and snowball data.
     *
     * @param file the file to load the level from
     * @return a new BoardModel initialized with the loaded data
     */
    public static BoardModel loadFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            int monsterRow = scanner.nextInt();
            int monsterCol = scanner.nextInt();
            scanner.nextLine();

            List<List<PositionContent>> board = readBoard(scanner, rows, cols);
            Monster monster = new Monster(monsterRow, monsterCol);
            List<Snowball> snowballs = readSnowballs(scanner);

            return new BoardModel(board, monster, snowballs);
        } catch (Exception e) {
            throw new RuntimeException("Erro a carregar o nível: " + e.getMessage());
        }
    }

    /**
     * Reads the board content (PositionContent values) from the file.
     *
     * @param scanner the Scanner pointing to the file
     * @param rows the number of rows in the board
     * @param cols the number of columns in the board
     * @return the board as a list of lists of PositionContent
     */
    private static List<List<PositionContent>> readBoard(Scanner scanner, int rows, int cols) {
        List<List<PositionContent>> board = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(PositionContent.valueOf(scanner.next()));
            }
            board.add(row);
        }
        return board;
    }

    /**
     * Reads all snowballs from the file (after the board).
     * Each snowball is prefixed with "BALL", followed by type and coordinates.
     *
     * @param scanner the Scanner pointing to the file
     * @return a list of Snowball objects
     */
    private static List<Snowball> readSnowballs(Scanner scanner) {
        List<Snowball> snowballs = new ArrayList<>();
        while (scanner.hasNext()) {
            String marker = scanner.next();
            if (marker.equals("BALL")) {
                SnowballType type = SnowballType.valueOf(scanner.next());
                int r = scanner.nextInt();
                int c = scanner.nextInt();
                snowballs.add(new Snowball(r, c, type));
            }
        }
        return snowballs;
    }
}
