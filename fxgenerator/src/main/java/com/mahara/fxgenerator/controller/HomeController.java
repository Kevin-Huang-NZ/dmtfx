package com.mahara.fxgenerator.controller;

import com.mahara.fxgenerator.dao.TableDao;
import com.mahara.fxgenerator.model.Table;
import static com.mahara.fxgenerator.util.CheckUtil.*;

import com.mahara.fxgenerator.service.Generator;
import com.mahara.fxgenerator.service.MetaData;
import com.mahara.fxgenerator.util.JDBCTemplateFactory;
import com.mahara.fxgenerator.util.MakeDir;
import com.mahara.fxgenerator.util.UserSettingUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private static final String KEY_JDBC_URL = "jdbcUrl";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SCHEMA = "schema";
    private static final String KEY_BASE_PACKAGE = "basePackage";

    public static final String PACKAGE_CONTROLLER = ".controller";
    public static final String PACKAGE_REPOSITORY = ".dao";
    public static final String PACKAGE_REPOSITORY_IMPL = ".dao.jt";
    public static final String PACKAGE_MODEL = ".model";

    public static final String FOLDER_NAME_VIEW = "view";

    @FXML
    private AnchorPane homePane;

    @FXML
    private TextField jdbcUrlField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField schemaField;
    @FXML
    private TextField basePackageField;

    @FXML
    private TableView<Table> tableTableView;
    @FXML
    private TableColumn<Table, String> tableNameColumn;
    @FXML
    private TableColumn<Table, String> tableCommentColumn;

    private ObservableList<Table> observableData = FXCollections.observableArrayList();

    private TableDao tableDao = new TableDao();
    private String currentSchema;

    @FXML
    private void initialize() {
        initData();
        initTable();
    }

    private void initData() {
        jdbcUrlField.setText(UserSettingUtil.getValue(KEY_JDBC_URL));
        userNameField.setText(UserSettingUtil.getValue(KEY_USER_NAME));
        passwordField.setText(UserSettingUtil.getValue(KEY_PASSWORD));
        schemaField.setText(UserSettingUtil.getValue(KEY_SCHEMA));
        basePackageField.setText(UserSettingUtil.getValue(KEY_BASE_PACKAGE));
    }

    private void initTable() {
        tableNameColumn.setCellValueFactory(cellData -> cellData.getValue().tableNameProperty());
        tableCommentColumn.setCellValueFactory(cellData -> cellData.getValue().tableCommentProperty());

        tableTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void search() {
        if (isInputValid()) {
            try {
                var jdbcUrl = jdbcUrlField.getText();
                var userName = userNameField.getText();
                var password = passwordField.getText();
                // load tables时，初始化JDBCTemplate。生成时使用该对象，而不是使用界面上的输入内容重新初始化
                tableDao.setJdbcTemplate(JDBCTemplateFactory.build(jdbcUrl, userName, password).jt());
                currentSchema = schemaField.getText();
                var tables = tableDao.selectTable(currentSchema);
                observableData.setAll(tables);
                tableTableView.setItems(observableData);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleSearch() {
        search();
    }

    @FXML
    private void handleGenerate() {
        var selectedTables = tableTableView.getSelectionModel().getSelectedItems();
        if (selectedTables == null || selectedTables.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one table.");
            alert.showAndWait();
            return;
        }

        var outFolder = chooseFolder();
        if (!StringUtils.isEmpty(outFolder)) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//                Path outPath = Paths.get(outFolder, LocalDateTime.now().format(formatter));
                Timestamp tm = Timestamp.from(Instant.now());
                formatter.format(tm.toLocalDateTime());
                Files.createDirectories(Paths.get(outFolder));

                var basePackage = basePackageField.getText();
                var metaData = createFolders(basePackage, outFolder);

                Generator gen = new Generator();
                selectedTables.forEach(t -> {
                    gen.loadTableInfo(currentSchema, t.getTableName());
                    gen.generateAll(metaData);
                });

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Generate Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleGenerateAll() {
        if (observableData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("No tables.");
            alert.showAndWait();
            return;
        }
        var outFolder = chooseFolder();
        if (!StringUtils.isEmpty(outFolder)) {
            try {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//                Path outPath = Paths.get(outFolder, LocalDateTime.now().format(formatter));
                Files.createDirectories(Paths.get(outFolder));

                var basePackage = basePackageField.getText();
                var metaData = createFolders(basePackage, outFolder);

                Generator gen = new Generator();
                observableData.forEach(t -> {
                    gen.loadTableInfo(currentSchema, t.getTableName());
                    gen.generateAll(metaData);
                });

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Generate Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleSave() {
        var jdbcUrl = jdbcUrlField.getText();
        var userName = userNameField.getText();
        var password = passwordField.getText();
        var schema = schemaField.getText();
        var basePackage = basePackageField.getText();
        UserSettingUtil.setValue(KEY_JDBC_URL, jdbcUrl);
        UserSettingUtil.setValue(KEY_USER_NAME, userName);
        UserSettingUtil.setValue(KEY_PASSWORD, password);
        UserSettingUtil.setValue(KEY_SCHEMA, schema);
        UserSettingUtil.setValue(KEY_BASE_PACKAGE, basePackage);
    }

    private String chooseFolder() {
        var primaryStage = (Stage) homePane.getScene().getWindow();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Save to");
        dc.setInitialDirectory(new File(UserSettingUtil.getFilePath()));
        File outFolder = dc.showDialog(primaryStage);
        if (outFolder != null) {
            UserSettingUtil.setFilePath(outFolder.getPath());
            return outFolder.getPath();
        } else {
            return null;
        }
    }

    private MetaData createFolders(String basePackage, String outFolder) throws IOException {

        var modelPath = MakeDir.makeByPackage(outFolder, basePackage + PACKAGE_MODEL);
        var repositoryPath = MakeDir.makeByPackage(outFolder, basePackage + PACKAGE_REPOSITORY);
        var repositoryImplPath = MakeDir.makeByPackage(outFolder, basePackage + PACKAGE_REPOSITORY_IMPL);
        var controllerPath = MakeDir.makeByPackage(outFolder, basePackage + PACKAGE_CONTROLLER);
        var viewPath = MakeDir.makeByPackage(outFolder, FOLDER_NAME_VIEW);

        MetaData metaData = new MetaData();
        metaData.setBaseFolder(outFolder);
        metaData.setBasePkg(basePackage);
        metaData.setModelPkg(basePackage + PACKAGE_MODEL);
        metaData.setModelPath(modelPath.toString());
        metaData.setRepositoryPkg(basePackage + PACKAGE_REPOSITORY);
        metaData.setRepositoryPath(repositoryPath.toString());
        metaData.setRepositoryImplPkg(basePackage + PACKAGE_REPOSITORY_IMPL);
        metaData.setRepositoryImplPath(repositoryImplPath.toString());
        metaData.setControllerPkg(basePackage + PACKAGE_CONTROLLER);
        metaData.setControllerPath(controllerPath.toString());
        metaData.setViewFolder(FOLDER_NAME_VIEW);
        metaData.setViewPath(viewPath.toString());

        return metaData;
    }


    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();

        var jdbcUrl = jdbcUrlField.getText();
        if (isEmpty(jdbcUrl)) {
            sb.append("JDBC URL is required.\n");
        }

        var userName = userNameField.getText();
        if (isEmpty(userName)) {
            sb.append("User name is required.\n");
        }

        var password = passwordField.getText();
        if (isEmpty(password)) {
            sb.append("Password is required.\n");
        }

        var schema = schemaField.getText();
        if (isEmpty(schema)) {
            sb.append("Schema is required.\n");
        }

        var basePackage = basePackageField.getText();
        if (isEmpty(basePackage)) {
            sb.append("Base package is required.\n");
        }

        if (sb.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText(sb.toString());
            alert.showAndWait();
            return false;
        }
    }
}
