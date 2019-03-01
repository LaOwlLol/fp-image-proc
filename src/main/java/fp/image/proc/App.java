package fp.image.proc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../../../views/App.fxml"));
        primaryStage.setTitle("Registration Form FXML Application");
        primaryStage.setScene(new Scene(root, 1120, 840));
        primaryStage.show();
    }

}
