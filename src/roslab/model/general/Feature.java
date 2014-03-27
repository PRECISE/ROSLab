/**
 * 
 */
package roslab.model.general;

import java.util.Set;

/**
 * @author shaz
 *
 */
public class Feature {
	
	String name;
	Node parent;
	Set<Annotation> annotations;
	
	/**
	 * @param name
	 * @param parent
	 * @param annotations
	 */
	public Feature(String name, Node parent, Set<Annotation> annotations) {
		this.name = name;
		this.parent = parent;
		this.annotations = annotations;
	}

}
