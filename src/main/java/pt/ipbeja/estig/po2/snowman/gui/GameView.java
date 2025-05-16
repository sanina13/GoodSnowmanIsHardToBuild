package pt.ipbeja.estig.po2.snowman.gui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pt.ipbeja.estig.po2.snowman.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.model.PositionContent;

import java.util.List;

public class GameView {
    private static final int TILE_SIZE = 40;
    private final BoardModel boardModel;


    public GameView(BoardModel boardModel) {
        this.boardModel = boardModel;
    }

    public GridPane createContent(){
        GridPane grid = new GridPane();
        List<List<PositionContent>> board = boardModel.getBoard();

        for (int row = 0; row < board.size(); row++) {
            List<PositionContent> line = board.get(row);
            for (int col = 0; col < line.size(); col++) {
                PositionContent content = line.get(col);
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setStroke(Color.BLACK);

                switch (content){
                    case NO_SNOW -> tile.setFill(Color.LIGHTGRAY);
                    case SNOW -> tile.setFill(Color.WHITE);
                    case BLOCK -> tile.setFill(Color.DARKGRAY);
                    case SNOWMAN -> tile.setFill(Color.LIGHTBLUE);
                }

                StackPane cell = new StackPane(tile);
                grid.add(cell, col, row);
            }
        }

        return grid;
    }


}
