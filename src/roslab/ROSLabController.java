package roslab;

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

import org.apache.log4j.BasicConfigurator;

import roslab.model.electronics.Circuit;
import roslab.model.general.Configuration;
import roslab.model.general.Endpoint;
import roslab.model.general.Library;
import roslab.model.general.Link;
import roslab.model.general.Node;
import roslab.model.mechanics.HWBlock;
import roslab.model.software.ROSNode;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;
import roslab.ui.general.NewLinkDialog;
import roslab.ui.general.NewNodeDialog;
import roslab.ui.general.ROSLabTree;
import roslab.ui.software.NewPortDialog;
import roslab.ui.software.NewUserDefinedDialog;

public class ROSLabController implements Initializable {

    @FXML
    TreeView<String> swTreeView;

    ROSLabTree swTree;

    @FXML
    TreeView<String> hwTreeView;

    ROSLabTree hwTree;

    @FXML
    TreeView<String> eeTreeView;

    ROSLabTree eeTree;

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

    Library swLibrary = new Library(new ArrayList<Node>());
    Library hwLibrary = new Library(new ArrayList<Node>());
    Library eeLibrary = new Library(new ArrayList<Node>());
    Configuration swConfig;
    Configuration hwConfig;
    Configuration eeConfig;
    Group swUIObjects = new Group();
    Group hwUIObjects = new Group();
    Group eeUIObjects = new Group();

    Rectangle selectionRectangle;
    double selectionX;
    double selectionY;

    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert swTreeView != null : "fx:id\"swTreeView\" was not injected";
        assert hwTreeView != null : "fx:id\"hwTreeView\" was not injected";
        assert eeTreeView != null : "fx:id\"eeTreeView\" was not injected";

        BasicConfigurator.configure();

        // TODO: get highlighting and selection of Nodes based on selection
        // rectangle
        // enableSelectionRectangle(swPane);
        // enableSelectionRectangle(hwPane);
        // enableSelectionRectangle(eePane);

        // PythonLibraryHelper p = new PythonLibraryHelper();

        // TODO: Extract these similar initialization steps into a separate
        // method
        swLibrary.loadPlatform("landshark");
        swConfig = new Configuration("Demo", new ArrayList<Node>(), new ArrayList<Link>());
        swTree = new ROSLabTree(swLibrary, swConfig, this);
        swPane.getChildren().add(swUIObjects);
        swTreeView.setRoot(swTree);
        swTreeView.setShowRoot(false);
        swTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return swTree.new TreeCellImpl();
            }
        });

        eeLibrary.loadElectronics();
        eeConfig = new Configuration("Demo", new ArrayList<Node>(), new ArrayList<Link>());
        eeTree = new ROSLabTree(eeLibrary, eeConfig, this);
        eePane.getChildren().add(eeUIObjects);
        eeTreeView.setRoot(eeTree);
        eeTreeView.setShowRoot(false);
        eeTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return eeTree.new TreeCellImpl();
            }
        });

        hwConfig = new Configuration("Demo", new ArrayList<Node>(), new ArrayList<Link>());
        hwTree = new ROSLabTree(hwLibrary, hwConfig, this);
        hwPane.getChildren().add(hwUIObjects);
        hwTreeView.setRoot(hwTree);
        hwTreeView.setShowRoot(false);
        hwTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return hwTree.new TreeCellImpl();
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

    public void addLibraryNode(ROSNode n) {
        swLibrary.addNode(n);
        swTree.addLibraryNode(n);
    }

    public void removeLibraryNode(ROSNode n) {
        swLibrary.removeNode(n);
        swTree.removeLibraryNode(n);
    }

    public void updateLibraryNode(ROSNode n) {
        removeLibraryNode(n);
        addLibraryNode(n);
    }

    public void addLibraryNode(Circuit n) {
        eeLibrary.addNode(n);
        eeTree.addLibraryNode(n);
    }

    public void removeLibraryNode(Circuit n) {
        eeLibrary.removeNode(n);
        eeTree.removeLibraryNode(n);
    }

    public void updateLibraryNode(Circuit n) {
        removeLibraryNode(n);
        addLibraryNode(n);
    }

    public void addLibraryNode(HWBlock n) {
        hwLibrary.addNode(n);
        hwTree.addLibraryNode(n);
    }

    public void removeLibraryNode(HWBlock n) {
        hwLibrary.removeNode(n);
        hwTree.removeLibraryNode(n);
    }

    public void updateLibraryNode(HWBlock n) {
        removeLibraryNode(n);
        addLibraryNode(n);
    }

    public void addConfigNode(Node n) {
        UINode uin = new UINode(n, r.nextInt(400), r.nextInt(400));
        String nodeClassName = uin.getNode().getClass().getName();
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                swPane.getChildren().add(uin);
                swConfig.addNode(n);
                swTree.addConfigNode(n);
                break;
            case "HWBlock":
                hwPane.getChildren().add(uin);
                hwConfig.addNode(n);
                hwTree.addConfigNode(n);
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

                eeConfig.addNode(n);
                eeTree.addConfigNode(n);
                break;
        }
    }

    public void addConfigLink(Link l) {
        l.setUILink(new UILink(l));

        String nodeClassName = l.getSrc().getParent().getClass().getName();
        Object[] objArray = null;
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                swConfig.addLink(l);
                swTree.addConfigLink(l);
                objArray = swUIObjects.getChildren().toArray();
                break;
            case "HWBlock":
                hwConfig.addLink(l);
                hwTree.addConfigLink(l);
                objArray = hwUIObjects.getChildren().toArray();
                break;
            case "Circuit":
                eeConfig.addLink(l);
                eeTree.addConfigLink(l);
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
    }

    public void removeConfigNode(Node n) {
        String nodeClassName = n.getClass().getName();
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                swConfig.removeNode(n);
                swTree.removeConfigNode(n);
                swPane.getChildren().remove(n.getUINode());
                break;
            case "HWBlock":
                hwConfig.removeNode(n);
                hwTree.removeConfigNode(n);
                hwPane.getChildren().remove(n.getUINode());
                break;
            case "Circuit":
                eeConfig.removeNode(n);
                eeTree.removeConfigNode(n);
                eePane.getChildren().remove(n.getUINode());
                break;
        }

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
        String linkSrcClassName = l.getSrc().getClass().getName();
        switch (linkSrcClassName.substring(linkSrcClassName.lastIndexOf('.') + 1)) {
            case "ROSPort":
                swConfig.removeLink(l);
                swTree.removeConfigLink(l);
                break;
            case "Joint":
                hwConfig.removeLink(l);
                hwTree.removeConfigLink(l);
                break;
            case "Circuit":
                eeConfig.removeLink(l);
                eeTree.removeConfigLink(l);
                break;
        }
        l.destroy();
        l = null;
    }

    /**
     * @return the library
     */
    public Library getSWLibrary() {
        return swLibrary;
    }

    /**
     * @return the config
     */
    public Configuration getSWConfig() {
        return swConfig;
    }

    /**
     * @return the library
     */
    public Library getHWLibrary() {
        return hwLibrary;
    }

    /**
     * @return the config
     */
    public Configuration getHWConfig() {
        return hwConfig;
    }

    /**
     * @return the library
     */
    public Library getEELibrary() {
        return eeLibrary;
    }

    /**
     * @return the config
     */
    public Configuration getEEConfig() {
        return eeConfig;
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
    public boolean showNewLinkDialog(Configuration config) {
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
    public boolean showNewNodeDialog(Library library) {
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

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getStage() {
        return this.primaryStage;
    }
}
