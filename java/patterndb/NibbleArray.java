package patterndb;

import java.util.Arrays;

/**
 * A compact array that stores values in 4 bits (nibbles).
 * Each byte holds 2 entries, halving memory usage vs. a byte array.
 * Values must be in range [0, 15].
 */
public class NibbleArray {

    private int size;
    private byte[] arr;

    public NibbleArray(int size, int initVal) {
        this.size = size;
        this.arr = new byte[size / 2 + 1];
        byte fill = (byte) (((initVal & 0x0F) << 4) | (initVal & 0x0F));
        Arrays.fill(arr, fill);
    }

    public NibbleArray(int size) {
        this(size, 0xFF);
    }

    /** Get the nibble value at the given position (0-indexed). */
    public int get(int pos) {
        int i = pos / 2;
        int val = arr[i] & 0xFF; // treat as unsigned
        if (pos % 2 == 1) {
            return val & 0x0F; // odd: low nibble
        } else {
            return (val >> 4) & 0x0F; // even: high nibble
        }
    }

    /** Set the nibble value at the given position. */
    public void set(int pos, int val) {
        int i = pos / 2;
        int currVal = arr[i] & 0xFF;
        if (pos % 2 == 1) {
            arr[i] = (byte) ((currVal & 0xF0) | (val & 0x0F));
        } else {
            arr[i] = (byte) ((currVal & 0x0F) | ((val & 0x0F) << 4));
        }
    }

    /** Get the underlying byte array for file I/O. */
    public byte[] data() {
        return arr;
    }

    /** Get the storage size in bytes. */
    public int storageSize() {
        return arr.length;
    }

    /** Reset all nibbles to the given value. */
    public void reset(int val) {
        byte fill = (byte) (((val & 0x0F) << 4) | (val & 0x0F));
        Arrays.fill(arr, fill);
    }
}
