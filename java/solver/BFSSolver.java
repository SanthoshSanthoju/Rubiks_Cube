package solver;

import model.RubiksCube;
import model.RubiksCube.MOVE;
import java.util.*;

/**
 * Breadth-First Search solver.
 * Guarantees shortest solution but uses significant memory.
 * Uses HashMap for visited states and back-pointer reconstruction.
 */
public class BFSSolver {

    private List<MOVE> moves = new ArrayList<>();
    private Map<RubiksCube, Boolean> visited = new HashMap<>();
    private Map<RubiksCube, MOVE> moveDone = new HashMap<>();
    public RubiksCube rubiksCube;

    public BFSSolver(RubiksCube cube) {
        this.rubiksCube = cube.copy();
    }

    private RubiksCube bfs() {
        Queue<RubiksCube> q = new LinkedList<>();
        q.add(rubiksCube.copy());
        visited.put(rubiksCube.copy(), true);

        while (!q.isEmpty()) {
            RubiksCube node = q.poll();
            if (node.isSolved())
                return node;

            MOVE[] allMoves = MOVE.values();
            for (int i = 0; i < 18; i++) {
                RubiksCube next = node.copy();
                next.move(allMoves[i]);
                if (!visited.containsKey(next)) {
                    visited.put(next, true);
                    moveDone.put(next, allMoves[i]);
                    q.add(next);
                }
            }
        }
        return rubiksCube;
    }

    public List<MOVE> solve() {
        RubiksCube solvedCube = bfs();
        assert solvedCube.isSolved();

        RubiksCube currCube = solvedCube.copy();
        while (!currCube.equals(rubiksCube)) {
            MOVE currMove = moveDone.get(currCube);
            moves.add(currMove);
            currCube.invert(currMove);
        }
        rubiksCube = solvedCube;
        Collections.reverse(moves);
        return moves;
    }
}
