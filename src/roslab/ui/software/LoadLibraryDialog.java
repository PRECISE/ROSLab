package roslab.ui.software;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import roslab.ROSLabController;
import roslab.processors.general.LibraryParser;

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
        controller.loadSWLibrary(LibraryParser.SW_LIBRARY_PATH.resolve(libraryBox.getValue().toString() + ".yaml"));
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
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(LibraryParser.SW_LIBRARY_PATH)) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                if (LibraryParser.isValidLibraryYAML(entry)) {
                    libraries.add(fileName.substring(0, fileName.lastIndexOf('.')));
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
