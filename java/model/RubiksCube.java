package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for all Rubik's Cube representations.
 * Provides shared functionality (move dispatch, print, corner helpers)
 * while subclasses define the actual data storage and face rotations.
 */
public abstract class RubiksCube {

    public enum FACE {
        UP, LEFT, FRONT, RIGHT, BACK, DOWN
    }

    public enum COLOR {
        WHITE, GREEN, RED, BLUE, ORANGE, YELLOW, UNKNOWN
    }

    public enum MOVE {
        L, LPRIME, L2,
        R, RPRIME, R2,
        U, UPRIME, U2,
        D, DPRIME, D2,
        F, FPRIME, F2,
        B, BPRIME, B2
    }

    // --- Abstract methods that subclasses must implement ---

    public abstract COLOR getColor(FACE face, int row, int col);
    public abstract void setColor(FACE face, int row, int col, COLOR color);
    public abstract boolean isSolved();

    public abstract RubiksCube f();
    public abstract RubiksCube fPrime();
    public abstract RubiksCube f2();
    public abstract RubiksCube u();
    public abstract RubiksCube uPrime();
    public abstract RubiksCube u2();
    public abstract RubiksCube l();
    public abstract RubiksCube lPrime();
    public abstract RubiksCube l2();
    public abstract RubiksCube r();
    public abstract RubiksCube rPrime();
    public abstract RubiksCube r2();
    public abstract RubiksCube d();
    public abstract RubiksCube dPrime();
    public abstract RubiksCube d2();
    public abstract RubiksCube b();
    public abstract RubiksCube bPrime();
    public abstract RubiksCube b2();

    /** Deep copy of the cube state. */
    public abstract RubiksCube copy();

    // --- Concrete methods ---

    public static char getColorLetter(COLOR color) {
        switch (color) {
            case BLUE:   return 'B';
            case GREEN:  return 'G';
            case RED:    return 'R';
            case YELLOW: return 'Y';
            case WHITE:  return 'W';
            case ORANGE: return 'O';
            default:     return '?';
        }
    }

    public static String getMoveName(MOVE move) {
        switch (move) {
            case L:      return "L";
            case LPRIME: return "L'";
            case L2:     return "L2";
            case R:      return "R";
            case RPRIME: return "R'";
            case R2:     return "R2";
            case U:      return "U";
            case UPRIME: return "U'";
            case U2:     return "U2";
            case D:      return "D";
            case DPRIME: return "D'";
            case D2:     return "D2";
            case F:      return "F";
            case FPRIME: return "F'";
            case F2:     return "F2";
            case B:      return "B";
            case BPRIME: return "B'";
            case B2:     return "B2";
            default:     return "?";
        }
    }

    /** Perform a move by enum index. */
    public RubiksCube move(MOVE m) {
        switch (m) {
            case L:      return l();
            case LPRIME: return lPrime();
            case L2:     return l2();
            case R:      return r();
            case RPRIME: return rPrime();
            case R2:     return r2();
            case U:      return u();
            case UPRIME: return uPrime();
            case U2:     return u2();
            case D:      return d();
            case DPRIME: return dPrime();
            case D2:     return d2();
            case F:      return f();
            case FPRIME: return fPrime();
            case F2:     return f2();
            case B:      return b();
            case BPRIME: return bPrime();
            case B2:     return b2();
            default:     return this;
        }
    }

    /** Perform the inverse of a move. */
    public RubiksCube invert(MOVE m) {
        switch (m) {
            case L:      return lPrime();
            case LPRIME: return l();
            case L2:     return l2();
            case R:      return rPrime();
            case RPRIME: return r();
            case R2:     return r2();
            case U:      return uPrime();
            case UPRIME: return u();
            case U2:     return u2();
            case D:      return dPrime();
            case DPRIME: return d();
            case D2:     return d2();
            case F:      return fPrime();
            case FPRIME: return f();
            case F2:     return f2();
            case B:      return bPrime();
            case BPRIME: return b();
            case B2:     return b2();
            default:     return this;
        }
    }

