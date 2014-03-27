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

	List<Node> nodes;
	List<Link> links;
	
	/**
	 * @param nodes
	 * @param links
	 */
	public Configuration(List<Node> nodes, List<Link> links) {
		this.nodes = nodes;
		this.links = links;
	}
	
}
