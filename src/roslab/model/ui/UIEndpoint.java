/**
 * 
 */
package roslab.model.ui;

import roslab.model.general.Endpoint;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * @author Peter Gebhard
 *
 */
public class UIEndpoint extends Circle {
	
	String name;
	UINode parent;
	Endpoint endpoint;

	/**
	 * @param name
	 * @param parent
	 * @param endpoint
	 * @param centerX
	 * @param centerY
	 */
	public UIEndpoint(String name, UINode parent, Endpoint endpoint, double centerX, double centerY) {
		super(centerX, centerY, 0.5);
		this.name = name;
		this.parent = parent;
		this.endpoint = endpoint;
	}

}
