/**
 * 
 */
package roslab.model.electronics;

import java.util.Map;
import java.util.Set;

import roslab.model.general.Annotation;
import roslab.model.general.Endpoint;
<<<<<<< HEAD
import roslab.model.general.Feature;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 *
 */
public class Circuit extends Node implements Endpoint {
	
	Circuit spec;
	// TODO CircuitType type;
	// TODO List<CircuitResource> resources;

	/**
	 * @param name
	 * @param feature
	 * @param annotations
	 * @param spec
	 */
	public Circuit(String name, Map<String, Pin> pins,
			Set<Annotation> annotations, Circuit spec) {
		super(name, pins, annotations);
		this.spec = spec;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Pin> getPins() {
		return (Map<String, Pin>) this.features;
=======
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
>>>>>>> FETCH_HEAD
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#isFanIn()
	 */
	@Override
	public boolean isFanIn() {
<<<<<<< HEAD
		return true;
=======
		// TODO Auto-generated method stub
		return false;
>>>>>>> FETCH_HEAD
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#isFanOut()
	 */
	@Override
	public boolean isFanOut() {
<<<<<<< HEAD
		return true;
=======
		// TODO Auto-generated method stub
		return false;
>>>>>>> FETCH_HEAD
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#canConnect(roslab.model.general.Endpoint)
	 */
	@Override
	public boolean canConnect(Endpoint e) {
<<<<<<< HEAD
		// Only allow ROSPorts to connect to other ROSPorts
		if(e instanceof Circuit) {
			Circuit c = (Circuit) e;
			// TODO Perform pin analysis here.
		}
=======
		// TODO Auto-generated method stub
>>>>>>> FETCH_HEAD
		return false;
	}

}
