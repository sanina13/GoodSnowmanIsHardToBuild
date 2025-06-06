/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 *
 *This class was created to support Undo and Redo functionality.
 *It stores a copy of the board, snowballs, monster position, and movements history.
 */
package pt.ipbeja.estig.po2.snowman.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final List<List<PositionContent>> board;
    private final List<Snowball> snowballs;
    private final Monster monster;
    private final List<String> movementsHistory;

    /**
     * Constructs a GameState with deep copies of the provided data.
     *
     * @param board the current board state
     * @param snowballs the list of snowballs
     * @param monster the monster position
     * @param movementsHistory the movement history
     */
    public GameState(List<List<PositionContent>> board, List<Snowball> snowballs, Monster monster, List<String> movementsHistory) {
        this.board = deepCopyBoard(board);
        this.snowballs = deepCopySnowballs(snowballs);
        this.monster = deepCopyMonster(monster);
        this.movementsHistory = deepCopyMovementsHistory(movementsHistory);
    }

    /**
     * Creates a deep copy of the board.
     *
     * @param originalBoard the original board to copy
     * @return a deep copy of the board
     */
    private List<List<PositionContent>> deepCopyBoard(List<List<PositionContent>> originalBoard){
        List<List<PositionContent>> copiedBoard = new ArrayList<>();
        for(List<PositionContent> row : originalBoard) {
            List<PositionContent> newRow = new ArrayList<>(row);
            copiedBoard.add(newRow);
        }
        return copiedBoard;
    }

    /**
     * Creates a deep copy of the snowballs list.
     *
     * @param originalSnowballs the original snowballs
     * @return a deep copy of the snowballs
     */
    private List<Snowball> deepCopySnowballs(List<Snowball> originalSnowballs){
        List<Snowball> copiedSnowballs = new ArrayList<>();
        for(Snowball s : originalSnowballs){
            copiedSnowballs.add(new Snowball(s.getRow(), s.getCol(), s.getType()));
        }
        return copiedSnowballs;
    }

    /**
     * Creates a deep copy of the monster.
     *
     * @param originalMonster the original monster
     * @return a new Monster instance
     */
    private Monster deepCopyMonster(Monster originalMonster){
        return new Monster(originalMonster.getRow(), originalMonster.getCol());
    }

    /**
     * Creates a deep copy of the movement history.
     *
     * @param originalMovementsHistory the original history list
     * @return a deep copy of the history list
     */
    private List<String> deepCopyMovementsHistory(List<String> originalMovementsHistory){
        return new ArrayList<>(originalMovementsHistory);
    }

    /**
     * Gets a deep copy of the board.
     *
     * @return the copied board
     */
    public List<List<PositionContent>> getBoard() {
        return deepCopyBoard(this.board);
    }

    /**
     * Gets a deep copy of the snowballs list.
     *
     * @return the copied snowballs list
     */
    public List<Snowball> getSnowballs() {
        return deepCopySnowballs(this.snowballs);
    }

    /**
     * Gets a deep copy of the monster.
     *
     * @return a new Monster instance
     */
    public Monster getMonster() {
        return deepCopyMonster(this.monster);
    }

    /**
     * Gets a deep copy of the movement history.
     *
     * @return the copied movement history
     */
    public List<String> getMovementsHistory() {
        return deepCopyMovementsHistory(this.movementsHistory);
    }
}
