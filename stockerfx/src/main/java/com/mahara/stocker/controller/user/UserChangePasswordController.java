package com.mahara.stocker.controller.user;

import com.mahara.stocker.dao.UserRepository;
import com.mahara.stocker.model.SysRole;
import com.mahara.stocker.model.User;
import com.mahara.stocker.util.Base64;
import com.mahara.stocker.util.CheckUtil;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.Validator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.mahara.stocker.util.CheckUtil.*;

@Controller
public class UserChangePasswordController {
    private static final Logger log = LoggerFactory.getLogger(UserChangePasswordController.class);
    @FXML
    private AnchorPane userChangePasswordPane;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    private Stage dialogStage;
    private User user;
    private boolean saveClicked = false;

    @Autowired
    @Qualifier("jtUserRepository")
    private UserRepository userRepository;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            user.setPassword(Base64.encode(newPasswordField.getText()));
            userRepository.update(user);
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

    public boolean setData(User user) {
        this.user = user;

        return true;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();

        var vr = Validator.build(oldPasswordField, oldPasswordField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "?????????????????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(newPasswordField, newPasswordField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "?????????????????????"))
                .add(new ValidateRule<String>(v -> !patternCheck(PATTERN_PASSWORD, v), "???????????????????????????????????????????????????????????????8~20?????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(confirmPasswordField, confirmPasswordField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "???????????????????????????"))
                .add(new ValidateRule<String>(v -> !StringUtils.equals(v, newPasswordField.getText()), "????????????????????????????????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }


        if (sb.isEmpty()) {
            var existed = userRepository.findById(user.getId());
            if (existed != null) {
                if (!StringUtils.equals(Base64.encode(oldPasswordField.getText()), existed.getPassword())) {
                    sb.append("??????????????????\n");
                }
            } else {
                sb.append("????????????????????????????????????????????????\n");
            }
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
