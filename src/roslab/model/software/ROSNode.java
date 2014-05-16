/**
 * 
 */
package roslab.model.software;

import java.util.Map;

import roslab.model.general.Node;

/**
 * @author shaz
 *
 */
public class ROSNode extends Node {
	
	ROSNode spec;

	/**
	 * @param name
	 * @param feature
	 * @param annotations
	 * @param spec
	 */
	public ROSNode(String name, Map<String, ROSPort> ports,
			Map<String, String> annotations, ROSNode spec) {
		super(name, ports, annotations);
		this.spec = spec;
	}

}
