package roslab;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Link;
import roslab.model.general.Node;
import roslab.model.software.ROSMsgType;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;
import roslab.ui.general.NewLinkDialog;
import roslab.ui.general.NewNodeDialog;
import roslab.ui.general.ROSLabTree;
import roslab.ui.software.NewPortDialog;

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
    Rectangle selectionRectangle;
    double selectionX;
    double selectionY;

    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert treeView != null : "fx:id\"treeView\" was not injected";
        // System.out.println(this.getClass().getSimpleName() + ".initialize");
        // TODO: get highlighting and selection of Nodes based on selection
        // rectangle
        // enableSelectionRectangle(swPane);
        // enableSelectionRectangle(hwPane);
        // enableSelectionRectangle(eePane);
        config = new Configuration("Test", new ArrayList<Node>(), new ArrayList<Link>());

        // for (int i = 0; i < 5; i++) {
        // ROSNode rn = new ROSNode("test" + i);
        // for (int j = 0; j < 10; j++) {
        // rn.addPort(new ROSPort("p" + String.valueOf(r.nextInt(10000)), rn,
        // new ROSTopic("/t" + String.valueOf(r.nextInt(10000)),
        // (ROSMsgType) ROSMsgType.typeMap.keySet().toArray()[0],
        // r.nextBoolean()), true, true));
        // }
        // library.addNode(rn);
        //
        // Circuit cn = new Circuit("test" + i);
        // for (int j = 0; j < 10; j++) {
        // cn.addPin(new Pin(String.valueOf(r.nextInt(10000)), cn));
        // }
        // library.addNode(cn);
        //
        // HWBlock hw = new HWBlock("test" + i,
        // HWBlockType.values()[r.nextInt(HWBlockType.values().length)]);
        // for (int j = 0; j < 10; j++) {
        // hw.addJoint(new Joint(String.valueOf(r.nextInt(10000)), hw,
        // r.nextBoolean(), r.nextBoolean()));
        // }
        // library.addNode(hw);
        //
        // swPane.getChildren().add(new UINode(rn, r.nextInt(1000),
        // r.nextInt(1000)));
        // eePane.getChildren().add(new UINode(cn, r.nextInt(1000),
        // r.nextInt(1000)));
        // hwPane.getChildren().add(new UINode(hw, r.nextInt(1000),
        // r.nextInt(1000)));
        // }
        //
        // for (int i = 0; i < 20; i++) {
        // UINode n1 = (UINode)
        // swPane.getChildren().get(r.nextInt(swPane.getChildren().size()));
        // UINode n2 = (UINode)
        // swPane.getChildren().get(r.nextInt(swPane.getChildren().size()));
        // for (ROSPort p : ((ROSNode) n1.getNode()).getPorts().values()) {
        // if (p.canConnect((Endpoint) ((ROSNode)
        // n2.getNode()).getPorts().values().toArray()[0])) {
        // new UILink(p.connect((Endpoint) ((ROSNode)
        // n2.getNode()).getPorts().values().toArray()[0]));
        // }
        // }
        // }

        // TEST
        // ROSNode testNode = (ROSNode) rosNodesList.get(0);
        // try {
        // ROSNodeCodeGenerator.buildNode(testNode, new File(testNode.getName()
        // + ".cpp"));
        // }
        // catch (IOException e) {
        // e.printStackTrace();
        // }

        library.loadPlatform("test");
        tree = new ROSLabTree(library, config, this);

        treeView.setRoot(tree);
        treeView.setShowRoot(false);
        treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return tree.new TreeCellImpl();
            }
        });

        // addDragDrop(swPane);

        for (Node n : library.getNodes()) {
            addConfigNode(n);
        }
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

    public void addConfigNode(Node n) {
        UINode uin = new UINode(n, r.nextInt(400), r.nextInt(400));
        String nodeClassName = uin.getNode().getClass().getName();
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                swPane.getChildren().add(uin);
                break;
            case "HWBlock":
                hwPane.getChildren().add(uin);
                break;
            case "Circuit":
                eePane.getChildren().add(uin);
                break;
        }
        config.addNode(n);
        tree.addConfigNode(n);
    }

    public void addConfigLink(Link l) {
        l.setUILink(new UILink(l));
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
    }

    public void removeConfigLink(Link l) {
        l.getUILink().destroy();
        config.removeLink(l);
        tree.removeConfigLink(l);
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

    private void openLibrary() {
        // TODO Use XStream here!
    }

    private void openConfiguration() {
        // TODO Use XStream here!
    }

    private void saveLibrary() {
        // TODO Use XStream here!
    }

    private void saveConfiguration() {
        // TODO Use XStream here!
    }

    @FXML
    private void tabChanged() {

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
            loader.setLocation(getClass().getResource("ui/general/fxml/NewLinkDialog.fxml"));
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
            loader.setLocation(getClass().getResource("ui/general/fxml/NewNodeDialog.fxml"));
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
    public boolean showNewPortDialog() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/software/fxml/NewPortDialog.fxml"));
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
            controller.setTypes(ROSMsgType.typeMap.keySet());
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

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
