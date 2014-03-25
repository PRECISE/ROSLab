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
	List<Channel> channels;
	
	/**
	 * @param nodes
	 * @param channels
	 */
	public Configuration(List<Node> nodes, List<Channel> channels) {
		this.nodes = nodes;
		this.channels = channels;
	}
	
}
