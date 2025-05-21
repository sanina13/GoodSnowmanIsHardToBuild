package pt.ipbeja.estig.po2.snowman.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pt.ipbeja.estig.po2.snowman.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.model.PositionContent;
import pt.ipbeja.estig.po2.snowman.model.Snowball;
import pt.ipbeja.estig.po2.snowman.model.SnowballType;

import java.util.List;
import java.util.Objects;

public class GameView {
    private static final int TILE_SIZE = 40;
    private final BoardModel boardModel;
    private TextArea movesArea;
    private VBox layout;
    private GridPane grid;
    private final Image snowImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/snow.jpg")));
    private final Image grassImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/grass.jpg")));
    private final Image blockImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/block.jpg")));
    private final Image snowmanImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/boneco de neve.jpg")));
    private final Image monsterImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/monstro.jpg")));
    private final Image smallBallImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/snowballSmall.jpg")));
    private final Image avgBallImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/snowballAverage.jpg")));
    private final Image bigBallImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/snowballBig.png")));
    private final Image bigAvgBallImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/snowballBigAverage.png")));



    public GameView(BoardModel boardModel) {
        this.boardModel = boardModel;
    }


    public GridPane createGridPane() {
        GridPane grid = new GridPane();
        List<List<PositionContent>> board = boardModel.getBoard();

        for (int row = 0; row < board.size(); row++) {
            List<PositionContent> line = board.get(row);
            for (int col = 0; col < line.size(); col++) {
                StackPane cell = createCell(row, col, line.get(col));
                grid.add(cell, col, row);
            }
        }

        return grid;
    }

    private StackPane createCell(int row, int col, PositionContent content) {
        ImageView background = switch (content) {
            case NO_SNOW -> new ImageView(grassImage);
            case SNOW -> new ImageView(snowImage);
            case BLOCK -> new ImageView(blockImage);
            case SNOWMAN -> new ImageView(snowmanImage);
        };
        background.setFitWidth(TILE_SIZE);
        background.setFitHeight(TILE_SIZE);

        StackPane cell = new StackPane();
        cell.getChildren().add(background);

        addSnowballIfPresent(cell, row, col);
        addMonsterIfPresent(cell, row, col);

        return cell;
    }

    private void addSnowballIfPresent(StackPane cell, int row, int col) {
        Snowball snowball = boardModel.getSnowballAt(row, col);
        if (snowball != null) {
            Image ballImg = switch (snowball.getType()) {
                case SMALL -> smallBallImage;
                case AVERAGE -> avgBallImage;
                case BIG -> bigBallImage;
                case BIG_AVERAGE -> bigAvgBallImage;
                default -> null;
            };
            ImageView ballView = new ImageView(ballImg);
            ballView.setFitWidth(TILE_SIZE);
            ballView.setFitHeight(TILE_SIZE);
            cell.getChildren().add(ballView);
        }
    }

    private void addMonsterIfPresent(StackPane cell, int row, int col) {
        if (boardModel.getMonster().getRow() == row && boardModel.getMonster().getCol() == col) {
            ImageView monsterView = new ImageView(monsterImage);
            monsterView.setFitWidth(TILE_SIZE);
            monsterView.setFitHeight(TILE_SIZE);
            cell.getChildren().add(monsterView);
        }
    }


    public TextArea createMovesArea(){
        this.movesArea = new TextArea();
        this.movesArea.setEditable(false);
        this.movesArea.setPrefRowCount(5);
        this.movesArea.setPrefColumnCount(10);
        return this.movesArea;
    }

    public VBox createContent(){
        this.grid = createGridPane();
        createMovesArea();
        this.layout = new VBox(10, this.grid, this.movesArea);
        return this.layout;
    }

    public void updateMovementsArea(){
        movesArea.clear();
        List<String> movsList = boardModel.getMovementsHistory();
        for(String mov : movsList){
            movesArea.appendText(mov + "\n");
        }
    }

    public void refreshBoard(){
        this.layout.getChildren().remove(this.grid);
        this.grid = createGridPane();
        this.layout.getChildren().add(0, this.grid);
    }

    public void gameWon(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You win!");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations! You’ve built a complete snowman!");
        alert.showAndWait();
    }

}
