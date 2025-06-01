package pt.ipbeja.estig.po2.snowman.model;

public class Score implements Comparable<Score>{
    private final String namePlayer;
    private final int actualLevel;
    private final int movCount;

    public Score(String namePlayer, int actualLevel, int movCount) {
        this.namePlayer = namePlayer;
        this.actualLevel = actualLevel;
        this.movCount = movCount;
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public int getActualLevel() {
        return actualLevel;
    }

    public int getMovCount() {
        return movCount;
    }

    @Override
    public int compareTo(Score other){
        return Integer.compare(this.movCount, other.movCount);
    }

    @Override
    public String toString() {
        return String.format("%s | Nível %d | %d movs", namePlayer, actualLevel, movCount);
    }


}
