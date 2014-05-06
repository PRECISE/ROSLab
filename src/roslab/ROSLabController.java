package roslab;

import java.net.URL;
import java.util.ResourceBundle;

import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Node;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ROSLabController implements Initializable {
	
	@FXML
	TreeView<Node> tree;
	
	Library library;
	Configuration config;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert tree != null : "fx:id\"tree\" was not injected";
		System.out.println(this.getClass().getSimpleName() + ".initialize");
		loadTree();
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	private void openLibrary() {
		
	}
	
	private void openConfiguration() {
		
	}
	
	private void loadTree() {
		TreeItem<Node> dummyRoot = new TreeItem<Node>();
		TreeItem<Node> libraryNode = new TreeItem<Node>(new Node("Library", null, null));
		for (Node n : library.getNodes()) {
			//libraryNode.getChildren().add(new Object());
		}
		TreeItem<Node> configNode = new TreeItem<Node>(new Node("Configuration", null, null));
		dummyRoot.getChildren().addAll(libraryNode, configNode);
		
		
		
		tree.setRoot(dummyRoot);
		tree.setShowRoot(false);
	}
	
}
