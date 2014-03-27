/**
 * 
 */
package roslab.model.electronics;

import java.util.Map;
import java.util.Set;

import roslab.model.general.Annotation;
import roslab.model.general.Endpoint;
import roslab.model.general.Node;
import roslab.model.general.Property;

/**
 * @author shaz
 *
 */
public class Circuit extends Node implements Endpoint {

	/**
	 * @param name
	 * @param properties
	 * @param annotations
	 */
	public Circuit(String name, Map<String, Property> properties,
			Set<Annotation> annotations) {
		super(name, properties, annotations);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#isFanIn()
	 */
	@Override
	public boolean isFanIn() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#isFanOut()
	 */
	@Override
	public boolean isFanOut() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#canConnect(roslab.model.general.Endpoint)
	 */
	@Override
	public boolean canConnect(Endpoint e) {
		// TODO Auto-generated method stub
		return false;
	}

}
