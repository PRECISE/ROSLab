/**
 * 
 */
package roslab.model.electronics;

import java.util.List;

import roslab.model.general.Endpoint;
import roslab.model.general.Link;

/**
 * @author Peter Gebhard
 *
 */
public class WireBundle extends Link {
	
	List<Wire> wires;

	/**
	 * @param src
	 * @param dest
	 */
	public WireBundle(Endpoint src, Endpoint dest, List<Wire> wires) {
		super(src, dest);
		this.wires = wires;
	}

}
