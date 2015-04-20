package roslab;

import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.apache.log4j.BasicConfigurator;

import roslab.model.general.Configuration;
import roslab.model.general.Endpoint;
import roslab.model.general.Library;
import roslab.model.general.Link;
import roslab.model.general.Node;
import roslab.model.software.ROSMsgType;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.software.ROSTopic;
import roslab.model.ui.UIEndpoint;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;
import roslab.ui.general.NewLinkDialog;
import roslab.ui.general.NewNodeDialog;
import roslab.ui.general.ROSLabTree;
import roslab.ui.software.NewPortDialog;
import roslab.ui.software.NewUserDefinedDialog;

public class ROSLabController implements Initializable {

    @FXML
    TreeView<String> treeView;

    ROSLabTree tree;

    @FXML
    AnchorPane swPane;

    @FXML
    AnchorPane hwPane;

    @FXML
    AnchorPane eePane;

    @FXML
    Tab swTab;

    @FXML
    Tab hwTab;

    @FXML
    Tab eeTab;

    Random r = new Random();
    Library library = new Library(new ArrayList<Node>());
    Configuration config;
    Group eeUIObjects;
    Rectangle selectionRectangle;
    double selectionX;
    double selectionY;
    Line addPortLine;
    Point portLineStart = new Point();

    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert treeView != null : "fx:id\"treeView\" was not injected";

        BasicConfigurator.configure();

        // TODO: get highlighting and selection of Nodes based on selection
        // rectangle
        // enableSelectionRectangle(swPane);
        // enableSelectionRectangle(hwPane);
        // enableSelectionRectangle(eePane);

        library.loadPlatform("landshark");
//        library.loadElectronics();
        // PythonLibraryHelper p = new PythonLibraryHelper();
        config = new Configuration("Demo", new ArrayList<Node>(), new ArrayList<Link>());
        tree = new ROSLabTree(library, config, this);
        eeUIObjects = new Group();
        eePane.getChildren().add(eeUIObjects);

