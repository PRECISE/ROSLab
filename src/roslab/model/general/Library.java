/**
 * 
 */
package roslab.model.general;

import java.util.List;

/**
 * @author shaz
 *
 */
public class Library {

	List<? extends Node> nodes;

	/**
	 * @param nodes
	 */
	public Library(List<? extends Node> nodes) {
		this.nodes = nodes;
	}
	
}
