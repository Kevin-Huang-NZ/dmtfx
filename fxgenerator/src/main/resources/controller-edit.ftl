<#setting number_format="#">
package [=controllerPkg];

import [=repositoryPkg].[=repositoryName];
import [=modelPkg].[=modelName];
import [=basePackage].util.validate.Validator;
import [=basePackage].util.validate.ValidateRule;
import [=basePackage].util.validate.ValidateResult;
import [=basePackage].util.CheckUtil;
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

import static [=basePackage].util.CheckUtil.*;

@Controller
public class [=editControllerName] {
    private static final Logger log = LoggerFactory.getLogger([=editControllerName].class);
    private static final DateTimeFormatter formatterYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private AnchorPane [=lcTableName]EditPane;
    <#list fields as field>
    @FXML
    private TextField [=field.lcColumnName]Field;
    </#list>
//    @FXML
//    private ComboBox<Pair<String, String>> statusCombo;
//    @FXML
//    private ComboBox<SysRole> rolesCombo;

    private Stage dialogStage;
    private [=modelName] bean;
    private boolean saveClicked = false;

    @Autowired
    private [=repositoryName] repository;

//    private List<Pair<String, String>> statusDic;
//    private List<SysRole> sysRoles;

    @FXML
    private void initialize() {
        saveClicked = false;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            <#list fields as field>
                <#if field.isNullable == 'YES' >
                    <#switch field.javaDataType>
                        <#case 'String'>
            bean.set[=field.ucColumnName]([=field.lcColumnName]Field.getText());
                            <#break>
                        <#case 'Long'>
            var [=field.lcColumnName] = [=field.lcColumnName]Field.getText();
            if (isEmpty([=field.lcColumnName])) {
                bean.set[=field.ucColumnName](null);
            } else {
                bean.set[=field.ucColumnName](Long.valueOf([=field.lcColumnName]));
            }
                            <#break>
                        <#case 'Integer'>
            var [=field.lcColumnName] = [=field.lcColumnName]Field.getText();
            if (isEmpty([=field.lcColumnName])) {
                bean.set[=field.ucColumnName](null);
            } else {
                bean.set[=field.ucColumnName](Integer.valueOf([=field.lcColumnName]));
            }
                            <#break>
                        <#case 'BigDecimal'>
            var [=field.lcColumnName] = [=field.lcColumnName]Field.getText();
            if (isEmpty([=field.lcColumnName])) {
                bean.set[=field.ucColumnName](null);
            } else {
                bean.set[=field.ucColumnName](new BigDecimal([=field.lcColumnName]));
            }
                        <#break>
                        <#case 'Timestamp'>
            var [=field.lcColumnName] = [=field.lcColumnName]Field.getText();
            if (isEmpty([=field.lcColumnName])) {
                bean.set[=field.ucColumnName](null);
            } else {
                bean.set[=field.ucColumnName](Timestamp.valueOf(LocalDateTime.parse([=field.lcColumnName], formatterYMDHMS)));
            }
                            <#break>
                        <#default>
            var [=field.lcColumnName] = [=field.lcColumnName]Field.getText();
            if (isEmpty([=field.lcColumnName])) {
                bean.set[=field.ucColumnName](null);
            } else {
                bean.set[=field.ucColumnName](([=field.javaDataType]) [=field.lcColumnName]);
            }
                    </#switch>
                <#else>
                    <#switch field.javaDataType>
                        <#case 'String'>
            bean.set[=field.ucColumnName]([=field.lcColumnName]Field.getText());
                            <#break>
                        <#case 'Long'>
            bean.set[=field.ucColumnName](Long.valueOf([=field.lcColumnName]Field.getText()));
                            <#break>
                        <#case 'Integer'>
            bean.set[=field.ucColumnName](Integer.valueOf([=field.lcColumnName]Field.getText()));
                            <#break>
                        <#case 'BigDecimal'>
            bean.set[=field.ucColumnName](new BigDecimal([=field.lcColumnName]Field.getText()));
                            <#break>
                        <#case 'Timestamp'>
            bean.set[=field.ucColumnName](Timestamp.valueOf(LocalDateTime.parse([=field.lcColumnName]Field.getText(), formatterYMDHMS)));
                            <#break>
                        <#default>
            bean.set[=field.ucColumnName](([=field.javaDataType]) [=field.lcColumnName]Field.getText());
                    </#switch>
                </#if>
            </#list>
//            bean.setStatus(statusCombo.getValue().getKey());
//            bean.setRoles(rolesCombo.getValue().getRoleNo());

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

//    public void setData([=modelName] bean, List<Pair<String, String>> statusDic, List<SysRole> sysRoles) {
    public void setData([=modelName] bean) {
        this.bean = bean;
//        this.statusDic = statusDic;
//        this.sysRoles = sysRoles;

//        initFields();
        initFieldData();
    }

//    private void initFields() {
//        statusCombo.setItems(FXCollections.observableArrayList(statusDic));
//        statusCombo.setConverter(new StringConverter<Pair<String, String>>() {
//            @Override
//            public String toString(Pair<String, String> object) {
//                return object == null? "请选择..." : object.getValue();
//            }
//            @Override
//            public Pair<String, String> fromString(String value) {
//                return null;
//            }
//        });
//        statusCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
//        });
//
//        rolesCombo.setItems(FXCollections.observableArrayList(sysRoles));
//        rolesCombo.setConverter(new StringConverter<SysRole>() {
//            @Override
//            public String toString(SysRole object) {
//                return object == null? "请选择..." : object.getRoleName();
//            }
//            @Override
//            public SysRole fromString(String value) {
//                return null;
//            }
//        });
//        rolesCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
//        });
//    }

    private void initFieldData() {
        <#list fields as field>
            <#switch field.javaDataType>
                <#case 'String'>
        [=field.lcColumnName]Field.setText(bean.get[=field.ucColumnName]());
                    <#break>
                <#case 'Timestamp'>
        var [=field.lcColumnName] = bean.get[=field.ucColumnName]();
        if ([=field.lcColumnName] == null) {
            [=field.lcColumnName]Field.setText("");
        } else {
            [=field.lcColumnName]Field.setText(formatterYMDHMS.format([=field.lcColumnName].toLocalDateTime());
        }
                    <#break>
                <#default>
        var [=field.lcColumnName] = bean.get[=field.ucColumnName]();
        if ([=field.lcColumnName] == null) {
            [=field.lcColumnName]Field.setText("");
        } else {
            [=field.lcColumnName]Field.setText([=field.lcColumnName].toString());
        }
            </#switch>
        </#list>
//        statusCombo.setValue(
//                statusDic.stream().filter(p -> StringUtils.equals(p.getKey(), bean.getStatus()))
//                        .findFirst()
//                        .orElseGet(()-> {return new Pair<>("1", "正常");})
//        );
//        rolesCombo.setValue(
//                sysRoles.stream().filter(p -> StringUtils.equals(p.getRoleNo(), bean.getRoles()))
//                        .findFirst().orElse(null)
//        );
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();
        ValidateResult vr = null;
        <#list fields as field>
        vr = Validator.build([=field.lcColumnName]Field, [=field.lcColumnName]Field.getText())
            <#if field.isNullable == 'YES' >
                <#switch field.javaDataType>
                    <#case 'Long'>
                .add(new ValidateRule<String>(v -> {
                        if (isEmpty(v)) {
                            return true;
                        } else {
                            return !patternCheck(PATTERN_INT, v);
                        }
                    }, "[=field.columnTitle]必需是整型数字。"))
                        <#break>
                    <#case 'Integer'>
                .add(new ValidateRule<String>(v -> {
                        if (isEmpty(v)) {
                            return true;
                        } else {
                            return !patternCheck(PATTERN_INT, v);
                        }
                    }, "[=field.columnTitle]必需是整型数字。"))
                        <#break>
                    <#case 'BigDecimal'>
                .add(new ValidateRule<String>(v -> {
                        if (isEmpty(v)) {
                            return true;
                        } else {
                            return !patternCheck(PATTERN_DECIMALS, v);
                        }
                    }, "[=field.columnTitle]必需是数字。"))
                        <#break>
                    <#case 'Timestamp'>
                .add(new ValidateRule<String>(v -> {
                        if (isEmpty(v)) {
                            return true;
                        } else {
                            return !patternCheck(PATTERN_YMDHMS, v);
                        }
                    }, "[=field.columnTitle]格式不正确，格式要求：yyyy-MM-dd HH:mm:ss。"))
                        <#break>
                    <#default>
                        <#if field.characterMaximumLength?has_content >
                .add(new ValidateRule<String>(v -> overLength(v, [=field.characterMaximumLength]), "[=field.columnTitle]超长，最多[=field.characterMaximumLength]字符。"))
                        </#if>
                </#switch>
            <#else>
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入[=field.columnTitle]。"))
                <#switch field.javaDataType>
                    <#case 'Long'>
                .add(new ValidateRule<String>(v -> !patternCheck(PATTERN_INT, v), "[=field.columnTitle]必需是整型数字。"))
                        <#break>
                    <#case 'Integer'>
                .add(new ValidateRule<String>(v -> !patternCheck(PATTERN_INT, v), "[=field.columnTitle]必需是整型数字。"))
                        <#break>
                    <#case 'BigDecimal'>
                .add(new ValidateRule<String>(v -> !patternCheck(PATTERN_DECIMALS, v), "[=field.columnTitle]必需是数字。"))
                        <#break>
                    <#case 'Timestamp'>
                .add(new ValidateRule<String>(v -> !patternCheck(PATTERN_YMDHMS, v), "[=field.columnTitle]格式不正确，格式要求：yyyy-MM-dd HH:mm:ss。"))
                        <#break>
                    <#default>
                        <#if field.characterMaximumLength?has_content >
                .add(new ValidateRule<String>(v -> overLength(v, [=field.characterMaximumLength]), "[=field.columnTitle]超长，最多[=field.characterMaximumLength]字符。"))
                        </#if>
                </#switch>
            </#if>
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }
        </#list>

        <#if hasUniqueKey >
        if (sb.isEmpty()) {
            var existed = repository.findByUniqueKey(<#list ukFields as field>[=field.lcColumnName], </#list>bean.getId());
            if (existed != null) {
                sb.append("数据重复，保存失败。\n");
            }
        }
        </#if>

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
