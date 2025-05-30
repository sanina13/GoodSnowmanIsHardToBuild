package pt.ipbeja.estig.po2.snowman.gui;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
    private final Image snowImage         = loadImage("snow.png");
    private final Image grassImage        = loadImage("grass.png");
    private final Image blockImage        = loadImage("block.png");
    private final Image snowmanImage      = loadImage("boneco de neve.png");
    private final Image monsterImage      = loadImage("monster.png");
    private final Image smallBallImage    = loadImage("snowballSmall.png");
    private final Image avgBallImage      = loadImage("snowballAverage.png");
    private final Image bigBallImage      = loadImage("snowballBig.png");
    private final Image bigAvgBallImage   = loadImage("snowballBigAverage.png");
    private final Image bigSmallBallImage = loadImage("snowballBigSmall.png");
    private final Image avgSmallBallImage = loadImage("snowballAverageSmall.png");
    private MediaPlayer mediaPlayer;


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
                case BIG_SMALL -> bigSmallBallImage;
                case AVERAGE_SMALL -> avgSmallBallImage;
            };
            ImageView ballView = new ImageView(ballImg);
            ballView.setFitWidth(TILE_SIZE);
            ballView.setFitHeight(TILE_SIZE);
            cell.getChildren().add(ballView);

            //Label
            Label label = new Label(getSnowballLabelText(snowball.getType()));
            label.setStyle("-fx-text-fill: black; -fx-font-size: 9; -fx-font-weight: bold;");
            cell.getChildren().add(label);
        }
    }

    private String getSnowballLabelText(SnowballType type) {
        return switch (type) {
            case SMALL -> "S";
            case AVERAGE -> "A";
            case BIG -> "B";
            case BIG_AVERAGE -> "B+A";
            case BIG_SMALL -> "B+S";
            case AVERAGE_SMALL -> "A+S";
        };
    }


    private void addMonsterIfPresent(StackPane cell, int row, int col) {
        if (boardModel.getMonster().getRow() == row && boardModel.getMonster().getCol() == col) {
            ImageView monsterView = new ImageView(monsterImage);
            monsterView.setFitWidth(TILE_SIZE);
            monsterView.setFitHeight(TILE_SIZE);
            cell.getChildren().add(monsterView);
        }
    }

    public void createMovesArea(){
        this.movesArea = new TextArea();
        this.movesArea.setEditable(false);
        this.movesArea.setPrefRowCount(5);
        this.movesArea.setPrefColumnCount(10);
    }

    public VBox createContent(){
        this.grid = createGridPane();
        createMovesArea();
        this.layout = new VBox(10, this.grid, this.movesArea);
        startBackgroundMusic();
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

    public void gameWon() {
        // Espera um ciclo de renderização para garantir que o boneco aparece
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("You win!");
            alert.setHeaderText(null);
            if(boardModel.getCurrentLevel() == 1){
                alert.setContentText("You’ve built a complete snowman! You will play now level 2!");
            }else{
                alert.setContentText("You’ve built a complete snowman! You finish the game!");
            }

            alert.showAndWait();

            // Só após clicar OK e ver o boneco → espera 1s e muda de nível
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
            pause.setOnFinished(e -> boardModel.loadLevel(2)); // ou changeLevel()
            pause.play();
        });
    }


    private Image loadImage(String fileName) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + fileName)));
    }

    private void startBackgroundMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // reinicia se já estiver a tocar
        }

        Media media = new Media(Objects.requireNonNull(getClass().getResource("/sound/background.mp3")).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop infinito
        mediaPlayer.setVolume(0.2); // volume opcional
        mediaPlayer.play();
    }


}
