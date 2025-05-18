package pt.ipbeja.estig.po2.snowman.model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.gui.GameView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BoardModel extends Application {

    private  Monster monster;
    private List<List<PositionContent>> board;
    private List<Snowball> snowballs;
    private List<String> movementsHistory;
    public static final int ROWS = 10;
    public static final int COLS = 10;

    public BoardModel() {
        this.monster = new Monster(4, 4);
        // JavaFX vai usar este construtor vazio, por isso não inicializamos aqui o modelo
    }

    public void initModel() {

        board = new ArrayList<>();

        //cria Lista de Historico de movimentos
        movementsHistory = new ArrayList<>();

        for (int i = 0; i < ROWS; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < COLS; j++) {
                row.add(PositionContent.NO_SNOW);
            }
            board.add(row);
        }

        // Exemplo de pos inicial
        board.get(2).set(3, PositionContent.SNOWMAN);
        board.get(4).set(5, PositionContent.BLOCK);
        board.get(1).set(1, PositionContent.SNOW);

        // Iniciar snowballs
        snowballs = new ArrayList<>();
        snowballs.add(new Snowball(3, 3, SnowballType.SMALL));
        snowballs.add(new Snowball(5, 5, SnowballType.AVERAGE));
    }


    public void moveMonster(Direction direction){
        int currentRow = monster.getRow();
        int currentCol = monster.getCol();
        char firstLetter = (char) ('A' + currentCol); // ASCI Se currentCol = 3, faz-se: 'A' + 3 = 65 + 3 = 68
        String moveResume = "";


        int newRow = currentRow;
        int newCol = currentCol;

        switch (direction){
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
        }

        char secondLetter = (char) ('A' + newCol);

        if(newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS){
            monster.setPosition(newRow, newCol);
            moveResume = "(" + currentRow  + ", " + firstLetter + ") -> (" + newRow + ", " + secondLetter + ")";
            movementsHistory.add(moveResume);
        }

    }

    public void growSnowballIfOnSnow(Snowball snowball, Direction direction){
        int newRow = snowball.getRow();
        int newCol = snowball.getCol();

        switch (direction){
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
        }

        if(newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS){
            if(board.get(newRow).get(newCol) == PositionContent.SNOW){
                switch (snowball.getType()){
                    case SMALL -> snowball.setType(SnowballType.AVERAGE);
                    case AVERAGE -> snowball.setType(SnowballType.BIG);
                    default -> {}
                }
                snowball.setPosition(newRow, newCol);
            }
        }
    }

    private Snowball getSnowballAt(int row, int col) {
        for (Snowball s : snowballs) {
            if (s.getRow() == row && s.getCol() == col) {
                return s;
            }
        }
        return null;
    }


    public void tryStackSnowball(Snowball mover, Direction direction){
        int newRow = mover.getRow();
        int newCol = mover.getCol();


        switch (direction){
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
        }

        Snowball targetSnowball = getSnowballAt(newRow, newCol);

        if(newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < 10){
            if(targetSnowball != null){
                switch (mover.getType()){
                    case AVERAGE -> {
                        if(targetSnowball.getType() == SnowballType.BIG){
                            targetSnowball.setType(SnowballType.BIG_AVERAGE);
                            snowballs.remove(mover);
                        }
                    }
                    case SMALL -> {
                        if(targetSnowball.getType() == SnowballType.BIG_AVERAGE){
                            board.get(newRow).set(newCol, PositionContent.SNOWMAN);
                            snowballs.remove(targetSnowball);
                            snowballs.remove(mover);
                        }
                    }

                    default -> {
                        // Outros casos que não provoca nada...
                    }
                }
            }
        }

    }


    public List<List<PositionContent>> getBoard() {
        return board;
    }

    public  List<Snowball> getSnowballs(){
        return snowballs;
    }


    @Override
    public void start(Stage primaryStage) {
        initModel(); // inicializa o tabuleiro

        //Test methods
        testMonsterToTheLeft();
        testCreateAverageSnowball();
        testCreateBigSnowball();
        testMaintainBigSnowball();
        testAverageBigSnowman();
        testCompleteSnowman();

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
        this.monster.setPosition(4, 4);
        moveMonster(Direction.LEFT);

        assert this.monster.getCol() == 3 : "Monster didn't move to the left";
    }

    void testCreateAverageSnowball(){
        Snowball snowball = new Snowball(3, 3, SnowballType.SMALL);
        snowballs = new ArrayList<>();
        snowballs.add(snowball);
        board.get(3).set(4, PositionContent.SNOW);
        growSnowballIfOnSnow(snowball, Direction.RIGHT);

        assert  snowball.getType() == SnowballType.AVERAGE : "Expected Average got " + snowball.getType();
    }

    void testCreateBigSnowball(){
        Snowball snowball = new Snowball(4, 4, SnowballType.AVERAGE);
        snowballs = new ArrayList<>();
        snowballs.add(snowball);
        board.get(4).set(3, PositionContent.SNOW);
        growSnowballIfOnSnow(snowball, Direction.LEFT);

        assert  snowball.getType() == SnowballType.BIG : "Expected Big got " + snowball.getType();
    }

    void testMaintainBigSnowball(){
        Snowball snowball = new Snowball(3, 3, SnowballType.BIG);
        snowballs = new ArrayList<>();
        snowballs.add(snowball);
        board.get(3).set(4, PositionContent.SNOW);
        growSnowballIfOnSnow(snowball, Direction.RIGHT);

        assert  snowball.getType() == SnowballType.BIG : "Expected Big got " + snowball.getType();
    }

    void testAverageBigSnowman(){
        Snowball snowballAvg = new Snowball(5, 5, SnowballType.AVERAGE);
        Snowball snowballBig = new Snowball(5, 6, SnowballType.BIG);

        snowballs = new ArrayList<>();
        snowballs.addAll(Arrays.asList(snowballAvg, snowballBig));

        tryStackSnowball(snowballAvg, Direction.RIGHT);

        assert snowballBig.getType() == SnowballType.BIG_AVERAGE : "Expected BIG_AVERAGE got " + snowballBig.getType();
    }

    void testCompleteSnowman(){
        Snowball snowballSmall = new Snowball(3, 3, SnowballType.SMALL);
        Snowball snowballBigAvg = new Snowball(3, 4, SnowballType.BIG_AVERAGE);

        snowballs = new ArrayList<>();
        snowballs.addAll(Arrays.asList(snowballSmall, snowballBigAvg));

        tryStackSnowball(snowballSmall, Direction.RIGHT);

        assert board.get(3).get(4) == PositionContent.SNOWMAN : "Expected Snowman got " + board.get(3).get(4);
    }
}