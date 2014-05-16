/**
 * 
 */
package roslab.model.electronics;

import java.util.Map;

import roslab.model.general.Endpoint;
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
			Map<String, String> annotations, Circuit spec) {
		super(name, pins, annotations);
		this.spec = spec;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Pin> getPins() {
		return (Map<String, Pin>) this.features;
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#isFanIn()
	 */
	@Override
	public boolean isFanIn() {
		return true;
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#isFanOut()
	 */
	@Override
	public boolean isFanOut() {
		return true;
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#canConnect(roslab.model.general.Endpoint)
	 */
	@Override
	public boolean canConnect(Endpoint e) {
		// Only allow ROSPorts to connect to other ROSPorts
		if(e instanceof Circuit) {
			Circuit c = (Circuit) e;
			// TODO Perform pin analysis here.
		}
		return false;
	}

}
