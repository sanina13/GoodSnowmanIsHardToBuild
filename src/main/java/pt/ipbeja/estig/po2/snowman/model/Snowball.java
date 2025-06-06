/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 *
 * Represents a snowball in the game, with position and type.
 */
package pt.ipbeja.estig.po2.snowman.model;

/**
 * Class that models a snowball element with a specific type and position on the board.
 */
public class Snowball extends MobileElement {
    private SnowballType type;

    /**
     * Constructs a Snowball object with a given position and type.
     *
     * @param row the row position
     * @param col the column position
     * @param type the type of the snowball
     */
    public Snowball(int row, int col, SnowballType type) {
        super(row, col);
        this.type = type;
    }

    /**
     * Gets the current type of the snowball.
     *
     * @return the snowball's type
     */
    public SnowballType getType() {
        return type;
    }

    /**
     * Sets the type of the snowball.
     *
     * @param type the new type to assign
     */
    public void setType(SnowballType type) {
        this.type = type;
    }
}
