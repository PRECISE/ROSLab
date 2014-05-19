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

	Node node;
	List<UIEndpoint> endpoints;

	/**
	 * @param node
	 * @param endpoints
	 * @param x
	 * @param y
	 */
	public UINode(Node node, List<UIEndpoint> endpoints, double x, double y) {
		super(x, y, 15, 15);
		this.node = node;
		this.endpoints = endpoints;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.node.getName();
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
