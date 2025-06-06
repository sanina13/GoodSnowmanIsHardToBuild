/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 */
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
import java.util.*;


public class BoardModel extends Application {
    private GameView view;
    private Monster monster;
    private String playerName;
    private final List<Score> topScores = new ArrayList<>();
    private List<List<PositionContent>> board;
    private List<Snowball> snowballs;
    private List<String> movementsHistory;
    public static final int ROWS = 10;
    public static final int COLS = 10;
    private static int currentLevel;
    private final Stack<GameState> undoStack = new Stack<>();
    private final Stack<GameState> redoStack = new Stack<>();


    /**
     * BoardModel method.
     */
    public BoardModel() {
       //
    }

    /**
     * BoardModel method.
     * @param board the board
     * @param monster the monster
     * @param snowballs the snowballs
     */
    public BoardModel(List<List<PositionContent>> board, Monster monster, List<Snowball> snowballs) {
        this.board = board;
        this.monster = monster;
        this.snowballs = snowballs;
        this.movementsHistory = new ArrayList<>();
    }


    /**
     * setView method.
     * @param view the view
     */
    public void setView(GameView view){
        this.view = view;
    }

    /**
     * loadLevel method.
     * @param level the level
     */
    public void loadLevel(int level){
        currentLevel = level;
        if (movementsHistory != null) movementsHistory.clear();
        if (board != null) board.clear();
        if (snowballs != null) snowballs.clear();
        switch (level){
            case 1 -> initLevel1();
            case 2 -> initLevel2();
        }
        if (view != null) {
            view.updateMovementsArea();
            view.refreshBoard();
        }
    }

    /**
     * initLevel1 method.
     */
    public void initLevel1() {
        board = new ArrayList<>();
        movementsHistory = new ArrayList<>();

        for (int i = 0; i < ROWS; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < COLS; j++) {
                row.add(PositionContent.NO_SNOW);
            }
            board.add(row);
        }

        board.get(4).set(5, PositionContent.BLOCK);
        board.get(7).set(3, PositionContent.SNOW);

        snowballs = new ArrayList<>();
        snowballs.add(new Snowball(3, 3, SnowballType.SMALL));
        snowballs.add(new Snowball(5, 6, SnowballType.AVERAGE));
        snowballs.add(new Snowball(5, 7, SnowballType.BIG_AVERAGE));
        snowballs.add(new Snowball(3, 7, SnowballType.BIG));

