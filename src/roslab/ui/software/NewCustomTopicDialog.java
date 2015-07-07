package roslab.ui.software;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import roslab.ROSLabController;
import roslab.model.general.Node;
import roslab.model.software.ROSMsgType;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.software.ROSTopic;

@SuppressWarnings("deprecation")
public class NewCustomTopicDialog implements Initializable {

    @FXML
    private TextField nodeField;
    @FXML
    private TextField topicField;
    @FXML
    private ComboBox<ROSMsgType> typeBox;
    @FXML
    private ComboBox<String> directionBox;

    private Stage dialogStage;
    private boolean addClicked = false;

    private ROSLabController controller;

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Returns true if the user clicked Add, false otherwise.
     *
     * @return
     */
    public boolean isAddClicked() {
        return addClicked;
    }

    /**
     * Called when the user clicks add.
     */
    @FXML
    private void handleAdd() {
        if (isInputValid() && controller != null) {
            ROSNode n = new ROSNode(nodeField.getText()); //TODO custom rate here?
            n.addAnnotation("user-defined", "true");
        	n.addAnnotation("custom-type", "topic");    
        	String topic = "/" + topicField.getText();
            n.addPort(new ROSPort(topic, n, new ROSTopic(topic, typeBox.getValue(),
                    directionBox.getValue() == "Subscribe"), false, false));
            controller.addLibraryNode(n);
            addClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";
    	for(Node n: controller.getSWLibrary().getNodes()) {
    		if(n.getName().toLowerCase().equals(nodeField.getText().toLowerCase())) {
                errorMessage += "Node name already exists in library!\n";    			
    		}
    	}
    	if (nodeField.getText().matches("^.*\\s+.*$")) {
    		errorMessage += "Node name must not contain whitespace!\n";
    	}
        if (nodeField.getText() == null || nodeField.getText().equals("")) {
            errorMessage += "No node name given!\n";
        }
        //Check for topic name / message type mismatch
    	for(Node n: controller.getSWLibrary().getNodes()) {
    		for(ROSPort p: ((ROSNode)n).getPorts().values()) {
    			if(p.getTopicName().equals("/" + topicField.getText()) && !p.getType().equals(typeBox.getValue())) {
    				errorMessage += "Topic name \"" + p.getTopicName() + "\" already exists in this library"
    						+ " with message type \"" + p.getType() + "\".\n";
    			}
    		}
    	}
    	if (topicField.getText().matches("^.*\\s+.*$")) {
    		errorMessage += "Topic name must not contain whitespace!\n";
    	}
        if (topicField.getText() == null || topicField.getText().equals("")) {
            errorMessage += "No topic name given!\n";
        }
        if (typeBox.getValue() == null) {
            errorMessage += "No type selected!\n";
        }
        if (directionBox.getValue() == null) {
            errorMessage += "No direction selected!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        }
        else {
            // Show the error message
            Dialogs.create().owner(dialogStage).title("Invalid Fields").masthead("Please correct invalid fields").message(errorMessage).showError();
            return false;
        }
    }

    public void setRLController(ROSLabController rosLabController) {
        this.controller = rosLabController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<ROSMsgType> types = FXCollections.observableArrayList(ROSMsgType.typeMap.keySet());
        FXCollections.sort(types);
        typeBox.setItems(types);
        directionBox.setItems(FXCollections.observableArrayList("Publish", "Subscribe"));
    }
}
