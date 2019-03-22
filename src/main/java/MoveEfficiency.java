/**
 * Predicted move to some direction, comparing to other moves by number of empty tiles
 * after moving, then by resulting score.
 */
public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        int tiles = Integer.compare(numberOfEmptyTiles, o.numberOfEmptyTiles);
        if (tiles == 0) return Integer.compare(score, o.score);
        else return tiles;
    }
}
