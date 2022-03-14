package com.mahara.stocker.controller;

import com.mahara.stocker.controller.user.UserEditDialogController;
import com.mahara.stocker.dao.UserRepository;
import com.mahara.stocker.model.LoginUser;
import com.mahara.stocker.model.User;
import com.mahara.stocker.util.Base64;
import com.mahara.stocker.util.CheckUtil;
import com.mahara.stocker.util.FXMLLoaderUtil;
import static com.mahara.stocker.util.CheckUtil.*;

import com.mahara.stocker.util.UserSettingUtil;
import com.mahara.stocker.util.validate.ValidateRule;
import com.mahara.stocker.util.validate.Validator;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField passwordField;
    @FXML
    private CheckBox saveMe;

    @Autowired
    @Qualifier("jtUserRepository")
    private UserRepository userRepository;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    private User loginUser;

    @FXML
    private void initialize() {
        clear();
        userNameField.requestFocus();
        LoginUser loginUser = UserSettingUtil.getLoginUser();
        if (loginUser != null) {
            userNameField.setText(loginUser.getPhone());
            passwordField.setText(loginUser.getPassword());
            saveMe.setSelected(true);
        }
//        userNameField.setText("13811650908");
//        passwordField.setText("1234Qwer");
    }

    @FXML
    private void handleLogin() {
        if (isInputValid()) {
            if (saveMe.isSelected()) {
                UserSettingUtil.setLoginUser(new LoginUser(userNameField.getText(), passwordField.getText()));
            } else {
                UserSettingUtil.removeLoginUser();
            }
            initRoot();
        }
    }

    @FXML
    private void handleClear() {
        clear();
    }

    private void initRoot() {
        try {
            fxmlLoaderUtil.initialize("/view/Layout.fxml");
            var layout = (BorderPane) fxmlLoaderUtil.getView();
            Scene scene = new Scene(layout);

            var primaryStage = (Stage) loginPane.getScene().getWindow();
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((bounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((bounds.getHeight() - primaryStage.getHeight()) / 2);
            primaryStage.sizeToScene();

            LayoutController controller = fxmlLoaderUtil.getController();
            controller.setDate(loginUser);

            // 显示默认页面
//            fxmlLoaderUtil.initialize("/view/sys/SysPageList.fxml");
//            var homePage = (AnchorPane) fxmlLoaderUtil.getView();
//            var defaultTab = new Tab();
//            defaultTab.setText("Page");
//            defaultTab.setContent(homePage);
//            var tabPane = (TabPane)layout.getCenter();
//            tabPane.getTabs().add(defaultTab);

        } catch (IOException | IllegalStateException e) {
            log.error("Loaded failed: {}.", "Root & SysPageList", e);
        }
    }

    private void clear() {
        this.userNameField.setText(null);
        this.passwordField.setText(null);
    }

    private boolean isInputValid() {
        StringBuilder sb = new StringBuilder();
        var vr = Validator.build(userNameField, userNameField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入您的手机号。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        vr = Validator.build(passwordField, passwordField.getText())
                .add(new ValidateRule<String>(CheckUtil::isEmpty, "请输入您的密码。"))
                .validate();
        if (!vr.isSuccess()) {
            sb.append(vr.getErrorMessage() + "\n");
        }

        if (sb.isEmpty()) {
            var user = userRepository.findByUniqueKey(userNameField.getText(), null);
            if (user == null) {
                sb.append("手机号或者密码不正确。\n");
            } else if (!StringUtils.equals(user.getPassword(), Base64.encode(passwordField.getText()))) {
                sb.append("手机号或者密码不正确。\n");
            } else if (StringUtils.equals(user.getStatus(), "0")) {
                sb.append("账户被锁定，请联系管理员。\n");
            } else {
                loginUser = user;
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
