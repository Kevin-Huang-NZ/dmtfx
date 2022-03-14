package com.mahara.stocker.controller.sys;

//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.context.AnalysisContext;
//import com.alibaba.excel.read.listener.PageReadListener;
//import com.alibaba.excel.read.listener.ReadListener;
//import com.alibaba.excel.util.ListUtils;
import com.mahara.stocker.dao.SysPageRepository;
import com.mahara.stocker.model.SysPage;
import com.mahara.stocker.service.SysService;
import com.mahara.stocker.util.*;
import com.mahara.stocker.util.excel.ExcelUtil;
import com.mahara.stocker.util.excel.SheetColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.mahara.stocker.util.CheckUtil.*;

@Controller
public class SysPageListController {
    private static final Logger log = LoggerFactory.getLogger(SysPageListController.class);
    @FXML
    private AnchorPane sysPageListPane;
    @FXML
    private TableView<SysPage> searchResultTable;
    @FXML
    private TableColumn<SysPage, String> pageNameColumn;
    @FXML
    private TableColumn<SysPage, String> pageTitleColumn;
    @FXML
    private TableColumn<SysPage, String> memoColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private TextField keyWordTextField;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<SysPage> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 15;
    private int totalPage = 0;

    // 行选中标记
    private Map<Long, Boolean> selectionFlag;

    @Autowired
    @Qualifier("jtSysPageRepository")
    private SysPageRepository sysPageRepository;

    @Autowired
    private SysService sysService;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    @FXML
    private void initialize() {
        pageNameColumn.setCellValueFactory(cellData -> cellData.getValue().pageNameProperty());
        pageTitleColumn.setCellValueFactory(cellData -> cellData.getValue().pageTitleProperty());
        memoColumn.setCellValueFactory(cellData -> cellData.getValue().memoProperty());
        // 选中弹框修改
//        searchResultTable.getSelectionModel().selectedItemProperty().addListener(
//                (observable, oldValue, newValue) -> showEditDialog(newValue)
//        );

        // 双击编辑
        searchResultTable.setOnMouseClicked(event -> {
//            log.debug("Mouse event on the table: {}", event.getClickCount());
            var selectionModel = searchResultTable.getSelectionModel();
            var selectedIndex = selectionModel.getSelectedIndex();
            var selected = selectionModel.getSelectedItem();
            if (selected != null) {
                if (event.getClickCount() == 2) {
                    showEditDialog(selected);
                }
                // 单击切换选中/未选中状态(ctrl + click 可以取消选中状态)
//                else if (event.getClickCount() == 1) {
//                    boolean isSelected = selectionFlag.get(selected.getId());
//                    selectionFlag.replaceAll((k, v) -> false);
//                    if (isSelected) {
//                        selectionModel.clearSelection(selectedIndex);
//                    }
//                    selectionFlag.put(selected.getId(), !isSelected);
//                }
            }
        });
        search();
    }

    @FXML
    private void handleSearch() {
        pageNo = 1;
        search();
    }

    @FXML
    private void handleNew() {
        var tmp = new SysPage();
        boolean saveClicked = this.showEditDialog(tmp);
        if (saveClicked) {
            observableData.add(tmp);
            selectionFlag.put(tmp.getId(), false);
        }
    }

