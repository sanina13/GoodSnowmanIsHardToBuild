package pt.ipbeja.estig.po2.snowman.model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.gui.GameView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BoardModel extends Application {
    private GameView view;
    private Monster monster;
    private List<List<PositionContent>> board;
    private List<Snowball> snowballs;
    private List<String> movementsHistory;
    public static final int ROWS = 10;
    public static final int COLS = 10;

    public BoardModel() {
        this.monster = new Monster(4, 4);
        // JavaFX vai usar este construtor vazio, por isso não inicializamos aqui o modelo
    }

    public void setView(GameView view){
        this.view = view;
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
        snowballs.add(new Snowball(5, 6, SnowballType.AVERAGE));
        snowballs.add(new Snowball(5, 7, SnowballType.BIG_AVERAGE));
        snowballs.add(new Snowball(3, 7, SnowballType.BIG));
    }

    //Monster Methods

    public void moveMonster(Direction direction){
        int currentRow = monster.getRow();
        int currentCol = monster.getCol();
        int[] next = calculateNextPositon(currentRow, currentCol, direction);
        int newRow = next[0];
        int newCol = next[1];

        if (checkConditons(newRow, newCol)) return;

        Snowball snowball = getSnowballAt(newRow, newCol);

        if (snowball == null){
            if(board.get(newRow).get(newCol) == PositionContent.SNOW){
                snowballs.add(new Snowball(newRow, newCol, SnowballType.SMALL));
                return;
            }else{
                moveMonsterTo(currentRow, currentCol, newRow, newCol);
            }
        } else{
            // se Tiver snowball
            int[] after = calculateNextPositon(newRow, newCol, direction);
            int afterRow = after[0] , afterCol = after[1];

            if(checkConditons(afterRow, afterCol)) return;

            Snowball snowballCheck = getSnowballAt(afterRow, afterCol); //--

            if (snowballCheck == null){
                snowball.setPosition(afterRow, afterCol);
                moveMonsterTo(currentRow, currentCol, newRow, newCol);
            }else {
                tryStackSnowball(snowball, direction);
                if(tryStackSnowball(snowball, direction)) moveMonsterTo(currentRow, currentCol, newRow, newCol);
            }
        }
    }

    private void moveMonsterTo(int currentRow, int currentCol, int newRow, int newCol) {
        monster.setPosition(newRow, newCol);
        char firstLetter = (char) ('A' + currentCol); // ASCI Se currentCol = 3, faz-se: 'A' + 3 = 65 + 3 = 68
        char secondLetter = (char) ('A' + newCol);
        String moveResume = "(" + currentRow + ", " + firstLetter + ") -> (" + newRow + ", " + secondLetter + ")";
        movementsHistory.add(moveResume);
        // colocar os movimentos na interface
        if (view != null) {
            view.updateMovementsArea();
            view.refreshBoard();

        }
    }


    //Snowball Methods

    public void growSnowballIfOnSnow(Snowball snowball, Direction direction){
        int[] next = calculateNextPositon(snowball.getRow(), snowball.getCol(), direction);
        int newRow = next[0];
        int newCol = next[1];

        if(!isInsideBoard(newRow, newCol)) return;

        if(board.get(newRow).get(newCol) == PositionContent.SNOW){
            switch (snowball.getType()){
                case SMALL -> snowball.setType(SnowballType.AVERAGE);
                case AVERAGE -> snowball.setType(SnowballType.BIG);
                default -> {}
            }
            snowball.setPosition(newRow, newCol);
        }

    }

    public Snowball getSnowballAt(int row, int col) {
        for (Snowball s : snowballs) {
            if (s.getRow() == row && s.getCol() == col) {
                return s;
            }
        }
        return null;
    }

    public boolean tryStackSnowball(Snowball mover, Direction direction){
        int[] next = calculateNextPositon(mover.getRow(), mover.getCol(), direction);
        int newRow = next[0];
        int newCol = next[1];

        if(!isInsideBoard(newRow, newCol)) return false;

        Snowball targetSnowball = getSnowballAt(newRow, newCol);

        if(targetSnowball != null){
            switch (mover.getType()){
                case AVERAGE -> {
                    if(targetSnowball.getType() == SnowballType.BIG){
                        targetSnowball.setType(SnowballType.BIG_AVERAGE);
                        snowballs.remove(mover);
                    }
                    return true;
                }
                case SMALL -> {
                    if(targetSnowball.getType() == SnowballType.BIG_AVERAGE){
                        board.get(newRow).set(newCol, PositionContent.SNOWMAN);
                        view.gameWon();
                        saveSnowmanToFile(newRow, newCol);
                        snowballs.remove(targetSnowball);
                        snowballs.remove(mover);
                        return true;
                    }
                }

                default -> {
                    // Outros casos que não provoca nada...
                }
            }
        }
        return false;
    }

    //Helpful methods
    private int[] calculateNextPositon(int row, int col, Direction direction){
        return switch (direction) {
            case UP -> new int[]{row - 1, col};
            case DOWN -> new int[]{row + 1, col};
            case RIGHT -> new int[]{row, col + 1};
            case LEFT -> new int[]{row, col - 1};
        };
    }

    private boolean isInsideBoard(int row, int col){
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    private boolean checkConditons(int row, int col){
        if(!(isInsideBoard(row, col))) return true;
        return board.get(row).get(col) == PositionContent.BLOCK || board.get(row).get(col) == PositionContent.SNOWMAN;
    }

    private void saveSnowmanToFile(int row, int col) {
        String fileName = generateFileName();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writeHeader(writer);
            writeBoard(writer);
            writeMovements(writer);
            writeSummary(writer, row, col);
            System.out.println("Ficheiro '" + fileName + "' guardado com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao guardar o ficheiro: " + e.getMessage());
        }
    }

    private String generateFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDate = now.format(formatter);
        return "snowman" + formattedDate + ".txt";
    }

    private void writeHeader(BufferedWriter writer) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        writer.write("Snowman Save Log - " + timestamp);
        writer.newLine();
        writer.newLine();
    }

    private void writeBoard(BufferedWriter writer) throws IOException {
        writer.write("Mapa:");
        writer.newLine();
        for (List<PositionContent> line : board) {
            for (PositionContent pc : line) {
                writer.write(pc.toString() + " ");
            }
            writer.newLine();
        }
        writer.newLine();
    }

    private void writeMovements(BufferedWriter writer) throws IOException {
        writer.write("Movimentos:");
        writer.newLine();
        for (String move : movementsHistory) {
            writer.write(move);
            writer.newLine();
        }
        writer.newLine();
    }

    private void writeSummary(BufferedWriter writer, int row, int col) throws IOException {
        writer.write("Total de movimentos: " + movementsHistory.size());
        writer.newLine();
        char colLetter = (char) ('A' + col);
        writer.write("Snowman criado em: (" + row + ", " + colLetter + ")");
        writer.newLine();
    }


    //getters
    public List<List<PositionContent>> getBoard() {
        return board;
    }

    public  List<Snowball> getSnowballs(){
        return snowballs;
    }

    public List<String> getMovementsHistory(){
        return movementsHistory;
    }

    public Monster getMonster(){
        return this.monster;
    }

    //Strat methods
    @Override
    public void start(Stage primaryStage) {
        initModel(); // inicializa o tabuleiro

        //Test methods
        //testMonsterToTheLeft();
//       testCreateAverageSnowball();
//       testCreateBigSnowball();
//       testMaintainBigSnowball();
//       testAverageBigSnowman();
//       testCompleteSnowman();

        GameView view = new GameView(this);
        this.setView(view);
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
        scene.getRoot().requestFocus();
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