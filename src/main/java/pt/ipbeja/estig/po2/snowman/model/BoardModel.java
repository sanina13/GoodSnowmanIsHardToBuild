package pt.ipbeja.estig.po2.snowman.model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.gui.GameView;

import java.util.ArrayList;
import java.util.List;


public class BoardModel extends Application {

    private  Monster monster;
    private List<List<PositionContent>> board;
    public static final int ROWS = 10;
    public static final int COLS = 10;

    public BoardModel() {
        this.monster = new Monster(4, 4);
        // JavaFX vai usar este construtor vazio, por isso não inicializamos aqui o modelo
    }

    public void initModel() {
        board = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < COLS; j++) {
                row.add(PositionContent.NO_SNOW);
            }
            board.add(row);
        }

        // Exemplo
        board.get(2).set(3, PositionContent.SNOWMAN);
        board.get(4).set(5, PositionContent.BLOCK);
        board.get(1).set(1, PositionContent.SNOW);
    }

    public void moveMonster(Direction direction){
        int currentRow = monster.getRow();
        int currentCol = monster.getCol();

        int newRow = currentRow;
        int newCol = currentCol;

        switch (direction){
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
        }

        //Colocar Validação
        monster.setPosition(newRow, newCol);
        System.out.println("Movido para linha " + newRow + " e coluna " + newCol);
    }

    public List<List<PositionContent>> getBoard() {
        return board;
    }


    @Override
    public void start(Stage primaryStage) {
        initModel(); // inicializa o tabuleiro
        GameView view = new GameView(this);
        Scene scene = new Scene(view.createContent());

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> moveMonster(Direction.UP);
                case DOWN -> moveMonster(Direction.DOWN);
                case LEFT -> moveMonster(Direction.LEFT);
                case RIGHT -> moveMonster(Direction.RIGHT);
                default -> {} // ignora outras teclas
            }
        });

        primaryStage.setTitle("Snowman Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // chama JavaFX
    }

    //Tests

    void testMonsterToTheLeft(){

    }

    void testCreateAverageSnowball(){

    }

    void testCreateBigSnowball(){

    }

    void testMaintainBigSnowball(){

    }

    void testAverageBigSnowman(){

    }

    void testCompleteSnowman(){

    }
}