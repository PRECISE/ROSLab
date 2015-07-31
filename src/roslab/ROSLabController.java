package roslab;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
import roslab.ui.software.NewCustomPortDialog;
import roslab.ui.software.NewCustomTopicDialog;

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
        eeConfig = new Configuration("Demo", eeLibrary, new ArrayList<Node>(), new ArrayList<Link>());
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

        hwConfig = new Configuration("Demo", hwLibrary, new ArrayList<Node>(), new ArrayList<Link>());
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

        createCanvasContextMenu(swLibrary, swConfig);
        createCanvasContextMenu(eeLibrary, eeConfig);
        createCanvasContextMenu(hwLibrary, hwConfig);
        // addDragDrop(swPane);
    }

    public void loadSWComponents(String library) {
        swPane.getChildren().clear();
        swUIObjects.getChildren().clear();
        if (!"".equals(library)) {
            swLibrary.loadPlatform(library);
        }
        swConfig = new Configuration("Demo", swLibrary, new ArrayList<Node>(), new ArrayList<Link>());
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

    public void createCanvasContextMenu(final Library lib, final Configuration config) {
        final AnchorPane pane;
        final Group uiObjects;
        final ContextMenu menu;
        if (swLibrary.equals(lib)) {
            pane = swPane;
            uiObjects = swUIObjects;
            addSWNodeMenu = new ContextMenu();
            menu = addSWNodeMenu;
        }
        else if (hwLibrary.equals(lib)) {
            pane = hwPane;
            uiObjects = hwUIObjects;
            addHWNodeMenu = new ContextMenu();
            menu = addHWNodeMenu;
        }
        else if (eeLibrary.equals(lib)) {
            pane = eePane;
            uiObjects = eeUIObjects;
            addEENodeMenu = new ContextMenu();
            menu = addEENodeMenu;
        }
        else {
            return;
        }

        MenuItem addNodeItem = new MenuItem("Add Node");
        addNodeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showNewNodeDialog(lib);
            }
        });
        menu.getItems().add(addNodeItem);

        if (!lib.equals(swLibrary)) {
            MenuItem addLinkItem = new MenuItem("Add Link");
            addLinkItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    showNewLinkDialog(config);
                }
            });
            menu.getItems().add(addLinkItem);
        }

        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY && !uiObjects.getChildren().contains(mouseEvent.getTarget())) { // TODO
                    // test
                    menu.show(swPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
                else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
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
                            if ("controller".equals(l.getDest().getParentNode().getAnnotation("custom-type")) && l.getDest().getUILinks().size() == 0) {
                                removeConfigPort(l.getDest().getParentNode(), l.getDest().getName());
                            }
                        }
                        else {
                            l.getSrc().removeUILink(l);
                            if ("controller".equals(l.getSrc().getParentNode().getAnnotation("custom-type")) && l.getSrc().getUILinks().size() == 0) {
                                removeConfigPort(l.getSrc().getParentNode(), l.getSrc().getName());
                            }
                        }
                        swUIObjects.getChildren().remove(l);
                    }
                    e.getUILinks().clear();
                    e.removeFromGroup(swUIObjects);
                }
                swConfig.removeNode(n);
                swTree.removeConfigNode(n);
                n.getUINode().removeFromGroup(swUIObjects);
                break;
            case "HWBlock":
                hwConfig.removeNode(n);
                hwTree.removeConfigNode(n);
                n.getUINode().removeFromGroup(hwUIObjects);
                break;
            case "Circuit":
                eeConfig.removeNode(n);
                eeTree.removeConfigNode(n);
                n.getUINode().removeFromGroup(eeUIObjects);
                break;
        }

        // Remove any links associated with this node
        for (Endpoint e : n.getEndpoints()) {
            Iterator<? extends Link> it = e.getLinks().iterator();
            while (it.hasNext()) {
                removeConfigLink(it.next());
                it.remove();
            }
        }

        n = null;
    }

    public void removeMatchingConfigNodes(Node node, Configuration config) {
        ArrayList<Node> toRemove = new ArrayList<Node>();
        boolean isCustomTopic = "topic".equals(node.getAnnotation("custom-type"));
        for (Node n : config.getNodes()) {
            if (node.getName().equals(n.getSpec().getName())) {
                toRemove.add(n);
            }
            if (isCustomTopic && "controller".equals(n.getSpec().getAnnotation("custom-type"))) {
                String topicName = ((ROSNode) node).getPorts().keySet().iterator().next();
                removeConfigPort(n, topicName);
            }
        }
        Iterator<Node> iter = toRemove.iterator();
        while (iter.hasNext()) {
            removeConfigNode(iter.next());
            iter.remove();
        }
    }

    public void removeConfigLink(Link l) {
        String linkSrcClassName = l.getSrc().getClass().getName();
        switch (linkSrcClassName.substring(linkSrcClassName.lastIndexOf('.') + 1)) {
            case "ROSPort":
                swConfig.removeLink(l);
                swTree.removeConfigLink(l);
                l.getUILink().removeFromGroup(swUIObjects);
                break;
            case "Joint":
                hwConfig.removeLink(l);
                hwTree.removeConfigLink(l);
                l.getUILink().removeFromGroup(hwUIObjects);
                break;
            case "Circuit":
                eeConfig.removeLink(l);
                eeTree.removeConfigLink(l);
                l.getUILink().removeFromGroup(eeUIObjects);
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

        // Unzip package and load all xml files
        FileInputStream fis = null;
        ZipInputStream zipIs = null;
        ZipEntry zEntry = null;
        try {
            fis = new FileInputStream(openFile);
            zipIs = new ZipInputStream(new BufferedInputStream(fis));
            while ((zEntry = zipIs.getNextEntry()) != null) {
                try {
                    byte[] tmp = new byte[4 * 1024];
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int size = 0;
                    while ((size = zipIs.read(tmp)) != -1) {
                        bos.write(tmp, 0, size);
                    }

                    bos.flush();

                    switch (zEntry.getName()) {
                        case "swConfig.xml":
                            // swConfig = (Configuration)
                            // xstream.fromXML(bos.toString());
                            break;
                        case "eeConfig.xml":
                            // eeConfig = (Configuration)
                            // xstream.fromXML(bos.toString());
                            break;
                        case "hwConfig.xml":
                            // hwConfig = (Configuration)
                            // xstream.fromXML(bos.toString());
                            break;
                    }

                    bos.reset();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            zipIs.close();
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            ArrayList<Node> nodeTypes = new ArrayList<Node>(library.getNodes());
            if (library == swLibrary) {
                for (Node configNode : swConfig.getNodes()) {
                    if ("controller".equals(configNode.getAnnotation("custom-type"))) {
                        Node toRemove = null;
                        for (Node n : nodeTypes) {
                            if (configNode.getSpec().getName().equals(n.getName())) {
                                toRemove = n;
                            }
                        }
                        nodeTypes.remove(toRemove);
                    }
                }
            }
            controller.setNodes(nodeTypes);
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
    public boolean showNewCustomPortDialog(Node node) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/software/NewCustomPortDialog.fxml"));
            GridPane page = (GridPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Port");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            NewCustomPortDialog controller = loader.getController();
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
        ROSPort toAdd = new ROSPort(pName, ((ROSNode) node), new ROSTopic(pName, new ROSMsgType(pType), isSub), false, false);
        ((ROSNode) node).addPort(toAdd); // Add to this node's features
        ((ROSNode) node.getSpec()).addPort(toAdd); // Add to library node's features
        updateLibraryNode((ROSNode) node.getSpec()); // Update library with library node
        // for (Node n : swConfig.getNodes()) {
        // if (n instanceof ROSNode && n.getSpec().equals(node.getSpec())) {
        // ((ROSNode) n).addPort(new ROSPort(pName, ((ROSNode) node), new
        // ROSTopic(pName, new ROSMsgType(pType), isSub), false, false));
        // }
        // }
        refreshConfigPorts();
        refreshConfigLinks(swConfig);
    }

    public void removeConfigPort(Node node, String pName) {
        ((ROSNode) node).removePort(pName);
        // Add to library node's features
        ((ROSNode) node.getSpec()).removePort(pName);
        // Update library with library node
        updateLibraryNode((ROSNode) node.getSpec());
        // for (Node n : swConfig.getNodes()) {
        // if (n instanceof ROSNode && n.getSpec().equals(node.getSpec())) {
        // ((ROSNode) n).removePort(pName);
        // }
        // }
        refreshConfigPorts();
        refreshConfigLinks(swConfig);
    }

    public void refreshConfigPorts() {
        for (Node n : swConfig.getNodes()) {
            UINode uin = n.getUINode();
            for (UIEndpoint e : uin.getUIEndpoints()) {
                e.removeFromGroup(swUIObjects);
            }
            uin.resetEndpoints(this);
            for (UIEndpoint e : uin.getUIEndpoints()) {
                e.addToGroup(swUIObjects);
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
            removeConfigLink(link);
        }
        config.getLinks().clear();
        for (Node nodeA : config.getNodes()) {
            for (Node nodeB : config.getNodes()) {
                for (Endpoint endA : nodeA.getEndpoints()) {
                    for (Endpoint endB : nodeB.getEndpoints()) {
                        System.out.println("Matching " + endB.getParent().getName() + " " + endA.getParent().getName());
                        if (endA.equals(endB)) {
                            continue;
                        }
                        if (endA.canConnect(endB) && endA instanceof ROSPort && ((ROSPort) endA).isSubscriber()) {
                            addConfigLink(endB.connect(endA));
                            System.out.println("Adding");
                        }
                    }
                }
            }
        }
        System.out.println("Config link count: " + config.getLinks().size());
        for (Link l : config.getLinks()) {
            System.out.println(l.getSrc().getParent().getName() + " -> " + l.getDest().getParent().getName());
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
                e.getUIEndpoint().killDrawTask();
            }
        }
    }
}
