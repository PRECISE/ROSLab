/**
 *
 */
package roslab.ui.general;

/**
 * Dialog to edit details of a person.
 *
 * @author Peter Gebhard
 */
import java.net.URL;
import java.util.List;
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

@SuppressWarnings("deprecation")
public class NewNodeDialog implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Node> nodeBox;

    private Stage dialogStage;
    private boolean addClicked = false;
    private ObservableList<Node> nodes;

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
            controller.addConfigNode(nodeBox.getValue().clone(nameField.getText()));
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

        if (nameField.getText() == null || nameField.getText().equals("")) {
            errorMessage += "No name given!\n";
        }

        if (nodeBox.getValue() == null) {
            errorMessage += "No node type selected!\n";
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

    public void setNodes(List<Node> nodes) {
        this.nodes = FXCollections.observableArrayList(nodes);
        this.nodeBox.setItems(this.nodes);
    }

    public void setRLController(ROSLabController rosLabController) {
        this.controller = rosLabController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
    }
}
