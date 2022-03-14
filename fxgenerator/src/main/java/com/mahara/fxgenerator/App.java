package com.mahara.fxgenerator;

import com.mahara.fxgenerator.util.FreeMarkerConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("JavaFx Generator");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/warehouse.png").openStream()));
        initHome();
        FreeMarkerConfig.instance().init();
    }

    private void initHome() {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/Home.fxml"));
            var homeView = (AnchorPane) fxmlLoader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(homeView);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            log.error("Loaded fxml failed: {}.", "Home", e);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

}