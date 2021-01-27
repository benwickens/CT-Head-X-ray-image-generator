// Ben Wickens - 988947 - All code in this project, apart from the framework provided is my own.

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("cs-255.fxml"));
        primaryStage.setTitle("CS-255 Graphics");
        primaryStage.setScene(new Scene(root, 830, 665));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
