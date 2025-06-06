package pt.ipbeja.po2.app.model;

import pt.ipbeja.estig.po2.snowman.model.*;

/**
 * Authors: Miguel Sanina (26874), Tiago Sanina (20318)
 */
public class BoardModelTest {

    public static void main(String[] args) {
        BoardModel model = new BoardModel();
        model.loadLevel(1);

        testMonsterToTheLeft(model);
        testCreateAverageSnowball(model);
        testCreateBigSnowball(model);
        testMaintainBigSnowball(model);
        testAverageBigSnowman(model);
        testCompleteSnowman(model);
    }

    /**
     * Tests if the monster moves one cell to the left correctly.
     * @param model the BoardModel instance to test
     */
    public static void testMonsterToTheLeft(BoardModel model) {
        model.getMonster().setPosition(4, 4);
        model.moveMonster(Direction.LEFT);
        assert model.getMonster().getCol() == 3 : "Monster didn't move to the left";
    }

    /**
     * Tests if a SMALL snowball becomes AVERAGE when rolled onto a snow tile.
     * @param model the BoardModel instance to test
     */
    public static void testCreateAverageSnowball(BoardModel model) {
        Snowball snowball = new Snowball(3, 3, SnowballType.SMALL);
        model.getBoard().get(3).set(4, PositionContent.SNOW);
        model.growSnowballIfOnSnow(snowball, Direction.RIGHT);
        assert snowball.getType() == SnowballType.AVERAGE : "Expected AVERAGE";
    }

    /**
     * Tests if an AVERAGE snowball becomes BIG when rolled onto a snow tile.
     * @param model the BoardModel instance to test
     */
    public static void testCreateBigSnowball(BoardModel model) {
        Snowball snowball = new Snowball(4, 4, SnowballType.AVERAGE);
        model.getBoard().get(4).set(3, PositionContent.SNOW);
        model.growSnowballIfOnSnow(snowball, Direction.LEFT);
        assert snowball.getType() == SnowballType.BIG : "Expected BIG, got " + snowball.getType();
    }

    /**
     * Tests that a BIG snowball does not grow further even if pushed onto snow.
     * @param model the BoardModel instance to test
     */
    public static void testMaintainBigSnowball(BoardModel model) {
        Snowball snowball = new Snowball(3, 3, SnowballType.BIG);
        model.getBoard().get(3).set(4, PositionContent.SNOW);
        model.growSnowballIfOnSnow(snowball, Direction.RIGHT);
        assert snowball.getType() == SnowballType.BIG : "Expected BIG, got " + snowball.getType();
    }

    /**
     * Tests stacking an AVERAGE snowball on a BIG one to form BIG_AVERAGE.
     * @param model the BoardModel instance to test
     */
    public static void testAverageBigSnowman(BoardModel model) {
        Snowball avg = new Snowball(5, 5, SnowballType.AVERAGE);
        Snowball big = new Snowball(5, 6, SnowballType.BIG);
        model.getBoard().get(5).set(6, PositionContent.NO_SNOW);
        model.getTopScores().clear();
        model.tryStackSnowball(avg, Direction.RIGHT);
        assert big.getType() == SnowballType.BIG_AVERAGE : "Expected BIG_AVERAGE, got " + big.getType();
    }

    /**
     * Tests completing a snowman by stacking a SMALL snowball on a BIG_AVERAGE.
     * @param model the BoardModel instance to test
     */
    public static void testCompleteSnowman(BoardModel model) {
        Snowball small = new Snowball(3, 3, SnowballType.SMALL);
        Snowball bigAvg = new Snowball(3, 4, SnowballType.BIG_AVERAGE);
        model.getBoard().get(3).set(4, PositionContent.NO_SNOW);
        model.getTopScores().clear();
        model.getMovementsHistory().clear();
        model.tryStackSnowball(small, Direction.RIGHT);
        assert model.getBoard().get(3).get(4) == PositionContent.SNOWMAN : "Expected SNOWMAN, got " + model.getBoard().get(3).get(4);
    }
}
