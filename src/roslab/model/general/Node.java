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
	
	protected String name;
	protected Map<String, ? extends Feature> features;
	protected Set<Annotation> annotations;
	
	/**
	 * @param name
	 * @param feature
	 * @param annotations
	 */
	public Node(String name, Map<String, ? extends Feature> features,
			Set<Annotation> annotations) {
		this.name = name;
		this.features = features;
		this.annotations = annotations;
	}

}
