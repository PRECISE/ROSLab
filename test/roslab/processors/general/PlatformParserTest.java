/**
 *
 */
package roslab.processors.general;

import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import roslab.model.software.Platform;
import roslab.model.software.Platform.Device;
import roslab.model.software.ROSMsgType;
import roslab.processors.software.PlatformParser;

/**
 * @author Peter Gebhard
 */
public class PlatformParserTest {

    static Platform p = new Platform();
    static Yaml yaml = new Yaml();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        p.name = "TestPlatform";
        p.devices = new ArrayList<Device>();
        p.devices.add(new Device("IMU1", "/imu1", new ROSMsgType("IMU")));
        p.devices.add(new Device("IMU2", "/imu2", new ROSMsgType("IMU")));
        p.devices.add(new Device("GPS", "/gps", new ROSMsgType("GPS")));
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
     * {@link roslab.processors.software.PlatformParser#PlatformParser(java.io.File)}
     * .
     */
    @Test
    public void testPlatformParser() {
        // System.out.println(yaml.dump(p));

        PlatformParser pp = new PlatformParser(Paths.get("resources", "platforms", "test.yaml").toFile());
        System.out.println(pp.platform);

    }

}
