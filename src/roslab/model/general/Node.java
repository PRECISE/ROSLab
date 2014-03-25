/**
 * 
 */
package roslab.model.general;

import java.util.Map;
import java.util.Set;

/**
 * @author shaz
 *
 */
public class Node {
	
	String name;
	Map<String, Property> properties;
	Set<Annotation> annotations;
	
	/**
	 * @param name
	 * @param properties
	 * @param annotations
	 */
	public Node(String name, Map<String, Property> properties,
			Set<Annotation> annotations) {
		this.name = name;
		this.properties = properties;
		this.annotations = annotations;
	}

}
