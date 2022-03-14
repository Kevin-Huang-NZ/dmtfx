package com.mahara.stocker.controller.standard;

import com.mahara.stocker.dao.StandardRepository;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.util.CheckUtil;
import com.mahara.stocker.util.validate.Validator;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.ValidateResult;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.mahara.stocker.util.CheckUtil.*;

@Controller
public class StandardEditController {
    private static final Logger log = LoggerFactory.getLogger(StandardEditController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatterYMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private AnchorPane standardEditPane;
    @FXML
    private TextField standardNameField;
    @FXML
    private TextField versionCodeField;
    @FXML
    private DatePicker publishYmdField;
    @FXML
    private TextField languageNameField;
    @FXML
    private TextField countryRegionField;
    @FXML
    private TextArea memoField;

    private Stage dialogStage;
    private Standard bean;
    private boolean saveClicked = false;

    @Autowired
    private StandardRepository repository;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            bean.setStandardName(standardNameField.getText());
            bean.setVersionCode(versionCodeField.getText());
            bean.setPublishYmd(publishYmdField.getValue().format(formatterYMD));
            bean.setLanguageName(languageNameField.getText());
            bean.setCountryRegion(countryRegionField.getText());
            bean.setMemo(memoField.getText());

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

    public void setData(Standard bean) {
        this.bean = bean;

        initFields();
        initFieldData();
    }

    private void initFields() {
        publishYmdField.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                if (object == null) {
                    return "";
                } else {
                    return formatterYMD.format(object);
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (StringUtils.isEmpty(string)) {
                    return null;
                } else {
                    return LocalDate.parse(string, formatterYMD);
                }
            }
        });
    }

    private void initFieldData() {
        standardNameField.setText(bean.getStandardName());
        versionCodeField.setText(bean.getVersionCode());
        if (StringUtils.isEmpty(bean.getPublishYmd())) {
            publishYmdField.setValue(null);
        } else {
            publishYmdField.setValue(LocalDate.parse(bean.getPublishYmd(), formatterYMD));
        }
        languageNameField.setText(bean.getLanguageName());
        countryRegionField.setText(bean.getCountryRegion());
        memoField.setText(bean.getMemo());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();
        ValidateResult vr = null;
        vr = Validator.build(standardNameField, standardNameField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入标准名称。"))
                .add(new ValidateRule<String>(v -> overLength(v, 200), "标准名称超长，最多200字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(versionCodeField, versionCodeField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入版本号。"))
                .add(new ValidateRule<String>(v -> overLength(v, 50), "版本号超长，最多50字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(publishYmdField, publishYmdField.getValue())
                .add(new ValidateRule<LocalDate>(v -> v == null, "请选择发布日期。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(languageNameField, languageNameField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入语种。"))
                .add(new ValidateRule<String>(v -> overLength(v, 50), "语种超长，最多50字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(countryRegionField, countryRegionField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入适用国家/地区。"))
                .add(new ValidateRule<String>(v -> overLength(v, 400), "适用国家/地区超长，最多400字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(memoField, memoField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 500), "备注超长，最多500字符。"))
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
