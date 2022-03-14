package com.mahara.fxgenerator.util;

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

    public static String getValue(String key) {
        return prefs.get(key, null);
    }
    public static void setValue(String key, String value) {
        prefs.put(key, value);
    }

}