    /** Print the cube in planar cross format. */
    public void print() {
        System.out.println("Rubik's Cube:\n");

        for (int row = 0; row <= 2; row++) {
            System.out.print("       ");
            for (int col = 0; col <= 2; col++) {
                System.out.print(getColorLetter(getColor(FACE.UP, row, col)) + " ");
            }
            System.out.println();
        }
        System.out.println();

        for (int row = 0; row <= 2; row++) {
            for (int col = 0; col <= 2; col++)
                System.out.print(getColorLetter(getColor(FACE.LEFT, row, col)) + " ");
            System.out.print(" ");
            for (int col = 0; col <= 2; col++)
                System.out.print(getColorLetter(getColor(FACE.FRONT, row, col)) + " ");
            System.out.print(" ");
            for (int col = 0; col <= 2; col++)
                System.out.print(getColorLetter(getColor(FACE.RIGHT, row, col)) + " ");
            System.out.print(" ");
            for (int col = 0; col <= 2; col++)
                System.out.print(getColorLetter(getColor(FACE.BACK, row, col)) + " ");
            System.out.println();
        }
        System.out.println();

        for (int row = 0; row <= 2; row++) {
            System.out.print("       ");
            for (int col = 0; col <= 2; col++) {
                System.out.print(getColorLetter(getColor(FACE.DOWN, row, col)) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /** Randomly shuffle the cube with n moves. Returns the list of moves applied. */
    public List<MOVE> randomShuffleCube(int times) {
        List<MOVE> movesPerformed = new ArrayList<>();
        Random rand = new Random();
        MOVE[] allMoves = MOVE.values();
        for (int i = 0; i < times; i++) {
            MOVE m = allMoves[rand.nextInt(18)];
            movesPerformed.add(m);
            this.move(m);
        }
        return movesPerformed;
    }

    // --- Corner helpers (used by pattern database) ---

    /** Get color string of the corner at the given index (0-7). */
    public String getCornerColorString(int ind) {
        StringBuilder sb = new StringBuilder();
        switch (ind) {
            case 0: // UFR
                sb.append(getColorLetter(getColor(FACE.UP, 2, 2)));
                sb.append(getColorLetter(getColor(FACE.FRONT, 0, 2)));
                sb.append(getColorLetter(getColor(FACE.RIGHT, 0, 0)));
                break;
            case 1: // UFL
                sb.append(getColorLetter(getColor(FACE.UP, 2, 0)));
                sb.append(getColorLetter(getColor(FACE.FRONT, 0, 0)));
                sb.append(getColorLetter(getColor(FACE.LEFT, 0, 2)));
                break;
            case 2: // UBL
                sb.append(getColorLetter(getColor(FACE.UP, 0, 0)));
                sb.append(getColorLetter(getColor(FACE.BACK, 0, 2)));
                sb.append(getColorLetter(getColor(FACE.LEFT, 0, 0)));
                break;
            case 3: // UBR
                sb.append(getColorLetter(getColor(FACE.UP, 0, 2)));
                sb.append(getColorLetter(getColor(FACE.BACK, 0, 0)));
                sb.append(getColorLetter(getColor(FACE.RIGHT, 0, 2)));
                break;
            case 4: // DFR
                sb.append(getColorLetter(getColor(FACE.DOWN, 0, 2)));
                sb.append(getColorLetter(getColor(FACE.FRONT, 2, 2)));
                sb.append(getColorLetter(getColor(FACE.RIGHT, 2, 0)));
                break;
            case 5: // DFL
                sb.append(getColorLetter(getColor(FACE.DOWN, 0, 0)));
                sb.append(getColorLetter(getColor(FACE.FRONT, 2, 0)));
                sb.append(getColorLetter(getColor(FACE.LEFT, 2, 2)));
                break;
            case 6: // DBR
                sb.append(getColorLetter(getColor(FACE.DOWN, 2, 2)));
                sb.append(getColorLetter(getColor(FACE.BACK, 2, 0)));
                sb.append(getColorLetter(getColor(FACE.RIGHT, 2, 2)));
                break;
            case 7: // DBL
                sb.append(getColorLetter(getColor(FACE.DOWN, 2, 0)));
                sb.append(getColorLetter(getColor(FACE.BACK, 2, 2)));
                sb.append(getColorLetter(getColor(FACE.LEFT, 2, 0)));
                break;
        }
        return sb.toString();
    }

    /** Returns 3-bit index (0-7) identifying which corner piece is at slot ind. */
    public int getCornerIndex(int ind) {
        String corner = getCornerColorString(ind);
        int ret = 0;

        for (char c : corner.toCharArray()) {
            if (c != 'W' && c != 'Y') continue;
            if (c == 'Y') ret |= (1 << 2);
        }
        for (char c : corner.toCharArray()) {
            if (c != 'R' && c != 'O') continue;
            if (c == 'O') ret |= (1 << 1);
        }
        for (char c : corner.toCharArray()) {
            if (c != 'B' && c != 'G') continue;
            if (c == 'G') ret |= (1 << 0);
        }
        return ret;
    }

    /** Returns the orientation (0, 1, or 2) of the corner at slot ind. */
    public int getCornerOrientation(int ind) {
        String corner = getCornerColorString(ind);

        String actualStr = "";
        for (char c : corner.toCharArray()) {
            if (c != 'W' && c != 'Y') continue;
            actualStr += c;
        }

        if (corner.charAt(1) == actualStr.charAt(0)) {
            return 1;
        } else if (corner.charAt(2) == actualStr.charAt(0)) {
            return 2;
        } else {
            return 0;
        }
    }
}
