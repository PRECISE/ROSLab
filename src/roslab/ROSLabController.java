package roslab;

import java.net.URL;
import java.util.ResourceBundle;

import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Node;
import roslab.model.ui.UINode;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert tree != null : "fx:id\"tree\" was not injected";
		System.out.println(this.getClass().getSimpleName() + ".initialize");
		loadTree();
		swPane.getChildren().add(new UINode(null, null, 0, 0));
		hwPane.getChildren().add(new UINode(null, null, 0, 0));
		eePane.getChildren().add(new UINode(null, null, 0, 0));
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	private void openLibrary() {
		
	}
	
	private void openConfiguration() {
		
	}
	
	private void loadTree() {
		TreeItem<Node> dummyRoot = new TreeItem<Node>();
		TreeItem<Node> libraryNode = new TreeItem<Node>(new Node("Library", null, null));
		//for (Node n : library.getNodes()) {
			//libraryNode.getChildren().add(new Object());
		//}
		TreeItem<Node> configNode = new TreeItem<Node>(new Node("Configuration", null, null));
		dummyRoot.getChildren().addAll(libraryNode, configNode);
		
		tree.setRoot(dummyRoot);
		tree.setShowRoot(false);
	}
	
}
