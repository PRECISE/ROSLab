package roslab;

import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roslab.model.electronics.Circuit;
import roslab.model.general.Configuration;
import roslab.model.general.Endpoint;
import roslab.model.general.Library;
import roslab.model.general.Link;
import roslab.model.general.Node;
import roslab.model.mechanics.HWBlock;
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
import roslab.ui.software.EditRateDialog;
import roslab.ui.software.LoadLibraryDialog;
import roslab.ui.software.NewCustomControllerDialog;
import roslab.ui.software.NewCustomTopicDialog;
import roslab.ui.software.NewPortDialog;

import com.thoughtworks.xstream.XStream;

public class ROSLabController implements Initializable {
    static Logger logger = LoggerFactory.getLogger(ROSLabController.class);

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
    Line addPortLine;
    Point portLineStart = new Point();
    ContextMenu addSWNodeMenu;
    ContextMenu addHWNodeMenu;
    ContextMenu addEENodeMenu;

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

        loadSWComponents("");

        // TODO refactor all three to one helper method
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

        createAddNodeMenu(swLibrary);
        createAddNodeMenu(eeLibrary);
        createAddNodeMenu(hwLibrary);
        // addDragDrop(swPane);
    }

    public void loadSWComponents(String library) {
        swPane.getChildren().clear();
        swUIObjects.getChildren().clear();
        if (!"".equals(library)) {
            swLibrary.loadPlatform(library);
        }
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
    }

    public void createAddNodeMenu(final Library lib) {
    	final AnchorPane pane;
    	final Group uiObjects;
    	final ContextMenu menu;
    	if(swLibrary.equals(lib)) {
    		pane = swPane;
    		uiObjects = swUIObjects;
    		addSWNodeMenu = new ContextMenu();
    		menu = addSWNodeMenu;	
    	} else if(hwLibrary.equals(lib)) {
    		pane = hwPane;
    		uiObjects = hwUIObjects;
    		addHWNodeMenu = new ContextMenu();
    		menu = addHWNodeMenu;
    	} else if(eeLibrary.equals(lib)) {
    		pane = eePane;
    		uiObjects = eeUIObjects;
    		addEENodeMenu = new ContextMenu();
    		menu = addEENodeMenu;
    	} else return;
        MenuItem addItem = new MenuItem("Add Node");
        addItem.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent event) {
        		showNewNodeDialog(lib);
        	}
        });
        menu.getItems().add(addItem);
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY &&
                		!uiObjects.getChildren().contains(mouseEvent.getTarget())) { //TODO test
                	menu.show(swPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                	menu.hide();
                }
            }
        });
    }

    // private void enableSelectionRectangle(final Pane p) {
    // p.setOnMousePressed(new EventHandler<MouseEvent>() {
    // @Override
    // public void handle(MouseEvent mouseEvent) {
    // selectionX = mouseEvent.getX();
    // selectionY = mouseEvent.getY();
    // selectionRectangle = new Rectangle(selectionX, selectionY, 0, 0);
    // selectionRectangle.getStyleClass().add("SelectionRectangle");
    // p.getChildren().add(selectionRectangle);
    // }
    // });
    // p.setOnMouseReleased(new EventHandler<MouseEvent>() {
    // @Override
    // public void handle(MouseEvent mouseEvent) {
    // p.getChildren().remove(selectionRectangle);
    // }
    // });
    //
    // p.setOnMouseDragged(new EventHandler<MouseEvent>() {
    // @Override
    // public void handle(MouseEvent mouseEvent) {
    // double x = mouseEvent.getX();
    // double y = mouseEvent.getY();
    // if (x < selectionX) {
    // selectionRectangle.setX(x);
    // selectionRectangle.setWidth(selectionX - x);
    // }
    // else {
    // selectionRectangle.setWidth(x - selectionRectangle.getX());
    // }
    // if (y < selectionY) {
    // selectionRectangle.setY(y);
    // selectionRectangle.setHeight(selectionY - y);
    // }
    // else {
    // selectionRectangle.setHeight(y - selectionRectangle.getY());
    // }
    // }
    // });
    // }

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
        Group grp = null;
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                grp = swUIObjects;
                swConfig.addNode(n);
                swTree.addConfigNode(n);
                refreshConfigLinks(swConfig);
                break;
            case "HWBlock":
                hwConfig.addNode(n);
                hwTree.addConfigNode(n);
                grp = hwUIObjects;
                break;
            case "Circuit":
                eeConfig.addNode(n);
                eeTree.addConfigNode(n);
                grp = eeUIObjects;
                break;
        }
        uin.addToGroup(grp, this);

        // Order all of the UI objects
        for (Object nn : grp.getChildren().toArray()) {
            if (nn instanceof UINode) {
                ((UINode) nn).toTheFront();
            }
            if (nn instanceof UILink) {
                ((UILink) nn).toBack();
            }
        }
    }

    public void addConfigLink(Link l) {
        UILink uil = new UILink(l);
        l.setUILink(uil);

        String nodeClassName = l.getSrc().getParent().getClass().getName();
        Group grp = null;
        switch (nodeClassName.substring(nodeClassName.lastIndexOf('.') + 1)) {
            case "ROSNode":
                swConfig.addLink(l);
                swTree.addConfigLink(l);
                grp = swUIObjects;
                break;
            case "HWBlock":
                hwConfig.addLink(l);
                hwTree.addConfigLink(l);
                grp = hwUIObjects;
                break;
            case "Circuit":
                eeConfig.addLink(l);
                eeTree.addConfigLink(l);
                grp = eeUIObjects;
                break;
        }

        grp.getChildren().add(l.getUILink());
        // Order all of the UI objects
        for (Object nn : grp.getChildren().toArray()) {
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
                for (UIEndpoint e : n.getUINode().getUIEndpoints()) {
                    for (UILink l : e.getUILinks()) {
                        swConfig.removeLink(l.getLink());
                        swTree.removeConfigLink(l.getLink());
                        if (e.equals(l.getSrc())) {
                            l.getDest().removeUILink(l);
                        }
                        else {
                            l.getSrc().removeUILink(l);
                        }
                        swUIObjects.getChildren().remove(l);
                    }
                    e.getUILinks().clear();
                    // e.removeFromGroup(swUIObjects);
                    // swUIObjects.getChildren().remove(e.getEndpointText());
                    // swUIObjects.getChildren().remove(e.getPortLine());
                    // swUIObjects.getChildren().remove(e);
                }
                n.getUINode().removeFromGroup(swUIObjects);
                // swUIObjects.getChildren().remove(n.getUINode().getNodeUIText());
                // swUIObjects.getChildren().remove(n.getUINode());
                swConfig.removeNode(n);
                swTree.removeConfigNode(n);
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
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("LAB files (*.lab)", "*.lab");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File openFile = fileChooser.showOpenDialog(primaryStage);

        // TODO Use XStream here!
        // Unzip package and load all xml files
    }

    @FXML
    private void saveConfiguration() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("LAB files (*.lab)", "*.lab");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File saveFile = fileChooser.showSaveDialog(primaryStage);

        if (saveFile != null) {
            FileOutputStream fos = null;
            ZipOutputStream zipOut = null;
            XStream xstream = new XStream();
            try {
                fos = new FileOutputStream(saveFile);
                zipOut = new ZipOutputStream(new BufferedOutputStream(fos));

                InputStream is = new ByteArrayInputStream(xstream.toXML(swConfig).getBytes());
                ZipEntry ze = new ZipEntry("swConfig.xml");
                zipOut.putNextEntry(ze);
                byte[] tmp = new byte[4 * 1024];
                int size = 0;
                while ((size = is.read(tmp)) != -1) {
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();

                is = new ByteArrayInputStream(xstream.toXML(eeConfig).getBytes());
                ze = new ZipEntry("eeConfig.xml");
                zipOut.putNextEntry(ze);
                tmp = new byte[4 * 1024];
                while ((size = is.read(tmp)) != -1) {
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();

                is = new ByteArrayInputStream(xstream.toXML(hwConfig).getBytes());
                ze = new ZipEntry("hwConfig.xml");
                zipOut.putNextEntry(ze);
                tmp = new byte[4 * 1024];
                while ((size = is.read(tmp)) != -1) {
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();

                zipOut.close();
            }
            catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                }
                catch (Exception ex) {

                }
            }
        }
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
    public boolean showCustomNodeDialog(String nodeType) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/software/NewCustom" + nodeType + "Dialog.fxml"));
            GridPane page = (GridPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Custom " + nodeType + " Node");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            if ("Controller".equals(nodeType)) {
                NewCustomControllerDialog controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setRLController(this);
                // Show the dialog and wait until the user closes it
                dialogStage.showAndWait();
                return controller.isAddClicked();
            }
            else {
                NewCustomTopicDialog controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setRLController(this);
                // Show the dialog and wait until the user closes it
                dialogStage.showAndWait();
                return controller.isAddClicked();
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showEditRateDialog(Node node) {
        if (!(node instanceof ROSNode && "controller".equals(node.getAnnotation("custom-type")))) {
            return false;
        }
        ROSNode rosNode = (ROSNode) node;
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/software/EditRateDialog.fxml"));
            GridPane page = (GridPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Rate");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditRateDialog controller = loader.getController();
            controller.setNode(rosNode);
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

    public boolean showLoadLibraryDialog() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/software/LoadLibraryDialog.fxml"));
            GridPane page = (GridPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Load Library");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            LoadLibraryDialog controller = loader.getController();
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

    public void addConfigPort(Node node, String pName, String pType, boolean isSub) {
        // Add to each matching node's features

        // Add to library node's features
        ((ROSNode) node.getSpec()).addPort(new ROSPort(pName, ((ROSNode) node), new ROSTopic(pName, new ROSMsgType(pType), isSub), false, false));
        // Update library with library node
        updateLibraryNode((ROSNode) node.getSpec());
        for (Node n : swConfig.getNodes()) {
            if (n instanceof ROSNode && n.getSpec().equals(node.getSpec())) {
                ((ROSNode) n).addPort(new ROSPort(pName, ((ROSNode) node), new ROSTopic(pName, new ROSMsgType(pType), isSub), false, false));
            }
        }
        refreshConfigPorts();
        refreshConfigLinks(swConfig);
    }

    public void removeConfigPort(Node node, String pName) {
        // Add to library node's features
        ((ROSNode) node.getSpec()).removePort(pName);
        // Update library with library node
        updateLibraryNode((ROSNode) node.getSpec());
        for (Node n : swConfig.getNodes()) {
            if (n instanceof ROSNode && n.getSpec().equals(node.getSpec())) {
                ((ROSNode) n).removePort(pName);
            }
        }
        refreshConfigPorts();
        refreshConfigLinks(swConfig);
    }

    public void refreshConfigPorts() {
        for (Node n : swConfig.getNodes()) {
            UINode uin = n.getUINode();
            for (UIEndpoint e : uin.getUIEndpoints()) {
                e.removeFromGroup(swUIObjects);
                // swUIObjects.getChildren().remove(e.getEndpointText());
                // swUIObjects.getChildren().remove(e.getPortLine());
                // swUIObjects.getChildren().remove(e);
            }
            uin.resetEndpoints(this);
            for (UIEndpoint e : uin.getUIEndpoints()) {
                e.addToGroup(swUIObjects);
                // swUIObjects.getChildren().add(e);
                // swUIObjects.getChildren().add(e.getEndpointText());
                // swUIObjects.getChildren().add(e.getPortLine());
            }
        }
    }

    public void refreshConfigLinks(Configuration config) {
        ArrayList<Link> linksToRemove = new ArrayList<Link>();
        for (Link link : config.getLinks()) {
            swUIObjects.getChildren().remove(link.getUILink());
            linksToRemove.add(link);
        }
        for (Link link : linksToRemove) {
            System.out.println("Removing " + link.getName());
            removeConfigLink(link);
        }
        config.getLinks().clear();
        for (Node nodeA : config.getNodes()) {
            for (Node nodeB : config.getNodes()) {
                for (Endpoint endA : nodeA.getEndpoints()) {
                    for (Endpoint endB : nodeB.getEndpoints()) {
                        if (endA.equals(endB)) {
                            continue;
                        }
                        if (endA.canConnect(endB) && endA instanceof ROSPort && ((ROSPort) endA).isSubscriber()) {
                            Link link = new Link(endB, endA);
                            addConfigLink(link);
                        }
                    }
                }
            }
        }
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getStage() {
        return this.primaryStage;
    }

    public void killDrawTasks() {
        for (Node n : swConfig.getNodes()) {
            for (Endpoint e : n.getEndpoints()) {
                if (e.getUIEndpoint().killDrawTask()) {
                    System.out.println("Killed " + e.getName());
                }
                else {
                    System.out.println("Didn't kill " + e.getName());
                }
            }
        }
    }
}
