package com.mahara.stocker.controller.sys;

import com.mahara.stocker.dao.SysFunRepository;
import com.mahara.stocker.dao.SysPageRepository;
import com.mahara.stocker.dic.ActionType;
import com.mahara.stocker.model.SysFun;
import com.mahara.stocker.model.SysPage;
import com.mahara.stocker.service.SysService;
import com.mahara.stocker.util.FXMLLoaderUtil;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SysFunListController {
    private static final Logger log = LoggerFactory.getLogger(SysFunListController.class);
    private static final List<ActionType> ACTION_TYPE =
            Arrays.asList(
//                    new ActionType("c", "Create"),
//                    new ActionType("r", "Retrieve"),
//                    new ActionType("u", "Update"),
//                    new ActionType("d", "Delete"),
//                    new ActionType("e", "Else")
                    new ActionType("c", "新建"),
                    new ActionType("r", "查询"),
                    new ActionType("u", "更新"),
                    new ActionType("d", "删除"),
                    new ActionType("e", "其他")
            );

    @FXML
    private AnchorPane sysFunListPane;
    @FXML
    private TableView<SysFun> searchResultTable;
    @FXML
    private TableColumn<SysFun, String> funNoColumn;
    @FXML
    private TableColumn<SysFun, String> pageColumn;
    @FXML
    private TableColumn<SysFun, String> actionTypeColumn;
    @FXML
    private TableColumn<SysFun, String> actionNameColumn;
    @FXML
    private TableColumn<SysFun, String> memoColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private ComboBox<SysPage> pageCombo;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<SysFun> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    @Autowired
    @Qualifier("jtSysFunRepository")
    private SysFunRepository sysFunRepository;

    @Autowired
    @Qualifier("jtSysPageRepository")
    private SysPageRepository sysPageRepository;

    @Autowired
    private SysService sysService;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    private Map<String, String> actionTypeMap = new HashMap<>();
    private Map<String, String> pageNameMap = new HashMap<>();
    @FXML
    private void initialize() {
        // 整理actionType列表，放到Map中，方便取值
        ACTION_TYPE.forEach(at -> actionTypeMap.put(at.getActionType(), at.getActionTypeName()));

        // 获取SysPage列表，初始化pageCombo，并把pageName和pageTitle存储到map，方便取值
        var allSysPages = sysPageRepository.findAll();
        allSysPages.forEach(p -> pageNameMap.put(p.getPageName(), p.getPageTitle()));
        pageCombo.setItems(FXCollections.observableArrayList(allSysPages));
        // 在combo box中显示pageTitle
        pageCombo.setConverter(new StringConverter<SysPage>() {
            @Override
            public String toString(SysPage object) {
                return object == null?"所有页面": object.getPageTitle();
            }
            @Override
            public SysPage fromString(String value) {
                return null;
            }
        });
        //响应ComboBox的change事件
        pageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            pageNo = 1;
            search();
        });

        funNoColumn.setCellValueFactory(cellData -> cellData.getValue().funNoProperty());
        pageColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().pageNameProperty().getValue();
            return new SimpleStringProperty(pageNameMap.get(value));
        });
        actionTypeColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().actionTypeProperty().getValue();
            return new SimpleStringProperty(actionTypeMap.get(value));
        });
        actionNameColumn.setCellValueFactory(cellData -> cellData.getValue().actionNameProperty());
        memoColumn.setCellValueFactory(cellData -> cellData.getValue().memoProperty());
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
        var tmp = new SysFun();
        boolean saveClicked = this.showEditDialog(tmp);
        if (saveClicked) {
//            observableData.add(tmp);
            search();
        }
    }

    @FXML
    private void handleEdit() {
        var selectionModel = searchResultTable.getSelectionModel();
        var selectedIndex = selectionModel.getSelectedIndex();
        if (selectedIndex >= 0) {
            var selected = selectionModel.getSelectedItem();
            boolean saveClicked = this.showEditDialog(selected);
            if (saveClicked) {
                search();
            }
        } else {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("请选择1行编辑。");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        var selectionModel = searchResultTable.getSelectionModel();
        var selectedIndex = selectionModel.getSelectedIndex();
        if (selectedIndex >= 0) {
            var selected = selectionModel.getSelectedItem();
            sysService.deleteSysFun(selected.getId());
            searchResultTable.getItems().remove(selectedIndex);
        } else {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("请选择您要删除的行。");

            alert.showAndWait();
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
        var selectedPage = pageCombo.getValue();
        var pageName = selectedPage == null? "" : selectedPage.getPageName();
        var searchResult = sysFunRepository.findByPageName(pageName, new PaginationIn(pageNo, pageSize));
        observableData.setAll(searchResult.getData());
        searchResultTable.setItems(observableData);

        totalPage = searchResult.getTotalPage();
        totalPageLabel.setText(String.valueOf(totalPage));
        pageNoTextField.setText(String.valueOf(pageNo));
    }

    private boolean showEditDialog(SysFun sysFun) {
        try {
            var primaryStage = (Stage) sysFunListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/sys/SysFunEditDialog.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("系统功能");
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
            SysFunEditDialogController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(sysFun, pageCombo.getItems(), ACTION_TYPE);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "SysFunEditDialog", e);
            return false;
        }
    }
}
