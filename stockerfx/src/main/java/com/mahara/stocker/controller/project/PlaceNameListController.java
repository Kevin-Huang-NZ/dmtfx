package com.mahara.stocker.controller.project;

import com.mahara.stocker.controller.BaseController;
import com.mahara.stocker.controller.project.PlaceNameEditController;
import com.mahara.stocker.dao.PlaceNameRepository;
import com.mahara.stocker.dao.ProjectRepository;
import com.mahara.stocker.dao.StandardRepository;
import com.mahara.stocker.model.PlaceName;
import com.mahara.stocker.model.Project;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.model.Transliteration;
import com.mahara.stocker.service.ProjectService;
import com.mahara.stocker.util.*;
import com.mahara.stocker.util.excel.ExcelUtil;
import com.mahara.stocker.util.excel.SheetColumn;
import com.mahara.stocker.util.task.TaskHelper;
import com.mahara.stocker.util.task.TaskResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mahara.stocker.util.CheckUtil.isEmpty;
import static com.mahara.stocker.util.CheckUtil.overLength;

@Controller
@Scope("prototype")
public class PlaceNameListController extends BaseController<PlaceName> {
    private static final Logger log = LoggerFactory.getLogger(PlaceNameListController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat formatterDecimal = new DecimalFormat("#.##");

    @FXML
    private AnchorPane placeNameListPane;
    @FXML
    private TableView<PlaceName> searchResultTable;
    @FXML
    private TableColumn<PlaceName, String> projectIdColumn;
    @FXML
    private TableColumn<PlaceName, String> originalColumn;
    @FXML
    private TableColumn<PlaceName, String> countryColumn;
    @FXML
    private TableColumn<PlaceName, String> languageColumn;
    @FXML
    private TableColumn<PlaceName, String> gecColumn;
    @FXML
    private TableColumn<PlaceName, String> romanColumn;
    @FXML
    private TableColumn<PlaceName, String> transliterationColumn;
    @FXML
    private TableColumn<PlaceName, String> freeTranslationColumn;
    @FXML
    private TableColumn<PlaceName, String> transResultColumn;
    @FXML
    private TableColumn<PlaceName, Void> operationColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private ComboBox<Project> projectCombo;
    @FXML
    private TextField keyWordTextField;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<PlaceName> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    @Autowired
    private PlaceNameRepository repository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    private List<Project> allProject;
    private Map<Long, String> projectMap = new HashMap<>();

    @FXML
    private void initialize() {
        intiData();
        initTable();
        search();
    }

    private void intiData() {
        // 获取所有项目，把id和项目名存储到map，方便取值
        allProject = projectRepository.findAll();
        allProject.forEach(p -> projectMap.put(p.getId(), p.getProjectName()));
        // projectCombo
        projectCombo.setItems(FXCollections.observableArrayList(allProject));
        projectCombo.setConverter(new StringConverter<Project>() {
            @Override
            public String toString(Project object) {
                return object == null?"所有项目": object.getProjectName();
            }
            @Override
            public Project fromString(String value) {
                return null;
            }
        });
    }

    private void initTable() {
        projectIdColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().projectIdProperty().getValue();
            return new SimpleStringProperty(projectMap.get(value));
        });
        originalColumn.setCellValueFactory(cellData -> cellData.getValue().originalProperty());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        languageColumn.setCellValueFactory(cellData -> cellData.getValue().languageProperty());
        gecColumn.setCellValueFactory(cellData -> cellData.getValue().gecProperty());
        romanColumn.setCellValueFactory(cellData -> cellData.getValue().romanProperty());
        transliterationColumn.setCellValueFactory(cellData -> cellData.getValue().transliterationProperty());
        freeTranslationColumn.setCellValueFactory(cellData -> cellData.getValue().freeTranslationProperty());
        transResultColumn.setCellValueFactory(cellData -> cellData.getValue().transResultProperty());
        addButtonToTable(operationColumn);
    }

    @FXML
    private void handleSearch() {
        pageNo = 1;
        search();
    }

    @FXML
    private void handleNew() {
        var tmp = new PlaceName();
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
            var selected = searchResultTable.getItems().get(index);
            repository.deleteById(selected.getId());
            searchResultTable.getItems().remove(index);
        }
    }

    @FXML
    protected void handleImportOriginal() {
        if (projectCombo.getValue() == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("请选择项目。");
            alert.showAndWait();
            return;
        }

        // 选择导入方式
        var importType = 0;
        ButtonType clearImport = new ButtonType("清空后导入", ButtonBar.ButtonData.YES);
        ButtonType appendImport = new ButtonType("追加导入", ButtonBar.ButtonData.NO);
        ButtonType cancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        var confirmDialog = new Alert(Alert.AlertType.CONFIRMATION, "请选择导入方式。", clearImport, appendImport, cancel);
        confirmDialog.setTitle(null);
        confirmDialog.setHeaderText(null);
        confirmDialog.showAndWait();
        if (confirmDialog.getResult() == clearImport) {
            importType = 1;
        } else if (confirmDialog.getResult() == appendImport) {
            importType = 2;
        }
        if (importType == 0) {
            return;
        }

        var primaryStage = (Stage) placeNameListPane.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("请选择文件");
        fc.setInitialDirectory(new File(UserSettingUtil.getFilePath()));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls/*.xlsx)", Arrays.asList("*.xls", "*.xlsx"));
        fc.getExtensionFilters().add(extFilter);
        File importFile = fc.showOpenDialog(primaryStage);
        if (importFile != null) {
            UserSettingUtil.setFilePath(importFile.getParent());
            var fileName = importFile.getName();
            var fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

            var checkBiConsumer = new BiConsumer<Map<Integer, String>, Integer>() {
                @Override
                public void accept(Map<Integer, String> integerStringMap, Integer integer) {
                    checkImportOriginal(integerStringMap, integer);
                }
            };

            var readFunction = new Function<Map<Integer, String>, PlaceName>() {
                @Override
                public PlaceName apply(Map<Integer, String> rowDataMap) {
                    PlaceName bean = new PlaceName();
                    bean.setProjectId(projectCombo.getValue().getId());
                    bean.setOriginal(rowDataMap.get(0));
                    bean.setCountry(rowDataMap.get(1));
                    bean.setLanguage(rowDataMap.get(2));
                    bean.setGec(rowDataMap.get(3));
                    bean.setMemo(rowDataMap.get(4));
                    if (StringUtils.isEmpty(rowDataMap.get(5))) {
                        bean.setRomanStatus("0");
                    } else {
                        bean.setRomanStatus("1");
                    }
                    bean.setRoman(rowDataMap.get(5));
                    bean.setTransStatus("0");
                    bean.setTransliteration(null);
                    bean.setFreeTranslation(null);
                    bean.setEmitStandard(null);
                    bean.setTransResult(null);
                    return bean;
                }
            };

            var saveConsumer = new Consumer<List<PlaceName>>() {
                @Override
                public void accept(List<PlaceName> dataList) {
                    repository.batchSave(dataList);
                }
            };

            var finalImportType = importType;
            Task<TaskResult> task = new Task<TaskResult>() {
                @Override
                protected TaskResult call() throws Exception {
                    try {
                        try (InputStream inputStream = new FileInputStream(importFile)) {
                            var sheetReader = ExcelUtil.read(inputStream, fileType).sheet().headRowNumber(0)
                                    .doCheck(checkBiConsumer);

                            if (finalImportType == 1) {
                                repository.deleteByProject(projectCombo.getValue().getId());
                            }

                            sheetReader.doBatchRead(readFunction, saveConsumer,100);
                        }
                    } catch (ValidationException ex) {
                        return new TaskResult(ex, ex.getMessage());
                    } catch (IOException ex) {
                        log.error("导入原始地名条目excel文件时发生异常。", ex);
                        return new TaskResult(ex, "读取文件时发生异常，请联系系统管理员。");
                    }
                    return new TaskResult();
                }
            };

            var successConsumer = new Consumer<TaskResult>() {
                @Override
                public void accept(TaskResult taskResult) {
                    pageNo = 1;
                    search();
                }
            };

            TaskHelper.build(task, primaryStage, fxmlLoaderUtil).successConsumer(successConsumer).run();
        }
    }

    @FXML
    protected void handleImportResult() {
        if (projectCombo.getValue() == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("请选择项目。");
            alert.showAndWait();
            return;
        }

        var primaryStage = (Stage) placeNameListPane.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("请选择文件");
        fc.setInitialDirectory(new File(UserSettingUtil.getFilePath()));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls/*.xlsx)", Arrays.asList("*.xls", "*.xlsx"));
        fc.getExtensionFilters().add(extFilter);
        File importFile = fc.showOpenDialog(primaryStage);
        if (importFile != null) {
            UserSettingUtil.setFilePath(importFile.getParent());
            var fileName = importFile.getName();
            var fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

            var checkBiConsumer = new BiConsumer<Map<Integer, String>, Integer>() {
                @Override
                public void accept(Map<Integer, String> integerStringMap, Integer integer) {
                    checkImportResult(integerStringMap, integer);
                }
            };

            var readFunction = new Function<Map<Integer, String>, PlaceName>() {
                @Override
                public PlaceName apply(Map<Integer, String> rowDataMap) {
                    PlaceName bean = new PlaceName();
                    bean.setProjectId(projectCombo.getValue().getId());
                    bean.setOriginal(rowDataMap.get(0));
                    bean.setRoman(rowDataMap.get(5));
                    bean.setTransResult(rowDataMap.get(8));
                    return bean;
                }
            };

            var updateConsumer = new Consumer<List<PlaceName>>() {
                @Override
                public void accept(List<PlaceName> dataList) {
                    repository.batchUpdateResult(dataList);
                }
            };

            Task<TaskResult> task = new Task<TaskResult>() {
                @Override
                protected TaskResult call() throws Exception {
                    try {
                        try (InputStream inputStream = new FileInputStream(importFile)) {
                            ExcelUtil.read(inputStream, fileType).sheet().headRowNumber(0)
                                    .doCheck(checkBiConsumer)
                                    .doBatchRead(readFunction, updateConsumer,100);
                        }
                    } catch (ValidationException ex) {
                        return new TaskResult(ex, ex.getMessage());
                    } catch (IOException ex) {
                        log.error("导入终译结果excel文件时发生异常。", ex);
                        return new TaskResult(ex, "读取文件时发生异常，请联系系统管理员。");
                    }
                    return new TaskResult();
                }
            };

            var successConsumer = new Consumer<TaskResult>() {
                @Override
                public void accept(TaskResult taskResult) {
                    pageNo = 1;
                    search();
                }
            };

            TaskHelper.build(task, primaryStage, fxmlLoaderUtil).successConsumer(successConsumer).run();
        }
    }

    @FXML
    protected void handleExport() {
        if (projectCombo.getValue() == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("请选择项目。");
            alert.showAndWait();
            return;
        }

        var primaryStage = (Stage) placeNameListPane.getScene().getWindow();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("请选择保存路径");
        dc.setInitialDirectory(new File(UserSettingUtil.getFilePath()));
        File exportFolder = dc.showDialog(primaryStage);
        if (exportFolder != null) {
            UserSettingUtil.setFilePath(exportFolder.getPath());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            Path exportFilePath = Paths.get(exportFolder.getPath(), projectCombo.getValue().getProjectName()
                    + LocalDateTime.now().format(formatter) + ".xlsx");

            Task<TaskResult> task = new Task<TaskResult>() {
                @Override
                protected TaskResult call() throws Exception {
                    try {
                        var excelWriterBuilder = ExcelUtil.write();
                        var excelSheetWriter = excelWriterBuilder.sheet("音译表").head(headers());
                        var pageNo = 1;
                        var pageSize = 10000;
                        var searchResult = repository.findByProject(projectCombo.getValue().getId(), new PaginationIn(pageNo, pageSize));
                        do {
                            if (searchResult.getData() != null && !searchResult.getData().isEmpty()) {
                                excelSheetWriter.doWrite(searchResult.getData().stream().map(p -> {
                                    var tmp = new ArrayList<String>(6);
                                    tmp.add(p.getOriginal());
                                    tmp.add(p.getCountry());
                                    tmp.add(p.getLanguage());
                                    tmp.add(p.getGec());
                                    tmp.add(p.getMemo());
                                    tmp.add(p.getRoman());
                                    tmp.add(p.getTransliteration());
                                    tmp.add(p.getFreeTranslation());
                                    tmp.add(p.getTransResult());
                                    tmp.add(p.getEmitStandard());
                                    return tmp;
                                }).collect(Collectors.toList()));
                            }
                            pageNo++;
                            searchResult = repository.findByProject(projectCombo.getValue().getId(), new PaginationIn(pageNo, pageSize));
                        } while (searchResult.getData() != null && !searchResult.getData().isEmpty());

                        try (OutputStream outputStream = Files.newOutputStream(exportFilePath)) {
                            excelWriterBuilder.finish(outputStream);
                        }

                    } catch (IOException ex) {
                        log.error("导出地名条目时发生异常。", ex);
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("文件写入异常");
                        alert.setHeaderText(null);
                        alert.setContentText("写Excel文件时出现异常，请联系系统管理员。");
                        alert.showAndWait();
                    }
                    return new TaskResult();
                }
            };
            TaskHelper.build(task, primaryStage, fxmlLoaderUtil).run();
        }
    }

    @FXML
    public void handleTranslate() {
        if (projectCombo.getValue() == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("请选择项目。");
            alert.showAndWait();
            return;
        }
        var primaryStage = (Stage) placeNameListPane.getScene().getWindow();
        Task<TaskResult> task = new Task<TaskResult>() {
            @Override
            protected TaskResult call() throws Exception {
                projectService.translate(projectCombo.getValue());
                return new TaskResult();
            }
        };

        var successConsumer = new Consumer<TaskResult>() {
            @Override
            public void accept(TaskResult taskResult) {
                pageNo = 1;
                search();
            }
        };
        TaskHelper.build(task, primaryStage, fxmlLoaderUtil).successConsumer(successConsumer).run();
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

    private boolean showEditDialog(PlaceName bean) {
        try {
            var primaryStage = (Stage) placeNameListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/project/PlaceNameEdit.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("PlaceName");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - editPage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - editPage.getPrefHeight()) / 2);

            var scene = new Scene(editPage);
            dialogStage.setScene(scene);

            PlaceNameEditController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(bean, allProject);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "PlaceNameEdit", e);
            return false;
        }
    }

    private void checkImportOriginal(Map<Integer, String> data, int rowIndex) throws ValidationException {
        int rowNo = rowIndex + 1;
        var original = data.get(0);
        if (isEmpty(original)) {
            throw new ValidationException(MessageUtil.format("原文不能为空。行号: {}", rowNo));
        } else if (overLength(original, 200)) {
            throw new ValidationException(MessageUtil.format("原文超长，最多200字符。行号: {}", rowNo));
        }
        var country = data.get(1);
        if (overLength(country, 100)) {
            throw new ValidationException(MessageUtil.format("国家/地区超长，最多100字符。行号: {}", rowNo));
        }
        var language = data.get(2);
        if (overLength(country, 100)) {
            throw new ValidationException(MessageUtil.format("语种超长，最多100字符。行号: {}", rowNo));
        }
        var gec = data.get(3);
        if (overLength(gec, 100)) {
            throw new ValidationException(MessageUtil.format("地理实体类别超长，最多100字符。行号: {}", rowNo));
        }
        var memo = data.get(4);
        if (overLength(memo, 500)) {
            throw new ValidationException(MessageUtil.format("备注超长，最多500字符。行号: {}", rowNo));
        }
        var roman = data.get(5);
        if (overLength(roman, 200)) {
            throw new ValidationException(MessageUtil.format("罗马字母转写超长，最多200字符。行号: {}", rowNo));
        }
    }

    private void checkImportResult(Map<Integer, String> data, int rowIndex) throws ValidationException {
        int rowNo = rowIndex + 1;
        var original = data.get(0);
        if (isEmpty(original)) {
            throw new ValidationException(MessageUtil.format("原文不能为空。行号: {}", rowNo));
        } else if (overLength(original, 200)) {
            throw new ValidationException(MessageUtil.format("原文超长，最多200字符。行号: {}", rowNo));
        }
        var roman = data.get(5);
        if (overLength(roman, 200)) {
            throw new ValidationException(MessageUtil.format("罗马字母转写超长，最多200字符。行号: {}", rowNo));
        }
        var transResult = data.get(8);
        if (isEmpty(transResult)) {
            throw new ValidationException(MessageUtil.format("终译不能为空。行号: {}", rowNo));
        } else if (overLength(original, 200)) {
            throw new ValidationException(MessageUtil.format("终译超长，最多200字符。行号: {}", rowNo));
        }
    }

    private List<SheetColumn> headers() {
        List<SheetColumn> list = new ArrayList<>(10);
        list.add(new SheetColumn("原文", 30));
        list.add(new SheetColumn("国家/地区", 30));
        list.add(new SheetColumn("语种", 30));
        list.add(new SheetColumn("地理实体类别", 30));
        list.add(new SheetColumn("备注", 30, "原文，国家/地区，语种，地理实体类别，备注这5列数据为原始数据，导入原文时，将这些列导入系统。"));
        list.add(new SheetColumn("罗马字母转写", 30, "原文如果包含罗马字母转写，则导入。终译结果如果包含，则覆盖导入。"));
        list.add(new SheetColumn("音译", 30));
        list.add(new SheetColumn("意译", 30));
        list.add(new SheetColumn("终译", 30));
        list.add(new SheetColumn("触发的译写规则", 60, "这里保存的是，系统自动翻译时，使用的译写规则。格式：类型 - 原文 - 匹配规则 - 匹配参数 => 音译 / 意译.可能包含多个规则。"));
        return list;
    }
}
