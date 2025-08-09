package video;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        
    	Parent root = FXMLLoader.load(getClass().getResource("VideoPlayer.fxml"));
        primaryStage.setTitle("Abido's Video Player");
        
        Scene sc = new Scene(root);
        sc.getStylesheets().add(getClass().getResource("player.css").toExternalForm());
        
        Image img = new Image("playervideo.png");
        primaryStage.getIcons().add(img);
        
        primaryStage.setScene(sc);
        primaryStage.show();
                
    }


    public static void main(String[] args) {
        launch(args);
    }
}