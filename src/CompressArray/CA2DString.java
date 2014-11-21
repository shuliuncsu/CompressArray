package CompressArray;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Compressed 2-D String array (line compression version). This array cost same
 * amount of memory at runtime, but is compressed in serialization. This class
 * provides several utility functions for changing value in the array. Ideal
 * when the data in the array is well organized.
 *
 * @author Shu Liu
 */
public class CA2DString implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 33491658567234L;

    /**
     * Number of rows
     */
    private int sizeX;

    /**
     * Number of columns
     */
    private int sizeY;

    /**
     * 2D array contains the full matrix
     */
    private String[][] fullMatrix;

    /**
     * Array of unique numbers for compression
     */
    private String[] lineNum;

    /**
     * Array of number of repetitions for compression
     */
    private int[] lineRep;

    /**
     * Constructor of compress 2D array
     *
     * @param sizeX Number of rows
     * @param sizeY Number of columns
     * @param defaultValue Default value in the array
     */
    public CA2DString(int sizeX, int sizeY, String defaultValue) {
        if (sizeX <= 0 || sizeY <= 0) {
            throw new IllegalArgumentException("Invalid size");
        }

        this.sizeX = sizeX;
        this.sizeY = sizeY;

        fullMatrix = new String[sizeX][sizeY];

        //Fill the array with default value
        if (defaultValue != null) {
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    fullMatrix[x][y] = defaultValue;
                }
            }
        }
    }

    /**
     * Set a value to a single location in the array
     *
     * @param value new value
     * @param x row index
     * @param y column index
     */
    public void set(String value, int x, int y) {
        fullMatrix[x][y] = value;
    }

    /**
     * Set a value to a region in the array
     *
     * @param value new value
     * @param fromX from row index (inclusive)
     * @param fromY from column index (inclusive)
     * @param toX to row index (inclusive)
     * @param toY to column index (inclusive)
     */
    public void set(String value, int fromX, int fromY, int toX, int toY) {
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                fullMatrix[x][y] = value;
            }
        }
    }

    /**
     * Add a value to a single location in the array
     *
     * @param value value to be added
     * @param x row index
     * @param y column index
     */
    public void add(String value, int x, int y) {
        fullMatrix[x][y] += value;
    }

    /**
     * Add a value to a region in the array
     *
     * @param value value to be added
     * @param fromX from row index (inclusive)
     * @param fromY from column index (inclusive)
     * @param toX to row index (inclusive)
     * @param toY to column index (inclusive)
     */
    public void add(String value, int fromX, int fromY, int toX, int toY) {
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                fullMatrix[x][y] += value;
            }
        }
    }

    /**
     * Add one or more rows to array
     *
     * @param beforeRow row index before which new rows to be added
     * @param numRows number of new rows to be added
     * @param defaultValue default value for new added rows
     */
    public void addRow(int beforeRow, int numRows, String defaultValue) {
        sizeX += numRows;

        String[][] newMatrix = new String[sizeX][sizeY];

        for (int i = 0; i < beforeRow; i++) {
            newMatrix[i] = fullMatrix[i];
        }

        for (int i = beforeRow; i < beforeRow + numRows; i++) {
            for (int j = 0; j < sizeY; j++) {
                newMatrix[i][j] = defaultValue;
            }
        }

        for (int i = beforeRow + numRows; i < sizeX; i++) {
            newMatrix[i] = fullMatrix[i - numRows];
        }

        fullMatrix = newMatrix;
    }

    /**
     * Add one or more columns to array
     *
     * @param beforeColumn column index before which new columns to be added
     * @param numColumns number of new columns to be added
     * @param defaultValue default value for new added columns
     */
    public void addColumn(int beforeColumn, int numColumns, String defaultValue) {
        sizeY += numColumns;

        String[][] newMatrix = new String[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < beforeColumn; j++) {
                newMatrix[i][j] = fullMatrix[i][j];
            }

            for (int j = beforeColumn; j < beforeColumn + numColumns; j++) {
                newMatrix[i][j] = defaultValue;
            }

            for (int j = beforeColumn + numColumns; j < sizeY; j++) {
                newMatrix[i][j] = fullMatrix[i][j - numColumns];
            }
        }

        fullMatrix = newMatrix;
    }

    /**
     * Remove a single row from array
     *
     * @param row row to be removed
     */
    public void removeRow(int row) {
        removeRow(row, row);
    }

    /**
     * Remove multiple rows from array
     *
     * @param fromRow first row to be removed (inclusive)
     * @param toRow last row to be removed (inclusive)
     */
    public void removeRow(int fromRow, int toRow) {
        sizeX -= 1 + toRow - fromRow;

        String[][] newMatrix = new String[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                newMatrix[i][j] = fullMatrix[i < fromRow ? i : i + 1 + toRow - fromRow][j];
            }
        }

        fullMatrix = newMatrix;
    }

    /**
     * Remove a single column from array
     *
     * @param column column to be removed
     */
    public void removeColumn(int column) {
        removeColumn(column, column);
    }

    /**
     * Remove multiple columns from array
     *
     * @param fromColumn first column to be removed (inclusive)
     * @param toColumn last column to be removed (inclusive)
     */
    public void removeColumn(int fromColumn, int toColumn) {
        sizeY -= 1 + toColumn - fromColumn;

        String[][] newMatrix = new String[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                newMatrix[i][j] = fullMatrix[i][j < fromColumn ? j : j + 1 + toColumn - fromColumn];
            }
        }

        fullMatrix = newMatrix;
    }

    /**
     * Get a single value from the array
     *
     * @param x row index
     * @param y column index
     *
     * @return a single value from the array
     */
    public String get(int x, int y) {
        return fullMatrix[x][y];
    }

    /**
     * Getter for number of rows
     *
     * @return number of rows
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Getter for the number of columns
     *
     * @return number of columns
     */
    public int getSizeY() {
        return sizeY;
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
        lineNum = (String[]) in.readObject();
        lineRep = (int[]) in.readObject();
        expandMatrix();
        lineNum = null;
        lineRep = null;
    }

    /**
     * Prepare compressed data from full matrix to write out
     */
    private void compressMatrix() {
        String temp = fullMatrix[0][0];
        int count = 0;
        ArrayList<String> number = new ArrayList<>();
        ArrayList<Integer> repetition = new ArrayList<>();

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (!fullMatrix[x][y].equals(temp)) {
                    number.add(temp);
                    repetition.add(count);
                    temp = fullMatrix[x][y];
                    count = 1;
                } else {
                    count++;
                }
            }
        }
        number.add(temp);
        repetition.add(count);

        lineNum = new String[number.size()];
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
        String value = lineNum[index];
        int count = lineRep[index];
        fullMatrix = new String[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (count == 0) {
                    index++;
                    value = lineNum[index];
                    count = lineRep[index];
                }
                fullMatrix[x][y] = value;
                count--;
            }
        }
    }

    /**
     * Initialize a compressed matrix by creating a deep copy
     *
     * @param matrix matrix to be copied from
     */
    public void deepCopyFrom(CA2DString matrix) {
        sizeX = matrix.sizeX;
        sizeY = matrix.sizeY;

        fullMatrix = new String[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                fullMatrix[i][j] = matrix.fullMatrix[i][j];
            }
        }
    }
}
