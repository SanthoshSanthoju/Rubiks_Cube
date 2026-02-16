import model.*;
import solver.*;
import patterndb.*;
import java.io.File;
import java.util.List;

/**
 * Rubik's Cube Solver - Java Port
 * Demonstrates all 3 representations and 4 solvers.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=== Rubik's Cube Solver (Java) ===\n");

        // --- Test all 3 representations ---
        System.out.println("--- Testing Cube Representations ---\n");

        RubiksCube3dArray cube3d = new RubiksCube3dArray();
        RubiksCube1dArray cube1d = new RubiksCube1dArray();
        RubiksCubeBitboard cubeBB = new RubiksCubeBitboard();

        System.out.println("3dArray solved: " + cube3d.isSolved());
        System.out.println("1dArray solved: " + cube1d.isSolved());
        System.out.println("Bitboard solved: " + cubeBB.isSolved());

        // Test a move and its inverse
        cube3d.u();
        cube3d.uPrime();
        cube1d.u();
        cube1d.uPrime();
        cubeBB.u();
        cubeBB.uPrime();
        System.out.println("\nAfter U then U' (should all be solved):");
        System.out.println("3dArray solved: " + cube3d.isSolved());
        System.out.println("1dArray solved: " + cube1d.isSolved());
        System.out.println("Bitboard solved: " + cubeBB.isSolved());

        // --- DFS Solver Test ---
        System.out.println("\n--- DFS Solver (max depth 8, shuffle 4 moves) ---\n");
        {
            RubiksCubeBitboard cube = new RubiksCubeBitboard();
            List<RubiksCube.MOVE> shuffleMoves = cube.randomShuffleCube(4);
            System.out.print("Shuffle: ");
            for (RubiksCube.MOVE m : shuffleMoves)
                System.out.print(RubiksCube.getMoveName(m) + " ");
            System.out.println();

            DFSSolver solver = new DFSSolver(cube, 8);
            List<RubiksCube.MOVE> solution = solver.solve();

            System.out.print("Solution (" + solution.size() + " moves): ");
            for (RubiksCube.MOVE m : solution)
                System.out.print(RubiksCube.getMoveName(m) + " ");
            System.out.println();
            System.out.println("Solved: " + solver.rubiksCube.isSolved());
        }

        // --- BFS Solver Test ---
        System.out.println("\n--- BFS Solver (shuffle 4 moves) ---\n");
        {
            RubiksCubeBitboard cube = new RubiksCubeBitboard();
            List<RubiksCube.MOVE> shuffleMoves = cube.randomShuffleCube(4);
            System.out.print("Shuffle: ");
            for (RubiksCube.MOVE m : shuffleMoves)
                System.out.print(RubiksCube.getMoveName(m) + " ");
            System.out.println();

            BFSSolver solver = new BFSSolver(cube);
            List<RubiksCube.MOVE> solution = solver.solve();

            System.out.print("Solution (" + solution.size() + " moves): ");
            for (RubiksCube.MOVE m : solution)
                System.out.print(RubiksCube.getMoveName(m) + " ");
            System.out.println();
            System.out.println("Solved: " + solver.rubiksCube.isSolved());
        }

        // --- IDDFS Solver Test ---
        System.out.println("\n--- IDDFS Solver (max depth 7, shuffle 5 moves) ---\n");
        {
            RubiksCubeBitboard cube = new RubiksCubeBitboard();
            List<RubiksCube.MOVE> shuffleMoves = cube.randomShuffleCube(5);
            System.out.print("Shuffle: ");
            for (RubiksCube.MOVE m : shuffleMoves)
                System.out.print(RubiksCube.getMoveName(m) + " ");
            System.out.println();

            IDDFSSolver solver = new IDDFSSolver(cube, 7);
            List<RubiksCube.MOVE> solution = solver.solve();

            System.out.print("Solution (" + solution.size() + " moves): ");
            for (RubiksCube.MOVE m : solution)
                System.out.print(RubiksCube.getMoveName(m) + " ");
            System.out.println();
            System.out.println("Solved: " + solver.rubiksCube.isSolved());
        }

        // --- IDA* Solver Test ---
        System.out.println("\n--- IDA* Solver (with Corner Pattern Database) ---\n");
        {
            String dbPath = "Databases/cornerDepth5V1.txt";

            // Check if database exists, if not generate it
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                System.out.println("Corner database not found. Generating (this may take a few minutes)...");
                new File("Databases").mkdirs();
                CornerDBMaker maker = new CornerDBMaker(dbPath, 0x99);
                maker.bfsAndStore();
                System.out.println("Database generated!\n");
            }

            RubiksCubeBitboard cube = new RubiksCubeBitboard();
            List<RubiksCube.MOVE> shuffleMoves = cube.randomShuffleCube(7);
            System.out.print("Shuffle: ");
            for (RubiksCube.MOVE m : shuffleMoves)
                System.out.print(RubiksCube.getMoveName(m) + " ");
            System.out.println();
            cube.print();

            IDAstarSolver solver = new IDAstarSolver(cube, dbPath);
            List<RubiksCube.MOVE> solution = solver.solve();

            System.out.print("Solution (" + solution.size() + " moves): ");
            for (RubiksCube.MOVE m : solution)
                System.out.print(RubiksCube.getMoveName(m) + " ");
            System.out.println();
            System.out.println("Solved: " + solver.rubiksCube.isSolved());
            solver.rubiksCube.print();
        }

        System.out.println("\n=== All tests complete! ===");
    }
}
