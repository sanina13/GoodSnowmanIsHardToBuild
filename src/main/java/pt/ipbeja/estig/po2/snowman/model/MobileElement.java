package pt.ipbeja.estig.po2.snowman.model;

public abstract class MobileElement {
    protected int row;
    protected int col;

    public MobileElement(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
