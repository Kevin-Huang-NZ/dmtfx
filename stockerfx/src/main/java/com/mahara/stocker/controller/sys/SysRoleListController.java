package com.mahara.stocker.controller.sys;

//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.context.AnalysisContext;
//import com.alibaba.excel.read.listener.PageReadListener;
//import com.alibaba.excel.read.listener.ReadListener;
//import com.alibaba.excel.util.ListUtils;
import com.mahara.stocker.controller.BaseController;
import com.mahara.stocker.dao.SysRoleRepository;
import com.mahara.stocker.model.SysRole;
import com.mahara.stocker.service.SysService;
import com.mahara.stocker.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.io.*;

@Controller
public class SysRoleListController extends BaseController<SysRole> {
    private static final Logger log = LoggerFactory.getLogger(SysRoleListController.class);
    @FXML
    private AnchorPane sysRoleListPane;
    @FXML
    private TableView<SysRole> searchResultTable;
    @FXML
    private TableColumn<SysRole, String> roleNoColumn;
    @FXML
    private TableColumn<SysRole, String> roleNameColumn;
    @FXML
    private TableColumn<SysRole, String> memoColumn;
    @FXML
    private TableColumn<SysRole, Void> operationColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private TextField keyWordTextField;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<SysRole> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    @Autowired
    @Qualifier("jtSysRoleRepository")
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysService sysService;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    @FXML
    private void initialize() {
        roleNoColumn.setCellValueFactory(cellData -> cellData.getValue().roleNoProperty());
        roleNameColumn.setCellValueFactory(cellData -> cellData.getValue().roleNameProperty());
        memoColumn.setCellValueFactory(cellData -> cellData.getValue().memoProperty());
//        addButtonToTable(operationColumn);
        customizedOperationColumn();
        //设置为多选模式
//        searchResultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        search();
    }

    @FXML
    private void handleSearch() {
        pageNo = 1;
        search();
    }

    @FXML
    private void handleNew() {
        var tmp = new SysRole();
        boolean saveClicked = this.showEditDialog(tmp);
        if (saveClicked) {
            observableData.add(tmp);
        }
    }

    @Override
    protected void handleEdit(int index) {
        if (index >= 0 && index < searchResultTable.getItems().size()) {
            var selected = searchResultTable.getItems().get(index);
            this.showEditDialog(selected);
        }
    }

    @Override
    protected void handleDelete(int index) {
        if (index >= 0 && index < searchResultTable.getItems().size()) {
            var selected = searchResultTable.getItems().get(index);
            sysService.deleteSysRole(selected.getId());
            searchResultTable.getItems().remove(index);
        }
    }

    private void handleChooseFunction(int index) {
        if (index >= 0 && index < searchResultTable.getItems().size()) {
            var selected = searchResultTable.getItems().get(index);
            this.showFunctionDialog(selected);
        }
    }

    @FXML
    private void handlePrevious() {
        pageNo = PaginationUtil.getPreviousPageNo(pageNo);
        search();
    }

    @FXML
    private void handleNext() {
        pageNo = PaginationUtil.getNextPageNo(pageNo, totalPage);
        search();
    }

    @FXML
    private void handleGoto() {
        pageNo = PaginationUtil.getGotoPageNo(pageNoTextField.getText(), totalPage);
        search();
    }

    private void search() {
        var keyWord = keyWordTextField.getText();
        var searchResult = sysRoleRepository.findByKeyWord(keyWord, new PaginationIn(pageNo, pageSize));
        observableData.setAll(searchResult.getData());
        searchResultTable.setItems(observableData);

        totalPage = searchResult.getTotalPage();
        totalPageLabel.setText(String.valueOf(totalPage));
        pageNoTextField.setText(String.valueOf(pageNo));
    }

    private boolean showEditDialog(SysRole sysRole) {
        try {
            var primaryStage = (Stage) sysRoleListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/sys/SysRoleEditDialog.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("用户角色");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - editPage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - editPage.getPrefHeight()) / 2);

            var scene = new Scene(editPage);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            SysRoleEditDialogController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(sysRole);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "SysRoleEditDialog", e);
            return false;
        }
    }

    private void showFunctionDialog(SysRole sysRole) {
        try {
            var primaryStage = (Stage) sysRoleListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/sys/SysFunChooseDialog.fxml");
            var choosePage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("Choose functions");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - choosePage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - choosePage.getPrefHeight()) / 2);

            var scene = new Scene(choosePage);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            SysFunChooseController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(sysRole);
            controller.search();

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "SysRoleEditDialog", e);
        }
    }

    private void customizedOperationColumn() {

        operationColumn.setCellFactory(param -> new TableCell<SysRole, Void>(){
            private final ButtonBar buttonBar = new ButtonBar();
            {
                // edit button
                var editIcon = new FontIcon("far-edit");
                editIcon.setIconColor(new Color(255/255.0, 158/255.0, 105/255.0, 0.8));
                var editButton = createButton(editIcon, "Edit...");
                editButton.setOnAction((ActionEvent event) -> {
                    handleEdit(getIndex());
                });

                // delete button
                var deleteIcon = new FontIcon("far-trash-alt");
                deleteIcon.setIconColor(new Color(242/255.0, 70/255.0, 36/255.0, 0.8));
                var deleteButton = createButton(deleteIcon, "Delete");
                deleteButton.setOnAction((ActionEvent event) -> {
                    handleDelete(getIndex());
                });

                // choose function
                var chooseIcon = new FontIcon("far-check-square");
                chooseIcon.setIconColor(new Color(121/255.0, 189/255.0, 151/255.0, 0.8));
                var chooseButton = createButton(chooseIcon, "Choose Functions...");
                chooseButton.setOnAction((ActionEvent event) -> {
                    handleChooseFunction(getIndex());
                });

                // set up button bar
                buttonBar.setButtonMinWidth(15.0);
                buttonBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                buttonBar.setPadding(new Insets(0.0, 10.0, 0.0, 0.0));

                buttonBar.getButtons().addAll(chooseButton, deleteButton, editButton);
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
}
