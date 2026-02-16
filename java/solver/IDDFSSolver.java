package solver;

import model.RubiksCube;
import model.RubiksCube.MOVE;
import java.util.ArrayList;
import java.util.List;

/**
 * Iterative Deepening Depth-First Search solver.
 * Runs DFS with increasing depth limits (1..max).
 * Combines DFS memory efficiency with BFS completeness.
 */
public class IDDFSSolver {

    private int maxSearchDepth;
    private List<MOVE> moves = new ArrayList<>();
    public RubiksCube rubiksCube;

    public IDDFSSolver(RubiksCube cube, int maxSearchDepth) {
        this.rubiksCube = cube.copy();
        this.maxSearchDepth = maxSearchDepth;
    }

    public IDDFSSolver(RubiksCube cube) {
        this(cube, 7);
    }

    public List<MOVE> solve() {
        for (int i = 1; i <= maxSearchDepth; i++) {
            DFSSolver dfsSolver = new DFSSolver(rubiksCube, i);
            moves = dfsSolver.solve();
            if (dfsSolver.rubiksCube.isSolved()) {
                rubiksCube = dfsSolver.rubiksCube;
                break;
            }
        }
        return moves;
    }
}
