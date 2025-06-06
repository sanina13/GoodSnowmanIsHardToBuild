/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 *
 * Abstract class representing a movable element in the game, such as a monster or snowball.
 */
package pt.ipbeja.estig.po2.snowman.model;

/**
 * Represents a movable element in the board with a row and column position.
 */
public abstract class MobileElement {
    protected int row;
    protected int col;

    /**
     * Constructs a MobileElement with the specified row and column.
     *
     * @param row the initial row position
     * @param col the initial column position
     */
    public MobileElement(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the current row position.
     *
     * @return the row position
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the current column position.
     *
     * @return the column position
     */
    public int getCol() {
        return col;
    }

    /**
     * Updates the position of the mobile element.
     *
     * @param row the new row position
     * @param col the new column position
     */
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
