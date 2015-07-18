/**
 * This test class is used to test the LibraryParser class.
 */
package roslab.processors.general;

import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import roslab.model.general.Library;
import roslab.model.general.Node;
import roslab.model.software.ROSMsgType;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.software.ROSTopic;

/**
 * @author Peter Gebhard
 */
public class LibraryParserTest {

    static Library lib = new Library();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        lib.setName("TestLibrary");
        lib.setNodes(new ArrayList<Node>());

        ROSNode n1 = new ROSNode("IMU1");
        n1.addPort(new ROSPort("/imu1", n1, new ROSTopic("/imu1", new ROSMsgType("Imu"), false), false, false));
        lib.addNode(n1);

        ROSNode n2 = new ROSNode("IMU2");
        n2.addPort(new ROSPort("/imu2", n2, new ROSTopic("/imu2", new ROSMsgType("Imu"), false), false, false));
        lib.addNode(n2);

        ROSNode n3 = new ROSNode("GPS");
        n3.addPort(new ROSPort("/gps", n3, new ROSTopic("/gps", new ROSMsgType("NavSatFix"), false), false, false));
        lib.addNode(n3);

        ROSNode n4 = new ROSNode("Joystick");
        n4.addPort(new ROSPort("/joy", n4, new ROSTopic("/joy", new ROSMsgType("Joy"), false), false, false));
        lib.addNode(n4);

        ROSNode n5 = new ROSNode("Cmd_Vel");
        n5.addPort(new ROSPort("/cmd_vel", n5, new ROSTopic("/cmd_vel", new ROSMsgType("Twist"), true), false, false));
        lib.addNode(n5);

        ROSNode n6 = new ROSNode("Controller");
        n6.setCustomFlag(true);
        n6.addPort(new ROSPort("/imu1", n6, new ROSTopic("/imu1", new ROSMsgType("Imu"), true), false, false));
        n6.addPort(new ROSPort("/imu2", n6, new ROSTopic("/imu2", new ROSMsgType("Imu"), true), false, false));
        n6.addPort(new ROSPort("/cmd_vel", n6, new ROSTopic("/cmd_vel", new ROSMsgType("Twist"), false), false, false));
        lib.addNode(n6);
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
     * {@link roslab.processors.general.LibraryParser#LibraryParser(java.io.File)}
     * .
     */
    @Test
    public void testLibraryParser() {
        Library testLib = LibraryParser.parseLibraryYAML(Paths.get("resources", "platforms", "TestLibrary.yaml").toFile());
        assertTrue(LibraryParser.emitLibraryYAML(testLib).equals(LibraryParser.emitLibraryYAML(lib)));
    }

}
