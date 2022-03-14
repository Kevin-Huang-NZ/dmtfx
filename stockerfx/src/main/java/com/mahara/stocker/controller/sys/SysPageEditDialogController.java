package com.mahara.stocker.controller.sys;

import static com.mahara.stocker.util.CheckUtil.*;

import com.mahara.stocker.dao.SysPageRepository;
import com.mahara.stocker.model.SysPage;
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

@Controller
public class SysPageEditDialogController {
    private static final Logger log = LoggerFactory.getLogger(SysPageEditDialogController.class);
    @FXML
    private AnchorPane sysPageEditPane;
    @FXML
    private TextField pageNameField;
    @FXML
    private TextField pageTitleField;
    @FXML
    private TextArea memoField;

    private Stage dialogStage;
    private SysPage sysPage;
    private boolean saveClicked = false;

    @Autowired
    @Qualifier("jtSysPageRepository")
    private SysPageRepository sysPageRepository;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            sysPage.setPageName(pageNameField.getText());
            sysPage.setPageTitle(pageTitleField.getText());
            sysPage.setMemo(memoField.getText());

            if (sysPage.getId() == 0l) {
                var saved = sysPageRepository.save(sysPage);
                sysPage.setId(saved.getId());
            } else {
                sysPageRepository.update(sysPage);
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

    public void setData(SysPage sysPage) {
        this.sysPage = sysPage;
        if (this.sysPage.getId() != 0l) {
            var found = sysPageRepository.findById(this.sysPage.getId());
            BeanUtils.copyProperties(found, this.sysPage);
            pageNameField.setDisable(true);
        }

        pageNameField.setText(this.sysPage.getPageName());
        pageTitleField.setText(this.sysPage.getPageTitle());
        memoField.setText(this.sysPage.getMemo());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();

        var vr = Validator.build(pageNameField, pageNameField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入页面名称。"))
                .add(new ValidateRule<String>(v -> !checkPageName(v), "页面名称必需以英文字母开头，可以包含大小写英文字母和数字，最长50字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(pageTitleField, pageTitleField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入页面标题。"))
                .add(new ValidateRule<String>(v -> overLength(v, 50), "页面标题超长，最多50字符。"))
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
