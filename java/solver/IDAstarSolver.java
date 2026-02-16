package solver;

import model.RubiksCube;
import model.RubiksCube.MOVE;
import patterndb.CornerPatternDatabase;
import java.util.*;

/**
 * IDA* (Iterative Deepening A*) solver.
 * Uses a CornerPatternDatabase as heuristic function h(n).
 * f(n) = depth + h(n). Iteratively deepens the bound until solution found.
 */
public class IDAstarSolver {

    private CornerPatternDatabase cornerDB;
    private List<MOVE> moves = new ArrayList<>();
    private Map<RubiksCube, MOVE> moveDone = new HashMap<>();
    private Set<RubiksCube> visited = new HashSet<>();
    public RubiksCube rubiksCube;

    private static class Node {
        RubiksCube cube;
        int depth;
        int estimate;

        Node(RubiksCube cube, int depth, int estimate) {
            this.cube = cube;
            this.depth = depth;
            this.estimate = estimate;
        }
    }

    public IDAstarSolver(RubiksCube cube, String fileName) {
        this.rubiksCube = cube.copy();
        this.cornerDB = new CornerPatternDatabase();
        this.cornerDB.fromFile(fileName);
    }

    private void resetStructure() {
        moves.clear();
        moveDone.clear();
        visited.clear();
    }

    /**
     * Returns [solvedCube, bound] if solved, or [rubiksCube, nextBound] if not.
     */
    private Object[] idAstar(int bound) {
        // Priority queue: smaller f = depth + estimate first, then smaller estimate
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            int f1 = a[0], f2 = b[0];
            int e1 = a[1], e2 = b[1];
            if (f1 != f2)
                return f1 - f2;
            return e1 - e2;
        });

        // We store nodes in a list and use indices in the PQ
        List<Node> nodeList = new ArrayList<>();
        List<Integer> moveIndices = new ArrayList<>();

        Node start = new Node(rubiksCube.copy(), 0, cornerDB.getNumMoves(rubiksCube));
        nodeList.add(start);
        moveIndices.add(0);
        pq.offer(new int[] { start.depth + start.estimate, start.estimate, 0 }); // [f, estimate, nodeIndex]

        int nextBound = 100;

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int nodeIdx = top[2];
            Node node = nodeList.get(nodeIdx);
            int moveIdx = moveIndices.get(nodeIdx);

            if (visited.contains(node.cube))
                continue;

            visited.add(node.cube.copy());
            moveDone.put(node.cube.copy(), MOVE.values()[moveIdx]);

            if (node.cube.isSolved())
                return new Object[] { node.cube, bound };

            int newDepth = node.depth + 1;
            MOVE[] allMoves = MOVE.values();
            for (int i = 0; i < 18; i++) {
                RubiksCube next = node.cube.copy();
                next.move(allMoves[i]);
                if (!visited.contains(next)) {
                    int est = cornerDB.getNumMoves(next);
                    if (est + newDepth > bound) {
                        nextBound = Math.min(nextBound, est + newDepth);
                    } else {
                        int idx = nodeList.size();
                        nodeList.add(new Node(next, newDepth, est));
                        moveIndices.add(i);
                        pq.offer(new int[] { newDepth + est, est, idx });
                    }
                }
            }
        }
        return new Object[] { rubiksCube, nextBound };
    }

    public List<MOVE> solve() {
        int bound = 1;
        Object[] result = idAstar(bound);
        while ((int) result[1] != bound) {
            resetStructure();
            bound = (int) result[1];
            result = idAstar(bound);
        }

        RubiksCube solvedCube = (RubiksCube) result[0];
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
