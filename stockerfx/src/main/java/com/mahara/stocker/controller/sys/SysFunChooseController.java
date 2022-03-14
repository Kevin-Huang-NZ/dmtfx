package com.mahara.stocker.controller.sys;

//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.context.AnalysisContext;
//import com.alibaba.excel.read.listener.PageReadListener;
//import com.alibaba.excel.read.listener.ReadListener;
//import com.alibaba.excel.util.ListUtils;

import com.mahara.stocker.dao.SysFunRepository;
import com.mahara.stocker.dao.SysPageRepository;
import com.mahara.stocker.dao.SysRoleFunRepository;
import com.mahara.stocker.model.*;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Scope("prototype")
public class SysFunChooseController {
    private static final Logger log = LoggerFactory.getLogger(SysFunChooseController.class);

    @FXML
    private AnchorPane sysFunChoosePane;
    @FXML
    private TableView<SysFunSelected> searchResultTable;
    @FXML
    private TableColumn<SysFunSelected, Boolean> selectColumn;
    @FXML
    private TableColumn<SysFunSelected, String> funNoColumn;
    @FXML
    private TableColumn<SysFunSelected, String> pageColumn;
    @FXML
    private TableColumn<SysFunSelected, String> actionNameColumn;
    @FXML
    private TableColumn<SysFunSelected, String> memoColumn;

    @FXML
    private Label totalPageLabel;
    @FXML
    private ComboBox<SysPage> pageCombo;
    @FXML
    private TextField pageNoTextField;

    private ObservableList<SysFunSelected> observableData = FXCollections.observableArrayList();

    private int pageNo = 1;
    private int pageSize = 10;
    private int totalPage = 0;

    @Autowired
    @Qualifier("jtSysFunRepository")
    private SysFunRepository sysFunRepository;

    @Autowired
    @Qualifier("jtSysPageRepository")
    private SysPageRepository sysPageRepository;

    @Autowired
    @Qualifier("jtSysRoleFunRepository")
    private SysRoleFunRepository sysRoleFunRepository;

    private Stage dialogStage;
    private SysRole sysRole;
    private Map<String, Boolean> funSelectedFlag = new HashMap<>();
    private Map<String, String> pageNameMap = new HashMap<>();


    @FXML
    private void initialize() {

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

        selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        funNoColumn.setCellValueFactory(cellData -> cellData.getValue().funNoProperty());
        pageColumn.setCellValueFactory(cellData -> {
            var value = cellData.getValue().pageNameProperty().getValue();
            return new SimpleStringProperty(pageNameMap.get(value));
        });
        actionNameColumn.setCellValueFactory(cellData -> cellData.getValue().actionNameProperty());
        memoColumn.setCellValueFactory(cellData -> cellData.getValue().memoProperty());
        //设置为多选模式
//        searchResultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        searchResultTable.setOnMouseClicked(event -> {
            var selectionModel = searchResultTable.getSelectionModel();
            var selected = selectionModel.getSelectedItem();
            if (selected != null) {
                if (event.getClickCount() == 1) {
                    boolean isSelected = selected.isIsSelected();
                    selected.setIsSelected(!isSelected);
                    funSelectedFlag.put(selected.getFunNo(), !isSelected);
                    if (isSelected) {
                        sysRoleFunRepository.deleteByRoleFunNo(sysRole.getRoleNo(), selected.getFunNo());
                    } else {
                        var tmp = new SysRoleFun();
                        tmp.setRoleNo(sysRole.getRoleNo());
                        tmp.setFunNo(selected.getFunNo());
                        sysRoleFunRepository.save(tmp);
                    }
                }
            }
            selectionModel.clearSelection();
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setData(SysRole sysRole) {
        this.sysRole = sysRole;
        sysRoleFunRepository.findByRoleNos(Arrays.asList(sysRole.getRoleNo())).forEach(rf -> funSelectedFlag.put(rf.getFunNo(), true));
    }

    @FXML
    private void handleSearch() {
        pageNo = 1;
        search();
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
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

    public void search() {
        var selectedPage = pageCombo.getValue();
        var pageName = selectedPage == null? "" : selectedPage.getPageName();
        var searchResult = sysFunRepository.findByPageName(pageName, new PaginationIn(pageNo, pageSize));
        var tableData = searchResult.getData().stream().map(f -> {
            var tmp = new SysFunSelected();
            BeanUtils.copyProperties(f, tmp);
            tmp.setIsSelected(isSelected(tmp.getFunNo()));
            return tmp;
        }).collect(Collectors.toList());
        observableData.setAll(tableData);
        searchResultTable.setItems(observableData);

        totalPage = searchResult.getTotalPage();
        totalPageLabel.setText(String.valueOf(totalPage));
        pageNoTextField.setText(String.valueOf(pageNo));
    }

    private boolean isSelected(String funNo) {
        var isSelected = funSelectedFlag.get(funNo);
        if (isSelected == null) {
            funSelectedFlag.put(funNo, false);
            return false;
        } else {
            return isSelected;
        }
    }

//    private void customizedSelectColumn() {
//
//        selectColumn.setCellFactory(param -> new TableCell<SysRole, Void>(){
//            private final CheckBox checkBox = new CheckBox();
//            {
//                checkBox.set
//            }
//
//            @Override
//            protected void updateItem(Void item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty) {
//                    setGraphic(null);
//                } else {
//                    setGraphic(checkBox);
//                }
//            }
//        });
//
//    }
}
