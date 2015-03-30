package roslab.processors.electronics;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PinMatcherTest {

    private Integer[][] testMatrix = { { 1, 1, 0, 0, 0 }, { 1, 0, 1, 1, 0 }, { 0, 1, 1, 0, 1 }, { 1, 0, 1, 1, 0 }, { 0, 1, 0, 0, 1 } };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMatch() {
        Map<Integer, Integer> matchMatrix = new HashMap<Integer, Integer>();
        matchMatrix.put(0, 0);
        matchMatrix.put(1, 3);
        matchMatrix.put(2, 1);
        matchMatrix.put(3, 2);
        matchMatrix.put(4, 4);
        // assertEquals(matchMatrix, PinMatcher.match(testMatrix));
        Integer[][] randMatrix = randomMatrix();
        Integer[][] beginMatrix = copyMatrix(randMatrix);
        matchMatrix = PinMatcher.match(randMatrix, null, null);
        while (matchMatrix == null) {
            randMatrix = randomMatrix();
            beginMatrix = copyMatrix(randMatrix);
            matchMatrix = PinMatcher.match(randMatrix, null, null);
        }
        System.out.println(beginMatrix);
    }

    private Integer[][] randomMatrix() {
        Random rand = new Random();
        int rows = 3 + rand.nextInt(8);
        int cols = 3 + rand.nextInt(8);
        Integer[][] matrix = new Integer[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                matrix[r][c] = rand.nextInt(2);
            }
        }
        return matrix;
    }

    private Integer[][] copyMatrix(Integer[][] m) {
        int rows = m.length;
        int cols = m[0].length;
        Integer[][] matrix = new Integer[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                matrix[r][c] = m[r][c];
            }
        }
        return matrix;
    }

}
