/**
 * 
 */
package roslab.processors;

import java.util.List;

import org.stringtemplate.v4.ST;

import roslab.model.general.Configuration;
import roslab.model.general.Link;
import roslab.model.general.Node;
import roslab.model.hardware.HWBlock;

/**
 * @author shaz
 *
 */
public class HardwareModelProcessor extends ModelProcessor {

	/**
	 * @param config
	 */
	public HardwareModelProcessor(Configuration config) {
		super(config);
		st = stg.getInstanceOf("HWBot");
	}

	/* (non-Javadoc)
	 * @see roslab.processors.ModelProcessor#output()
	 */
	@Override
	public String output() {
		for (Node n : config.getNodes()) {
			if (n instanceof HWBlock) {
				List<Link> hwlinks = config.getLinks(n);
			}
		}
		st.add("add_components", printAddComponentsSection());
		st.add("set_sub_parameters", printSetSubParametersSection());
		st.add("append_components", printAppendComponentsSection());
		st.add("attach_components", printAttachComponentsSection());
		st.add("add_tabs", printAddTabsSection());
		st.add("set_parameters", printSetParametersSection());
		return st.render();
	}
	
	private String printAddComponentsSection(List<HWBlock> blocks) {
		String str = "";
		for (HWBlock block : blocks) {
			str.concat("self.addComponent(\"" + block.getName() + "\", " + block.getType().name() + ", None)\n");
		}
		return str;
	}
	
	private String printSetSubParametersSection() {
		String str = "";
		return str;
	}
	
	private String printAppendComponentsSection() {
		String str = "";
		return str;
	}
	
	private String printAttachComponentsSection() {
		String str = "";
		return str;
	}
	
	private String printAddTabsSection() {
		String str = "";
		return str;
	}
	
	private String printSetParametersSection() {
		String str = "";
		return str;
	}

}
