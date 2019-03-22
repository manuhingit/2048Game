import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

    protected int score = 0;
    protected int maxTile = 2;

    Stack<Tile[][]> previousStates = new Stack<>();
    Stack<Integer> previousScores = new Stack();
    boolean isSaveNeeded = true;

    private void saveState(Tile[][] state) {
        Tile[][] clone = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                clone[i][j] = new Tile(state[i][j].value);
            }
        }
        previousStates.push(clone);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        score = previousScores.empty() ? score :previousScores.pop();
        gameTiles = previousStates.empty() ? gameTiles : previousStates.pop();
    }

    public void randomMove() {
        switch(((int)(Math.random() * 100)) % 4) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    public boolean hasBoardChanged() {
        Tile[][] previousState = previousStates.peek();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (previousState[i][j].value != gameTiles[i][j].value) return true;
            }
        }
        return false;
    }

    void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.add(getMoveEfficiency(this::left));
        queue.add(getMoveEfficiency(this::down));
        queue.add(getMoveEfficiency(this::right));
        queue.add(getMoveEfficiency(this::up));
        queue.peek().getMove().move();
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency result;
        if (!hasBoardChanged()) result = new MoveEfficiency(-1, 0, move);
        else result = new MoveEfficiency(getEmptyTiles().size(), score, move);
        rollback();
        return result;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public Model() {
        resetGameTiles();
    }

    void printField(Tile[][] field) {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                System.out.print(field[i][j].value + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    boolean canMove() {
        Tile[][] clone = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) clone[i] = gameTiles[i].clone();
        for (int a = 0; a < 4; a++) {
            for (int i = 0; i < FIELD_WIDTH; i++) {
                if (compressTiles(gameTiles[i]) || mergeTiles(gameTiles[i])) {
                    gameTiles = clone;
                    return true;
                }
            }
            rotateMatrix(1);
        }
        gameTiles = clone;
        return false;
    }

    void resetGameTiles() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }

        addTile();
        addTile();
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) emptyTiles.add(gameTiles[i][j]);
            }
        }
        return emptyTiles;
    }

    private void addTile() {
        List<Tile> list = getEmptyTiles();
        if (list.isEmpty()) return;
        list.get((int) (list.size() * Math.random())).value = Math.random() < 0.9 ? 2 : 4;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean result = false;
        for (int i = 1; i < tiles.length; i++) {
            if (tiles[i].value == 0) continue;
            int a = i - 1;
            while (a >= 0 && tiles[a].value == 0) {
                result = true;
                Tile temp = tiles[a];
                tiles[a] = tiles[a + 1];
                tiles[a + 1] = temp;
                a--;
            }
        }
        return result;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean result = false;
        for (int i = 1; i < tiles.length; i++) {
            if (tiles[i - 1].value == tiles[i].value && tiles[i].value != 0) {
                result = true;
                int mergeValue = tiles[i - 1].value * 2;
                tiles[i - 1].value = mergeValue;
                tiles[i] = new Tile();
                if (mergeValue > maxTile) maxTile = mergeValue;
                score += mergeValue;
            }
        }
        compressTiles(tiles);
        return result;
    }

    void left() {
        if (isSaveNeeded) saveState(gameTiles);
        boolean tileAdded = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                if (!tileAdded) {
                    tileAdded = true;
                    addTile();
                }
            }
        }
        isSaveNeeded = true;
    }

    void down() {
        saveState(gameTiles);
        rotateMatrix(1);
        left();
        rotateMatrix(3);
    }

    void right() {
        saveState(gameTiles);
        rotateMatrix(2);
        left();
        rotateMatrix(2);
    }

    void up() {
        saveState(gameTiles);
        rotateMatrix(3);
        left();
        rotateMatrix(1);
    }

    private void rotateMatrix( int N) {
        for (int k = 0; k < N; k++) {
            Tile[][] newField = new Tile[FIELD_WIDTH][FIELD_WIDTH];
            for (int i = 0; i < FIELD_WIDTH; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++) {
                    newField[j][FIELD_WIDTH - i - 1] = gameTiles[i][j];
                }
            }
            gameTiles = newField;
        }
    }
}
