package com.mahara.stocker.util;

import com.mahara.stocker.App;
import com.mahara.stocker.model.LoginUser;
import org.apache.commons.lang3.StringUtils;

import java.util.prefs.Preferences;

public class UserSettingUtil {
    private static final Preferences prefs = Preferences.userRoot();
    private static final String userHome = System.getProperty("user.home");

    public static String getFilePath() {
        String filePath = prefs.get("filePath", null);
        if (filePath == null) {
            return userHome;
        } else {
            return filePath;
        }
    }

    public static void setFilePath(String filePath) {
        prefs.put("filePath", filePath);
    }

    public static LoginUser getLoginUser() {
        String phone = prefs.get("loginUser.phone", null);
        String password = prefs.get("loginUser.password", null);
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
            return null;
        }
        return new LoginUser(phone, password);
    }

    public static void setLoginUser(LoginUser loginUser) {
        if (loginUser != null) {
            prefs.put("loginUser.phone", loginUser.getPhone());
            prefs.put("loginUser.password", loginUser.getPassword());
        }
    }

    public static void removeLoginUser() {
        prefs.remove("loginUser.phone");
        prefs.remove("loginUser.password");
    }
}
