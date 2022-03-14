package com.mahara.fxgenerator;

/**
 * 避免打包成fat jar文件后，直接运行App报错：
 * Error: JavaFX runtime components are missing, and are required to run this application.
 */
public class AppStarter {
    public static void main(String[] args) {
        App.main(args);
    }
}
