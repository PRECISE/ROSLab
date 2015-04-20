/**
 *
 */
package roslab.model.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 */
public class UINode extends Rectangle {

    private static final int CHARACTER_SIZE = 4; // Character pixel width
    private static final int DEFAULT_HEIGHT = 80;
    private static final int DEFAULT_WIDTH = 80;
    private static final int ENDPOINT_SIZE = 20;
    private static final double ENDPOINT_Y_OFFSET = 20;
    private static final int UINODE_WIDTH_PADDING = 70;
    private static final double TEXT_X_OFFSET = 10;
    private static final double TEXT_Y_OFFSET = 4;

    Node node;
    Text nodeText;
    List<UIEndpoint> endpoints = new ArrayList<UIEndpoint>();

    // X AND Y position of mouse during selection
    double mousex = 0;
    double mousey = 0;
    double mousexText = 0;
    double mouseyText = 0;

    /**
     * @param node
     * @param endpoints
     * @param xx
     * @param yy
     */
    public UINode(Node node, double xx, double yy) {
        super(xx, yy, DEFAULT_WIDTH + node.getName().length() * CHARACTER_SIZE, DEFAULT_HEIGHT);
        this.getStyleClass().add(getClass().getSimpleName());
        this.nodeText = new Text(this.getX() + TEXT_X_OFFSET, this.getY() + (this.getHeight() / 2) + TEXT_Y_OFFSET, node.getName());
        this.nodeText.setFont(new Font(16)); // TODO: Change to monospaced font
        // to correct center spacing
        this.nodeText.setMouseTransparent(true);
        this.node = node;
        if (node != null) {
            // Set node-class-specific style
            this.getStyleClass().add(this.node.getClass().getSimpleName());
        }
        if (node instanceof Endpoint) {
            // If the node itself is the endpoint, add it in the center and
            // invisible
            this.endpoints.add(new UIEndpoint((Endpoint) node, this, this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, false,
                    false));
        }
        else {
            int endpointIndex = 0;
            int endpointCount = node.getFeatures().values().size();
            int longestEndpointNameLeft = 0;
            int longestEndpointNameRight = 0;

            // Find longest endpoint name (to adjust node width to fit endpoint
            // names and the node's name)
            for (String s : node.getFeatures().keySet()) {
                if (endpointIndex < endpointCount / 2) {
                    longestEndpointNameLeft = Math.max(longestEndpointNameLeft, s.length());
                }
                else {
                    longestEndpointNameRight = Math.max(longestEndpointNameRight, s.length());
                }
                endpointIndex++;
            }

            // Check if the UINode is high enough to show all endpoints
            if (this.getWidth() < (longestEndpointNameLeft + longestEndpointNameRight + this.node.getName().length()) * CHARACTER_SIZE
                    + UINODE_WIDTH_PADDING) {
                this.setWidth((longestEndpointNameLeft + longestEndpointNameRight + this.node.getName().length()) * CHARACTER_SIZE
                        + UINODE_WIDTH_PADDING);
            }

            // Check if the UINode is high enough to show all endpoints
            if (this.getHeight() < Math.ceil(endpointCount / 2.0) * ENDPOINT_SIZE + ENDPOINT_Y_OFFSET) {
                this.setHeight(Math.ceil(endpointCount / 2.0) * ENDPOINT_SIZE + ENDPOINT_Y_OFFSET);
            }

            endpointIndex = 0;

            // Create endpoint objects in the appropriate location on the edges
            // of the node
            for (Feature f : node.getFeatures().values()) {
                // put half of the endpoints on the left side
                if (endpointIndex < endpointCount / 2) {
                    this.endpoints.add(new UIEndpoint((Endpoint) f, this, this.getX(), this.getY() + (ENDPOINT_SIZE * endpointIndex)
                            + ENDPOINT_Y_OFFSET, true, true));
                }
                else {
                    // put the other half on the right side
                    this.endpoints.add(new UIEndpoint((Endpoint) f, this, this.getX() + this.getWidth(), this.getY()
                            + (ENDPOINT_SIZE * (endpointIndex - endpointCount / 2)) + ENDPOINT_Y_OFFSET, false, true));
                }
                endpointIndex++;
            }
        }

        this.node.setUINode(this);

        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                mousex = getX() - mouseEvent.getX();
                mousey = getY() - mouseEvent.getY();
                mousexText = nodeText.getX() - mouseEvent.getX();
                mouseyText = nodeText.getY() - mouseEvent.getY();
                for (UIEndpoint e : endpoints) {
                    e.setMouse(mouseEvent);
                }
                setCursor(Cursor.CLOSED_HAND);
            }
        });

        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.OPEN_HAND);
            }
        });

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setX(mouseEvent.getX() + mousex);
                setY(mouseEvent.getY() + mousey);
                nodeText.setX(mouseEvent.getX() + mousexText);
                nodeText.setY(mouseEvent.getY() + mouseyText);
                for (UIEndpoint e : endpoints) {
                    e.updateXY(mouseEvent);
                }
            }
        });

        this.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    setCursor(Cursor.OPEN_HAND);
                }
            }
        });

        this.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    setCursor(Cursor.DEFAULT);
                }
            }
        });

        this.nodeText.toFront();
    }

    private void setWidth(int i) {
        this.setWidth(i);
        this.nodeText.setX(this.getX() + (this.getWidth() - this.nodeText.getText().length() * CHARACTER_SIZE) / 2);
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.node.getName();
    }

    /**
     * @return the node text string
     */
    public String getNodeText() {
        return nodeText.getText();
    }

    /**
     * @param text
     *            the node text string to set
     */
    public void setNodeText(String text) {
        nodeText.setText(text);
    }

    /**
     * @return the node text string
     */
    public Text getNodeUIText() {
        return nodeText;
    }

    /**
     * @return the endpoints
     */
    public List<UIEndpoint> getUIEndpoints() {
        return endpoints;
    }

    /**
     * @param endpoints
     *            the endpoints to set
     */
    public void setUIEndpoints(List<UIEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    /**
     * @return an endpoint
     */
    public UIEndpoint getUIEndpoint(Endpoint e) {
        for (UIEndpoint u : endpoints) {
            if (u.endpoint == e) {
                return u;
            }
        }
        return null;
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

    public void toTheFront() {
        this.toFront();
        this.nodeText.toFront();
    }

}
