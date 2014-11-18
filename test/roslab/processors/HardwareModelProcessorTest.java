/**
 *
 */
package roslab.processors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import roslab.model.general.Configuration;
import roslab.model.general.Feature;
import roslab.model.general.Link;
import roslab.model.mechanics.HWBlock;
import roslab.model.mechanics.HWBlockType;
import roslab.model.mechanics.Joint;
import roslab.model.ui.UIEndpoint;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;

/**
 * @author Peter Gebhard
 */
public class HardwareModelProcessorTest {

    static Configuration c;
    static String str;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        List<UINode> nodes = new ArrayList<UINode>();
        List<UILink> links = new ArrayList<UILink>();

        Map<String, String> brainAnn = new HashMap<String, String>();
        brainAnn.put("length", "48");
        brainAnn.put("height", "25");
        brainAnn.put("hardware", "proMini");

        Map<String, String> halfAnn = new HashMap<String, String>();
        halfAnn.put("width", "self.getParameter('brain').getParameter('width')");

        HWBlock brain = new HWBlock("brain", null, brainAnn, null, HWBlockType.Brains);
        HWBlock half1 = new HWBlock("half1", null, halfAnn, null, HWBlockType.HalfAnt);
        HWBlock half2 = new HWBlock("half2", null, halfAnn, null, HWBlockType.HalfAnt);

        Map<String, Joint> jmap1 = new HashMap<String, Joint>();
        brain.setFeatures(jmap1);
        Joint j11 = new Joint("topright", brain, null, false, false);
        Joint j12 = new Joint("topleft", brain, null, false, false);
        Joint j13 = new Joint("botright", brain, null, false, false);
        Joint j14 = new Joint("botleft", brain, null, false, false);
        brain.addJoint(j11);
        brain.addJoint(j12);
        brain.addJoint(j13);
        brain.addJoint(j14);

        Map<String, Feature> jmap2 = new HashMap<String, Feature>();
        Joint j21 = new Joint("topright", half1, null, false, false);
        Joint j22 = new Joint("topleft", half1, null, false, false);
        Joint j23 = new Joint("botright", half1, null, false, false);
        Joint j24 = new Joint("botleft", half1, null, false, false);
        jmap2.put(j21.getName(), j21);
        jmap2.put(j22.getName(), j22);
        jmap2.put(j23.getName(), j23);
        jmap2.put(j24.getName(), j24);
        half1.setFeatures(jmap2);

        Map<String, Feature> jmap3 = new HashMap<String, Feature>();
        half2.setFeatures(jmap3);
        Joint j31 = new Joint("topright", half2, null, false, false);
        Joint j32 = new Joint("topleft", half2, null, false, false);
        Joint j33 = new Joint("botright", half2, null, false, false);
        Joint j34 = new Joint("botleft", half2, null, false, false);
        half2.addJoint(j31);
        half2.addJoint(j32);
        half2.addJoint(j33);
        half2.addJoint(j34);

        UINode n1 = new UINode(brain, 0, 0);
        UINode n2 = new UINode(half1, 0, 0);
        UINode n3 = new UINode(half2, 0, 0);

        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);

        links.add(new UILink(new Link(j13, j21), new UIEndpoint(j13, n1, 0, 0, false), new UIEndpoint(j21, n2, 0, 0, false)));
        links.add(new UILink(new Link(j12, j31), new UIEndpoint(j12, n1, 0, 0, false), new UIEndpoint(j31, n3, 0, 0, false)));

        c = new Configuration(nodes, links);
        str = new String(Files.readAllBytes(Paths.get("test/roslab/processors/HWBotTest.py")));
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
     * Test method for {@link roslab.processors.HardwareModelProcessor#output()}
     * .
     */
    @Test
    public void testOutput() {
        HardwareModelProcessor hmp = new HardwareModelProcessor(c);
        try {
            Files.write(Paths.get("HWBotTestOutput.py"), hmp.output().getBytes(), StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        org.junit.Assert.assertTrue(str.equals(hmp.output()));
    }

}
