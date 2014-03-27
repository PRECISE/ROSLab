/**
 * 
 */
package roslab.model.general;

import java.util.List;

/**
 * @author shaz
 *
 */
public class Configuration {

	List<? extends Node> nodes;
	List<? extends Link> links;
	
	/**
	 * @param nodes
	 * @param links
	 */
	public Configuration(List<? extends Node> nodes, List<? extends Link> links) {
		this.nodes = nodes;
		this.links = links;
	}
	
}
