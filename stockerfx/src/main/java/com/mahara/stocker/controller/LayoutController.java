package com.mahara.stocker.controller;

import com.mahara.stocker.controller.user.UserChangePasswordController;
import com.mahara.stocker.controller.user.UserEditDialogController;
import com.mahara.stocker.dao.SysPageRepository;
import com.mahara.stocker.dao.SysRoleFunRepository;
import com.mahara.stocker.model.SysRoleFun;
import com.mahara.stocker.model.User;
import com.mahara.stocker.util.FXMLLoaderUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LayoutController {
    private static final Logger log = LoggerFactory.getLogger(LayoutController.class);
    @FXML
    private BorderPane root;
    @FXML
    private GridPane topMenu;
    @FXML
    private Label loginUserLabel;
    @FXML
    private MenuButton loginUserMenu;

    @Autowired
    private FXMLLoaderUtil fxmlLoaderUtil;

    @Autowired
    private SysRoleFunRepository sysRoleFunRepository;
    @Autowired
    private SysPageRepository sysPageRepository;

    private User loginUser;
    private Map<String, Boolean> loginUserRolePage = new HashMap<>();

    private ObservableList<MenuButton> allMenuButtons = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        topMenu.getChildren().forEach(m -> {
            if (m instanceof MenuButton) {
                allMenuButtons.add((MenuButton) m);
            }
        });
        allMenuButtons.add(loginUserMenu);
        changeMenuButtonAction();
    }

    public void setDate(User user) {
        loginUser = user;
        loginUserLabel.setText(loginUser.getUserName());
        var roles = loginUser.getRoles();

        var pages = sysPageRepository.findAll();
        pages.forEach(s -> loginUserRolePage.put(s.getPageName(), false));

        var roleFuns = sysRoleFunRepository.findByRoleNos(Arrays.asList(roles.split(";")));
        roleFuns.forEach(s -> loginUserRolePage.put(s.getFunNo().split("/")[0], true));
        checkPermission();
    }

    @FXML
    public void changePassword() {
        try {
            var primaryStage = (Stage) root.getScene().getWindow();

            fxmlLoaderUtil.initialize("/view/user/UserChangePassword.fxml");
            var editPage = (AnchorPane) fxmlLoaderUtil.getView();
            // Create the dialog Stage.
            var dialogStage = new Stage();
            dialogStage.setTitle("修改密码");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((bounds.getWidth() - editPage.getPrefWidth()) / 2);
            dialogStage.setY((bounds.getHeight() - editPage.getPrefHeight()) / 2);

            var scene = new Scene(editPage);
            dialogStage.setScene(scene);

            UserChangePasswordController controller = fxmlLoaderUtil.getController();
            controller.setDialogStage(dialogStage);
            if (controller.setData(loginUser)) {
                dialogStage.showAndWait();
            }
        } catch (IOException | IllegalStateException e) {
            log.error("Failed to load fxml file: {}.", "/view/user/UserChangePassword.fxml", e);
        }
    }

    @FXML
    public void signOut() {{
        try {
            fxmlLoaderUtil.initialize("/view/Login.fxml");
            var layout = (AnchorPane) fxmlLoaderUtil.getView();
            Scene scene = new Scene(layout);

            var primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            // 居中
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((bounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((bounds.getHeight() - primaryStage.getHeight()) / 2);
            primaryStage.sizeToScene();

        } catch (IOException | IllegalStateException e) {
            log.error("Loaded failed: {}.", "Login", e);
        }
    }
//        Platform.exit();
    }

    @FXML
    private void handleListProject() {
        addTab("/view/project/ProjectList.fxml", "项目");
    }

    @FXML
    private void handleListPlaceName() {
        addTab("/view/project/PlaceNameList.fxml", "地名条目");
    }

    @FXML
    private void handleListStandard() {
        addTab("/view/standard/StandardList.fxml", "译写标准");
    }

    @FXML
    private void handleListTransliteration() {
        addTab("/view/standard/TransliterationList.fxml", "音译表");
    }

    @FXML
    private void handleListCommonWord() {
        addTab("/view/standard/CommonWordList.fxml", "常用词");
    }

    @FXML
    private void handleListRoman() {
        addTab("/view/standard/RomanList.fxml", "罗马字母对照表");
    }

    @FXML
    private void handleListUser() {
        addTab("/view/user/UserList.fxml", "用户");
    }

    @FXML
    private void handleListSyRole() {
        addTab("/view/sys/SysRoleList.fxml", "用户角色");
    }

    @FXML
    private void handleListSysPage() {
        addTab("/view/sys/SysPageList.fxml", "系统页面");
    }

    @FXML
    private void handleListSysFun() {
        addTab("/view/sys/SysFunList.fxml", "系统功能");
    }

    private void addTab(String viewFileName, String tabText) {
        try {
            fxmlLoaderUtil.initialize(viewFileName);
            Parent view = fxmlLoaderUtil.getView();
            var tabPane = (TabPane)root.getCenter();
            var selectionMode = tabPane.getSelectionModel();
            var viewId = view.getId();
            var tabs = tabPane.getTabs();
            var findTabResult = tabs.stream().filter(tab -> StringUtils.equals(tab.getContent().getId(), viewId)).findFirst();
            if (findTabResult.isPresent()) {
                selectionMode.select(findTabResult.get());
            } else {
                var newTab = new Tab();
                newTab.setText(tabText);
                newTab.setContent(view);
                tabs.add(newTab);
                selectionMode.select(newTab);
            }
        } catch (IOException | IllegalStateException e) {
            log.error("Loaded failed: {}.", viewFileName, e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Internal Error");
            alert.setHeaderText(null);
            alert.setContentText("Please contact with your IT support.");
            alert.showAndWait();
        }
    }

    private void changeMenuButtonAction() {
        allMenuButtons.forEach(mt -> {
            mt.setOnMouseEntered(e -> {
                hideOtherMenuButton(mt);
                mt.show();
            });
        });
    }

    private void hideOtherMenuButton(MenuButton current) {
        allMenuButtons.forEach(mt -> {
            if (mt != current) {
                mt.hide();
            }
        });
    }

    private void checkPermission() {
        // top menu暂时只检查button和menu button
        topMenu.getChildren().forEach(m -> {
            if (m instanceof MenuButton) {
                checkPermissionMenuButton((MenuButton) m);
            } else {
                var cssId = m.getId();
                if (!StringUtils.isEmpty(cssId)) {
                    var hasPermission = loginUserRolePage.get(cssId);
                    if (hasPermission == null || !hasPermission) {
                        m.setVisible(false);
                    }
                }
            }
        });


        // top menu 把栏目前移，补空位
        var topMenuNodes = topMenu.getChildren();
        var currentIndex = 1;
        for (var mn : topMenuNodes) {
            if (mn.isVisible()) {
                GridPane.setColumnIndex(mn, currentIndex);
                currentIndex++;
            }
        }
    }

    private void checkPermissionMenuButton(MenuButton mb) {
        var menuItems = mb.getItems();
        var all = menuItems.size();
        var hidden = 0;
        for (var mi : menuItems) {
            var cssId = mi.getId();
            if (!StringUtils.isEmpty(cssId)) {
                var hasPermission = loginUserRolePage.get(cssId);
                if (hasPermission == null || !hasPermission) {
                    mi.setVisible(false);
                    hidden++;
                }
            }
        }
        if (all == hidden) {
            mb.setVisible(false);
        }
    }
}
