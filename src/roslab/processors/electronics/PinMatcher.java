/**
 *
 */
package roslab.processors.electronics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roslab.model.electronics.Pin;

/**
 * Pin Matching algorithm
 *
 * @author Peter Gebhard
 */
public class PinMatcher {

    /**
     * Solves a pin-match matrix.
     *
     * @return a mapping of matchings
     */
    public static Map<Integer, Integer> match(Integer[][] pinMatrix, Pin[] rowPins, Pin[] colPins) {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();

        while (remainingConnectables(pinMatrix)) {
            Integer[] rowSums = rowSums(pinMatrix);
            Integer[] colSums = columnSums(pinMatrix);

            int minTotal = Integer.MAX_VALUE;
            int minRow = 0;
            int minCol = 0;

            List<Integer> rowSumIndices = minNonZeroSum(rowSums);
            for (Integer r : rowSumIndices) {
                int c = maskedMinNonZeroSumIndex(colSums, pinMatrix[r]);
                if (colSums[c] < minTotal) {
                    minRow = r;
                    minCol = c;
                    minTotal = colSums[c];
                }
            }

            result.put(minRow, minCol);

            maskMatrixRow(pinMatrix, minRow);

            // Mask a column if that column pin is not one-to-many
            if (rowPins == null || colPins == null
                    || colPins[minCol].getServiceByName(rowPins[minCol].getAssignedService().getName()).getOne_to_many() != '+') {
                maskMatrixColumn(pinMatrix, minCol);
            }
        }

        // Return the match result
        return result;
    }

    private static List<Integer> minNonZeroSum(Integer[] sums) {
        if (sums.length == 0) {
            throw new IllegalArgumentException();
        }

        int min = Integer.MAX_VALUE;
        List<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < sums.length; i++) {
            if (sums[i] > 0 && sums[i] < min) {
                min = sums[i];
            }
        }

        for (int i = 0; i < sums.length; i++) {
            if (sums[i] == min) {
                indices.add(i);
            }
        }

        return indices;
    }

    private static int maskedMinNonZeroSumIndex(Integer[] sums, Integer[] mask) {
        if (sums.length == 0 || mask.length == 0 || sums.length != mask.length) {
            throw new IllegalArgumentException();
        }

        int sum = Integer.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < sums.length; i++) {
            if (sums[i] > 0 && sums[i] < sum && mask[i] > 0) {
                sum = sums[i];
                index = i;
            }
        }

        return index;
    }

    private static void maskMatrixRow(Integer[][] pinMatrix, int row) {
        for (int j = 0; j < pinMatrix[row].length; j++) {
            pinMatrix[row][j] = -1;
        }
    }

    private static void maskMatrixColumn(Integer[][] pinMatrix, int col) {
        for (int i = 0; i < pinMatrix.length; i++) {
            pinMatrix[i][col] = -1;
        }
    }

    private static Integer[] rowSums(Integer[][] pinMatrix) {
        Integer[] result = new Integer[pinMatrix.length];
        for (int e = 0; e < result.length; e++) {
            result[e] = 0;
        }

        for (int i = 0; i < pinMatrix.length; i++) {
            for (int j = 0; j < pinMatrix[i].length; j++) {
                if (pinMatrix[i][j] >= 0) {
                    result[i] += pinMatrix[i][j];
                }
            }
        }

        return result;
    }

    private static Integer[] columnSums(Integer[][] pinMatrix) {
        Integer[] result = new Integer[pinMatrix[0].length];
        for (int e = 0; e < result.length; e++) {
            result[e] = 0;
        }

        for (int i = 0; i < pinMatrix.length; i++) {
            for (int j = 0; j < pinMatrix[i].length; j++) {
                if (pinMatrix[i][j] >= 0) {
                    result[j] += pinMatrix[i][j];
                }
            }
        }

        return result;
    }

    private static boolean fullyConnected(Integer[][] pinMatrix) {
        for (int i = 0; i < pinMatrix.length; i++) {
            for (int j = 0; j < pinMatrix[i].length; j++) {
                if (pinMatrix[i][j] >= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean remainingConnectables(Integer[][] pinMatrix) {
        for (int i = 0; i < pinMatrix.length; i++) {
            for (int j = 0; j < pinMatrix[i].length; j++) {
                if (pinMatrix[i][j] >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

}
