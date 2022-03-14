package com.mahara.stocker.controller.sys;

import com.mahara.stocker.dao.SysFunRepository;
import com.mahara.stocker.dic.ActionType;
import com.mahara.stocker.model.SysFun;
import com.mahara.stocker.model.SysPage;
import com.mahara.stocker.util.CheckUtil;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.Validator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.mahara.stocker.util.CheckUtil.*;

@Controller
public class SysFunEditDialogController {
    private static final Logger log = LoggerFactory.getLogger(SysFunEditDialogController.class);
    private static final String ACTION_TYPE_ELSE = "e";
    @FXML
    private AnchorPane sysFunEditPane;
    @FXML
    private ComboBox<SysPage> pageCombo;
    @FXML
    private ComboBox<ActionType> actionTypeCombo;
    @FXML
    private TextField actionNoField;
    @FXML
    private TextField actionNameField;
    @FXML
    private TextArea memoField;

    private Stage dialogStage;
    private SysFun sysFun;
    private List<SysPage> sysPages;
    private List<ActionType> actionTypes;
    private boolean saveClicked = false;

    @Autowired
    @Qualifier("jtSysFunRepository")
    private SysFunRepository sysFunRepository;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            sysFun.setFunNo(pageCombo.getValue().getPageName() + "/" + actionNoField.getText());
            sysFun.setPageName(pageCombo.getValue().getPageName());
            sysFun.setActionType(actionTypeCombo.getValue().getActionType());
            sysFun.setActionNo(actionNoField.getText());
            sysFun.setActionName(actionNameField.getText());
            sysFun.setMemo(memoField.getText());

            if (sysFun.getId() == 0l) {
                var saved = sysFunRepository.save(sysFun);
                sysFun.setId(saved.getId());
            } else {
                sysFunRepository.update(sysFun);
            }
            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        saveClicked = false;
        dialogStage.close();
    }


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setData(SysFun sysFun, List<SysPage> sysPages, List<ActionType> actionTypes) {
        // 1、初始化pageCombo
        this.sysPages = sysPages;
        pageCombo.setItems(FXCollections.observableArrayList(this.sysPages));
        pageCombo.setConverter(new StringConverter<SysPage>() {
            @Override
            public String toString(SysPage object) {
                return object == null? "Please select page" : object.getPageTitle();
            }
            @Override
            public SysPage fromString(String value) {
                return null;
            }
        });
        // 响应ComboBox的change事件
        pageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
        });

        // 2、初始化actionTypeCombo
        this.actionTypes = actionTypes;
        actionTypeCombo.setItems(FXCollections.observableArrayList(this.actionTypes));
        actionTypeCombo.setConverter(new StringConverter<ActionType>() {
            @Override
            public String toString(ActionType object) {
                return object == null? "Please action type" : object.getActionTypeName();
            }
            @Override
            public ActionType fromString(String value) {
                return null;
            }
        });
        // 响应ComboBox的change事件
        actionTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (StringUtils.equals(newVal.getActionType(), ACTION_TYPE_ELSE)) {
                    actionNoField.setText("");
                    actionNoField.setEditable(true);
                } else {
                    actionNoField.setText(newVal.getActionType());
                    actionNoField.setEditable(false);
                }
            }
        });

        // 3、设置界面项目的值
        this.sysFun = sysFun;
        if (this.sysFun.getId() != 0l) {
            var found = sysFunRepository.findById(this.sysFun.getId());
            BeanUtils.copyProperties(found, this.sysFun);
            pageCombo.setDisable(true);
            actionTypeCombo.setDisable(true);
            actionNoField.setDisable(true);
        }
        pageCombo.setValue(sysPages.stream().filter(p -> StringUtils.equals(p.getPageName(), this.sysFun.getPageName())).findFirst().orElse(null));
        actionTypeCombo.setValue(actionTypes.stream().filter(at -> StringUtils.equals(at.getActionType(), this.sysFun.getActionType())).findFirst().orElse(null));
        actionNoField.setText(this.sysFun.getActionNo());
        actionNameField.setText(this.sysFun.getActionName());
        memoField.setText(this.sysFun.getMemo());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();

        var vr = Validator.build(pageCombo, pageCombo.getValue())
                .add(new ValidateRule<SysPage>(v -> v == null, "请选择页面。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(actionTypeCombo, actionTypeCombo.getValue())
                .add(new ValidateRule<ActionType>(v -> v == null, "请选择动作类型。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        } else {
            var actionTypeValue = actionTypeCombo.getValue().getActionType();
            if (StringUtils.equals(actionTypeValue, ACTION_TYPE_ELSE)) {
                vr = Validator.build(actionNoField, actionNoField.getText())
                        .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入动作编号。"))
                        .add(new ValidateRule<String>(v -> !checkActionNo(v), "动作编号必需以英文字母开头，可以包含大小写英文字母和数字，最长29字符。"))
                        .validate();
                if (!vr.isSuccess()) {
                    sb.append(vr.getErrorMessage() + "\n");
                }
            }
        }
        vr = Validator.build(actionNameField, actionNameField.getText())
            .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入动作名称。"))
            .add(new ValidateRule<String>(v -> overLength(v, 50), "动作名称超长，最多50字符。"))
            .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(memoField, memoField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 50), "备注超长，最多500字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }


        if (sb.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText(sb.toString());
            alert.showAndWait();
            return false;
        }
    }
}
