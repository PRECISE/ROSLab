/**
 * 
 */
package roslab.model.ui;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * @author shaz
 *
 */
public class UIEndpoint extends Circle {

	/**
	 * 
	 */
	public UIEndpoint() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param radius
	 */
	public UIEndpoint(double radius) {
		super(radius);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param radius
	 * @param fill
	 */
	public UIEndpoint(double radius, Paint fill) {
		super(radius, fill);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param centerX
	 * @param centerY
	 * @param radius
	 */
	public UIEndpoint(double centerX, double centerY, double radius) {
		super(centerX, centerY, radius);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @param fill
	 */
	public UIEndpoint(double centerX, double centerY, double radius, Paint fill) {
		super(centerX, centerY, radius, fill);
		// TODO Auto-generated constructor stub
	}

}
