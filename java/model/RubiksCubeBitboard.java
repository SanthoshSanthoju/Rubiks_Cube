package model;

/**
 * Rubik's Cube representation using bitboards: long[6].
 * Each face is a 64-bit long. Each of 8 perimeter stickers uses 8 bits (one-hot
 * color encoding).
 * The center sticker (index 8) is implicit (always the face's color).
 *
 * Sticker layout per face:
 * 0 1 2
 * 7 8 3 (8 = center, not stored)
 * 6 5 4
 */
public class RubiksCubeBitboard extends RubiksCube {

    private long[] bitboard = new long[6];
    private long[] solvedSideConfig = new long[6];

    private static final int[][] ARR = {
            { 0, 1, 2 },
            { 7, 8, 3 },
            { 6, 5, 4 }
    };

    private static final long ONE_8 = (1L << 8) - 1; // 0xFF
    private static final long ONE_24 = (1L << 24) - 1; // 0xFFFFFF

    public RubiksCubeBitboard() {
        for (int side = 0; side < 6; side++) {
            long clr = 1L << side;
            bitboard[side] = 0;
            for (int faceIdx = 0; faceIdx < 8; faceIdx++) {
                bitboard[side] |= clr << (8 * faceIdx);
            }
            solvedSideConfig[side] = bitboard[side];
        }
    }

    private RubiksCubeBitboard(long[] src, long[] solvedCfg) {
        System.arraycopy(src, 0, bitboard, 0, 6);
        System.arraycopy(solvedCfg, 0, solvedSideConfig, 0, 6);
    }

    @Override
    public RubiksCube copy() {
        return new RubiksCubeBitboard(this.bitboard, this.solvedSideConfig);
    }

    private void rotateFace(int ind) {
        long side = bitboard[ind];
        long top = side >>> (8 * 6); // unsigned shift
        bitboard[ind] = (side << 16) | top;
    }

    private void rotateSide(int s1, int s1_1, int s1_2, int s1_3,
            int s2, int s2_1, int s2_2, int s2_3) {
        long clr1 = (bitboard[s2] & (ONE_8 << (8 * s2_1))) >>> (8 * s2_1);
        long clr2 = (bitboard[s2] & (ONE_8 << (8 * s2_2))) >>> (8 * s2_2);
        long clr3 = (bitboard[s2] & (ONE_8 << (8 * s2_3))) >>> (8 * s2_3);

        bitboard[s1] = (bitboard[s1] & ~(ONE_8 << (8 * s1_1))) | (clr1 << (8 * s1_1));
        bitboard[s1] = (bitboard[s1] & ~(ONE_8 << (8 * s1_2))) | (clr2 << (8 * s1_2));
        bitboard[s1] = (bitboard[s1] & ~(ONE_8 << (8 * s1_3))) | (clr3 << (8 * s1_3));
    }

    @Override
    public COLOR getColor(FACE face, int row, int col) {
        int idx = ARR[row][col];
        if (idx == 8)
            return COLOR.values()[face.ordinal()];

        long side = bitboard[face.ordinal()];
        long color = (side >>> (8 * idx)) & ONE_8;

        int bitPos = 0;
        while (color != 0) {
            color = color >>> 1;
            bitPos++;
        }
        return COLOR.values()[bitPos - 1];
    }

    @Override
    public void setColor(FACE face, int row, int col, COLOR color) {
        int idx = ARR[row][col];
        if (idx == 8)
            return;

        bitboard[face.ordinal()] &= ~(ONE_8 << (8 * idx));
        long newColor = 1L << color.ordinal();
        bitboard[face.ordinal()] |= (newColor << (8 * idx));
    }

    @Override
    public boolean isSolved() {
        for (int i = 0; i < 6; i++)
            if (solvedSideConfig[i] != bitboard[i])
                return false;
        return true;
    }

    // --- Face rotations ---

