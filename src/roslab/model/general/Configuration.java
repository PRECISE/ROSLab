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

<<<<<<< HEAD
	List<? extends Node> nodes;
	List<? extends Link> links;
=======
	List<Node> nodes;
	List<Link> links;
>>>>>>> FETCH_HEAD
	
	/**
	 * @param nodes
	 * @param links
	 */
<<<<<<< HEAD
	public Configuration(List<? extends Node> nodes, List<? extends Link> links) {
=======
	public Configuration(List<Node> nodes, List<Link> links) {
>>>>>>> FETCH_HEAD
		this.nodes = nodes;
		this.links = links;
	}
	
}
