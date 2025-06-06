/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 *
 * Represents the monster character in the game, which is a movable element.
 */
package pt.ipbeja.estig.po2.snowman.model;

/**
 * Represents the monster controlled by the player.
 */
public class Monster extends MobileElement {

    /**
     * Creates a new Monster at the specified row and column.
     *
     * @param row the initial row position of the monster
     * @param col the initial column position of the monster
     */
    public Monster(int row, int col){
        super(row, col);
    }
}

