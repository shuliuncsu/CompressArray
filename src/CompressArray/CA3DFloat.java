package CompressArray;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Compressed 3-D float array (line compression version). This array cost same
 * amount of memory at runtime, but is compressed in serialization. This class
 * provides several utility functions for changing value in the array. Ideal
 * when the data in the array is well organized.
 *
 *
 * @author Shu Liu
 */
public class CA3DFloat implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 33491658567234L;

    /**
     * Size of the first index
     */
    private int sizeX;

    /**
     * Size of the second index
     */
    private int sizeY;

    /**
     * Size of the third index
     */
    private int sizeZ;

    /**
     * 3D array contains the full matrix
     */
    private float[][][] fullMatrix;

    /**
     * Array of unique numbers for compression
     */
    private float[] lineNum;

    /**
     * Array of number of repetitions for compression
     */
    private int[] lineRep;

    /**
     * Constructor of compress 3D array
     *
     * @param sizeX size of the first index
     * @param sizeY size of the second index
     * @param sizeZ size of the third index
     * @param defaultValue default value in the array
     */
    public CA3DFloat(int sizeX, int sizeY, int sizeZ, int defaultValue) {
        if (sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) {
            throw new IllegalArgumentException("Invalid size");
        }

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        fullMatrix = new float[sizeX][sizeY][sizeZ];

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    fullMatrix[x][y][z] = defaultValue;
                }
            }
        }
    }

    /**
     * Set a value to a single location in the array
     *
     * @param value new value
     * @param x first index
     * @param y second index
     * @param z third index
     */
    public void set(float value, int x, int y, int z) {
        fullMatrix[x][y][z] = value;
    }

    /**
     * Set a value to a region in the array
     *
     * @param value new value
     * @param fromX from first index (inclusive)
     * @param fromY from second index (inclusive)
     * @param fromZ from third index (inclusive)
     * @param toX to first index (inclusive)
     * @param toY to second index (inclusive)
     * @param toZ to third index (inclusive)
     */
    public void set(float value, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    fullMatrix[x][y][z] = value;
                }
            }
        }
    }

    /**
     * Add a value to a single location in the array
     *
     * @param value value to be added
     * @param x first index
     * @param y second index
     * @param z third index
     */
    public void add(float value, int x, int y, int z) {
        fullMatrix[x][y][z] += value;
    }

    /**
     * Add a value to a region in the array
     *
     * @param value value to be added
     * @param fromX from first index (inclusive)
     * @param fromY from second index (inclusive)
     * @param fromZ from third index (inclusive)
     * @param toX to first index (inclusive)
     * @param toY to second index (inclusive)
     * @param toZ to third index (inclusive)
     */
    public void add(float value, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    fullMatrix[x][y][z] += value;
                }
            }
        }
    }

    /**
     * Multiply a value to a single location in the array
     *
     * @param value value to be multiplied
     * @param x first index
     * @param y second index
     * @param z third index
     */
    public void multiply(float value, int x, int y, int z) {
        fullMatrix[x][y][z] *= value;
    }

    /**
     * Multiply a value to a region in the array
     *
     * @param value value to be multiplied
     * @param fromX from first index (inclusive)
     * @param fromY from second index (inclusive)
     * @param fromZ from third index (inclusive)
     * @param toX to first index (inclusive)
     * @param toY to second index (inclusive)
     * @param toZ to third index (inclusive)
     */
    public void multiply(float value, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    fullMatrix[x][y][z] *= value;
                }
            }
        }
    }

    /**
     * Get a single value from the array
     *
     * @param x first index
     * @param y second index
     * @param z third index
     * @return a single value from the array
     */
    public float get(int x, int y, int z) {
        return fullMatrix[x][y][z];
    }

    /**
     * Getter for the size of first dimension
     *
     * @return size of first dimension
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Getter for the size of second dimension
     *
     * @return size of second dimension
     */
    public int getSizeY() {
        return sizeY;
    }

    /**
     * Getter for the size of third dimension
     *
     * @return size of third dimension
     */
    public int getSizeZ() {
        return sizeZ;
    }

    /**
     * Write object to output stream
     *
     * @param out object output stream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        compressMatrix();
        out.writeInt(sizeX);
        out.writeInt(sizeY);
        out.writeInt(sizeZ);
        out.writeObject(lineNum);
        out.writeObject(lineRep);
        lineNum = null;
        lineRep = null;
    }

    /**
     * Read object from input stream
     *
     * @param in object input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        sizeX = in.readInt();
        sizeY = in.readInt();
        sizeZ = in.readInt();
        lineNum = (float[]) in.readObject();
        lineRep = (int[]) in.readObject();
        expandMatrix();
        lineNum = null;
        lineRep = null;
    }

    /**
     * Prepare compressed data from full matrix to write out
     */
    private void compressMatrix() {
        float temp = fullMatrix[0][0][0];
        int count = 0;
        ArrayList<Float> number = new ArrayList<>();
        ArrayList<Integer> repetition = new ArrayList<>();

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    if (fullMatrix[x][y][z] != temp) {
                        number.add(temp);
                        repetition.add(count);
                        temp = fullMatrix[x][y][z];
                        count = 1;
                    } else {
                        count++;
                    }
                }
            }
        }
        number.add(temp);
        repetition.add(count);

        lineNum = new float[number.size()];
        for (int index = 0; index < lineNum.length; index++) {
            lineNum[index] = number.get(index);
        }
        lineRep = new int[repetition.size()];
        for (int index = 0; index < lineRep.length; index++) {
            lineRep[index] = repetition.get(index);
        }
    }

    /**
     * Expand compressed data to full matrix
     */
    private void expandMatrix() {

        int index = 0;
        float value = lineNum[index];
        int count = lineRep[index];

        fullMatrix = new float[sizeX][sizeY][sizeZ];

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    if (count == 0) {
                        index++;
                        value = lineNum[index];
                        count = lineRep[index];
                    }
                    fullMatrix[x][y][z] = value;
                    count--;
                }
            }
        }
    }

    /**
     * Initialize a compressed matrix by creating a deep copy
     *
     * @param matrix matrix to be copied from
     */
    public void deepCopyFrom(CA3DFloat matrix) {
        sizeX = matrix.sizeX;
        sizeY = matrix.sizeY;
        sizeZ = matrix.sizeZ;

        fullMatrix = new float[sizeX][sizeY][sizeZ];

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                for (int k = 0; k < sizeZ; k++) {
                    fullMatrix[i][j][k] = matrix.fullMatrix[i][j][k];
                }
            }
        }
    }
}
