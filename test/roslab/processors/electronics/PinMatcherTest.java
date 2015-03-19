package roslab.processors.electronics;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
        assertEquals(matchMatrix, PinMatcher.match(testMatrix));
    }

}
