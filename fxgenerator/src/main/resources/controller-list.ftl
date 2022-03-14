package [=controllerPkg];

import [=controllerPkg].BaseController;
import [=controllerPkg].[=editControllerName];
import [=repositoryPkg].[=repositoryName];
import [=modelPkg].[=modelName];
import [=basePackage].util.*;
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
public class [=listControllerName] extends BaseController<[=modelName]> {
    private static final Logger log = LoggerFactory.getLogger([=listControllerName].class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat formatterDecimal = new DecimalFormat("#.##");

//    private static final List<Pair<String, String>> DIC_STATUS =
//            Arrays.asList(
//                    new Pair("0", "冻结"),
//                    new Pair("1", "正常")
//            );

    @FXML
    private AnchorPane [=lcTableName]ListPane;
    @FXML
    private TableView<[=modelName]> searchResultTable;
    <#list fields as field>
    @FXML
        <#switch field.javaDataType>
            <#case 'String'>
    private TableColumn<[=modelName], String> [=field.lcColumnName]Column;
                <#break>
            <#case 'Long'>
    private TableColumn<[=modelName], Number> [=field.lcColumnName]Column;
                <#break>
            <#case 'Integer'>
    private TableColumn<[=modelName], Number> [=field.lcColumnName]Column;
                <#break>
            <#case 'BigDecimal'>
    private TableColumn<[=modelName], String> [=field.lcColumnName]Column;
                <#break>
            <#case 'Timestamp'>
    private TableColumn<[=modelName], String> [=field.lcColumnName]Column;
                <#break>
            <#default>
    private TableColumn<[=modelName], String> [=field.lcColumnName]Column;
        </#switch>
    </#list>
    @FXML
    private TableColumn<[=modelName], Void> operationColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private TextField keyWordTextField;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<[=modelName]> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    @Autowired
    private [=repositoryName] repository;

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
//        DIC_STATUS.forEach(p -> statusMap.put(p.getKey(), p.getValue()));

        // 获取所有SysRole，把roleNo和roleName存储到map，方便取值
//        allRoles = sysRoleRepository.findAll();
//        allRoles.forEach(p -> roleMap.put(p.getRoleNo(), p.getRoleName()));
//        // roleCombo
//        roleCombo.setItems(FXCollections.observableArrayList(allRoles));
//        roleCombo.setConverter(new StringConverter<SysRole>() {
//            @Override
//            public String toString(SysRole object) {
//                return object == null?"All roles": object.getRoleName();
//            }
//            @Override
//            public SysRole fromString(String value) {
//                return null;
//            }
//        });
//        roleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
//        });
    }

    private void initTable() {
        <#list fields as field>
            <#switch field.javaDataType>
                <#case 'String'>
        [=field.lcColumnName]Column.setCellValueFactory(cellData -> cellData.getValue().[=field.lcColumnName]Property());
                    <#break>
                <#case 'Long'>
        [=field.lcColumnName]Column.setCellValueFactory(cellData -> cellData.getValue().[=field.lcColumnName]Property());
                    <#break>
                <#case 'Integer'>
        [=field.lcColumnName]Column.setCellValueFactory(cellData -> cellData.getValue().[=field.lcColumnName]Property());
                    <#break>
                <#case 'BigDecimal'>
        [=field.lcColumnName]Column.setCellValueFactory(cellData -> {
            var value = cellData.getValue()[=field.lcColumnName]Property().getValue();
            var text = value == null ? "" : formatterDecimal.format(value);
            return new SimpleStringProperty(text);
        });
                    <#break>
                <#case 'Timestamp'>
        [=field.lcColumnName]Column.setCellValueFactory(cellData -> {
            var value = cellData.getValue()[=field.lcColumnName]Property().getValue();
            var text = value == null ? "" : formatterYMDHMS.format(value.toLocalDateTime());
            return new SimpleStringProperty(text);
        });
                    <#break>
                <#default>
        [=field.lcColumnName]Column.setCellValueFactory(cellData -> {
            var value = cellData.getValue()[=field.lcColumnName]Property().getValue();
            var text = value == null ? "" : value.toString();
            return new SimpleStringProperty(text);
        });
            </#switch>
        </#list>
        addButtonToTable(operationColumn);

//        statusColumn.setCellValueFactory(cellData -> {
//            var value = cellData.getValue().statusProperty().getValue();
//            return new SimpleStringProperty(statusMap.get(value));
//        });
//        rolesColumn.setCellValueFactory(cellData -> {
//            var value = cellData.getValue().rolesProperty().getValue();
//            var roleNames = Arrays.stream(value.split(";")).map(p -> roleMap.get(p)).collect(Collectors.joining(" "));
//            return new SimpleStringProperty(roleNames);
//        });
    }

    @FXML
    private void handleSearch() {
        pageNo = 1;
        search();
    }

    @FXML
    private void handleNew() {
        var tmp = new [=modelName]();
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
//            var confirmDialog = new Alert(Alert.AlertType.CONFIRMATION, "删除译写标准，同时会删除相关的音译表、常用词、罗马字母对照表。确定删除吗？", ButtonType.YES, ButtonType.NO);
//            confirmDialog.setTitle(null);
//            confirmDialog.setHeaderText(null);
//            confirmDialog.showAndWait();
//            if (confirmDialog.getResult() == ButtonType.YES) {
//            }
            var selected = searchResultTable.getItems().get(index);
            repository.deleteById(selected.getId());
            searchResultTable.getItems().remove(index);
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

    private boolean showEditDialog([=modelName] bean) {
        try {
            var primaryStage = (Stage) [=lcTableName]ListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/[=viewFolder]/[=editViewName].fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("[=modelName]");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - editPage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - editPage.getPrefHeight()) / 2);

            var scene = new Scene(editPage);
            dialogStage.setScene(scene);

            [=editControllerName] controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(bean);
//            controller.setData(bean, DIC_STATUS, allRoles);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "[=editViewName]", e);
            return false;
        }
    }
}
