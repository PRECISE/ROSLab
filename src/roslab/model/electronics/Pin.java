/**
 * 
 */
package roslab.model.electronics;

import java.util.List;
import java.util.Set;

import roslab.model.general.Annotation;
import roslab.model.general.Feature;

/**
 * @author Peter Gebhard
 *
 */
public class Pin extends Feature {
	
	List<PinService> services;

	/**
	 * @param name
	 * @param parent
	 * @param annotations
	 */
	public Pin(String name, Circuit parent, Set<Annotation> annotations, List<PinService> services) {
		super(name, parent, annotations);
		this.services = services;
	}

}