        // Setup TreeView
        treeView.setRoot(tree);
        treeView.setShowRoot(false);
        treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return tree.new TreeCellImpl();
            }
        });

        // addDragDrop(swPane);
    }
    
    private void addDragDrop(final Pane p) {
    	
        p.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data is dragged over the target */
                /*
                 * accept it only if it is not dragged from the same node
                 * and if it has a string data
                 */
                if (event.getGestureSource() != p && event.getDragboard().hasString()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                event.consume();
            }
        });

        p.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data dropped */
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    // TODO: Add node here! swPane.setText(db.getString());
                    success = true;
                }
                /*
                 * let the source know whether the string was successfully
                 * transferred and used
                 */
                event.setDropCompleted(success);

                event.consume();
            }
        });

        p.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
                /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != p && event.getDragboard().hasString()) {
                    System.out.println("ENTERED!");
                }

                event.consume();
            }
        });
    }

    private void enableSelectionRectangle(final Pane p) {
        p.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                selectionX = mouseEvent.getX();
                selectionY = mouseEvent.getY();
                selectionRectangle = new Rectangle(selectionX, selectionY, 0, 0);
                selectionRectangle.getStyleClass().add("SelectionRectangle");
                p.getChildren().add(selectionRectangle);
            }
        });
        p.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                p.getChildren().remove(selectionRectangle);
            }
        });
        
        p.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double x = mouseEvent.getX();
                double y = mouseEvent.getY();
                if (x < selectionX) {
                    selectionRectangle.setX(x);
                    selectionRectangle.setWidth(selectionX - x);
                }
                else {
                    selectionRectangle.setWidth(x - selectionRectangle.getX());
                }
                if (y < selectionY) {
                    selectionRectangle.setY(y);
                    selectionRectangle.setHeight(selectionY - y);
                }
                else {
                    selectionRectangle.setHeight(y - selectionRectangle.getY());
                }
            }
        });
    }

    public void addLibraryNode(Node n) {
    	System.out.println("Add: " + n); //TODO delete
        library.addNode(n);
        tree.addLibraryNode(n);
    }

    public void removeLibraryNode(Node n) {
    	System.out.println("in " + library.getNodes().size());
    	System.out.println("Rm: " + n); //TODO delete
        System.out.println(library.removeNode(n)); //TODO remove print
        tree.removeLibraryNode(n);
    	System.out.println("in " + library.getNodes().size());
    }

    public void updateLibraryNode(Node n) {
    	System.out.println("out " + library.getNodes().size());
        removeLibraryNode(n);
        addLibraryNode(n);
    	System.out.println("out " + library.getNodes().size());
    }

    public void addConfigNode(Node n) {
        UINode uin = new UINode(n, r.nextInt(400), r.nextInt(400));
        String nodeClassName = uin.getNode().getClass().getName();
        boolean refreshPorts = false;
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                uin.addCustomPortListener(config, this);
                swPane.getChildren().add(uin);
                refreshPorts = true;
                break;
            case "HWBlock":
                hwPane.getChildren().add(uin);
                break;
            case "Circuit":
                eeUIObjects.getChildren().add(uin);
                eeUIObjects.getChildren().add(uin.getNodeUIText());
                eeUIObjects.getChildren().addAll(uin.getUIEndpoints());

                // Order all of the UI objects
                for (Object nn : eeUIObjects.getChildren().toArray()) {
                    if (nn instanceof UINode) {
                        ((UINode) nn).toTheFront();
                    }
                    if (nn instanceof UILink) {
                        ((UILink) nn).toBack();
                    }
                }

                // eePane.getChildren().add(uin);
                break;
        }
        config.addNode(n);
        tree.addConfigNode(n);
        if(refreshPorts) refreshConfigPorts();
    }

    public void addConfigLink(Link l) {
        l.setUILink(new UILink(l));

        String nodeClassName = l.getSrc().getParent().getClass().getName();
        Object[] objArray = null;
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                // swPane.getChildren().add(uin);
                break;
            case "HWBlock":
                // hwPane.getChildren().add(uin);
                break;
            case "Circuit":
                objArray = eeUIObjects.getChildren().toArray();
                break;
        }
        // Order all of the UI objects
        for (Object nn : objArray) {
            if (nn instanceof UINode) {
                ((UINode) nn).toTheFront();
            }
            if (nn instanceof UILink) {
                ((UILink) nn).toBack();
            }
        }

        config.addLink(l);
        tree.addConfigLink(l);
    }

    public void removeConfigNode(Node n) {
        String nodeClassName = n.getClass().getName();
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                swPane.getChildren().remove(n.getUINode());
                break;
            case "HWBlock":
                hwPane.getChildren().remove(n.getUINode());
                break;
            case "Circuit":
                eePane.getChildren().remove(n.getUINode());
                break;
        }
        config.removeNode(n);
        tree.removeConfigNode(n);

        // Remove any links associated with this node
        for (Endpoint e : n.getEndpoints()) {
            List<? extends Link> links = e.getLinks();
            for (Link l : links) {
                removeConfigLink(l);
            }
        }

        n = null;
    }

    public void removeConfigLink(Link l) {
        l.destroy();
        config.removeLink(l);
        tree.removeConfigLink(l);
        l = null;
    }

    /**
     * @return the library
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * @return the config
     */
    public Configuration getConfig() {
        return config;
    }

    @FXML
    private void openLibrary() {
        // TODO Use XStream here!
    }

    @FXML
    private void saveLibrary() {
        // TODO Use XStream here!
    }

    @FXML
    private void openConfiguration() {
        // TODO Use XStream here!
    }

    @FXML
    private void saveConfiguration() {
        // TODO Use XStream here!
    }

    @FXML
    private void tabChanged() {
        // TODO Update Library Tree and Config Tree when switching tabs?
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param person
     *            the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showNewLinkDialog() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/general/NewLinkDialog.fxml"));
            GridPane page = (GridPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Link");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            NewLinkDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEndpoints(config.getEndpoints());
            controller.setRLController(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and
     * true
     * is returned.
     *
     * @param person
     *            the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showNewNodeDialog() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/general/NewNodeDialog.fxml"));
            GridPane page = (GridPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Node");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            NewNodeDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setNodes(library.getNodes());
            controller.setRLController(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isAddClicked();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and
     * true
     * is returned.
     *
     * @param person
     *            the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showNewUserDefinedDialog() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/software/NewUserDefinedDialog.fxml"));
            GridPane page = (GridPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New User-Defined Node");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            NewUserDefinedDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setRLController(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isAddClicked();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and
     * true
     * is returned.
     *
     * @param node
     * @param person
     *            the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showNewPortDialog(Node node) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/software/NewPortDialog.fxml"));
            GridPane page = (GridPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Port");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            NewPortDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setNode(node);
            controller.setRLController(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isAddClicked();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void updateConfigPorts(Node node, String pName, String pType, boolean isSub) {
//    	String portKey;
//    	if(isSub) {
//    		portKey = pName + "_sub";
//    	} else {
//    		portKey = pName + "_pub";
//    	}
    	//Add to each matching node's features
        for(Node n: config.getNodes()) {
        	if(n instanceof ROSNode && n.getSpec().equals(node.getSpec())) {
        		((ROSNode)n).addPort(new ROSPort(pName,((ROSNode)node),
        				new ROSTopic(pName, new ROSMsgType(pType), isSub), false, false));
        	}        	
        }
		//Add to library node's features
		((ROSNode)node.getSpec()).addPort(new ROSPort(pName,((ROSNode)node),
				new ROSTopic(pName, new ROSMsgType(pType), isSub), false, false));
		//Update library with library node
		updateLibraryNode(node.getSpec());
		//TODO update all UIEndpoints and links?
		for(javafx.scene.Node n: swPane.getChildren()) {
			if(n instanceof UINode) {
				((UINode)n).redrawEndpoints();
			}
		}
		refreshConfigPorts();
    }
    
    public void refreshConfigPorts() {
//    	for(Link link: config.getLinks()) {
//    		removeConfigLink(link);
//    	}
    	config.getLinks().clear();
//		for(javafx.scene.Node n: swPane.getChildren()) {
//			if(n instanceof UINode) {
//				for(UIEndpoint eUI: ((UINode)n).getUIEndpoints()) {
//					for(UILink lUI: eUI.getUILinks()) {
//						lUI.disconnect();
//						lUI.getSrc().removeUILink(lUI);
//						lUI.getDest().removeUILink(lUI);
//						removeConfigLink(lUI.getLink());
//						eUI.removeUILink(lUI);
//						eUI.getEndpoint().disconnect(lUI.getLink());
//						lUI.getLink().destroy();				
//					}
//				}
//			}
//		}
    	for(Node srcNode: config.getNodes()) {
    		for(Node destNode: config.getNodes()) {
    			for(Endpoint srcEnd: srcNode.getEndpoints()) {
    				for(Endpoint destEnd: destNode.getEndpoints()) {
    					if(srcEnd.equals(destEnd)) continue;
    					if(destEnd.canConnect(srcEnd)) {
							 Link link = new Link(srcEnd, destEnd);
							 addConfigLink(link);	   						
    					}
    				}
    			}
    		}
    	}
//		for(javafx.scene.Node src: swPane.getChildren()) {
//			for(javafx.scene.Node dest: swPane.getChildren()) {
//				if(src instanceof UINode && dest instanceof UINode) {
//					for(javafx.scene.Node srcUI: ((UINode)src).getChildren()) {
//						for(javafx.scene.Node destUI: ((UINode)dest).getChildren()) {
//							if(srcUI instanceof UIEndpoint && destUI instanceof UIEndpoint) {
//								 Endpoint srcEnd = ((UIEndpoint)srcUI).getEndpoint();
//								 Endpoint destEnd = ((UIEndpoint)destUI).getEndpoint();
//								 if(srcEnd instanceof ROSPort && destEnd instanceof ROSPort) {
//									 ROSPort srcRos = (ROSPort)srcEnd;
//									 ROSPort destRos = (ROSPort)destEnd;
//									 System.out.println("trying " + srcRos + " " + destRos);
//									 if(destRos.canConnect(srcRos)) {
//										 System.out.println("cool");
//										 Link link = new Link(srcEnd, destEnd);
//										 addConfigLink(link);										 
//									 }
//								 }
//							}
//						}		
//					}					
//				}
//
//			}
//		}
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getStage() {
        return this.primaryStage;
    }
}
