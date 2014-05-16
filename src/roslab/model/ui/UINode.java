/**
 * 
 */
package roslab.model.ui;

import java.util.List;

import javafx.scene.shape.Rectangle;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 *
 */
public class UINode extends Rectangle {
	
	String name;
	List<UIEndpoint> endpoints;
	Node node;

	/**
	 * @param name
	 * @param endpoints
	 * @param node
	 * @param x
	 * @param y
	 */
	public UINode(String name, List<UIEndpoint> endpoints, Node node, double x, double y) {
		super(x, y, 15, 15);
		this.name = name;
		this.endpoints = endpoints;
		this.node = node;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the endpoints
	 */
	public List<UIEndpoint> getEndpoints() {
		return endpoints;
	}

	/**
	 * @param endpoints the endpoints to set
	 */
	public void setEndpoints(List<UIEndpoint> endpoints) {
		this.endpoints = endpoints;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

}
