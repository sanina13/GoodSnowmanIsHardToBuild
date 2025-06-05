package pt.ipbeja.estig.po2.snowman.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final List<List<PositionContent>> board;
    private final List<Snowball> snowballs;
    private final Monster monster;
    private final List<String> movementsHistory;

    public GameState(List<List<PositionContent>> board, List<Snowball> snowballs, Monster monster, List<String> movementsHistory) {
        this.board = deepCopyBoard(board);
        this.snowballs = deepCopySnowballs(snowballs);
        this.monster = deepCopyMonster(monster);
        this.movementsHistory = deepCopyMovementsHistory(movementsHistory);
    }


    private List<List<PositionContent>> deepCopyBoard(List<List<PositionContent>> originalBoard){
        List<List<PositionContent>> copiedBoard = new ArrayList<>();

        for(List<PositionContent> row : originalBoard) {
            List<PositionContent> newRow = new ArrayList<>(row);
            copiedBoard.add(newRow);
        }

        return copiedBoard;
    }

    private  List<Snowball> deepCopySnowballs(List<Snowball> originalSnowballs){
        List<Snowball> copiedSnowballs = new ArrayList<>();

        for(Snowball s : originalSnowballs){
            copiedSnowballs.add(
                    new Snowball(s.getRow(), s.getCol(), s.getType())
            );
        }

        return copiedSnowballs;
    }

    private Monster deepCopyMonster(Monster originalMonster){
        return new Monster(originalMonster.getRow(), originalMonster.getCol());
    }

    private List<String> deepCopyMovementsHistory(List<String> originalMovementsHistory){
        return new ArrayList<>(originalMovementsHistory);
    }

    public List<List<PositionContent>> getBoard() {
        return deepCopyBoard(this.board);
    }

    public List<Snowball> getSnowballs() {
        return deepCopySnowballs(this.snowballs);
    }

    public Monster getMonster() {
        return deepCopyMonster(this.monster);
    }

    public List<String> getMovementsHistory() {
        return deepCopyMovementsHistory(this.movementsHistory);
    }
}
