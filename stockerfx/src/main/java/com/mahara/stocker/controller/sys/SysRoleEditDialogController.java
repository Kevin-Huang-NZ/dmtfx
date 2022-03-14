package com.mahara.stocker.controller.sys;

import com.mahara.stocker.dao.SysRoleRepository;
import com.mahara.stocker.model.SysRole;
import com.mahara.stocker.util.CheckUtil;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.Validator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import static com.mahara.stocker.util.CheckUtil.*;

@Controller
public class SysRoleEditDialogController {
    private static final Logger log = LoggerFactory.getLogger(SysRoleEditDialogController.class);
    @FXML
    private AnchorPane sysRoleEditPane;
    @FXML
    private TextField roleNoField;
    @FXML
    private TextField roleNameField;
    @FXML
    private TextArea memoField;

    private Stage dialogStage;
    private SysRole sysRole;
    private boolean saveClicked = false;

    @Autowired
    @Qualifier("jtSysRoleRepository")
    private SysRoleRepository sysRoleRepository;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            sysRole.setRoleNo(roleNoField.getText());
            sysRole.setRoleName(roleNameField.getText());
            sysRole.setMemo(memoField.getText());

            if (sysRole.getId() == 0l) {
                var saved = sysRoleRepository.save(sysRole);
                sysRole.setId(saved.getId());
            } else {
                sysRoleRepository.update(sysRole);
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

    public void setData(SysRole sysRole) {
        this.sysRole = sysRole;
        if (this.sysRole.getId() != 0l) {
            var found = sysRoleRepository.findById(this.sysRole.getId());
            BeanUtils.copyProperties(found, this.sysRole);
//            roleNoField.setEditable(false);
            roleNoField.setDisable(true);
        }

        roleNoField.setText(this.sysRole.getRoleNo());
        roleNameField.setText(this.sysRole.getRoleName());
        memoField.setText(this.sysRole.getMemo());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();

        var vr = Validator.build(roleNoField, roleNoField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入角色编号。"))
                .add(new ValidateRule<String>(v -> !checkRoleNo(v), "角色编号应为1位大/小写英文字母或数字。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(roleNameField, roleNameField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入角色名称。"))
                .add(new ValidateRule<String>(v -> overLength(v, 50), "角色名称超长，最多50字符。"))
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
