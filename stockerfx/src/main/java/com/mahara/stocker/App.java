package com.mahara.stocker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.StringWriter;

/**
 * JavaFX App
 */
public class App extends Application {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    private Stage primaryStage;
    private BorderPane root;
    private AnchorPane login;

    private ApplicationContext context;

    @Override
    public void start(Stage stage) throws IOException {

        context = new ClassPathXmlApplicationContext(new String[] {"spring-context.xml", "spring-db.xml"});

        primaryStage = stage;
        primaryStage.setTitle("外语地名翻译");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/dms-favicon.png").openStream()));
        initLogin();
    }

    private void initLogin() {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/Login.fxml"));
            // 使用spring application context中的controller
            fxmlLoader.setControllerFactory(context::getBean);
            this.login = (AnchorPane) fxmlLoader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(this.login);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            log.error("Loaded fxml failed: {}.", "Login", e);
        }
    }
    private static void showError(Thread t, Throwable e) {
        log.error("发生系统级异常，请联系系统管理员。", e);
        if (Platform.isFxApplicationThread()) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("发生系统级异常，请联系系统管理员。");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(App::showError);
        launch(args);
    }

}