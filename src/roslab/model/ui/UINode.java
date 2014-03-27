/**
 * 
 */
package roslab.model.ui;

import java.util.List;

import roslab.model.general.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

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

}
