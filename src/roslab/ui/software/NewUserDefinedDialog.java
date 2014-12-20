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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import roslab.ROSLabController;
import roslab.model.software.ROSNode;

public class NewUserDefinedDialog implements Initializable {

    @FXML
    private TextField nameField;

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
            ROSNode n = new ROSNode(nameField.getText());
            n.addAnnotation("user-defined", "true");
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

        if (nameField.getText() == null || nameField.getText().equals("")) {
            errorMessage += "No name given!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        }
        else {
            // Show the error message
            Action response = Dialogs.create().owner(dialogStage).title("Invalid Fields").masthead("Please correct invalid fields")
                    .message(errorMessage).showError();
            return false;
        }
    }

    public void setRLController(ROSLabController rosLabController) {
        this.controller = rosLabController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