    @Override
    public RubiksCube u() {
        rotateFace(0);
        long temp = bitboard[2] & ONE_24;
        bitboard[2] = (bitboard[2] & ~ONE_24) | (bitboard[3] & ONE_24);
        bitboard[3] = (bitboard[3] & ~ONE_24) | (bitboard[4] & ONE_24);
        bitboard[4] = (bitboard[4] & ~ONE_24) | (bitboard[1] & ONE_24);
        bitboard[1] = (bitboard[1] & ~ONE_24) | temp;
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
        long clr1 = (bitboard[2] & (ONE_8 << (8 * 0))) >>> (8 * 0);
        long clr2 = (bitboard[2] & (ONE_8 << (8 * 6))) >>> (8 * 6);
        long clr3 = (bitboard[2] & (ONE_8 << (8 * 7))) >>> (8 * 7);

        rotateSide(2, 0, 7, 6, 0, 0, 7, 6);
        rotateSide(0, 0, 7, 6, 4, 4, 3, 2);
        rotateSide(4, 4, 3, 2, 5, 0, 7, 6);

        bitboard[5] = (bitboard[5] & ~(ONE_8 << (8 * 0))) | (clr1 << (8 * 0));
        bitboard[5] = (bitboard[5] & ~(ONE_8 << (8 * 6))) | (clr2 << (8 * 6));
        bitboard[5] = (bitboard[5] & ~(ONE_8 << (8 * 7))) | (clr3 << (8 * 7));
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
        long clr1 = (bitboard[0] & (ONE_8 << (8 * 4))) >>> (8 * 4);
        long clr2 = (bitboard[0] & (ONE_8 << (8 * 5))) >>> (8 * 5);
        long clr3 = (bitboard[0] & (ONE_8 << (8 * 6))) >>> (8 * 6);

        rotateSide(0, 4, 5, 6, 1, 2, 3, 4);
        rotateSide(1, 2, 3, 4, 5, 0, 1, 2);
        rotateSide(5, 0, 1, 2, 3, 6, 7, 0);

        bitboard[3] = (bitboard[3] & ~(ONE_8 << (8 * 6))) | (clr1 << (8 * 6));
        bitboard[3] = (bitboard[3] & ~(ONE_8 << (8 * 7))) | (clr2 << (8 * 7));
        bitboard[3] = (bitboard[3] & ~(ONE_8 << (8 * 0))) | (clr3 << (8 * 0));
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
        long clr1 = (bitboard[0] & (ONE_8 << (8 * 2))) >>> (8 * 2);
        long clr2 = (bitboard[0] & (ONE_8 << (8 * 3))) >>> (8 * 3);
        long clr3 = (bitboard[0] & (ONE_8 << (8 * 4))) >>> (8 * 4);

        rotateSide(0, 2, 3, 4, 2, 2, 3, 4);
        rotateSide(2, 2, 3, 4, 5, 2, 3, 4);
        rotateSide(5, 2, 3, 4, 4, 6, 7, 0);

        bitboard[4] = (bitboard[4] & ~(ONE_8 << (8 * 6))) | (clr1 << (8 * 6));
        bitboard[4] = (bitboard[4] & ~(ONE_8 << (8 * 7))) | (clr2 << (8 * 7));
        bitboard[4] = (bitboard[4] & ~(ONE_8 << (8 * 0))) | (clr3 << (8 * 0));
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
        long clr1 = (bitboard[0] & (ONE_8 << (8 * 0))) >>> (8 * 0);
        long clr2 = (bitboard[0] & (ONE_8 << (8 * 1))) >>> (8 * 1);
        long clr3 = (bitboard[0] & (ONE_8 << (8 * 2))) >>> (8 * 2);

        rotateSide(0, 0, 1, 2, 3, 2, 3, 4);
        rotateSide(3, 2, 3, 4, 5, 4, 5, 6);
        rotateSide(5, 4, 5, 6, 1, 6, 7, 0);

        bitboard[1] = (bitboard[1] & ~(ONE_8 << (8 * 6))) | (clr1 << (8 * 6));
        bitboard[1] = (bitboard[1] & ~(ONE_8 << (8 * 7))) | (clr2 << (8 * 7));
        bitboard[1] = (bitboard[1] & ~(ONE_8 << (8 * 0))) | (clr3 << (8 * 0));
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
        long clr1 = (bitboard[2] & (ONE_8 << (8 * 4))) >>> (8 * 4);
        long clr2 = (bitboard[2] & (ONE_8 << (8 * 5))) >>> (8 * 5);
        long clr3 = (bitboard[2] & (ONE_8 << (8 * 6))) >>> (8 * 6);

        rotateSide(2, 4, 5, 6, 1, 4, 5, 6);
        rotateSide(1, 4, 5, 6, 4, 4, 5, 6);
        rotateSide(4, 4, 5, 6, 3, 4, 5, 6);

        bitboard[3] = (bitboard[3] & ~(ONE_8 << (8 * 4))) | (clr1 << (8 * 4));
        bitboard[3] = (bitboard[3] & ~(ONE_8 << (8 * 5))) | (clr2 << (8 * 5));
        bitboard[3] = (bitboard[3] & ~(ONE_8 << (8 * 6))) | (clr3 << (8 * 6));
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
        if (!(o instanceof RubiksCubeBitboard))
            return false;
        RubiksCubeBitboard other = (RubiksCubeBitboard) o;
        for (int i = 0; i < 6; i++)
            if (bitboard[i] != other.bitboard[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        long finalHash = bitboard[0];
        for (int i = 1; i < 6; i++)
            finalHash ^= bitboard[i];
        return Long.hashCode(finalHash);
    }
}
