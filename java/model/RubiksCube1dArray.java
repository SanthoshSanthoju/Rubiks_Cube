package model;

/**
 * Rubik's Cube representation using a flat 1D char array: char[54].
 * Index formula: face * 9 + row * 3 + col.
 */
public class RubiksCube1dArray extends RubiksCube {

    private char[] cube = new char[54];

    public RubiksCube1dArray() {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    cube[i * 9 + j * 3 + k] = getColorLetter(COLOR.values()[i]);
    }

    private RubiksCube1dArray(char[] src) {
        System.arraycopy(src, 0, cube, 0, 54);
    }

    @Override
    public RubiksCube copy() {
        return new RubiksCube1dArray(this.cube);
    }

    private static int idx(int face, int row, int col) {
        return face * 9 + row * 3 + col;
    }

    private void rotateFace(int ind) {
        char[] temp = new char[9];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                temp[i * 3 + j] = cube[idx(ind, i, j)];

        for (int i = 0; i < 3; i++)
            cube[idx(ind, 0, i)] = temp[(2 - i) * 3];
        for (int i = 0; i < 3; i++)
            cube[idx(ind, i, 2)] = temp[i];
        for (int i = 0; i < 3; i++)
            cube[idx(ind, 2, 2 - i)] = temp[i * 3 + 2];
        for (int i = 0; i < 3; i++)
            cube[idx(ind, 2 - i, 0)] = temp[2 * 3 + (2 - i)];
    }

    @Override
    public COLOR getColor(FACE face, int row, int col) {
        char c = cube[idx(face.ordinal(), row, col)];
        switch (c) {
            case 'B':
                return COLOR.BLUE;
            case 'R':
                return COLOR.RED;
            case 'G':
                return COLOR.GREEN;
            case 'O':
                return COLOR.ORANGE;
            case 'Y':
                return COLOR.YELLOW;
            default:
                return COLOR.WHITE;
        }
    }

    @Override
    public void setColor(FACE face, int row, int col, COLOR color) {
        cube[idx(face.ordinal(), row, col)] = getColorLetter(color);
    }

    @Override
    public boolean isSolved() {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    if (cube[idx(i, j, k)] != getColorLetter(COLOR.values()[i]))
                        return false;
        return true;
    }

    @Override
    public RubiksCube u() {
        rotateFace(0);
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++)
            temp[i] = cube[idx(4, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[idx(4, 0, 2 - i)] = cube[idx(1, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[idx(1, 0, 2 - i)] = cube[idx(2, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[idx(2, 0, 2 - i)] = cube[idx(3, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[idx(3, 0, 2 - i)] = temp[i];
        return this;
    }

    @Override
    public RubiksCube uPrime() {
        u();
        u();
        u();
        return this;
    }

    @Override
    public RubiksCube u2() {
        u();
        u();
        return this;
    }

    @Override
    public RubiksCube l() {
        rotateFace(1);
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++)
            temp[i] = cube[idx(0, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[idx(0, i, 0)] = cube[idx(4, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[idx(4, 2 - i, 2)] = cube[idx(5, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[idx(5, i, 0)] = cube[idx(2, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[idx(2, i, 0)] = temp[i];
        return this;
    }

    @Override
    public RubiksCube lPrime() {
        l();
        l();
        l();
        return this;
    }

    @Override
    public RubiksCube l2() {
        l();
        l();
        return this;
    }

    @Override
    public RubiksCube f() {
        rotateFace(2);
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++)
            temp[i] = cube[idx(0, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[idx(0, 2, i)] = cube[idx(1, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[idx(1, 2 - i, 2)] = cube[idx(5, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[idx(5, 0, 2 - i)] = cube[idx(3, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[idx(3, i, 0)] = temp[i];
        return this;
    }

    @Override
    public RubiksCube fPrime() {
        f();
        f();
        f();
        return this;
    }

    @Override
    public RubiksCube f2() {
        f();
        f();
        return this;
    }

    @Override
    public RubiksCube r() {
        rotateFace(3);
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++)
            temp[i] = cube[idx(0, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[idx(0, 2 - i, 2)] = cube[idx(2, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[idx(2, 2 - i, 2)] = cube[idx(5, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[idx(5, 2 - i, 2)] = cube[idx(4, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[idx(4, i, 0)] = temp[i];
        return this;
    }

    @Override
    public RubiksCube rPrime() {
        r();
        r();
        r();
        return this;
    }

    @Override
    public RubiksCube r2() {
        r();
        r();
        return this;
    }

    @Override
    public RubiksCube b() {
        rotateFace(4);
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++)
            temp[i] = cube[idx(0, 0, 2 - i)];
        for (int i = 0; i < 3; i++)
            cube[idx(0, 0, 2 - i)] = cube[idx(3, 2 - i, 2)];
        for (int i = 0; i < 3; i++)
            cube[idx(3, 2 - i, 2)] = cube[idx(5, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[idx(5, 2, i)] = cube[idx(1, i, 0)];
        for (int i = 0; i < 3; i++)
            cube[idx(1, i, 0)] = temp[i];
        return this;
    }

    @Override
    public RubiksCube bPrime() {
        b();
        b();
        b();
        return this;
    }

    @Override
    public RubiksCube b2() {
        b();
        b();
        return this;
    }

    @Override
    public RubiksCube d() {
        rotateFace(5);
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++)
            temp[i] = cube[idx(2, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[idx(2, 2, i)] = cube[idx(1, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[idx(1, 2, i)] = cube[idx(4, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[idx(4, 2, i)] = cube[idx(3, 2, i)];
        for (int i = 0; i < 3; i++)
            cube[idx(3, 2, i)] = temp[i];
        return this;
    }

    @Override
    public RubiksCube dPrime() {
        d();
        d();
        d();
        return this;
    }

    @Override
    public RubiksCube d2() {
        d();
        d();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RubiksCube1dArray))
            return false;
        RubiksCube1dArray other = (RubiksCube1dArray) o;
        for (int i = 0; i < 54; i++)
            if (cube[i] != other.cube[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return new String(cube).hashCode();
    }
}
