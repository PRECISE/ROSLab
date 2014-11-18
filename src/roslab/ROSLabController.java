package roslab;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import roslab.model.electronics.Circuit;
import roslab.model.electronics.Pin;
import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Node;
import roslab.model.mechanics.HWBlock;
import roslab.model.mechanics.Joint;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;

public class ROSLabController implements Initializable {

    @FXML
    TreeView<String> tree;

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

    Library library = new Library(new ArrayList<Node>());
    Configuration config;
    Rectangle selectionRectangle;
    double selectionX;
    double selectionY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert tree != null : "fx:id\"tree\" was not injected";
        // System.out.println(this.getClass().getSimpleName() + ".initialize");
        // TODO: get highlighting and selection of Nodes based on selection
        // rectangle
        // enableSelectionRectangle(swPane);
        // enableSelectionRectangle(hwPane);
        // enableSelectionRectangle(eePane);
        config = new Configuration(new ArrayList<UINode>(), new ArrayList<UILink>());
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            HashMap<String, ROSPort> m = new HashMap<String, ROSPort>();
            HashMap<String, Pin> m2 = new HashMap<String, Pin>();
            HashMap<String, Joint> m3 = new HashMap<String, Joint>();
            for (int j = 0; j < r.nextInt(100); j++) {
                String name = String.valueOf(r.nextInt(1000000));
                m.put(name, new ROSPort(name, null, null, null, r.nextBoolean(), r.nextBoolean(), r.nextBoolean()));
            }
            for (int j = 0; j < r.nextInt(100); j++) {
                String name = String.valueOf(r.nextInt(1000000));
                m2.put(name, new Pin(name, null, null, null));
            }
            for (int j = 0; j < r.nextInt(100); j++) {
                String name = String.valueOf(r.nextInt(1000000));
                m3.put(name, new Joint(name, null, null, r.nextBoolean(), r.nextBoolean()));
            }
            ROSNode rn = new ROSNode("test" + i, m, new HashMap<String, String>(), null);
            library.addNode(rn);
            Circuit cn = new Circuit("test" + i, m2, new HashMap<String, String>(), null);
            library.addNode(cn);
            HWBlock hw = new HWBlock("test" + i, m3, new HashMap<String, String>(), null, null);
            library.addNode(hw);
            swPane.getChildren().add(UINode.buildUINode(rn, r.nextInt(1000), r.nextInt(1000)));
            eePane.getChildren().add(UINode.buildUINode(cn, r.nextInt(1000), r.nextInt(1000)));
            hwPane.getChildren().add(UINode.buildUINode(hw, r.nextInt(1000), r.nextInt(1000)));
        }
        List<Node> rosNodesList = library.getNodesOfClass(ROSNode.class);
        for (int i = 0; i < 20; i++) {
            ROSNode rn1 = (ROSNode) rosNodesList.get(r.nextInt(rosNodesList.size()));
            ROSNode rn2 = (ROSNode) rosNodesList.get(r.nextInt(rosNodesList.size()));

        }
        loadTree();
        tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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

    private void openLibrary() {
        // Use XStream here!
    }

    private void openConfiguration() {
        // Use XStream here!
    }

    private void saveLibrary() {
        // Use XStream here!
    }

    private void saveConfiguration() {
        // Use XStream here!
    }

    @FXML
    private void tabChanged() {

    }

    // TODO: Improve Tree to stay in sync with Library data structure...how can
    // they be integrated?
    private void loadTree() {
        TreeItem<String> dummyRoot = new TreeItem<String>();
        TreeItem<String> libraryNode = new TreeItem<String>("Library");
        boolean nodeAdded;
        for (Node n : library.getNodes()) {
            nodeAdded = false;
            for (TreeItem<String> s : libraryNode.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    s.getChildren().add(new TreeItem<String>(n.getName()));
                    nodeAdded = true;
                }
            }
            if (!nodeAdded) {
                TreeItem<String> newItem = new TreeItem<String>(n.getClass().getSimpleName());
                newItem.getChildren().add(new TreeItem<String>(n.getName()));
                libraryNode.getChildren().add(newItem);
                nodeAdded = true;
            }
        }
        TreeItem<String> configNode = new TreeItem<String>("Configuration");
        dummyRoot.getChildren().addAll(libraryNode, configNode);

        tree.setRoot(dummyRoot);
        tree.setShowRoot(false);
    }
}
