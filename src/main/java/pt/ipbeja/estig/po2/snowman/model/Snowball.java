package pt.ipbeja.estig.po2.snowman.model;

public class Snowball extends MobileElement{
    private SnowballType type;

    public Snowball(int row, int col, SnowballType type) {
        super(row, col);
        this.type = type;
    }

    public SnowballType getType() {
        return type;
    }

    public void setType(SnowballType type) {
        this.type = type;
    }
}
