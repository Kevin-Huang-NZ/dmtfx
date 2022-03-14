package com.mahara.stocker.controller.project;

import com.mahara.stocker.controller.BaseController;
import com.mahara.stocker.controller.project.ProjectEditController;
import com.mahara.stocker.dao.ProjectRepository;
import com.mahara.stocker.dao.StandardRepository;
import com.mahara.stocker.model.Project;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.service.ProjectService;
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
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Scope("prototype")
public class ProjectListController extends BaseController<Project> {
    private static final Logger log = LoggerFactory.getLogger(ProjectListController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatterYMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DecimalFormat formatterDecimal = new DecimalFormat("#.##");

    private static final List<Pair<String, String>> DIC_STATUS =
            Arrays.asList(
                    new Pair("1", "执行中"),
                    new Pair("9", "已结束")
            );

    @FXML
    private AnchorPane projectListPane;
    @FXML
    private TableView<Project> searchResultTable;
    @FXML
    private TableColumn<Project, String> projectNameColumn;
    @FXML
    private TableColumn<Project, String> standardIdColumn;
    @FXML
    private TableColumn<Project, String> startDateColumn;
    @FXML
    private TableColumn<Project, String> dueDateColumn;
    @FXML
    private TableColumn<Project, String> statusColumn;
    @FXML
    private TableColumn<Project, String> memoColumn;
    @FXML
    private TableColumn<Project, Void> operationColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private TextField keyWordTextField;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<Project> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    @Autowired
    private ProjectRepository repository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    private Map<String, String> statusMap = new HashMap<>();
    private List<Standard> allStandard;
    private Map<Long, String> standardMap = new HashMap<>();

    @FXML
    private void initialize() {
        intiData();
        initTable();
        search();
    }

    private void intiData() {
        DIC_STATUS.forEach(p -> statusMap.put(p.getKey(), p.getValue()));

        // 获取所有译写标准，把id和名称存储到map，方便取值
        allStandard = standardRepository.findAll();
        allStandard.forEach(p -> standardMap.put(p.getId(), p.getStandardName()));
    }

    private void initTable() {
        projectNameColumn.setCellValueFactory(cellData -> cellData.getValue().projectNameProperty());
        standardIdColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().standardIdProperty().getValue();
            return new SimpleStringProperty(standardMap.get(value));
        });
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        dueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());
        statusColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().statusProperty().getValue();
            return new SimpleStringProperty(standardMap.get(value));
        });
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
        var tmp = new Project();
        tmp.setStartDate(LocalDate.now().format(formatterYMD));
        tmp.setStatus("1");
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
            var confirmDialog = new Alert(Alert.AlertType.CONFIRMATION, "删除项目，同时会删除项目下所有地名条目。确定删除吗？", confirmBtn, cancelBtn);
            confirmDialog.setTitle(null);
            confirmDialog.setHeaderText(null);
            confirmDialog.showAndWait();
            if (confirmDialog.getResult() == confirmBtn) {
                var selected = searchResultTable.getItems().get(index);
                projectService.deleteProject(selected.getId());
                searchResultTable.getItems().remove(index);
            }
        }
    }

//    @FXML
//    protected void handleImport() {
//    }
//
//    @FXML
//    protected void handleExport() {
//    }

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

    private boolean showEditDialog(Project bean) {
        try {
            var primaryStage = (Stage) projectListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/project/ProjectEdit.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("项目");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - editPage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - editPage.getPrefHeight()) / 2);

            var scene = new Scene(editPage);
            dialogStage.setScene(scene);

            ProjectEditController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(bean, DIC_STATUS, allStandard);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "ProjectEdit", e);
            return false;
        }
    }
}
