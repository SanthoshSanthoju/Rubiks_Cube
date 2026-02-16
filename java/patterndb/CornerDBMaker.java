package patterndb;

import model.RubiksCube;
import model.RubiksCube.MOVE;
import model.RubiksCubeBitboard;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Builds the Corner Pattern Database via BFS from the solved state.
 * Explores all states reachable up to depth 8 and records the minimum
 * number of moves for each corner configuration.
 * Saves the result to a binary file (~50 MB).
 */
public class CornerDBMaker {

    private String fileName;
    private CornerPatternDatabase cornerDB;

    public CornerDBMaker(String fileName) {
        this.fileName = fileName;
        this.cornerDB = new CornerPatternDatabase();
    }

    public CornerDBMaker(String fileName, int initVal) {
        this.fileName = fileName;
        this.cornerDB = new CornerPatternDatabase(initVal);
    }

    /**
     * BFS from solved state to depth 8, storing minimum moves for each corner
     * config.
     * 
     * @return true on success
     */
    public boolean bfsAndStore() {
        RubiksCubeBitboard cube = new RubiksCubeBitboard();
        Queue<RubiksCubeBitboard> q = new LinkedList<>();
        q.add(cube);
        cornerDB.setNumMoves(cube, 0);

        int currDepth = 0;
        MOVE[] allMoves = MOVE.values();

        while (!q.isEmpty()) {
            int n = q.size();
            currDepth++;
            if (currDepth == 9)
                break;

            System.out.println("CornerDBMaker: Processing depth " + currDepth + ", queue size: " + n);

            for (int counter = 0; counter < n; counter++) {
                RubiksCubeBitboard node = q.poll();
                for (int i = 0; i < 18; i++) {
                    RubiksCubeBitboard next = (RubiksCubeBitboard) node.copy();
                    next.move(allMoves[i]);
                    if (cornerDB.getNumMoves(next) > currDepth) {
                        cornerDB.setNumMoves(next, currDepth);
                        q.add(next);
                    }
                }
            }
        }

        cornerDB.toFile(fileName);
        System.out.println("CornerDBMaker: Database saved to " + fileName);
        return true;
    }
}
