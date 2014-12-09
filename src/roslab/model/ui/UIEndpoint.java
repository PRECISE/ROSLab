/**
 *
 */
package roslab.model.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import roslab.model.general.Endpoint;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 */
public class UIEndpoint extends Group {

    private static final int CHARACTER_SIZE = 7;
    private static final int DEFAULT_RADIUS = 5;
    private static final double TEXT_X_OFFSET = 5;
    private static final double TEXT_Y_OFFSET = 3;
    Endpoint endpoint;
    Circle endpointCircle;
    Text endpointText;
    UINode uiparent;
    List<UILink> uilinks = new ArrayList<UILink>();
    boolean rightSideText;

    // X AND Y position of mouse during actions
    double mousexCircle = 0;
    double mouseyCircle = 0;
    double mousexText = 0;
    double mouseyText = 0;

    /**
     * @param name
     * @param parent
     * @param endpoint
     * @param centerX
     * @param centerY
     */
    public UIEndpoint(Endpoint endpoint, UINode uiparent, double centerX, double centerY, boolean rightSideText) {
        super();
        this.endpoint = endpoint;
        this.endpointCircle = new Circle(centerX, centerY, DEFAULT_RADIUS);
        this.endpointCircle.getStyleClass().add(getClass().getSimpleName());
        if (endpoint != null) {
            // Set node-class-specific style
            this.endpointCircle.getStyleClass().add(this.endpoint.getClass().getSimpleName());
        }
        this.rightSideText = rightSideText;
        setupEndpointText(this.endpoint, this.rightSideText);
        this.uiparent = uiparent;
        this.getChildren().addAll(this.endpointCircle, this.endpointText);
    }

    /**
     * @param endpoint
     * @param rightSideText
     */
    private void setupEndpointText(Endpoint endpoint, boolean rightSideText) {
        if (rightSideText) {
            // set text on right side
            this.endpointText = new Text(this.endpointCircle.getCenterX() + this.endpointCircle.getRadius() + TEXT_X_OFFSET,
                    this.endpointCircle.getCenterY() + TEXT_Y_OFFSET, endpoint.getName());
        }
        else {
            // set text on left side
            this.endpointText = new Text(this.endpointCircle.getCenterX() - this.endpointCircle.getRadius() - TEXT_X_OFFSET
                    - endpoint.getName().length() * CHARACTER_SIZE, this.endpointCircle.getCenterY() + TEXT_Y_OFFSET, endpoint.getName());
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.endpoint.getName();
    }

    /**
     * @return the endpoint
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint
     *            the endpoint to set
     */
    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        setupEndpointText(this.endpoint, this.rightSideText);
    }

    /**
     * @return the parent
     */
    public UINode getParentUINode() {
        return uiparent;
    }

    /**
     * @param parent
     *            the parent to set
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
     * @return the uilinks
     */
    public List<UILink> getUILinks() {
        return uilinks;
    }

    /**
     * @param uilinks
     *            the uilinks to set
     */
    public void setUILinks(List<UILink> uilinks) {
        this.uilinks = uilinks;
    }

    /**
     * @param uilink
     *            the uilink to add
     */
    public void addUILink(UILink uilink) {
        this.uilinks.add(uilink);
        this.getChildren().add(uilink);  // TODO Does this really work?
    }

    /**
     * @param uilink
     *            the uilink to add
     */
    public void removeUILink(UILink uilink) {
        this.uilinks.remove(uilink);
    }

    public void setMouse(MouseEvent mouseEvent) {
        mousexCircle = endpointCircle.getCenterX() - mouseEvent.getX();
        mouseyCircle = endpointCircle.getCenterY() - mouseEvent.getY();
        mousexText = endpointText.getX() - mouseEvent.getX();
        mouseyText = endpointText.getY() - mouseEvent.getY();
        for (UILink l : uilinks) {
            l.setMouse(mouseEvent, this);
        }
    }

    public void updateXY(MouseEvent mouseEvent) {
        endpointCircle.setCenterX(mouseEvent.getX() + mousexCircle);
        endpointCircle.setCenterY(mouseEvent.getY() + mouseyCircle);
        endpointText.setX(mouseEvent.getX() + mousexText);
        endpointText.setY(mouseEvent.getY() + mouseyText);
        for (UILink l : uilinks) {
            l.updateXY(mouseEvent, this);
        }
    }

    public double getCenterX() {
        return this.endpointCircle.getCenterX();
    }

    public double getCenterY() {
        return this.endpointCircle.getCenterY();
    }
}
