/**
 *
 */
package roslab.model.ui;

import java.awt.MouseInfo;
import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import roslab.model.general.Endpoint;
import roslab.model.general.Node;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;

/**
 * @author Peter Gebhard
 */
public class UIEndpoint extends Circle {
    // TODO: Change the parent class to Circle so that we can handle Z-Ordering!

    private static final int CHARACTER_SIZE = 7;
    private static final int DEFAULT_RADIUS = 5;
    private static final double TEXT_X_OFFSET = 5;
    private static final double TEXT_Y_OFFSET = 3;
    Endpoint endpoint;
    Line addPortLine;
    Text endpointText;
    UINode uiparent;
    List<UILink> uilinks = new ArrayList<UILink>();
    boolean rightSideText;    
    LineDraw drawTask;
    Thread drawLineThread;

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
    public UIEndpoint(Endpoint endpoint, UINode uiparent, double centerX, double centerY, boolean rightSideText, boolean visible) {
        super(centerX, centerY, DEFAULT_RADIUS);
        this.getStyleClass().add(getClass().getSimpleName());
        this.endpoint = endpoint;
        if (endpoint != null) {
            // Set node-class-specific style
            this.getStyleClass().add(this.endpoint.getClass().getSimpleName());
        }
        this.rightSideText = rightSideText;
        setupEndpointText(this.endpoint, this.rightSideText);
        addPortLine = new Line(centerX, centerY, centerX, centerY);
        addPortLine.setMouseTransparent(true);
        this.uiparent = uiparent;
        this.getChildren().addAll(this.endpointCircle, this.endpointText, addPortLine);
        this.setVisible(visible);
        this.endpointText.setVisible(visible);
        addPortLine.setVisible(false);
        
        endpointCircle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	setCursor(Cursor.CLOSED_HAND);
            	setParentToFront();
            }
        });
            
        endpointCircle.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.OPEN_HAND);
                addPortLine.setVisible(false);
                if(getEndpoint() instanceof ROSPort && getParentNode() instanceof ROSNode) {
                	drawTask.kill();
                }
            }
        });
        
        endpointCircle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    setCursor(Cursor.OPEN_HAND);
                }
            }
        });
        
        endpointCircle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    setCursor(Cursor.DEFAULT);
                }
            }
        });
                
        endpointCircle.setOnDragDetected(new EventHandler<MouseEvent>(){
        	@Override
        	public void handle(MouseEvent event) {
        		if(getEndpoint() instanceof ROSPort && getParentNode() instanceof ROSNode) {
        			Dragboard db = startDragAndDrop(TransferMode.COPY);
        			db.setDragView(new Image("/roslab/model/ui/Drag.png"));
        			ClipboardContent content = new ClipboardContent();
        			String endpointType = ((ROSPort)getEndpoint()).getType().toString();
        			String endpointName = getEndpoint().getName();
        			int isSub = 0;
        			if(((ROSPort)getEndpoint()).isInput()) {
        				isSub = 1;
        			}
        			content.putString(endpointName + " " + endpointType + " " + isSub);
        			db.setContent(content);
        			System.out.println(db.getString());
        			drawLine();
        		}
        		event.consume();
        	}    	
        });
        
    }
    
    private class LineDraw implements Runnable {
    	
	    private volatile boolean drawing = true;
	    
		@Override
		public void run() {
			addPortLine.setStartX(endpointCircle.getCenterX());
			addPortLine.setStartY(endpointCircle.getCenterY());			
	    	while(drawing) {
	    			double x = MouseInfo.getPointerInfo().getLocation().getX();
	    			double y = MouseInfo.getPointerInfo().getLocation().getY();
	    			Point2D p = getParentNode().getUINode().getParent().screenToLocal(x, y);
	    			addPortLine.setEndX(p.getX());
	    			addPortLine.setEndY(p.getY());
	    			addPortLine.setVisible(true); 	
	    	}
	    	addPortLine.setEndX(endpointCircle.getCenterX());
	    	addPortLine.setEndY(endpointCircle.getCenterY());
			addPortLine.setVisible(false);
		}	
		
		public void kill() {
			drawing = false;
		}
    }
    
    private void drawLine() {
    	drawTask = new LineDraw();
    	drawLineThread = new Thread(drawTask);
    	drawLineThread.start();
    }
    
    /**
     * @param endpoint
     * @param rightSideText
     */
    private void setupEndpointText(Endpoint endpoint, boolean rightSideText) {
        if (rightSideText) {
            // set text on right side
            this.endpointText = new Text(this.getCenterX() + this.getRadius() + TEXT_X_OFFSET, this.getCenterY() + TEXT_Y_OFFSET, endpoint.getName());
        }
        else {
            // set text on left side
            this.endpointText = new Text(this.getCenterX() - this.getRadius() - TEXT_X_OFFSET - endpoint.getName().length() * CHARACTER_SIZE,
                    this.getCenterY() + TEXT_Y_OFFSET, endpoint.getName());
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
        UILink thisUILink = this.uilinks.get(this.uilinks.indexOf(uilink));
        thisUILink.toBack();
        this.toFront();
    }

    /**
     * @param uilink
     *            the uilink to add
     */
    public void removeUILink(UILink uilink) {
        this.uilinks.remove(uilink);
    }

    public void setMouse(MouseEvent mouseEvent) {
        mousexCircle = this.getCenterX() - mouseEvent.getX();
        mouseyCircle = this.getCenterY() - mouseEvent.getY();
        mousexText = endpointText.getX() - mouseEvent.getX();
        mouseyText = endpointText.getY() - mouseEvent.getY();
        for (UILink l : uilinks) {
            l.setMouse(mouseEvent, this);
        }
    }

    public void updateXY(MouseEvent mouseEvent) {
        this.setCenterX(mouseEvent.getX() + mousexCircle);
        this.setCenterY(mouseEvent.getY() + mouseyCircle);
        endpointText.setX(mouseEvent.getX() + mousexText);
        endpointText.setY(mouseEvent.getY() + mouseyText);
        for (UILink l : uilinks) {
            l.updateXY(mouseEvent, this);
        }
    }
    
    public void setCircleStyle(boolean isSubscriber) {
    	if(isSubscriber) {
    		endpointCircle.setStyle("-fx-fill:yellow");
    	} else {
    		endpointCircle.setStyle("-fx-fill:blue");   		
    	}
    }
    
    private void setParentToFront() {
    	uiparent.toFront();
    }
}
