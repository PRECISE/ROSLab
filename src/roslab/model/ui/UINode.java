/**
 *
 */
package roslab.model.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import roslab.ROSLabController;
import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Node;
import roslab.model.general.Configuration;
import roslab.model.software.ROSMsgType;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.software.ROSTopic;

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
    private static final double TEXT_Y_OFFSET = 6;
    private static final double RATE_Y_OFFSET = 18;

    Node node;
    Text nodeText;
    Text rateText = null;
    List<UIEndpoint> endpoints = new ArrayList<UIEndpoint>();
    boolean inNode;
    ContextMenu rightClickMenu;

    // X AND Y position of mouse during selection
    double mousex = 0;
    double mousey = 0;
    double mousexName = 0;
    double mouseyName = 0;
    double mousexRate = 0;
    double mouseyRate = 0;

    /**
     * @param node
     * @param endpoints
     * @param xx
     * @param yy
     */
    public UINode(Node node, double xx, double yy) {
        super(xx, yy, DEFAULT_WIDTH + node.getName().length() * CHARACTER_SIZE, DEFAULT_HEIGHT);
        nodeText = new Text(getX(), getY() + (getHeight()) - TEXT_Y_OFFSET, node.getName());
        if(node instanceof ROSNode && "controller".equals(node.getAnnotation("custom-type"))) {
        	String rate = node.getAnnotation("Rate");
        	rateText = new Text(getX(), getY() + (getHeight()) + RATE_Y_OFFSET, "Rate:" + rate);
        }
        setNodeWidth(nodeText.getLayoutBounds().getWidth() + DEFAULT_WIDTH);
        getStyleClass().add(getClass().getSimpleName());

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
        	setEndpoints();
		}

        this.node.setUINode(this);

        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                mousex = getX() - mouseEvent.getX();
                mousey = getY() - mouseEvent.getY();
                mousexName = nodeText.getX() - mouseEvent.getX();
                mouseyName = nodeText.getY() - mouseEvent.getY();
                if(rateText != null) {
                    mousexRate = rateText.getX() - mouseEvent.getX();
                    mouseyRate = rateText.getY() - mouseEvent.getY();                	
                }
                for (UIEndpoint e : endpoints) {
                    e.setMouse(mouseEvent);
                }
                setCursor(Cursor.CLOSED_HAND);
                toTheFront();
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
                nodeText.setX(mouseEvent.getX() + mousexName);
                nodeText.setY(mouseEvent.getY() + mouseyName);
                if(rateText != null) {
                    rateText.setX(mouseEvent.getX() + mousexRate);
                    rateText.setY(mouseEvent.getY() + mouseyRate);               	
                }
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
        setDefaultColors();
        this.nodeText.toFront();
    }
    
    public void addCustomPortListener(final ROSLabController controller) {
    	
    	setOnDragOver(new EventHandler<DragEvent>() {
    		@Override
    		public void handle(DragEvent event) {
    			if(event.getDragboard().hasString()) {
    				String[] portInfo = event.getDragboard().getString().split(" "); //TODO spaces in names a problem
    				boolean isSub = !"1".equals(portInfo[2]);
    				if (!endpoints.contains(event.getGestureSource()) 
    						&& validPortAdd(portInfo[0], portInfo[1], isSub)) {
    					event.acceptTransferModes(TransferMode.COPY);  	
    				}
    			}           
    			event.consume();	
    		}
    	});
	    	
    	setOnDragEntered(new EventHandler<DragEvent>() {
    		public void handle(DragEvent event) {
    			if(event.getDragboard().hasString()) {
                    String[] portInfo = event.getDragboard().getString().split(" "); //TODO spaces in names a problem
                    boolean isSub = !"1".equals(portInfo[2]);
        			if (!endpoints.contains(event.getGestureSource()) 
        					&& validPortAdd(portInfo[0], portInfo[1], isSub)) {
        				setStyle("-fx-stroke: green");
        			} else {
        				setStyle("-fx-stroke: red");
        			}    				
    			}           
    			event.consume();
    		}
    	});

        setOnDragExited(new EventHandler<DragEvent>() {
        	public void handle(DragEvent event) {
        		setDefaultColors();
        		event.consume();
        	}
        });
        
        setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (!endpoints.contains(event.getGestureSource()) &&
                        event.getDragboard().hasString()) {
                    System.out.println(event.getDragboard().getString());
                    String[] portInfo = event.getDragboard().getString().split(" "); //TODO spaces in names a problem
                    boolean isSub = !"1".equals(portInfo[2]);
                    controller.addConfigPort(node, portInfo[0], portInfo[1], isSub);
                    event.setDropCompleted(true);
                }  
                controller.killDrawTasks();
                event.consume();
            }
        });

    }
    
    public void addRightClickMenu(final ROSLabController controller) {
    	rightClickMenu = new ContextMenu();
        if(node instanceof ROSNode && "controller".equals(node.getAnnotation("custom-type"))) {
        	MenuItem rateItem = new MenuItem("Edit Rate");
        	rateItem.setOnAction(new EventHandler<ActionEvent>() {
            	public void handle(ActionEvent event) {
            		System.out.println("change rate");
            		controller.showEditRateDialog(node);
            		updateRateText();         		
            	}
            });
        	rightClickMenu.getItems().add(rateItem);
        }
        MenuItem deleteItem = new MenuItem("Delete Node");
        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent event) {
        		controller.removeConfigNode(getNode());
        	}
        });
        rightClickMenu.getItems().add(deleteItem);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    rightClickMenu.show(getNode().getUINode() , mouseEvent.getScreenX(), mouseEvent.getScreenY());
                } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    rightClickMenu.hide();
                }
            }
        });
    }
    
    private void setNodeWidth(double d) {
        setWidth(d);
        centerText();
    }
    
    private void centerText() {
    	nodeText.setX(getX() + (getWidth() - nodeText.getLayoutBounds().getWidth()) / 2); 
    	if(rateText != null) {
    		rateText.setX(getX() + (getWidth() - rateText.getLayoutBounds().getWidth()) / 2); 
    	}
    }
    
    private void setNodeHeight(double d) {
    	setHeight(d);
    	nodeText.setY(getY() + getHeight() - TEXT_Y_OFFSET);
    	if(rateText != null) {
    		rateText.setY(getY() + getHeight() + RATE_Y_OFFSET);
  	  	}
    }
    
    private void updateRateText() {
    	String rate = node.getAnnotation("Rate");
    	rateText.setText("Rate:" + rate);
    	centerText();
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
        
    public void setEndpoints() {
      int subCount = 0;
      int pubCount = 0;
      int longestNameLeft = 0;
      int longestNameRight = 0;
      // Find longest endpoint names to adjust node width to fit all text
      for (Feature f : node.getFeatures().values()) {
    	  if (((ROSPort)f).getTopic().isSubscriber()) {
    		  longestNameLeft = Math.max(longestNameLeft, f.getName().length());
    		  subCount++;
    	  } else {
              longestNameRight = Math.max(longestNameRight, f.getName().length());
              pubCount++;
          }
      }
      
      // Check if the UINode is wide enough to show all endpoints
      double requiredWidth = (longestNameLeft + longestNameRight) * CHARACTER_SIZE 
    		  + nodeText.getLayoutBounds().getWidth() + UINODE_WIDTH_PADDING;
      if (getWidth() < requiredWidth) setNodeWidth(requiredWidth);
      
      // Check if the UINode is high enough to show all endpoints //TODO 3+ endpoints messing up
      double requiredHeight = Math.ceil(Math.max(subCount, pubCount)) * ENDPOINT_SIZE 
    		  + ENDPOINT_Y_OFFSET + TEXT_Y_OFFSET;
      if (getHeight() < requiredHeight) {
    	  setNodeHeight(requiredHeight);
      }
      
      // Create endpoint objects in the appropriate location on this Node's edges
      subCount = 0;
      pubCount = 0;
      for (Feature f : node.getFeatures().values()) {
          // Put subscribing nodes on left side
          if (((ROSPort)f).getTopic().isSubscriber()) {
        	  UIEndpoint subUI = new UIEndpoint((Endpoint) f, this, getX(), getY()
                      + (ENDPOINT_SIZE * subCount) + ENDPOINT_Y_OFFSET, true, true);
        	  subUI.setCircleStyle(true);
              this.endpoints.add(subUI);
              subCount++;
          } else {
              // Publishing nodes on right side
              endpoints.add(new UIEndpoint((Endpoint) f, this, getX() + getWidth(), getY()
                      + (ENDPOINT_SIZE * (pubCount)) + ENDPOINT_Y_OFFSET, false, true));
              pubCount++;
          }
      }
    }
    
    public void resetEndpoints(final ROSLabController controller) {
        endpoints.clear();
        setEndpoints();
        for(UIEndpoint e: endpoints) {
        	e.addRemoveCustomListener(controller);
        }
    }
    
    private boolean validPortAdd(String pName, String pType, boolean isSub) {
    	if("controller".equals(node.getAnnotation("custom-type"))) {
        	for(Feature f: node.getFeatures().values()) {
        		if(f instanceof ROSPort) {
        			ROSPort existing = (ROSPort)f;
        			if(isSub != existing.isSubscriber()) continue;
        			String existingMsgType = existing.getTopic().getType().toString();
        			if(pName.equals(existing.getName()) && pType.equals(existingMsgType)) {
        				return false;
        			}  			
        		}
        	}
        	return true;
    	}
    	return false;
    }

    public void toTheFront() {
        this.toFront();
    	for(UIEndpoint e: endpoints) {
    		e.toTheFront();	
    	}
        this.nodeText.toFront();
    }
    
    public void setDefaultColors() {
    	if(!(node instanceof ROSNode)) return;
    	String type = node.getAnnotation("custom-type");
    	if(type == null) {
    		setStyle("-fx-stroke: blue;");
    	} else if(type.equals("controller")) {
    		setStyle("-fx-stroke: lightskyblue; -fx-fill: paleturquoise;"); 
    	} else if(type.equals("topic")) {
    		setStyle("-fx-stroke: lightskyblue;"); 		
    	}
    }
    
    public void addToGroup(Group g, ROSLabController r) {
    	boolean isRos = node instanceof ROSNode;
    	if(isRos) {
            addCustomPortListener(r);
            addRightClickMenu(r);		
    	}  	
        g.getChildren().add(this);
        g.getChildren().add(nodeText);
        if(rateText != null) g.getChildren().add(rateText);
    	for(UIEndpoint e: endpoints) {
    		e.addToGroup(g);
    		if(isRos) e.addRemoveCustomListener(r);
    	}
    }
    
    public void removeFromGroup(Group g) {
        g.getChildren().remove(this); 
    	g.getChildren().remove(nodeText);
        if(rateText != null) g.getChildren().remove(rateText);
    	for(UIEndpoint e: endpoints) {
    		e.removeFromGroup(g);
    	}
    }

}
