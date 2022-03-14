package com.mahara.stocker.util.task;

import com.mahara.stocker.controller.standard.TransliterationListController;
import com.mahara.stocker.util.FXMLLoaderUtil;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

public class TaskHelper {
    private static final Logger log = LoggerFactory.getLogger(TaskHelper.class);
    private Task<TaskResult> task;
    private Stage stage;
    private FXMLLoaderUtil fxmlLoader;
    private Consumer<TaskResult> successConsumber = new Consumer<TaskResult>() {
        @Override
        public void accept(TaskResult taskResult) {
            return;
        }
    };

    private String successMessage = "处理成功。";

    private TaskHelper(Task<TaskResult> task, Stage stage, FXMLLoaderUtil fxmlLoader) {
        this.task = task;
        this.stage = stage;
        this.fxmlLoader = fxmlLoader;
    }

    public static TaskHelper build(Task<TaskResult> task, Stage stage, FXMLLoaderUtil fxmlLoader) {
        var tmp = new TaskHelper(task, stage, fxmlLoader);
        return tmp;
    }

    public TaskHelper successMessage(String successMessage) {
        this.successMessage = successMessage;
        return this;
    }

    public TaskHelper successConsumer(Consumer<TaskResult> successConsumber) {
        this.successConsumber = successConsumber;
        return this;
    }

    public void run() {
        try {
            fxmlLoader.initialize("/view/Loading.fxml");
            var tmp = (AnchorPane) fxmlLoader.getView();

            var loadingDialog = new Stage();
            loadingDialog.initOwner(stage);
            loadingDialog.initStyle(StageStyle.UNDECORATED);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            loadingDialog.setX((bounds.getWidth() - tmp.getPrefWidth()) / 2);
            loadingDialog.setY((bounds.getHeight() - tmp.getPrefHeight()) / 2);

            var scene = new Scene(tmp);
            loadingDialog.setScene(scene);

            task.setOnRunning((e) -> loadingDialog.show());
            task.setOnSucceeded((e) -> {
                loadingDialog.hide();
                TaskResult result = null;
                try {
                    result = task.get();
                } catch (Exception ex) {
                    result = new TaskResult(ex, "系统异常，请联系系统管理员。");
                }

                if (result.isSuccess()) {
                    var doneDialog = new Alert(Alert.AlertType.INFORMATION);
                    doneDialog.setTitle(null);
                    doneDialog.setHeaderText(null);
                    doneDialog.setContentText(successMessage);
                    doneDialog.showAndWait();

                    successConsumber.accept(result);
                } else {
                    var doneDialog = new Alert(Alert.AlertType.ERROR);
                    doneDialog.setTitle(null);
                    doneDialog.setHeaderText(null);
                    doneDialog.setContentText(result.getMessage());
                    doneDialog.showAndWait();
                }
            });
            task.setOnFailed((e) -> {
                var doneDialog = new Alert(Alert.AlertType.ERROR);
                doneDialog.setTitle(null);
                doneDialog.setHeaderText(null);
                doneDialog.setContentText("系统异常，请联系系统管理员。");
                doneDialog.showAndWait();
            });

            new Thread(task).start();

        } catch (IOException | IllegalStateException e) {
            log.error("执行后台任务时发生异常。",e);
            var doneDialog = new Alert(Alert.AlertType.ERROR);
            doneDialog.setTitle(null);
            doneDialog.setHeaderText(null);
            doneDialog.setContentText("系统异常，请联系系统管理员。");
            doneDialog.showAndWait();
        }
    }
}