        this.monster = new Monster(4, 4);
    }

    /**
     * initLevel2 method.
     */
    public void initLevel2() {
        board = new ArrayList<>();

        movementsHistory = new ArrayList<>();

        for (int i = 0; i < ROWS; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < COLS; j++) {
                row.add(PositionContent.NO_SNOW);
            }
            board.add(row);
        }

        board.get(4).set(5, PositionContent.BLOCK);
        board.get(4).set(7, PositionContent.BLOCK);
        board.get(7).set(6, PositionContent.BLOCK);
        board.get(2).set(8, PositionContent.BLOCK);
        board.get(7).set(3, PositionContent.SNOW);

        snowballs = new ArrayList<>();
        snowballs.add(new Snowball(3, 3, SnowballType.SMALL));
        snowballs.add(new Snowball(5, 6, SnowballType.AVERAGE));
        snowballs.add(new Snowball(5, 7, SnowballType.BIG_AVERAGE));
        snowballs.add(new Snowball(3, 7, SnowballType.BIG));

        this.monster = new Monster(4, 4);
    }

    /**
     * changeLevel method.
     */
    public void changeLevel(){
        currentLevel = (currentLevel == 1) ? 2 : 1;
        loadLevel(currentLevel);
    }

    /**
     * replaceBoard method.
     * @param newBoard the newBoard
     */
    public void replaceBoard(BoardModel newBoard) {
        this.board = newBoard.getBoard();
        this.monster = newBoard.getMonster();
        this.snowballs = newBoard.snowballs;
        this.movementsHistory = new ArrayList<>();
        this.setPlayerName(newBoard.getPlayerName());
        if (view != null) {
            view.updateMovementsArea();
            view.refreshBoard();
        }
    }

    /**
     * moveMonster method.
     * @param direction the direction
     */
    public void moveMonster(Direction direction){
        int currentRow = monster.getRow();
        int currentCol = monster.getCol();
        undoStack.push(new GameState(this.board, this.snowballs, this.monster, this.movementsHistory));
        redoStack.clear();
        int[] next = calculateNextPositon(currentRow, currentCol, direction);
        int newRow = next[0];
        int newCol = next[1];

        if (isBlockedOrOutOfBounds(newRow, newCol)) return;

        Snowball snowball = getSnowballAt(newRow, newCol);

        if (snowball == null){
            if(board.get(newRow).get(newCol) == PositionContent.SNOW){
                snowballs.add(new Snowball(newRow, newCol, SnowballType.SMALL));
            }else{
                moveMonsterTo(currentRow, currentCol, newRow, newCol);
            }
        } else{
            handleSnowballPush(snowball, newRow, newCol, direction, currentRow, currentCol);
        }
    }

    /**
     * moveMonsterTo method.
     * @param currentRow the currentRow
     * @param currentCol the currentCol
     * @param newRow the newRow
     * @param newCol the newCol
     */
    private void moveMonsterTo(int currentRow, int currentCol, int newRow, int newCol) {
        monster.setPosition(newRow, newCol);
        char firstLetter = (char) ('A' + currentCol);
        char secondLetter = (char) ('A' + newCol);
        String moveResume = "(" + currentRow + ", " + firstLetter + ") -> (" + newRow + ", " + secondLetter + ")";
        movementsHistory.add(moveResume);
        if (view != null) {
            view.updateMovementsArea();
            view.refreshBoard();

        }
    }


    /**
     * growSnowballIfOnSnow method.
     * @param snowball the snowball
     * @param direction the direction
     */
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

    /**
     * getSnowballAt method.
     * @param row the row
     * @param col the col
     * @return the result as a Snowball
     */
    public Snowball getSnowballAt(int row, int col) {
        for (Snowball s : snowballs) {
            if (s.getRow() == row && s.getCol() == col) {
                return s;
            }
        }
        return null;
    }

    /**
     * tryStackSnowball method.
     * @param mover the mover
     * @param direction the direction
     * @return the result as a boolean
     */
    public boolean tryStackSnowball(Snowball mover, Direction direction) {
        int[] next = calculateNextPositon(mover.getRow(), mover.getCol(), direction);
        int newRow = next[0];
        int newCol = next[1];

        if (!isInsideBoard(newRow, newCol)) return false;

        Snowball target = getSnowballAt(newRow, newCol);
        if (target == null) return false;

        return switch (mover.getType()) {
            case AVERAGE -> tryStackAverage(mover, target);
            case SMALL -> tryStackSmall(newRow, newCol, mover, target);
            default -> false;
        };
    }

    /**
     * Attempts to stack an AVERAGE snowball on a BIG one to create a BIG_AVERAGE snowball.
     *
     * @param mover the snowball being moved
     * @param target the target snowball on the board
     * @return true if stacking was successful, false otherwise
     */
    private boolean tryStackAverage(Snowball mover, Snowball target) {
        if (target.getType() == SnowballType.BIG) {
            target.setType(SnowballType.BIG_AVERAGE);
            snowballs.remove(mover);
            return true;
        }
        return false;
    }

    /**
     * Attempts to stack a SMALL snowball on top of another snowball.
     * It can create AVERAGE_SMALL, BIG_SMALL, or a complete SNOWMAN.
     *
     * @param row the row where the target snowball is located
     * @param col the column where the target snowball is located
     * @param mover the SMALL snowball being moved
     * @param target the target snowball on the board
     * @return true if stacking was successful, false otherwise
     */
    private boolean tryStackSmall(int row, int col, Snowball mover, Snowball target) {
        switch (target.getType()) {
            case BIG_AVERAGE -> {
                registerScore();
                board.get(row).set(col, PositionContent.SNOWMAN);
                view.gameWon();
                saveSnowmanToFile(row, col);
                snowballs.remove(target);
                snowballs.remove(mover);
                return true;
            }
            case AVERAGE -> {
                target.setType(SnowballType.AVERAGE_SMALL);
                snowballs.remove(mover);
                return true;
            }
            case BIG -> {
                target.setType(SnowballType.BIG_SMALL);
                snowballs.remove(mover);
                return true;
            }
            default -> {
                return false;
            }
        }
    }


    /**
     * growSnowball method.
     * @param snowball the snowball
     * @param row the row
     * @param col the col
     */
    private void growSnowball(Snowball snowball, int row, int col){
        if(board.get(row).get(col) == PositionContent.SNOW){
            switch (snowball.getType()){
                case SMALL -> snowball.setType(SnowballType.AVERAGE);
                case AVERAGE -> snowball.setType(SnowballType.BIG);
                default -> {}
            }
        }
    }

    /**
     * separateIfComposite method.
     * @param snowball the snowball
     * @param row the row
     * @param col the col
     * @param direction the direction
     */
    private void separateIfComposite(Snowball snowball, int row, int col, Direction direction){
        int[] afterSplit = calculateNextPositon(row, col, direction);
        int splitRow = afterSplit[0], splitCol = afterSplit[1];

        if (!isInsideBoard(splitRow, splitCol)) return;

        SnowballType separatedType;
        SnowballType baseType;

        switch (snowball.getType()) {
            case BIG_SMALL -> {
                baseType = SnowballType.BIG;
                separatedType = SnowballType.SMALL;
            }
            case AVERAGE_SMALL -> {
                baseType = SnowballType.AVERAGE;
                separatedType = SnowballType.SMALL;
            }
            case BIG_AVERAGE -> {
                baseType = SnowballType.BIG;
                separatedType = SnowballType.AVERAGE;
            }
            default -> {
                return;
            }
        }

        Snowball separated = new Snowball(splitRow, splitCol, separatedType);
        Snowball target = getSnowballAt(splitRow, splitCol);

        boolean success;

        if (target == null) {
            snowballs.add(separated);
            success = true;
        } else {
            success = tryStackSnowballManual(separated, target);
        }

        if(success){
            snowball.setType(baseType);
        }
    }

    /**
     * tryStackSnowballManual method.
     * @param source the source
     * @param target the target
     * @return the result as a boolean
     */
    private boolean tryStackSnowballManual(Snowball source, Snowball target) {
        int newRow = target.getRow();
        int newCol = target.getCol();
        switch (source.getType()) {
            case SMALL -> {
                switch (target.getType()) {
                    case BIG -> {
                        target.setType(SnowballType.BIG_SMALL);
                        return true;
                    }
                    case AVERAGE -> {
                        target.setType(SnowballType.AVERAGE_SMALL);
                        return true;
                    }
                    case BIG_AVERAGE -> {
                        registerScore();
                        board.get(newRow).set(newCol, PositionContent.SNOWMAN);
                        view.gameWon();
                        saveSnowmanToFile(newRow, newCol);
                        snowballs.remove(target);
                        return true;
                    }
                }
            }
            case AVERAGE -> {
                if (target.getType() == SnowballType.BIG) {
                    target.setType(SnowballType.BIG_AVERAGE);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * handleSnowballPush method.
     * @param snowball the snowball
     * @param newRow the newRow
     * @param newCol the newCol
     * @param direction the direction
     * @param currentRow the currentRow
     * @param currentCol the currentCol
     */
    private void handleSnowballPush(Snowball snowball, int newRow, int newCol, Direction direction, int currentRow, int currentCol){
        separateIfComposite(snowball, newRow, newCol, direction);

        int[] after = calculateNextPositon(newRow, newCol, direction);
        int afterRow = after[0], afterCol = after[1];

        if (isBlockedOrOutOfBounds(afterRow, afterCol)) return;

        Snowball snowballCheck = getSnowballAt(afterRow, afterCol);

        growSnowball(snowball, afterRow, afterCol);

        if (snowballCheck == null) {
            snowball.setPosition(afterRow, afterCol);
            moveMonsterTo(currentRow, currentCol, newRow, newCol);
        } else {
            if (tryStackSnowball(snowball, direction)) {
                moveMonsterTo(currentRow, currentCol, newRow, newCol);
            }
        }
    }


    /**
     * calculateNextPositon method.
     * @param row the row
     * @param col the col
     * @param direction the direction
     * @return the result as a int[]
     */
    private int[] calculateNextPositon(int row, int col, Direction direction){
        return switch (direction) {
            case UP -> new int[]{row - 1, col};
            case DOWN -> new int[]{row + 1, col};
            case RIGHT -> new int[]{row, col + 1};
            case LEFT -> new int[]{row, col - 1};
        };
    }

    /**
     * isInsideBoard method.
     * @param row the row
     * @param col the col
     * @return the result as a boolean
     */
    private boolean isInsideBoard(int row, int col){
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    /**
     * isBlockedOrOutOfBounds method.
     * @param row the row
     * @param col the col
     * @return the result as a boolean
     */
    private boolean isBlockedOrOutOfBounds(int row, int col){
        if(!(isInsideBoard(row, col))) return true;
        return board.get(row).get(col) == PositionContent.BLOCK || board.get(row).get(col) == PositionContent.SNOWMAN;
    }


    /**
     * registerScore method.
     */
    private void registerScore() {
        if (playerName == null || playerName.isBlank() || movementsHistory == null) return;

        Score score = new Score(playerName, currentLevel, movementsHistory.size());
        topScores.add(score);
        topScores.sort(Comparator.naturalOrder());
        if (topScores.size() > 3) {
            topScores.remove(3);
        }

        if (view != null) {
            view.updateScoresArea();
        }
    }


    /**
     * undo method.
     */
    public void undo(){
        if(undoStack.isEmpty()){
            return;
        }
        redoStack.push(new GameState(board, snowballs, monster, movementsHistory));
        GameState state = undoStack.pop();

        board = state.getBoard();
        snowballs = state.getSnowballs();
        monster = state.getMonster();
        movementsHistory = state.getMovementsHistory();

        view.refreshBoard();
        view.updateMovementsArea();
    }

    /**
     * redo method.
     */
    public void redo(){
        if(redoStack.isEmpty()){
            return;
        }
        undoStack.push(new GameState(board, snowballs, monster, movementsHistory));
        GameState state = redoStack.pop();

        board = state.getBoard();
        snowballs = state.getSnowballs();
        monster = state.getMonster();
        movementsHistory = state.getMovementsHistory();

        view.refreshBoard();
        view.updateMovementsArea();
    }




    /**
     * saveSnowmanToFile method. HELP WITH LLMS
     * @param row the row
     * @param col the col
     */
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

    /**
     * generateFileName method. HELP WITH LLMS
     * @return the result as a String
     */
    private String generateFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDate = now.format(formatter);
        return "snowman" + formattedDate + ".txt";
    }

    /**
     * writeHeader method. HELP WITH LLMS
     * @param writer the writer
     */
    private void writeHeader(BufferedWriter writer) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        writer.write("Snowman Save Log - " + timestamp);
        writer.newLine();
        writer.newLine();
    }

    /**
     * writeBoard method.
     * @param writer the writer
     */
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

    /**
     * writeMovements method.
     * @param writer the writer
     */
    private void writeMovements(BufferedWriter writer) throws IOException {
        writer.write("Movimentos:");
        writer.newLine();
        for (String move : movementsHistory) {
            writer.write(move);
            writer.newLine();
        }
        writer.newLine();
    }

    /**
     * writeSummary method.
     * @param writer the writer
     * @param row the row
     * @param col the col
     */
    private void writeSummary(BufferedWriter writer, int row, int col) throws IOException {
        writer.write("Total de movimentos: " + movementsHistory.size());
        writer.newLine();
        char colLetter = (char) ('A' + col);
        writer.write("Snowman criado em: (" + row + ", " + colLetter + ")");
        writer.newLine();
    }


    //getters
    /**
     * getBoard method.
     * @return the result as a List<List<PositionContent>>
     */
    public List<List<PositionContent>> getBoard() {
        return board;
    }

    /**
     * getMovementsHistory method.
     * @return the result as a List<String>
     */
    public List<String> getMovementsHistory(){
        return movementsHistory;
    }

    /**
     * getMonster method.
     * @return the result as a Monster
     */
    public Monster getMonster(){
        return this.monster;
    }

    /**
     * getCurrentLevel method.
     * @return the result as a int
     */
    public int getCurrentLevel(){
        return currentLevel;
    }

    /**
     * getPlayerName method.
     * @return the result as a String
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * getTopScores method.
     * @return the result as a List<Score>
     */
    public List<Score> getTopScores() {
        return topScores;
    }


    /**
     * setPlayerName method.
     * @param name the name
     */
    public void setPlayerName(String name) {
        if (name == null || name.isBlank()) {
            this.playerName = "AAA";
        } else {
            this.playerName = name.trim().toUpperCase().substring(0, Math.min(3, name.length()));
        }
    }



    @Override
    public void start(Stage primaryStage) {
        currentLevel = 1;
        loadLevel(currentLevel);

        //Test methods
        //testMonsterToTheLeft();
        //testCreateAverageSnowball();
        //testCreateBigSnowball();
        //testMaintainBigSnowball();
        //testAverageBigSnowman();
        //testCompleteSnowman();

        GameView view = new GameView(this);
        this.setView(view);
        Scene scene = new Scene(view.createContent());

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> moveMonster(Direction.UP);
                case DOWN -> moveMonster(Direction.DOWN);
                case LEFT -> moveMonster(Direction.LEFT);
                case RIGHT -> moveMonster(Direction.RIGHT);
                case L -> changeLevel();
                default -> {}
            }
        });

        primaryStage.setTitle("Snowman Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getRoot().requestFocus();

        view.startBackgroundMusic();
    }

    /**
     * main method.
     * @param args the args
     */
    public static void main(String[] args) {
        launch(args);
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