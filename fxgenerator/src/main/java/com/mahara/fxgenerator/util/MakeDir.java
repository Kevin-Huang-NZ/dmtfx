package com.mahara.fxgenerator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MakeDir {
	public static Path makeByPackage(String baseDir, String packageName) throws IOException {
		if (baseDir == null || baseDir.trim().length() == 0) {
			return null;
		}
		if (packageName == null || packageName.trim().length() == 0) {
			return null;
		}
		var path = Paths.get(baseDir, packageName.split("\\."));
		Files.createDirectories(path);
		return path;
	}

	public static Path make(String[] folders, String baseDir) throws IOException {
		if (baseDir == null || baseDir.trim().length() == 0) {
			return null;
		}

		if (folders == null || folders.length == 0) {
			return null;
		}

		var path = Paths.get(baseDir, folders);
		Files.createDirectories(path);
		return path;
	}
}
