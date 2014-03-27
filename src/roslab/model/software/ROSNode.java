/**
 * 
 */
package roslab.model.software;

import java.util.Map;
import java.util.Set;

import roslab.model.general.Annotation;
import roslab.model.general.Node;
import roslab.model.general.Property;

/**
 * @author Peter Gebhard
 *
 */
public class ROSNode extends Node {
	
	ROSNode spec;

	/**
	 * @param name
	 * @param properties
	 * @param annotations
	 * @param spec
	 */
	public ROSNode(String name, Map<String, Property> properties,
			Set<Annotation> annotations, ROSNode spec) {
		super(name, properties, annotations);
		this.spec = spec;
	}

}
