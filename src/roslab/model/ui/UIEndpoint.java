/**
 * 
 */
package roslab.model.ui;

import javafx.scene.shape.Circle;
import roslab.model.general.Endpoint;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 *
 */
public class UIEndpoint extends Circle {
	
	String name;
	UINode uiparent;
	Endpoint endpoint;

	/**
	 * @param name
	 * @param parent
	 * @param endpoint
	 * @param centerX
	 * @param centerY
	 */
	public UIEndpoint(String name, UINode uiparent, Endpoint endpoint, double centerX, double centerY) {
		super(centerX, centerY, 0.5);
		this.name = name;
		this.uiparent = uiparent;
		this.endpoint = endpoint;
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
	 * @return the parent
	 */
	public UINode getParentUINode() {
		return uiparent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParentUINode(UINode uiparent) {
		this.uiparent = uiparent;
	}
	
	/**
	 * @return the parent
	 */
	public Node getParentNode() {
		return this.uiparent.getNode();
	}

	/**
	 * @return the endpoint
	 */
	public Endpoint getEndpoint() {
		return endpoint;
	}

	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

}
