/**
 *
 */
package roslab.processors.general;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import roslab.model.software.ROSPortType;
import roslab.processors.general.Platform.Device;

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
        p.devices.add(p.new Device("IMU1", "/imu1", new ROSPortType("IMU")));
        p.devices.add(p.new Device("IMU2", "/imu2", new ROSPortType("IMU")));
        p.devices.add(p.new Device("GPS", "/gps", new ROSPortType("GPS")));
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
     * {@link roslab.processors.general.PlatformParser#PlatformParser(java.io.File)}
     * .
     */
    @Test
    public void testPlatformParser() {
        System.out.println(yaml.dump(p));
    }

}
