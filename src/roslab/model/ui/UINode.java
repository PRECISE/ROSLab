/**
 * 
 */
package roslab.model.ui;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @author shaz
 *
 */
public class UINode extends Rectangle {

	/**
	 * 
	 */
	public UINode() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param width
	 * @param height
	 */
	public UINode(double width, double height) {
		super(width, height);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param width
	 * @param height
	 * @param fill
	 */
	public UINode(double width, double height, Paint fill) {
		super(width, height, fill);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public UINode(double x, double y, double width, double height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}

}
