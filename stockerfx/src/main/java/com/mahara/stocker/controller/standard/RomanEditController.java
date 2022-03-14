package com.mahara.stocker.controller.standard;

import com.mahara.stocker.dao.RomanRepository;
import com.mahara.stocker.model.Roman;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.util.CheckUtil;
import com.mahara.stocker.util.validate.Validator;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.ValidateResult;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.mahara.stocker.util.CheckUtil.*;

@Controller
public class RomanEditController {
    private static final Logger log = LoggerFactory.getLogger(RomanEditController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private AnchorPane romanEditPane;
    @FXML
    private ComboBox<Standard> standardIdCombo;
    @FXML
    private TextField originalAlphaField;
    @FXML
    private TextField romanAlphaField;

    private Stage dialogStage;
    private Roman bean;
    private boolean saveClicked = false;

    @Autowired
    private RomanRepository repository;

    private List<Standard> standards;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            bean.setStandardId(standardIdCombo.getValue().getId());
            bean.setOriginalAlpha(originalAlphaField.getText());
            bean.setRomanAlpha(romanAlphaField.getText());

            if (bean.getId() == 0l) {
                var saved = repository.save(bean);
                bean.setId(saved.getId());
            } else {
                repository.update(bean);
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
    public void setData(Roman bean, List<Standard> standards) {
        this.bean = bean;
        this.standards = standards;

        initFields();
        initFieldData();
    }

    private void initFields() {

        standardIdCombo.setItems(FXCollections.observableArrayList(standards));
        standardIdCombo.setConverter(new StringConverter<Standard>() {
            @Override
            public String toString(Standard object) {
                return object == null? "请选择..." : object.getStandardName();
            }
            @Override
            public Standard fromString(String value) {
                return null;
            }
        });
//        standardIdCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
//        });
    }

    private void initFieldData() {
        standardIdCombo.setValue(
                standards.stream().filter(p -> p.getId() == bean.getStandardId())
                        .findFirst().orElse(null)
        );
        originalAlphaField.setText(bean.getOriginalAlpha());
        romanAlphaField.setText(bean.getRomanAlpha());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();
        ValidateResult vr = null;
        vr = Validator.build(standardIdCombo, standardIdCombo.getValue())
                .add(new ValidateRule<Standard>(v -> v == null, "请选择所属译写标准。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(originalAlphaField, originalAlphaField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入原文字母。"))
                .add(new ValidateRule<String>(v -> overLength(v, 10), "原文字母超长，最多10字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(romanAlphaField, romanAlphaField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入罗马字母。"))
                .add(new ValidateRule<String>(v -> overLength(v, 10), "罗马字母超长，最多10字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }


        if (sb.isEmpty()) {
            return true;
        } else {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText(sb.toString());
            alert.showAndWait();
            return false;
        }
    }
}
