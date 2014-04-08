/**
 * 
 */
package roslab.model.general;

import java.util.List;

import roslab.model.ui.UILink;
import roslab.model.ui.UINode;

/**
 * @author Peter Gebhard
 *
 */
public class Configuration {

	List<UINode> nodes;
	List<UILink> links;
	
	/**
	 * @param nodes
	 * @param links
	 */
	public Configuration(List<UINode> nodes, List<UILink> links) {
		this.nodes = nodes;
		this.links = links;
	}
	
}
