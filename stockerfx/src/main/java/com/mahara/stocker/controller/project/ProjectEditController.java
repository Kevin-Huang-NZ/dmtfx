package com.mahara.stocker.controller.project;

import com.mahara.stocker.dao.ProjectRepository;
import com.mahara.stocker.model.Project;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.util.validate.Validator;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.ValidateResult;
import com.mahara.stocker.util.CheckUtil;
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
public class ProjectEditController {
    private static final Logger log = LoggerFactory.getLogger(ProjectEditController.class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatterYMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private AnchorPane projectEditPane;
    @FXML
    private ComboBox<Standard> standardIdCombo;
    @FXML
    private TextField projectNameField;
    @FXML
    private DatePicker startDateField;
    @FXML
    private DatePicker dueDateField;
    @FXML
    private ComboBox<Pair<String, String>> statusCombo;
    @FXML
    private TextArea memoField;

    private Stage dialogStage;
    private Project bean;
    private boolean saveClicked = false;

    @Autowired
    private ProjectRepository repository;

    private List<Pair<String, String>> statusDic;
    private List<Standard> standards;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            bean.setStandardId(standardIdCombo.getValue().getId());
            bean.setProjectName(projectNameField.getText());
            bean.setStartDate(startDateField.getValue().format(formatterYMD));
            bean.setDueDate(dueDateField.getValue().format(formatterYMD));
            bean.setStatus(statusCombo.getValue().getKey());
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

    public void setData(Project bean, List<Pair<String, String>> statusDic, List<Standard> standards) {
        this.bean = bean;
        this.statusDic = statusDic;
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

        startDateField.setConverter(new StringConverter<LocalDate>() {
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

        dueDateField.setConverter(new StringConverter<LocalDate>() {
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

        statusCombo.setItems(FXCollections.observableArrayList(statusDic));
        statusCombo.setConverter(new StringConverter<Pair<String, String>>() {
            @Override
            public String toString(Pair<String, String> object) {
                return object == null? "请选择..." : object.getValue();
            }
            @Override
            public Pair<String, String> fromString(String value) {
                return null;
            }
        });
    }

    private void initFieldData() {
        standardIdCombo.setValue(
                standards.stream().filter(p -> p.getId() == bean.getStandardId())
                        .findFirst().orElse(null)
        );
        projectNameField.setText(bean.getProjectName());
        if (StringUtils.isEmpty(bean.getStartDate())) {
            startDateField.setValue(null);
        } else {
            startDateField.setValue(LocalDate.parse(bean.getStartDate(), formatterYMD));
        }
        if (StringUtils.isEmpty(bean.getDueDate())) {
            dueDateField.setValue(null);
        } else {
            dueDateField.setValue(LocalDate.parse(bean.getDueDate(), formatterYMD));
        }
        statusCombo.setValue(
                statusDic.stream().filter(p -> StringUtils.equals(p.getKey(), bean.getStatus()))
                        .findFirst()
                        .orElseGet(()-> {return new Pair<>("1", "执行中");})
        );
        memoField.setText(bean.getMemo());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();
        ValidateResult vr = null;
        vr = Validator.build(projectNameField, projectNameField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入项目名。"))
                .add(new ValidateRule<String>(v -> overLength(v, 100), "项目名超长，最多100字符。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(standardIdCombo, standardIdCombo.getValue())
                .add(new ValidateRule<Standard>(v -> v == null, "请选择使用的译写标准。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(startDateField, startDateField.getValue())
                .add(new ValidateRule<LocalDate>(v -> v == null, "请选择开始日期。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(dueDateField, dueDateField.getValue())
                .add(new ValidateRule<LocalDate>(v -> v == null, "请选择结束日期。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        vr = Validator.build(statusCombo, statusCombo.getValue())
                .add(new ValidateRule<Pair<String, String>>(v -> v == null, "请选择项目状态。"))
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
