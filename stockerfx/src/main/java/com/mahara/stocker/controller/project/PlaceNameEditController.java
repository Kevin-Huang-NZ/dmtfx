package com.mahara.stocker.controller.project;

import com.mahara.stocker.dao.PlaceNameRepository;
import com.mahara.stocker.model.PlaceName;
import com.mahara.stocker.model.Project;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.util.validate.Validator;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.ValidateResult;
import com.mahara.stocker.util.CheckUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
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
public class PlaceNameEditController {
    private static final Logger log = LoggerFactory.getLogger(PlaceNameEditController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private AnchorPane placeNameEditPane;
    @FXML
    private ComboBox<Project> projectIdCombo;
    @FXML
    private TextField originalField;
    @FXML
    private TextField countryField;
    @FXML
    private TextField languageField;
    @FXML
    private TextField gecField;
    @FXML
    private TextArea memoField;
    @FXML
    private TextField romanField;
    @FXML
    private TextField transliterationField;
    @FXML
    private TextField freeTranslationField;
    @FXML
    private TextField transResultField;
    @FXML
    private TextArea emitStandardField;

    private Stage dialogStage;
    private PlaceName bean;
    private boolean saveClicked = false;

    @Autowired
    private PlaceNameRepository repository;

    private List<Project> allProject;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            bean.setProjectId(projectIdCombo.getValue().getId());
            bean.setOriginal(originalField.getText());
            bean.setCountry(countryField.getText());
            bean.setLanguage(languageField.getText());
            bean.setGec(gecField.getText());
            bean.setMemo(memoField.getText());
            bean.setRoman(romanField.getText());
            bean.setTransliteration(transliterationField.getText());
            bean.setFreeTranslation(freeTranslationField.getText());
            bean.setTransResult(transResultField.getText());
            bean.setEmitStandard(emitStandardField.getText());

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

//    public void setData(PlaceName bean, List<Pair<String, String>> statusDic, List<SysRole> sysRoles) {
    public void setData(PlaceName bean, List<Project> allProject) {
        this.bean = bean;
        this.allProject = allProject;

        initFields();
        initFieldData();
    }

    private void initFields() {
        projectIdCombo.setItems(FXCollections.observableArrayList(allProject));
        projectIdCombo.setConverter(new StringConverter<Project>() {
            @Override
            public String toString(Project object) {
                return object == null? "请选择..." : object.getProjectName();
            }
            @Override
            public Project fromString(String value) {
                return null;
            }
        });
        projectIdCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
        });
    }

    private void initFieldData() {
        projectIdCombo.setValue(
                allProject.stream().filter(p -> p.getId() == bean.getProjectId())
                        .findFirst().orElse(null)
        );
        originalField.setText(bean.getOriginal());
        countryField.setText(bean.getCountry());
        languageField.setText(bean.getLanguage());
        gecField.setText(bean.getGec());
        memoField.setText(bean.getMemo());
        romanField.setText(bean.getRoman());
        transliterationField.setText(bean.getTransliteration());
        freeTranslationField.setText(bean.getFreeTranslation());
        transResultField.setText(bean.getTransResult());
        emitStandardField.setText(bean.getEmitStandard());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();
        ValidateResult vr = null;
        vr = Validator.build(projectIdCombo, projectIdCombo.getValue())
                .add(new ValidateRule<Project>(v -> v == null, "请选择项目。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(originalField, originalField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入原文。"))
                .add(new ValidateRule<String>(v -> overLength(v, 200), "原文超长，最多200字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(countryField, countryField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 100), "国家/地区超长，最多100字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(languageField, languageField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 100), "语种超长，最多100字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(gecField, gecField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 100), "地理实体类别超长，最多100字符。"))
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
        vr = Validator.build(romanField, romanField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 200), "罗马字母转写超长，最多200字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(transliterationField, transliterationField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 200), "音译超长，最多200字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(freeTranslationField, freeTranslationField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 200), "意译超长，最多200字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(transResultField, transResultField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 200), "终译超长，最多200字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(emitStandardField, emitStandardField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 1000), "触发的译写规则超长，最多1,000字符。"))
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
