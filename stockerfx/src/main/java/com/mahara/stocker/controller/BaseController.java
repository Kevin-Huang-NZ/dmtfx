package com.mahara.stocker.controller;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public abstract class BaseController<T> {
    protected abstract void handleEdit(int index);
    protected abstract void handleDelete(int index);

    protected void addButtonToTable(TableColumn<T, Void> targetColum) {
        targetColum.setCellFactory(param -> new TableCell<T, Void>(){
            private final ButtonBar buttonBar = new ButtonBar();
            {
                // edit button
                var editIcon = new FontIcon("far-edit");
                editIcon.setIconColor(new Color(255/255.0, 158/255.0, 105/255.0, 0.8));
                var editButton = createButton(editIcon, "编辑...");
                editButton.setOnAction((ActionEvent event) -> {
                    handleEdit(getIndex());
                });

                // delete button
                var deleteIcon = new FontIcon("far-trash-alt");
                deleteIcon.setIconColor(new Color(242/255.0, 70/255.0, 36/255.0, 0.8));
                var deleteButton = createButton(deleteIcon, "删除");
                deleteButton.setOnAction((ActionEvent event) -> {
                    handleDelete(getIndex());
                });

                buttonBar.setButtonMinWidth(15.0);
                buttonBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                buttonBar.setPadding(new Insets(0.0, 10.0, 0.0, 0.0));
                buttonBar.getButtons().addAll( deleteButton, editButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBar);
                }
            }
        });
    }

    protected Button createButton(FontIcon icon, String tooltip) {
        icon.setIconSize(15);
        var tmp = new Button("");
        tmp.setTooltip(new Tooltip(tooltip));
        tmp.setMinSize(15.0, 15.0);
        tmp.setPrefSize(15.0, 15.0);
        tmp.setMaxSize(15.0, 15.0);
        tmp.setGraphic(icon);
        ButtonBar.setButtonUniformSize(tmp, false);
        tmp.getStyleClass().add("column-button");

        return tmp;
    }
}
