package patterndb;

import model.RubiksCube;
import java.io.*;

/**
 * Abstract base class for pattern databases.
 * Uses a NibbleArray (4-bit entries) to store the minimum number of moves
 * to solve a particular aspect of the cube from each state.
 */
public abstract class PatternDatabase {

    private NibbleArray database;
    private int size;
    private int numItems;

    public PatternDatabase(int size) {
        this.size = size;
        this.database = new NibbleArray(size, 0xFF);
        this.numItems = 0;
    }

    public PatternDatabase(int size, int initVal) {
        this.size = size;
        this.database = new NibbleArray(size, initVal);
        this.numItems = 0;
    }

    /** Subclasses compute the database index for a given cube state. */
    public abstract int getDatabaseIndex(RubiksCube cube);

    public boolean setNumMoves(int ind, int numMoves) {
        int oldMoves = getNumMoves(ind);
        if (oldMoves == 0xF) {
            numItems++;
        }
        if (oldMoves > numMoves) {
            database.set(ind, numMoves);
            return true;
        }
        return false;
    }

    public boolean setNumMoves(RubiksCube cube, int numMoves) {
        return setNumMoves(getDatabaseIndex(cube), numMoves);
    }

    public int getNumMoves(int ind) {
        return database.get(ind);
    }

    public int getNumMoves(RubiksCube cube) {
        return getNumMoves(getDatabaseIndex(cube));
    }

    public int getSize() {
        return size;
    }

    public int getNumItems() {
        return numItems;
    }

    public boolean isFull() {
        return numItems == size;
    }

    /** Write the database to a binary file. */
    public void toFile(String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(database.data());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write database to file: " + filePath, e);
        }
    }

    /** Read the database from a binary file. Returns true on success. */
    public boolean fromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return false;

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = database.data();
            if (file.length() != data.length) {
                throw new RuntimeException("Database corrupt! File size mismatch for: " + filePath);
            }
            int bytesRead = fis.read(data);
            if (bytesRead != data.length) {
                throw new RuntimeException("Failed to read complete database from: " + filePath);
            }
            this.numItems = this.size;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Reset the database, clearing all entries. */
    public void reset() {
        if (numItems != 0) {
            database.reset(0xFF);
            numItems = 0;
        }
    }
}
