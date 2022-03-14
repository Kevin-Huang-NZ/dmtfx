package com.mahara.stocker.controller.standard;

import com.mahara.stocker.controller.BaseController;
import com.mahara.stocker.controller.standard.StandardEditController;
import com.mahara.stocker.dao.StandardRepository;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.service.StandardService;
import com.mahara.stocker.util.*;
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
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Scope("prototype")
public class StandardListController extends BaseController<Standard> {
    private static final Logger log = LoggerFactory.getLogger(StandardListController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//    private static final List<Pair<String, String>> DIC_STATUS =
//            Arrays.asList(
//                    new Pair("0", "冻结"),
//                    new Pair("1", "正常")
//            );

    @FXML
    private AnchorPane standardListPane;
    @FXML
    private TableView<Standard> searchResultTable;
    @FXML
    private TableColumn<Standard, String> standardNameColumn;
    @FXML
    private TableColumn<Standard, String> versionCodeColumn;
    @FXML
    private TableColumn<Standard, String> publishYmdColumn;
    @FXML
    private TableColumn<Standard, String> languageNameColumn;
    @FXML
    private TableColumn<Standard, String> countryRegionColumn;
    @FXML
    private TableColumn<Standard, String> memoColumn;
    @FXML
    private TableColumn<Standard, Void> operationColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private TextField keyWordTextField;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<Standard> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    @Autowired
    private StandardRepository repository;

    @Autowired
    private StandardService standardService;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

//    private Map<String, String> statusMap = new HashMap<>();
//    private List<SysRole> allRoles;
//    private Map<String, String> roleMap = new HashMap<>();

    @FXML
    private void initialize() {
        intiData();
        initTable();
        search();
    }

    private void intiData() {
    }

    private void initTable() {
        standardNameColumn.setCellValueFactory(cellData -> cellData.getValue().standardNameProperty());
        versionCodeColumn.setCellValueFactory(cellData -> cellData.getValue().versionCodeProperty());
        publishYmdColumn.setCellValueFactory(cellData -> cellData.getValue().publishYmdProperty());
        languageNameColumn.setCellValueFactory(cellData -> cellData.getValue().languageNameProperty());
        countryRegionColumn.setCellValueFactory(cellData -> cellData.getValue().countryRegionProperty());
        memoColumn.setCellValueFactory(cellData -> cellData.getValue().memoProperty());
        addButtonToTable(operationColumn);
    }

    @FXML
    private void handleSearch() {
        pageNo = 1;
        search();
    }

    @FXML
    private void handleNew() {
        var tmp = new Standard();
        boolean saveClicked = this.showEditDialog(tmp);
        if (saveClicked) {
            //observableData.add(tmp);
            search();
        }
    }

    @Override
    protected void handleEdit(int index) {
        if (index >= 0 && index < searchResultTable.getItems().size()) {
            var selected = searchResultTable.getItems().get(index);
            var checkExist = repository.findById(selected.getId());
            if (checkExist == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("数据不存在");
                alert.setHeaderText(null);
                alert.setContentText("您要编辑的数据不存在，或者已经被删除。");
                alert.showAndWait();
                search();
            } else {
                boolean saveClicked = this.showEditDialog(checkExist);
                if (saveClicked) {
                    search();
                }
            }
        }
    }

    @Override
    protected void handleDelete(int index) {
        if (index >= 0 && index < searchResultTable.getItems().size()) {
            ButtonType confirmBtn = new ButtonType("确定", ButtonBar.ButtonData.YES);
            ButtonType cancelBtn = new ButtonType("取消", ButtonBar.ButtonData.NO);
            var confirmDialog = new Alert(Alert.AlertType.CONFIRMATION, "删除译写标准，同时会删除相关的音译表、常用词、罗马字母对照表。确定删除吗？", confirmBtn, cancelBtn);
            confirmDialog.setTitle(null);
            confirmDialog.setHeaderText(null);
            confirmDialog.showAndWait();
            if (confirmDialog.getResult() == confirmBtn) {
                var selected = searchResultTable.getItems().get(index);
                standardService.deleteStandard(selected.getId());
                searchResultTable.getItems().remove(index);
            }
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
        var searchResult = repository.findByKeyWord(keyWord, new PaginationIn(pageNo, pageSize));
        observableData.setAll(searchResult.getData());
        searchResultTable.setItems(observableData);

        totalPage = searchResult.getTotalPage();
        totalPageLabel.setText(String.valueOf(totalPage));
        pageNoTextField.setText(String.valueOf(pageNo));
    }

    private boolean showEditDialog(Standard bean) {
        try {
            var primaryStage = (Stage) standardListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/standard/StandardEdit.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("译写标准");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - editPage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - editPage.getPrefHeight()) / 2);

            var scene = new Scene(editPage);
            dialogStage.setScene(scene);

            StandardEditController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(bean);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "StandardEdit", e);
            return false;
        }
    }
}
