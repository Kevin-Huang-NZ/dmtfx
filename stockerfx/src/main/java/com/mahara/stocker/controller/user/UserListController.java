package com.mahara.stocker.controller.user;

import com.mahara.stocker.controller.BaseController;
import com.mahara.stocker.controller.user.UserEditDialogController;
import com.mahara.stocker.dao.SysRoleRepository;
import com.mahara.stocker.dao.UserRepository;
import com.mahara.stocker.model.SysPage;
import com.mahara.stocker.model.SysRole;
import com.mahara.stocker.model.User;
import com.mahara.stocker.util.*;
import com.mahara.stocker.util.excel.ExcelUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Scope("prototype")
public class UserListController extends BaseController<User> {
    private static final Logger log = LoggerFactory.getLogger(UserListController.class);
    private static final List<Pair<String, String>> DIC_GENDER =
            Arrays.asList(
                new Pair("0", "未声明"),
                new Pair("1", "男"),
                new Pair("2", "女")
            );
    private static final List<Pair<String, String>> DIC_STATUS =
            Arrays.asList(
                    new Pair("0", "冻结"),
                    new Pair("1", "正常")
            );

    @FXML
    private AnchorPane userListPane;
    @FXML
    private TableView<User> searchResultTable;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> genderColumn;
    @FXML
    private TableColumn<User, String> statusColumn;
    @FXML
    private TableColumn<User, String> rolesColumn;
    @FXML
    private TableColumn<User, Void> operationColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private TextField keyWordTextField;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<User> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    @Autowired
    @Qualifier("jtUserRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("jtSysRoleRepository")
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    private List<SysRole> allRoles;
    private Map<String, String> genderMap = new HashMap<>();
    private Map<String, String> statusMap = new HashMap<>();
    private Map<String, String> roleMap = new HashMap<>();

    @FXML
    private void initialize() {
        intiData();
        initTable();
        search();
    }

    private void intiData() {
        DIC_GENDER.forEach(p -> genderMap.put(p.getKey(), p.getValue()));
        DIC_STATUS.forEach(p -> statusMap.put(p.getKey(), p.getValue()));

        // 获取所有SysRole，把roleNo和roleName存储到map，方便取值
        allRoles = sysRoleRepository.findAll();
        allRoles.forEach(p -> roleMap.put(p.getRoleNo(), p.getRoleName()));
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
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        genderColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().genderProperty().getValue();
            return new SimpleStringProperty(genderMap.get(value));
        });
        statusColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().statusProperty().getValue();
            return new SimpleStringProperty(statusMap.get(value));
        });
        rolesColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().rolesProperty().getValue();
            var roleNames = Arrays.stream(value.split(";")).map(p -> roleMap.get(p)).collect(Collectors.joining(" "));
            return new SimpleStringProperty(roleNames);
        });
        addButtonToTable(operationColumn);
    }

    @FXML
    private void handleSearch() {
        pageNo = 1;
        search();
    }

    @FXML
    private void handleNew() {
        var tmp = new User();
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
            var checkExist = userRepository.findById(selected.getId());
            if (checkExist == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("数据不存在");
                alert.setHeaderText(null);
                alert.setContentText("您选择的用户不存在，或者已经被删除。");
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
            var selected = searchResultTable.getItems().get(index);
            userRepository.deleteById(selected.getId());
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
        var searchResult = userRepository.findByNameOrPhone(keyWord, new PaginationIn(pageNo, pageSize));
        observableData.setAll(searchResult.getData());
        searchResultTable.setItems(observableData);

        totalPage = searchResult.getTotalPage();
        totalPageLabel.setText(String.valueOf(totalPage));
        pageNoTextField.setText(String.valueOf(pageNo));
    }

    private boolean showEditDialog(User user) {
        try {
            var primaryStage = (Stage) userListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/user/UserEditDialog.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("用户");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - editPage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - editPage.getPrefHeight()) / 2);

            var scene = new Scene(editPage);
            dialogStage.setScene(scene);

            UserEditDialogController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            if (controller.setData(user, DIC_GENDER, allRoles, DIC_STATUS)) {
                dialogStage.showAndWait();
                return controller.isSaveClicked();
            } else {
                return false;
            }
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "UserEditDialog", e);
            return false;
        }
    }
}
