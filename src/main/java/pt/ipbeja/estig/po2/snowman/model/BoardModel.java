package pt.ipbeja.estig.po2.snowman.model;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.gui.GameView;

import java.util.ArrayList;
import java.util.List;


public class BoardModel extends Application {

    private  Monster monster;
    private List<List<PositionContent>> board;
    private List<Snowball> snowballs;
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

        int newRow = currentRow;
        int newCol = currentCol;

        switch (direction){
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
        }

        //Colocar Validação VALIDAÇÃO COLOCA DIA 16 A NOITE IGUAL AO GROWSNOWBALL
        monster.setPosition(newRow, newCol);
        System.out.println("Movido para linha " + newRow + " e coluna " + newCol);
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

        if (this.monster.getCol() == 3){
            System.out.println("Monster move to the Left passed!");
        }else {
            System.out.println("Monster dont move to the Left!");
        }
    }

    void testCreateAverageSnowball(){
        Snowball snowball = new Snowball(3, 3, SnowballType.SMALL);
        snowballs = new ArrayList<>();
        snowballs.add(snowball);
        board.get(3).set(4, PositionContent.SNOW);
        growSnowballIfOnSnow(snowball, Direction.RIGHT);

        if(snowball.getType() == SnowballType.AVERAGE){
            System.out.println("testCreateAverageSnowball passed!");
        }else{
            System.out.println("testCreateAverageSnowball failed");
        }
    }

    void testCreateBigSnowball(){
        Snowball snowball = new Snowball(4, 4, SnowballType.AVERAGE);
        snowballs = new ArrayList<>();
        snowballs.add(snowball);
        board.get(4).set(3, PositionContent.SNOW);
        growSnowballIfOnSnow(snowball, Direction.LEFT);

        if(snowball.getType() == SnowballType.BIG){
            System.out.println("testCreateBigSnowball passed!");
        }else{
            System.out.println("testCreateBigSnowball failed!");
        }

    }

    void testMaintainBigSnowball(){

    }

    void testAverageBigSnowman(){

    }

    void testCompleteSnowman(){

    }
}