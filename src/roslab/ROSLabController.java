package roslab;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Node;
import roslab.model.software.ROSNode;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;
import roslab.ui.ROSLabTree;

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

    private Main mainApp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert treeView != null : "fx:id\"treeView\" was not injected";
        // System.out.println(this.getClass().getSimpleName() + ".initialize");
        // TODO: get highlighting and selection of Nodes based on selection
        // rectangle
        // enableSelectionRectangle(swPane);
        // enableSelectionRectangle(hwPane);
        // enableSelectionRectangle(eePane);
        config = new Configuration("TEST", new ArrayList<UINode>(), new ArrayList<UILink>());

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
        tree = new ROSLabTree(library, config);

        treeView.setRoot(tree);
        treeView.setShowRoot(false);

        addDragDrop(swPane);

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

    private void addConfigNode(Node n) {
        UINode uin = new UINode(n, r.nextInt(400), r.nextInt(400));
        if (uin.getNode() instanceof ROSNode) {
            swPane.getChildren().add(uin);
        }
        config.addUINode(uin);
        tree.addConfigNode(uin);
    }

    private void addConfigLink(UILink l) {
        // TODO: Change this to take Link as input
        config.addUILink(l);
        tree.addConfigLink(l);
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

    public void setMainApp(Main main) {
        this.mainApp = main;
    }
}
