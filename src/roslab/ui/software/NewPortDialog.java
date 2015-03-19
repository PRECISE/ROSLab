/**
 *
 */
package roslab.ui.software;

/**
 * Dialog to edit details of a person.
 *
 * @author Peter Gebhard
 */
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
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
public class NewPortDialog implements Initializable {

    @FXML
    private CheckBox customizedBox;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<ROSMsgType> typeBox;
    @FXML
    private ComboBox<String> directionBox;

    private Stage dialogStage;
    private boolean addClicked = false;

    private ROSLabController controller;
    private ROSNode node;

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
            node.addPort(new ROSPort(nameField.getText(), node, customizedBox.isSelected() ? new ROSTopic(nameField.getText(), typeBox.getValue(),
                    directionBox.getValue() == "Subscribe") : new ROSTopic(nameField.getText(), typeBox.getValue(),
                            directionBox.getValue() == "Subscribe"), false, false));
            controller.updateLibraryNode(node);
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

    public void setNode(Node node) {
        this.node = (ROSNode) node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<ROSMsgType> types = FXCollections.observableArrayList(ROSMsgType.typeMap.keySet());
        FXCollections.sort(types);
        typeBox.setItems(types);
        directionBox.setItems(FXCollections.observableArrayList("Publish", "Subscribe"));
    }
}
