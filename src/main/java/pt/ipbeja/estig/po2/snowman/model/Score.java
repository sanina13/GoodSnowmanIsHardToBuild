/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 *
 * Represents a player's score in the game, including name, level, and move count.
 */
package pt.ipbeja.estig.po2.snowman.model;

/**
 * Class that stores a score, containing the player's name, the level played, and the number of moves used.
 */
public class Score implements Comparable<Score> {
    private final String namePlayer;
    private final int actualLevel;
    private final int movCount;

    /**
     * Constructs a new Score object.
     *
     * @param namePlayer the player's name (up to 3 characters)
     * @param actualLevel the level number
     * @param movCount the number of movements used
     */
    public Score(String namePlayer, int actualLevel, int movCount) {
        this.namePlayer = namePlayer;
        this.actualLevel = actualLevel;
        this.movCount = movCount;
    }

    /**
     * Gets the player's name.
     *
     * @return the name of the player
     */
    public String getNamePlayer() {
        return namePlayer;
    }

    /**
     * Gets the level the score refers to.
     *
     * @return the level number
     */
    public int getActualLevel() {
        return actualLevel;
    }

    /**
     * Gets the number of movements made.
     *
     * @return the number of moves
     */
    public int getMovCount() {
        return movCount;
    }

    /**
     * Compares this score with another score based on the number of movements.
     *
     * @param other the other score to compare to
     * @return comparison result (for sorting)
     */
    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.movCount, other.movCount);
    }

    /**
     * Returns a string representation of the score.
     *
     * @return a formatted string with name, level, and moves
     */
    @Override
    public String toString() {
        return String.format("%s | Nível %d | %d movs", namePlayer, actualLevel, movCount);
    }
}
