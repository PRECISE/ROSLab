package roslab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/general/ROSLab.fxml"));
            VBox root = (VBox) loader.load();

            // Give the controller access to the main app.
            ROSLabController controller = loader.getController();
            controller.setStage(this.primaryStage);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("ui/general/main.css").toString());
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("ROSLab");
            this.primaryStage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
