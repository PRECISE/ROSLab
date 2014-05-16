/**
 * 
 */
package roslab.model.software;

import java.util.Set;

import roslab.model.general.Annotation;
import roslab.model.general.Endpoint;
import roslab.model.general.Feature;

/**
 * @author Peter Gebhard
 *
 */
public class ROSPort extends Feature implements Endpoint {
	
	ROSPortType type;
	boolean direction;
	boolean fanIn;
	boolean fanOut;

	/**
	 * @param name
	 * @param parent
	 * @param annotations
	 * @param type
	 * @param direction
	 * @param fanIn
	 * @param fanOut
	 */
	public ROSPort(String name, ROSNode parent, Set<Annotation> annotations, ROSPortType type, boolean direction, boolean fanIn, boolean fanOut) {
		super(name, parent, annotations);
		this.type = type;
		this.direction = direction;
		this.fanIn = fanIn;
		this.fanOut = fanOut;
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#isFanIn()
	 */
	@Override
	public boolean isFanIn() {
		return fanIn;
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#isFanOut()
	 */
	@Override
	public boolean isFanOut() {
		return fanOut;
	}

	/* (non-Javadoc)
	 * @see roslab.model.general.Endpoint#canConnect(roslab.model.general.Endpoint)
	 */
	@Override
	public boolean canConnect(Endpoint e) {
		// Only allow ROSPorts to connect to other ROSPorts
		if(e instanceof ROSPort) {
			ROSPort p = (ROSPort) e;
			// Valid connection if directions are opposite, but types match.
			return (this.direction != p.direction) && (this.type == p.type);
		}
		return false;
	}

}
