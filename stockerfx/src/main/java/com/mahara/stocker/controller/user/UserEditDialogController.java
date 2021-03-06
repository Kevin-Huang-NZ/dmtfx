package com.mahara.stocker.controller.user;

import com.mahara.stocker.dao.UserRepository;
import com.mahara.stocker.dic.ActionType;
import com.mahara.stocker.model.SysPage;
import com.mahara.stocker.model.SysRole;
import com.mahara.stocker.model.User;
import com.mahara.stocker.util.Base64;
import com.mahara.stocker.util.CheckUtil;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.Validator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
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
public class UserEditDialogController {
    private static final Logger log = LoggerFactory.getLogger(UserEditDialogController.class);
    @FXML
    private AnchorPane userEditPane;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Pair<String, String>> genderCombo;
    @FXML
    private ComboBox<SysRole> rolesCombo;
    @FXML
    private ComboBox<Pair<String, String>> statusCombo;

    private Stage dialogStage;
    private User user;
    private boolean saveClicked = false;

    @Autowired
    @Qualifier("jtUserRepository")
    private UserRepository userRepository;

    private List<Pair<String, String>> genderDic;
    private List<SysRole> sysRoles;
    private List<Pair<String, String>> statusDic;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            user.setUserName(userNameField.getText());
            user.setPhone(phoneField.getText());
            user.setPassword(Base64.encode(passwordField.getText()));
            user.setGender(genderCombo.getValue().getKey());
            user.setRoles(rolesCombo.getValue().getRoleNo());
            user.setStatus(statusCombo.getValue().getKey());

            if (user.getId() == 0l) {
                var saved = userRepository.save(user);
                user.setId(saved.getId());
            } else {
                userRepository.update(user);
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

    public boolean setData(User user, List<Pair<String, String>> genderDic, List<SysRole> sysRoles, List<Pair<String, String>> statusDic) {
        this.user = user;
        this.genderDic = genderDic;
        this.sysRoles = sysRoles;
        this.statusDic = statusDic;

        initFields();
        initFieldData();
        return true;
    }

    private void initFields() {
        genderCombo.setItems(FXCollections.observableArrayList(genderDic));
        genderCombo.setConverter(new StringConverter<Pair<String, String>>() {
            @Override
            public String toString(Pair<String, String> object) {
                return object == null? "?????????..." : object.getValue();
            }
            @Override
            public Pair<String, String> fromString(String value) {
                return null;
            }
        });
        genderCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
        });

        rolesCombo.setItems(FXCollections.observableArrayList(sysRoles));
        rolesCombo.setConverter(new StringConverter<SysRole>() {
            @Override
            public String toString(SysRole object) {
                return object == null? "?????????..." : object.getRoleName();
            }
            @Override
            public SysRole fromString(String value) {
                return null;
            }
        });
        rolesCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
        });

        statusCombo.setItems(FXCollections.observableArrayList(statusDic));
        statusCombo.setConverter(new StringConverter<Pair<String, String>>() {
            @Override
            public String toString(Pair<String, String> object) {
                return object == null? "?????????..." : object.getValue();
            }
            @Override
            public Pair<String, String> fromString(String value) {
                return null;
            }
        });
        statusCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
        });
    }

    private void initFieldData() {
        userNameField.setText(user.getUserName());
        phoneField.setText(user.getPhone());
        passwordField.setText(Base64.decode(user.getPassword()));
        genderCombo.setValue(
                genderDic.stream().filter(p -> StringUtils.equals(p.getKey(), user.getGender()))
                        .findFirst()
                        .orElseGet(()-> {return new Pair<>("0", "?????????");})
        );
        rolesCombo.setValue(
                sysRoles.stream().filter(p -> StringUtils.equals(p.getRoleNo(), user.getRoles()))
                        .findFirst().orElse(null)
        );
        statusCombo.setValue(
                statusDic.stream().filter(p -> StringUtils.equals(p.getKey(), user.getStatus()))
                        .findFirst()
                        .orElseGet(()-> {return new Pair<>("1", "??????");})
        );
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();

        var vr = Validator.build(userNameField, userNameField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "????????????????????????"))
                .add(new ValidateRule<String>(v -> overLength(v, 50), "???????????????????????????50?????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(phoneField, phoneField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "?????????????????????"))
                .add(new ValidateRule<String>(v -> !patternCheck(PATTERN_MOBILE, v), "???????????????????????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(passwordField, passwordField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "??????????????????"))
                .add(new ValidateRule<String>(v -> !patternCheck(PATTERN_PASSWORD, v), "????????????????????????????????????????????????????????????8~20?????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(genderCombo, genderCombo.getValue())
                .add(new ValidateRule<Pair<String, String>>(v -> v == null, "??????????????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(rolesCombo, rolesCombo.getValue())
                .add(new ValidateRule<SysRole>(v -> v == null, "??????????????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(statusCombo, statusCombo.getValue())
                .add(new ValidateRule<Pair<String, String>>(v -> v == null, "????????????????????????"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        if (sb.isEmpty()) {
            var existed = userRepository.findByUniqueKey(phoneField.getText(), user.getId());
            if (existed != null) {
                sb.append("??????????????????\n");
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
