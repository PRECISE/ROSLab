/**
 * 
 */
package roslab.model.ui;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 * 
 */
public class UINode extends Rectangle {

	Node node;
	List<UIEndpoint> endpoints;
	
	// X AND Y position of mouse during selection
	double mousex = 0;
	double mousey = 0;

	/**
	 * @param node
	 * @param endpoints
	 * @param xx
	 * @param yy
	 */
	public UINode(Node node, List<UIEndpoint> endpoints, double xx, double yy) {
		super(xx, yy, 50, 50);
		this.getStyleClass().add(getClass().getSimpleName());
		this.node = node;
		if (node != null) {
			// Set node-class-specific style
			this.getStyleClass().add(this.node.getClass().getSimpleName());
		}
		this.endpoints = endpoints;

//		THIS IS AN OPTIONAL WAY TO ENABLE DRAGGING WITHOUT USING THE BUILDNODE METHOD
//
//		// EventListener for MousePressed
//		onMousePressedProperty().set(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				// record the current mouse X and Y position on Node
//				mousex = event.getSceneX();
//				mousey = event.getSceneY();
//				// get the x and y position measure from Left-Top
//				x = getLayoutX();
//				y = getLayoutY();
//			}
//		});
//
//		// Event Listener for MouseDragged
//		onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				// Get the exact moved X and Y
//				x += event.getSceneX() - mousex;
//				y += event.getSceneY() - mousey;
//
//				// set the position of Node after calculation
//				setLayoutX(x);
//				setLayoutY(y);
//
//				// again set current Mouse x AND y position
//				mousex = event.getSceneX();
//				mousey = event.getSceneY();
//			}
//		});
	}

	public static UINode buildUINode(Node node, List<UIEndpoint> endpoints,
			double x, double y) {
		final UINode n = new UINode(node, endpoints, x, y);
		n.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				// record a delta distance for the drag and drop operation.
				n.mousex = n.getX() - mouseEvent.getX();
				n.mousey = n.getY() - mouseEvent.getY();
				n.setCursor(Cursor.CLOSED_HAND);
			}
		});
		n.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				n.setCursor(Cursor.OPEN_HAND);
			}
		});
		n.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				n.setX(mouseEvent.getX() + n.mousex);
				n.setY(mouseEvent.getY() + n.mousey);
			}
		});
		n.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (!mouseEvent.isPrimaryButtonDown()) {
					n.setCursor(Cursor.OPEN_HAND);
				}
			}
		});
		n.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (!mouseEvent.isPrimaryButtonDown()) {
					n.setCursor(Cursor.DEFAULT);
				}
			}
		});
		return n;
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
	 * @param endpoints
	 *            the endpoints to set
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
	 * @param node
	 *            the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

}
