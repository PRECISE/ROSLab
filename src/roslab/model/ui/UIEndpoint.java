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

	Endpoint endpoint;
	UINode uiparent;

	/**
	 * @param name
	 * @param parent
	 * @param endpoint
	 * @param centerX
	 * @param centerY
	 */
	public UIEndpoint(Endpoint endpoint, UINode uiparent, double centerX, double centerY) {
		super(centerX, centerY, 0.5);
		this.endpoint = endpoint;
		this.uiparent = uiparent;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.endpoint.getName();
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
