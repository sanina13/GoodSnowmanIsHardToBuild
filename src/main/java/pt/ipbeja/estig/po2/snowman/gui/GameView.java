package pt.ipbeja.estig.po2.snowman.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pt.ipbeja.estig.po2.snowman.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.model.PositionContent;
import pt.ipbeja.estig.po2.snowman.model.Snowball;
import pt.ipbeja.estig.po2.snowman.model.SnowballType;

import java.util.List;

public class GameView {
    private static final int TILE_SIZE = 40;
    private final BoardModel boardModel;
    private TextArea movesArea;
    private VBox layout;
    private GridPane grid;



    public GameView(BoardModel boardModel) {
        this.boardModel = boardModel;
    }


    public GridPane createGridPane(){
        GridPane grid = new GridPane();
        List<List<PositionContent>> board = boardModel.getBoard();

        for (int row = 0; row < board.size(); row++) {
            List<PositionContent> line = board.get(row);
            for (int col = 0; col < line.size(); col++) {
                PositionContent content = line.get(col);
                Rectangle tile = createTile(content);

                if(isMonsterPosition(row, col)){
                    tile.setFill(Color.BLACK);
                }

                applySnowballColor(tile, row, col);

                StackPane cell = new StackPane(tile);
                grid.add(cell, col, row);
            }
        }
        return grid;
    }

    private Rectangle createTile(PositionContent content){
        Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
        tile.setStroke(Color.BLACK);

        switch (content){
            case NO_SNOW -> tile.setFill(Color.LIGHTGRAY);
            case SNOW -> tile.setFill(Color.WHITE);
            case BLOCK -> tile.setFill(Color.BROWN);
            case SNOWMAN -> tile.setFill(Color.LIGHTBLUE);
        }

        return tile;
    }

    private boolean isMonsterPosition(int row, int col){
        return row == boardModel.getMonster().getRow() && col == boardModel.getMonster().getCol();
    }

    private void applySnowballColor(Rectangle tile, int row, int col){
        Snowball snowball = boardModel.getSnowballAt(row, col);

        if(snowball != null){
           switch (snowball.getType()){
               case SMALL -> tile.setFill(Color.PINK);
               case AVERAGE -> tile.setFill(Color.ORANGE);
               case BIG -> tile.setFill(Color.RED);
               case BIG_AVERAGE -> tile.setFill(Color.PURPLE);
               case BIG_SMALL -> tile.setFill(Color.MAGENTA);
               case AVERAGE_SMALL -> tile.setFill(Color.DARKORANGE);
           }
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
