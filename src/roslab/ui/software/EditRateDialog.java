package roslab.ui.software;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

import roslab.ROSLabController;
import roslab.model.software.ROSNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import roslab.model.general.Node;

@SuppressWarnings("deprecation")
public class EditRateDialog implements Initializable {
    
    @FXML
    private TextField rateField;

    private Stage dialogStage;
    private boolean addClicked = false;

    private ROSLabController controller;
    
    private ROSNode node;
    
    public void setNode(ROSNode node) {
    	this.node = node;
    }

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
     * Called when the user clicks save.
     */
    @FXML
    private void handleSave() {
        if (isInputValid() && controller != null) {
        	int newRate = Integer.parseInt(rateField.getText());
        	node.setRate(newRate);
        	node.getSpec().setRate(newRate);
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
        if (rateField.getText() == null || rateField.getText().equals("")) {
        	errorMessage += "No rate given!\n";
        }
        try {
        	Integer.parseInt(rateField.getText());
        } catch(NumberFormatException e) {
        	errorMessage += "Rate must be an integer!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        }
        else {
            // Show the error message
            Dialogs.create().owner(dialogStage).title("Invalid Fields").masthead("Please correct invalid field").message(errorMessage).showError();
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
