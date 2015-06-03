package roslab.ui.software;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import roslab.model.software.ROSMsgType;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.software.ROSTopic;

@SuppressWarnings("deprecation")
public class LoadLibraryDialog implements Initializable {
	
    @FXML
    private ComboBox<String> libraryBox;

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
    private void handleLoad() {
    	controller.loadSWComponents(libraryBox.getValue().toString());
        addClicked = true;
        dialogStage.close();
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void setRLController(ROSLabController rosLabController) {
        this.controller = rosLabController;
    }
    
    private ArrayList<String> getLibraries() {
    	ArrayList<String> libraries = new ArrayList<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("resources", "platforms"))) {
            for (Path entry : stream) {
            	String fileName = entry.getFileName().toString();
            	int i = fileName.lastIndexOf('.');
            	if (i > 0 && "yaml".equals(fileName.substring(i+1))) {
            		libraries.add(fileName.substring(0, i));
            	}
            }
        }
        catch (IOException x) {
            System.err.println(x);
        }
    	return libraries;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {  	
        ObservableList<String> libraries = FXCollections.observableArrayList(getLibraries());
        FXCollections.sort(libraries);
        libraryBox.setItems(libraries);
    }

}
