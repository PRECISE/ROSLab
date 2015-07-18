/**
 * This test class is used to test the LibraryParser class.
 */
package roslab.processors.general;

import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Link;
import roslab.model.software.ROSNode;
import roslab.model.ui.UINode;

/**
 * @author Peter Gebhard
 */
public class ConfigurationParserTest {

    static Library lib = new Library();
    static Configuration config = new Configuration("TestConfiguration", lib);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        lib = LibraryParser.parseLibraryYAML(Paths.get("resources", "platforms", "TestLibrary.yaml").toFile());
        config.setLibrary(lib);

        // Build nodes
        ROSNode n1 = new ROSNode("MyIMU", (ROSNode) lib.getNode("IMU1"));
        n1.setUINode(new UINode(n1, 5, 5));
        config.addNode(n1);

        ROSNode n2 = new ROSNode("MyIMU2", (ROSNode) lib.getNode("IMU2"));
        n2.setUINode(new UINode(n2, 15, 15));
        config.addNode(n2);

        ROSNode n3 = new ROSNode("MyController", (ROSNode) lib.getNode("Controller"));
        n3.setUINode(new UINode(n3, 25, 25));
        config.addNode(n3);

        // Build links
        Link l1 = new Link(n1.getEndpoint("/imu1"), n3.getEndpoint("/imu1"));
        config.addLink(l1);

        Link l2 = new Link(n2.getEndpoint("/imu2"), n3.getEndpoint("/imu2"));
        config.addLink(l2);
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
     * {@link roslab.processors.general.ConfigurationParser#parseRequiredLibrary(java.io.File)}
     * .
     */
    @Test
    public void testParseRequiredLibrary() {
        Library testLib = ConfigurationParser.parseRequiredLibrary(Paths.get("resources", "platforms", "TestConfiguration.yaml").toFile());
        assertTrue(LibraryParser.emitLibraryYAML(testLib).equals(LibraryParser.emitLibraryYAML(lib)));
    }

    /**
     * Test method for
     * {@link roslab.processors.general.ConfigurationParser#parseRequiredLibrary(java.io.File)}
     * .
     */
    @Test
    public void testParseConfigurationYAML() {
        Configuration testConfig = ConfigurationParser.parseConfigurationYAML(Paths.get("resources", "platforms", "TestConfiguration.yaml").toFile());
        assertTrue(ConfigurationParser.emitConfigurationYAML(testConfig).equals(ConfigurationParser.emitConfigurationYAML(config)));
    }
}
