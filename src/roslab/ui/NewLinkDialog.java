/**
 *
 */
package roslab.ui;

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
import javafx.stage.Stage;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import roslab.ROSLabController;
import roslab.model.general.Endpoint;
import roslab.model.general.Link;

public class NewLinkDialog implements Initializable {

    @FXML
    private ComboBox<Endpoint> srcBox;
    @FXML
    private ComboBox<Endpoint> destBox;

    private Stage dialogStage;
    private boolean okClicked = false;
    private ObservableList<Endpoint> endpoints;
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
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user changes Source.
     */
    @FXML
    private void handleSource() {
        if (srcBox.getValue() != null) {
            ObservableList<Endpoint> destEndpoints = FXCollections.observableArrayList();
            for (Endpoint e : srcBox.getItems()) {
                if (srcBox.getValue().canConnect(e)) {
                    destEndpoints.add(e);
                }
            }
            destBox.setItems(destEndpoints);
        }
    }

    /**
     * Called when the user changes Destination.
     */
    @FXML
    private void handleDest() {
        // TODO Anything to do here?
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            controller.addConfigLink(new Link(srcBox.getValue(), destBox.getValue()));

            okClicked = true;
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

        if (srcBox.getValue() == null) {
            errorMessage += "No valid source endpoint!\n";
        }
        if (destBox.getValue() == null) {
            errorMessage += "No valid destination endpoint!\n";
        }
        if (srcBox.getValue() != null && destBox.getValue() != null && !srcBox.getValue().canConnect(destBox.getValue())) {
            errorMessage += "Source endpoint cannot connect to destination endpoint!\n";
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

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = FXCollections.observableArrayList(endpoints);
        this.srcBox.setItems(this.endpoints);
    }

    public void setRLController(ROSLabController rosLabController) {
        this.controller = rosLabController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub

    }
}
