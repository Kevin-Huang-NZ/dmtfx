package com.mahara.stocker.controller.standard;

import com.mahara.stocker.controller.BaseController;
import com.mahara.stocker.controller.standard.CommonWordEditController;
import com.mahara.stocker.dao.CommonWordRepository;
import com.mahara.stocker.dao.StandardRepository;
import com.mahara.stocker.model.CommonWord;
import com.mahara.stocker.model.Roman;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.model.Transliteration;
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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mahara.stocker.util.CheckUtil.isEmpty;
import static com.mahara.stocker.util.CheckUtil.overLength;

@Controller
@Scope("prototype")
public class CommonWordListController extends BaseController<CommonWord> {
    private static final Logger log = LoggerFactory.getLogger(CommonWordListController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat formatterDecimal = new DecimalFormat("#.##");

    private static final List<Pair<String, String>> DIC_ORIGINAL_TYPE =
            Arrays.asList(
                    new Pair("1", "人名"),
                    new Pair("2", "通名"),
                    new Pair("3", "形容词"),
                    new Pair("x", "其它")
            );
    private static final List<Pair<String, String>> DIC_MATCH_WAY =
            Arrays.asList(
                    new Pair("1", "精确"),
                    new Pair("2", "前缀（词头）"),
                    new Pair("3", "后缀（词尾）"),
                    new Pair("4", "前置（在xxx之前）"),
                    new Pair("5", "后置（在xxx之后）")
            );

    @FXML
    private AnchorPane commonWordListPane;
    @FXML
    private TableView<CommonWord> searchResultTable;
    @FXML
    private TableColumn<CommonWord, String> standardIdColumn;
    @FXML
    private TableColumn<CommonWord, String> originalColumn;
    @FXML
    private TableColumn<CommonWord, String> originalAbbrColumn;
    @FXML
    private TableColumn<CommonWord, String> originalTypeColumn;
    @FXML
    private TableColumn<CommonWord, String> romanColumn;
    @FXML
    private TableColumn<CommonWord, String> matchWayColumn;
    @FXML
    private TableColumn<CommonWord, String> matchParamsColumn;
    @FXML
    private TableColumn<CommonWord, String> transliterationColumn;
    @FXML
    private TableColumn<CommonWord, String> freeTranslationColumn;
    @FXML
    private TableColumn<CommonWord, Void> operationColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private ComboBox<Standard> standardCombo;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<CommonWord> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    @Autowired
    private CommonWordRepository repository;
    @Autowired
    private StandardRepository standardRepository;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    private Map<String, String> originalTypeMap = new HashMap<>();
    private Map<String, String> matchWayMap = new HashMap<>();
    private List<Standard> allStandard;
    private Map<Long, String> standardMap = new HashMap<>();

    @FXML
    private void initialize() {
        intiData();
        initTable();
        search();
    }

    private void intiData() {
        DIC_ORIGINAL_TYPE.forEach(p -> originalTypeMap.put(p.getKey(), p.getValue()));
        DIC_MATCH_WAY.forEach(p -> matchWayMap.put(p.getKey(), p.getValue()));


        // 获取所有译写标准，把id和名称存储到map，方便取值
        allStandard = standardRepository.findAll();
        allStandard.forEach(p -> standardMap.put(p.getId(), p.getStandardName()));
        // roleCombo
        standardCombo.setItems(FXCollections.observableArrayList(allStandard));
        standardCombo.setConverter(new StringConverter<Standard>() {
            @Override
            public String toString(Standard object) {
                return object == null?"所有译写标准": object.getStandardName();
            }
            @Override
            public Standard fromString(String value) {
                return null;
            }
        });
        if (allStandard != null && allStandard.size() > 0) {
            // 默认选中第一个
            var first = allStandard.get(0);
            standardCombo.setValue(first);
        }
        standardCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            pageNo = 1;
            search();
        });
    }

    private void initTable() {
        standardIdColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().standardIdProperty().getValue();
            return new SimpleStringProperty(standardMap.get(value));
        });
        originalColumn.setCellValueFactory(cellData -> cellData.getValue().originalProperty());
        originalAbbrColumn.setCellValueFactory(cellData -> cellData.getValue().originalAbbrProperty());
        originalTypeColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().originalTypeProperty().getValue();
            return new SimpleStringProperty(originalTypeMap.get(value));
        });
        romanColumn.setCellValueFactory(cellData -> cellData.getValue().romanProperty());
        matchWayColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().matchWayProperty().getValue();
            return new SimpleStringProperty(matchWayMap.get(value));
        });
        matchParamsColumn.setCellValueFactory(cellData -> cellData.getValue().matchParamsProperty());
        transliterationColumn.setCellValueFactory(cellData -> cellData.getValue().transliterationProperty());
        freeTranslationColumn.setCellValueFactory(cellData -> cellData.getValue().freeTranslationProperty());
        addButtonToTable(operationColumn);
    }

    @FXML
    private void handleSearch() {
        pageNo = 1;
        search();
    }

    @FXML
    private void handleNew() {
        var tmp = new CommonWord();
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
    protected void handleImport() {
        if (standardCombo.getValue() == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("请选择译写标准。");
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

        var primaryStage = (Stage) commonWordListPane.getScene().getWindow();
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
                    checkImport(integerStringMap, integer);
                }
            };

            var readFunction = new Function<Map<Integer, String>, CommonWord>() {
                @Override
                public CommonWord apply(Map<Integer, String> rowDataMap) {
                    CommonWord bean = new CommonWord();
                    bean.setStandardId(standardCombo.getValue().getId());
                    bean.setOriginal(rowDataMap.get(0));
                    bean.setOriginalAbbr(rowDataMap.get(1));
                    bean.setOriginalType(rowDataMap.get(2));
                    bean.setRoman(rowDataMap.get(4));
                    bean.setMatchWay(rowDataMap.get(5));
                    bean.setMatchParams(rowDataMap.get(7));
                    bean.setTransliteration(rowDataMap.get(8));
                    bean.setFreeTranslation(rowDataMap.get(9));
                    return bean;
                }
            };

            var saveConsumer = new Consumer<List<CommonWord>>() {
                @Override
                public void accept(List<CommonWord> dataList) {
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
                                repository.deleteByStandard(standardCombo.getValue().getId());
                            }

                            sheetReader.doBatchRead(readFunction, saveConsumer,100);
                        }
                    } catch (ValidationException ex) {
                        return new TaskResult(ex, ex.getMessage());
                    } catch (IOException ex) {
                        log.error("导入音译表excel文件时发生异常。", ex);
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
        if (standardCombo.getValue() == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("请选择译写标准。");
            alert.showAndWait();
            return;
        }

        var primaryStage = (Stage) commonWordListPane.getScene().getWindow();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("请选择保存路径");
        dc.setInitialDirectory(new File(UserSettingUtil.getFilePath()));
        File exportFolder = dc.showDialog(primaryStage);
        if (exportFolder != null) {
            UserSettingUtil.setFilePath(exportFolder.getPath());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            Path exportFilePath = Paths.get(exportFolder.getPath(), standardCombo.getValue().getStandardName() + "-常用词-"
                    + LocalDateTime.now().format(formatter) + ".xlsx");


            Task<TaskResult> task = new Task<TaskResult>() {
                @Override
                protected TaskResult call() throws Exception {
                    try {
                        var excelWriterBuilder = ExcelUtil.write();
                        var excelSheetWriter = excelWriterBuilder.sheet("常用词").head(headers());

                        var pageNo = 1;
                        var pageSize = 10000;
                        var searchResult = repository.findByStandard(standardCombo.getValue().getId(), new PaginationIn(pageNo, pageSize));
                        do {
                            if (searchResult.getData() != null && !searchResult.getData().isEmpty()) {
                                excelSheetWriter.doWrite(searchResult.getData().stream().map( p -> {
                                    var tmp = new ArrayList<String>(10);
                                    tmp.add(p.getOriginal());
                                    tmp.add(p.getOriginalAbbr());
                                    tmp.add(p.getOriginalType());
                                    tmp.add(originalTypeMap.get(p.getOriginalType()));
                                    tmp.add(p.getRoman());
                                    tmp.add(p.getMatchWay());
                                    tmp.add(matchWayMap.get(p.getMatchWay()));
                                    tmp.add(p.getMatchParams());
                                    tmp.add(p.getTransliteration());
                                    tmp.add(p.getFreeTranslation());
                                    return tmp;
                                }).collect(Collectors.toList()));
                            }
                            pageNo++;
                            searchResult = repository.findByStandard(standardCombo.getValue().getId(), new PaginationIn(pageNo, pageSize));
                        } while(searchResult.getData() != null && !searchResult.getData().isEmpty());

                        try (OutputStream outputStream = Files.newOutputStream(exportFilePath)) {
                            excelWriterBuilder.finish(outputStream);
                        }
                    } catch (IOException ex) {
                        log.error("导出音译表时发生异常。", ex);
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
        var standard = standardCombo.getValue();
        var searchResult = repository.findByStandard(standard == null? null : standard.getId(), new PaginationIn(pageNo, pageSize));
        observableData.setAll(searchResult.getData());
        searchResultTable.setItems(observableData);

        totalPage = searchResult.getTotalPage();
        totalPageLabel.setText(String.valueOf(totalPage));
        pageNoTextField.setText(String.valueOf(pageNo));
    }

    private boolean showEditDialog(CommonWord bean) {
        try {
            var primaryStage = (Stage) commonWordListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/standard/CommonWordEdit.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("常用词");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - editPage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - editPage.getPrefHeight()) / 2);

            var scene = new Scene(editPage);
            dialogStage.setScene(scene);

            CommonWordEditController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(bean, DIC_ORIGINAL_TYPE, DIC_MATCH_WAY, allStandard);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "CommonWordEdit", e);
            return false;
        }
    }

    private void checkImport(Map<Integer, String> data, int rowIndex) throws ValidationException {
        int rowNo = rowIndex + 1;
        var original = data.get(0);
        if (isEmpty(original)) {
            throw new ValidationException(MessageUtil.format("原文不能为空。行号: {}", rowNo));
        } else if (overLength(original, 200)) {
            throw new ValidationException(MessageUtil.format("原文超长，最多200字符。行号: {}", rowNo));
        }

        var originalAbbr = data.get(1);
        if (overLength(originalAbbr, 100)) {
            throw new ValidationException(MessageUtil.format("原文缩写超长，最多100字符。行号: {}", rowNo));
        }

        var originalType = data.get(2);
        if (isEmpty(originalType)) {
            throw new ValidationException(MessageUtil.format("原文类型不能为空。行号: {}", rowNo));
        } else if (originalTypeMap.get(originalType) == null) {
            throw new ValidationException(MessageUtil.format("原文类型不正确，只能在1/2/3/x中选择1个值。行号: {}", rowNo));
        }

        var roman = data.get(4);
        if (overLength(roman, 200)) {
            throw new ValidationException(MessageUtil.format("罗马字母转写超长，最多200字符。行号: {}", rowNo));
        }

        var matchWay = data.get(5);
        if (isEmpty(matchWay)) {
            throw new ValidationException(MessageUtil.format("匹配方式不能为空。行号: {}", rowNo));
        } else if (matchWayMap.get(matchWay) == null) {
            throw new ValidationException(MessageUtil.format("匹配方式不正确，只能在1/2/3/4/5中选择1个值。行号: {}", rowNo));
        }

        var matchParams = data.get(7);
        if (overLength(matchParams, 200)) {
            throw new ValidationException(MessageUtil.format("匹配参数超长，最多200字符。行号: {}", rowNo));
        }

        var transliteration = data.get(8);
        if (overLength(transliteration, 200)) {
            throw new ValidationException(MessageUtil.format("音译超长，最多200字符。行号: {}", rowNo));
        }

        var freeTranslation = data.get(9);
        if (overLength(freeTranslation, 200)) {
            throw new ValidationException(MessageUtil.format("意译超长，最多200字符。行号: {}", rowNo));
        }

        if (StringUtils.isEmpty(transliteration) && StringUtils.isEmpty(freeTranslation)) {
            throw new ValidationException(MessageUtil.format("音译和意译不能同时为空。行号: {}", rowNo));
        }
    }

    private List<SheetColumn> headers() {
        List<SheetColumn> list = new ArrayList<>(10);
        list.add(new SheetColumn("原文", 30));
        list.add(new SheetColumn("原文缩写", 30));
        list.add(new SheetColumn("原文类型", 30, "原文类型：1-人名；2-通名；3-形容词；x-其它"));
        list.add(new SheetColumn("原文类型-含义", 30, "导入数据时，系统读取的是【原文类型】列，请确保填写了正确的值"));
        list.add(new SheetColumn("罗马字母转写", 30));
        list.add(new SheetColumn("匹配方式", 30, "匹配方式：1-精确；2-前缀（词头）；3-后缀（词尾）；4-前置（在xxx之前）；5-后置（在xxx之后）"));
        list.add(new SheetColumn("匹配方式-含义", 30, "导入数据时，系统读取的是【匹配方式】列，请确保填写了正确的值"));
        list.add(new SheetColumn("匹配参数", 30, "如果包含多个匹配参数，参数间使用分号(;)分隔"));
        list.add(new SheetColumn("音译", 30));
        list.add(new SheetColumn("意译", 30));
        return list;
    }
}