    @FXML
    private void handleImport() {
        var primaryStage = (Stage) sysPageListPane.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("请选择文件");
        fc.setInitialDirectory(new File(UserSettingUtil.getFilePath()));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls/*.xlsx)", Arrays.asList("*.xls", "*.xlsx"));
        fc.getExtensionFilters().add(extFilter);
        File importFile = fc.showOpenDialog(primaryStage);
//        log.debug("Import file: {}", importFile);
        if (importFile != null) {
            UserSettingUtil.setFilePath(importFile.getParent());
            var fileName = importFile.getName();
            var fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            // read excel
            try {
                try (InputStream inputStream = new FileInputStream(importFile)) {
                    ExcelUtil.read(inputStream, fileType).sheet().headRowNumber(0)
                            .doCheck(this::checkImport)
                            .doBatchRead(
                                (rowDataMap) -> {
                                    SysPage bean = new SysPage();
                                    bean.setPageName(rowDataMap.get(0));
                                    bean.setPageTitle(rowDataMap.get(1));
                                    bean.setMemo(rowDataMap.get(2));
                                    return bean;
                                },
                                (dataList) -> {
                                    sysPageRepository.batchSave(dataList);
                                },
                        100);
                }

            } catch (ValidationException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("文件内容错误");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("文件读取错误");
                alert.setHeaderText(null);
                alert.setContentText("读取文件时发生异常，请联系系统管理员。");
                alert.showAndWait();
            }

            search();
        }
    }

    @FXML
    private void handleExport() {
        var primaryStage = (Stage) sysPageListPane.getScene().getWindow();
        DirectoryChooser  dc = new DirectoryChooser();
        dc.setTitle("请选择保存路径");
        dc.setInitialDirectory(new File(UserSettingUtil.getFilePath()));
        File exportFolder = dc.showDialog(primaryStage);
//        log.debug("Export folder: {}", exportFolder);
        if (exportFolder != null) {
            UserSettingUtil.setFilePath(exportFolder.getPath());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            Path exportFilePath = Paths.get(exportFolder.getPath(), LocalDateTime.now().format(formatter) + ".xlsx");
            try {
                var excelWriterBuilder = ExcelUtil.write();
                var excelSheetWriter = excelWriterBuilder.sheet("Page").head(headers());

                var keyWord = keyWordTextField.getText();
                var pageNo = 1;
                var pageSize = 10000;
                var searchResult = sysPageRepository.findByKeyWord(keyWord, new PaginationIn(pageNo, pageSize));
                do {
                    if (searchResult.getData() != null && !searchResult.getData().isEmpty()) {
                        excelSheetWriter.doWrite(searchResult.getData().stream().map( p -> {
                            var tmp = new ArrayList<String>(3);
                            tmp.add(p.getPageName());
                            tmp.add(p.getPageTitle());
                            tmp.add(p.getMemo());
                            return tmp;
                        }).collect(Collectors.toList()));
                    }
                    pageNo++;
                    searchResult = sysPageRepository.findByKeyWord(keyWord, new PaginationIn(pageNo, pageSize));
                } while(searchResult.getData() != null && !searchResult.getData().isEmpty());

                try (OutputStream outputStream = Files.newOutputStream(exportFilePath)) {
                    excelWriterBuilder.finish(outputStream);
                }
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("文件写入异常");
                alert.setHeaderText(null);
                alert.setContentText("写Excel文件时出现异常，请联系系统管理员。");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleDelete() {
        var selectionModel = searchResultTable.getSelectionModel();
        var selectedIndex = selectionModel.getSelectedIndex();
        if (selectedIndex >= 0) {
            var selected = selectionModel.getSelectedItem();
            sysService.deleteSysPage(selected.getId());
            searchResultTable.getItems().remove(selectedIndex);
            selectionFlag.remove(selected.getId());
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
        var keyWord = keyWordTextField.getText();
        var searchResult = sysPageRepository.findByKeyWord(keyWord, new PaginationIn(pageNo, pageSize));
        observableData.setAll(searchResult.getData());
        searchResultTable.setItems(observableData);

        selectionFlag = new HashMap<>();
        observableData.forEach(t -> selectionFlag.put(t.getId(), false));

        totalPage = searchResult.getTotalPage();
        totalPageLabel.setText(String.valueOf(totalPage));
        pageNoTextField.setText(String.valueOf(pageNo));
    }

    private boolean showEditDialog(SysPage sysPage) {
        try {
            var primaryStage = (Stage) sysPageListPane.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/sys/SysPageEditDialog.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("系统页面");
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
            SysPageEditDialogController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(sysPage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "SysPageEditDialog", e);
            return false;
        }
    }

    private void checkImport(Map<Integer, String> data, int rowIndex) throws ValidationException {
        int rowNo = rowIndex + 2;

        if (!checkPageName(data.get(0))) {
            throw new ValidationException("页面名称必需以英文字母开头，可以包含大小写英文字母和数字，最长50字符。行号: {}" + rowNo);
        }
        var pageTitle = data.get(1);
        if (isEmpty(pageTitle)) {
            throw new ValidationException("页面标题不能为空。行号: {}" + rowNo);
        } else if (overLength(pageTitle, 50)) {
            throw new ValidationException("页面标题超长，最多50字符。行号: {}" + rowNo);
        }
        var memo = data.get(2);
        if (overLength(memo, 500)) {
            throw new ValidationException("备注超长，最多500字符。行号: {}" + rowNo);
        }
    }

    private List<SheetColumn> headers() {
        List<SheetColumn> list = new ArrayList<>(3);
        list.add(new SheetColumn("页面名称", 30));
        list.add(new SheetColumn("页面标题", 30));
        list.add(new SheetColumn("备注", 50));
        return list;
    }
}
