/**
 * 
 */
package roslab.model.software;

import java.util.Map;
import java.util.Set;

import roslab.model.general.Annotation;
import roslab.model.general.Node;
import roslab.model.general.Feature;

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
			Set<Annotation> annotations, ROSNode spec) {
		super(name, ports, annotations);
		this.spec = spec;
	}

}
