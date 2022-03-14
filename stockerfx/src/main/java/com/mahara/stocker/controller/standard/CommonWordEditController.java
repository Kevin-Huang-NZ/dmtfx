package com.mahara.stocker.controller.standard;

import com.mahara.stocker.dao.CommonWordRepository;
import com.mahara.stocker.model.CommonWord;
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
public class CommonWordEditController {
    private static final Logger log = LoggerFactory.getLogger(CommonWordEditController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private AnchorPane commonWordEditPane;
    @FXML
    private ComboBox<Standard> standardIdCombo;
    @FXML
    private TextField originalField;
    @FXML
    private TextField originalAbbrField;
    @FXML
    private ComboBox<Pair<String, String>> originalTypeCombo;
    @FXML
    private TextField romanField;
    @FXML
    private ComboBox<Pair<String, String>> matchWayCombo;
    @FXML
    private TextField matchParamsField;
    @FXML
    private TextField transliterationField;
    @FXML
    private TextField freeTranslationField;

    private Stage dialogStage;
    private CommonWord bean;
    private boolean saveClicked = false;

    @Autowired
    private CommonWordRepository repository;

    private List<Pair<String, String>> originalTypeDic;
    private List<Pair<String, String>> matchWayDic;
    private List<Standard> standards;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            bean.setStandardId(standardIdCombo.getValue().getId());
            bean.setOriginal(originalField.getText());
            bean.setOriginalAbbr(originalAbbrField.getText());
            bean.setOriginalType(originalTypeCombo.getValue().getKey());
            bean.setRoman(romanField.getText());
            bean.setMatchWay(matchWayCombo.getValue().getKey());
            bean.setMatchParams(matchParamsField.getText());
            bean.setTransliteration(transliterationField.getText());
            bean.setFreeTranslation(freeTranslationField.getText());

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

    public void setData(CommonWord bean, List<Pair<String, String>> originalTypeDic, List<Pair<String, String>> matchWayDic, List<Standard> standards) {
        this.bean = bean;
        this.originalTypeDic = originalTypeDic;
        this.matchWayDic = matchWayDic;
        this.standards = standards;

        initFields();
        initFieldData();
    }

    private void initFields() {
        originalTypeCombo.setItems(FXCollections.observableArrayList(originalTypeDic));
        originalTypeCombo.setConverter(new StringConverter<Pair<String, String>>() {
            @Override
            public String toString(Pair<String, String> object) {
                return object == null? "请选择..." : object.getValue();
            }
            @Override
            public Pair<String, String> fromString(String value) {
                return null;
            }
        });

        matchWayCombo.setItems(FXCollections.observableArrayList(matchWayDic));
        matchWayCombo.setConverter(new StringConverter<Pair<String, String>>() {
            @Override
            public String toString(Pair<String, String> object) {
                return object == null? "请选择..." : object.getValue();
            }
            @Override
            public Pair<String, String> fromString(String value) {
                return null;
            }
        });

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
    }

    private void initFieldData() {
        standardIdCombo.setValue(
                standards.stream().filter(p -> p.getId() == bean.getStandardId())
                        .findFirst().orElse(null)
        );
        originalField.setText(bean.getOriginal());
        originalAbbrField.setText(bean.getOriginalAbbr());
        originalTypeCombo.setValue(
                originalTypeDic.stream().filter(p -> StringUtils.equals(p.getKey(), bean.getOriginalType()))
                        .findFirst()
                        .orElseGet(()-> {return new Pair<>("1", "人名");})
        );
        romanField.setText(bean.getRoman());
        matchWayCombo.setValue(
                matchWayDic.stream().filter(p -> StringUtils.equals(p.getKey(), bean.getMatchWay()))
                        .findFirst()
                        .orElseGet(()-> {return new Pair<>("1", "精确");})
        );
        matchParamsField.setText(bean.getMatchParams());
        transliterationField.setText(bean.getTransliteration());
        freeTranslationField.setText(bean.getFreeTranslation());
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
        vr = Validator.build(originalField, originalField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入原文。"))
                .add(new ValidateRule<String>(v -> overLength(v, 200), "原文超长，最多200字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(originalAbbrField, originalAbbrField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 100), "原文缩写超长，最多100字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(originalTypeCombo, originalTypeCombo.getValue())
                .add(new ValidateRule<Pair<String, String>>(v -> v == null, "请选择原文类型。"))
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
        vr = Validator.build(matchWayCombo, matchWayCombo.getValue())
                .add(new ValidateRule<Pair<String, String>>(v -> v == null, "请选择匹配方式。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(matchParamsField, matchParamsField.getText())
                .add(new ValidateRule<String>(v -> overLength(v, 200), "匹配参数超长，最多200字符。"))
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

        if (StringUtils.isEmpty(transliterationField.getText()) && StringUtils.isEmpty(freeTranslationField.getText())) {
            sb.append("音译和意译不能同时为空。\n");
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
