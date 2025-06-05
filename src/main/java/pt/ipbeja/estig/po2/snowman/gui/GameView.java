package pt.ipbeja.estig.po2.snowman.gui;

import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.model.*;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GameView {
    private static final int TILE_SIZE = 40;
    private final BoardModel boardModel;
    private TextArea movesArea;
    private VBox layout;
    private GridPane grid;
    private TextArea scoresArea;
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

    // ==============================
    // Criação do Tabuleiro (GridPane)
    // ==============================

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

    // ==============================
    // Criação das Células do Tabuleiro
    // ==============================

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

    // ==============================
    // Desenho de Bolas de Neve na Célula
    // ==============================

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

    // ==============================
    // Desenho do Monstro na Célula
    // ==============================

    private void addMonsterIfPresent(StackPane cell, int row, int col) {
        if (boardModel.getMonster().getRow() == row && boardModel.getMonster().getCol() == col) {
            ImageView monsterView = new ImageView(monsterImage);
            monsterView.setFitWidth(TILE_SIZE);
            monsterView.setFitHeight(TILE_SIZE);
            cell.getChildren().add(monsterView);
        }
    }

    // ==============================
    // Criação das Áreas de Texto
    // ==============================

    public void createMovesArea(){
        this.movesArea = new TextArea();
        this.movesArea.setEditable(false);
        this.movesArea.setFocusTraversable(false);
        this.movesArea.setPrefRowCount(5);
        this.movesArea.setPrefColumnCount(10);
        this.movesArea.setOnMouseClicked(e -> requestFocusBack());
    }

    public TextArea createScoresArea() {
        this.scoresArea = new TextArea();
        this.scoresArea.setEditable(false);
        this.scoresArea.setFocusTraversable(false);
        this.scoresArea.setPrefRowCount(5);
        this.scoresArea.setPrefColumnCount(15);
        this.scoresArea.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        this.scoresArea.setOnMouseClicked(e -> requestFocusBack());
        return this.scoresArea;
    }

    // ==============================
    // Criação do Layout Principal
    // ==============================

    public VBox createContent() {
        askPlayerName();

        this.grid = createGridPane();
        createMovesArea();
        createScoresArea();

        GridPane gridWithScores = new GridPane();
        gridWithScores.add(grid, 0, 0);
        gridWithScores.add(scoresArea, 1, 0);
        gridWithScores.setHgap(20);

        Button loadLevelButton = new Button("Abrir Nível");
        loadLevelButton.setOnAction(e -> openLevelFile(getStageFromLayout()));

        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e ->{
                boardModel.undo();
                requestFocusBack();
        });
        Button redoButton = new Button("Redo");
        redoButton.setOnAction(e -> {
            boardModel.redo();
            requestFocusBack();
        });


        HBox buttonBox = new HBox(10, loadLevelButton, undoButton, redoButton);
        buttonBox.setStyle("-fx-padding: 10;");

        this.layout = new VBox(10, gridWithScores, movesArea, buttonBox);
        return this.layout;
    }

    // ==============================
    // Diálogo de Nome do Jogador
    // ==============================

    private void askPlayerName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nome do Jogador");
        dialog.setHeaderText("Bem-vindo ao Snowman!");
        dialog.setContentText("Insere o teu nome (máx. 3 letras):");

        String playerName = dialog.showAndWait().orElse("AAA");

        if (playerName.length() > 3) {
            playerName = playerName.substring(0, 3);
        }

        boardModel.setPlayerName(playerName);
    }

    // ==============================
    // Abrir Nível a partir de Ficheiro
    // ==============================

    private void openLevelFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar ficheiro de nível");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                BoardModel novoModelo = LevelLoader.loadFromFile(file);
                this.boardModel.replaceBoard(novoModelo);
                this.refreshBoard();
                this.updateMovementsArea();
                this.updateScoresArea();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro ao abrir o nível");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
        this.layout.getScene().getRoot().requestFocus();
    }

    private Stage getStageFromLayout() {
        return (Stage) this.layout.getScene().getWindow();
    }

    // ==============================
    // Atualização da Interface
    // ==============================

    public void updateMovementsArea(){
        movesArea.clear();
        List<String> movsList = boardModel.getMovementsHistory();
        for(String mov : movsList){
            movesArea.appendText(mov + "\n");
        }
    }

    public void refreshBoard() {
        if (layout.getChildren().get(0) instanceof GridPane gridWithScores) {
            gridWithScores.getChildren().remove(this.grid);
            this.grid = createGridPane();
            gridWithScores.add(this.grid, 0, 0);
        }
    }

    public void updateScoresArea() {
        scoresArea.clear();
        List<Score> topScores = boardModel.getTopScores();
        Score last = topScores.stream()
                .filter(s -> s.getNamePlayer().equals(boardModel.getPlayerName()))
                .max(Comparator.comparingInt(Score::getMovCount))
                .orElse(null);

        for (Score score : topScores) {
            boolean isTop = score.equals(last);
            scoresArea.appendText(score.toString() + (isTop ? " TOP" : "") + "\n");
        }
    }

    // ==============================
    // Alerta de Vitória
    // ==============================

    public void gameWon() {
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

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
            pause.setOnFinished(e -> boardModel.loadLevel(2));
            pause.play();
        });
    }

    // ==============================
    // Recursos Visuais e Som
    // ==============================

    private Image loadImage(String fileName) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + fileName)));
    }

    public void startBackgroundMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media media = new Media(Objects.requireNonNull(getClass().getResource("/sound/background.mp3")).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.2);
        mediaPlayer.play();
    }

    // ==============================
    // Utilitário para Restaurar Foco
    // ==============================

    private void requestFocusBack() {
        if (this.layout != null && this.layout.getScene() != null) {
            this.layout.getScene().getRoot().requestFocus();
        }
    }

}
