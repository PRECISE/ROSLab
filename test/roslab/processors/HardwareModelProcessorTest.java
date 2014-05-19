/**
 * 
 */
package roslab.processors;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import roslab.model.general.Node;
import roslab.model.hardware.HWBlock;
import roslab.model.hardware.HWBlockType;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;

/**
 * @author Peter Gebhard
 *
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
		
		nodes.add(new UINode("brain", null, new HWBlock("brain", null, brainAnn, null, HWBlockType.Brains), 0, 0));
		nodes.add(new UINode("half", null, new HWBlock("half", null, null, null, HWBlockType.HalfAnt), 0, 0));

		c = new Configuration(nodes, links);
		str = Files.readAllBytes(Paths.get("HWBotTest.py")).toString();
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
	 * Test method for {@link roslab.processors.HardwareModelProcessor#output()}.
	 */
	@Test
	public void testOutput() {
		HardwareModelProcessor hmp = new HardwareModelProcessor(c);
		org.junit.Assert.assertTrue(str.equals(hmp.output()));
	}

}
