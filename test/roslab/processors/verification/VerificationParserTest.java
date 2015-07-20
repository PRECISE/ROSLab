/**
 * This test class is used to test the LibraryParser class.
 */
package roslab.processors.verification;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import roslab.model.verification.ElectricalSpecs;
import roslab.model.verification.MechanicalSpecs;
import roslab.model.verification.VerificationDetails;
import roslab.model.verification.VerificationType;

/**
 * @author Peter Gebhard
 */
public class VerificationParserTest {

    static VerificationDetails verif;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ElectricalSpecs elecSpecs = new ElectricalSpecs();
        MechanicalSpecs mechSpecs = new MechanicalSpecs();

        verif = new VerificationDetails("Motor_Brushed_20x7mm", VerificationType.Motor, "Put words here.", elecSpecs, mechSpecs);

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link roslab.processors.verification.VerificationParser#parseVerificationYAML(java.io.File)}
     * .
     */
    @Test
    public void testParseVerificationYAML() {
        VerificationDetails testVerif = VerificationParser.parseVerificationYAML(Paths.get("resources", "electronics_lib", "TestVerification.yaml")
                .toFile());
        System.out.println(testVerif);
    }
}
