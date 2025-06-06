/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 *
 * This class is responsible for loading a level from a text file and converting it
 * into a BoardModel object to be used in the Snowman game.
 */
package pt.ipbeja.estig.po2.snowman.model;

import java.io.File;
import java.util.*;


public class LevelLoader {
    /**
     * Loads a level from a file and returns a BoardModel representing the game state.
     *
     * The file must follow a specific format:
     * - First line: number of rows and columns
     * - Next line: monster starting row and column
     * - Following lines: grid of PositionContent values
     * - Remaining lines (optional): snowballs prefixed with "BALL", followed by type and position
     *
     * @param file the file containing the level configuration
     * @return a BoardModel object representing the loaded level
     * @throws RuntimeException if there is an error loading or parsing the file
     */
    public static BoardModel loadFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            int monsterRow = scanner.nextInt();
            int monsterCol = scanner.nextInt();
            scanner.nextLine();

            List<List<PositionContent>> board = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                List<PositionContent> row = new ArrayList<>();
                for (int j = 0; j < cols; j++) {
                    row.add(PositionContent.valueOf(scanner.next()));
                }
                board.add(row);
            }

            Monster monster = new Monster(monsterRow, monsterCol);
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

            return new BoardModel(board, monster, snowballs);
        } catch (Exception e) {
            throw new RuntimeException("Erro a carregar o nível: " + e.getMessage());
        }
    }
}
