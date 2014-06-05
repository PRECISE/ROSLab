package roslab;

import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
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
import roslab.model.hardware.HWBlock;
import roslab.model.hardware.Joint;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.ui.UINode;
import roslab.ui.ROSLabTreeCell;

public class ROSLabController implements Initializable {
	
	@FXML
	TreeView<Node> tree;
	
	@FXML
	AnchorPane swPane;
	
	@FXML
	AnchorPane hwPane;
	
	@FXML
	AnchorPane eePane;
	
	Library library;
	Configuration config;
	Rectangle selectionRectangle;
	double selectionX;
	double selectionY;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert tree != null : "fx:id\"tree\" was not injected";
		//System.out.println(this.getClass().getSimpleName() + ".initialize");
		//TODO: get highlighting and selection of Nodes based on selection rectangle
		//enableSelectionRectangle(swPane);
		//enableSelectionRectangle(hwPane);
		//enableSelectionRectangle(eePane);
		//loadTree();
		Random r = new Random();
		for (int i = 0; i < 100; i++) {
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
			swPane.getChildren().add(UINode.buildUINode(new ROSNode("test", m, new HashMap<String, String>(), null), r.nextInt(1000), r.nextInt(1000)));
			eePane.getChildren().add(UINode.buildUINode(new Circuit("test", m2, new HashMap<String, String>(), null), r.nextInt(1000), r.nextInt(1000)));
			hwPane.getChildren().add(UINode.buildUINode(new HWBlock("test", m3, new HashMap<String, String>(), null, null), r.nextInt(1000), r.nextInt(1000)));
		}
		//swPane.getChildren().add(UINode.buildUINode(new Circuit("test", null, null, null), null, r.nextInt(2000), r.nextInt(2000)));
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
				} else {
					selectionRectangle.setWidth(x - selectionRectangle.getX());
				}
				if (y < selectionY) {
					selectionRectangle.setY(y);
					selectionRectangle.setHeight(selectionY - y);
				} else {
					selectionRectangle.setHeight(y - selectionRectangle.getY());
				}
			}
		});
	}

	private void openLibrary() {
		
	}
	
	private void openConfiguration() {
		
	}
	
	private void loadTree() {
		TreeItem<Node> dummyRoot = new TreeItem<Node>();
		TreeItem<Node> libraryNode = new TreeItem<Node>(new Node("Library", null, null));
		for (Node n : library.getNodes()) {
			libraryNode.getChildren().add(new TreeItem<Node>(n));
		}
		TreeItem<Node> configNode = new TreeItem<Node>(new Node("Configuration", null, null));
		dummyRoot.getChildren().addAll(libraryNode, configNode);
		
		tree.setRoot(dummyRoot);
		tree.setShowRoot(false);
	}
	
}
