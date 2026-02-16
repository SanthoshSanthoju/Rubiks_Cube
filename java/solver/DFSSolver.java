package solver;

import model.RubiksCube;
import model.RubiksCube.MOVE;
import java.util.ArrayList;
import java.util.List;

/**
 * Depth-First Search solver.
 * Recursively tries all 18 moves up to a maximum depth.
 */
public class DFSSolver {

    private List<MOVE> moves = new ArrayList<>();
    private int maxSearchDepth;
    public RubiksCube rubiksCube;

    public DFSSolver(RubiksCube cube, int maxSearchDepth) {
        this.rubiksCube = cube.copy();
        this.maxSearchDepth = maxSearchDepth;
    }

    public DFSSolver(RubiksCube cube) {
        this(cube, 8);
    }

    private boolean dfs(int depth) {
        if (rubiksCube.isSolved())
            return true;
        if (depth > maxSearchDepth)
            return false;

        MOVE[] allMoves = MOVE.values();
        for (int i = 0; i < 18; i++) {
            rubiksCube.move(allMoves[i]);
            moves.add(allMoves[i]);
            if (dfs(depth + 1))
                return true;
            moves.remove(moves.size() - 1);
            rubiksCube.invert(allMoves[i]);
        }
        return false;
    }

    public List<MOVE> solve() {
        dfs(1);
        return moves;
    }
}
