package com.mahara.fxgenerator.util;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

import java.util.Arrays;

public class NameConverter {
	public static String lowerCamel(String lowerUnderscore) {
		if (StringUtils.isEmpty(lowerUnderscore)) {
			return "";
		}
		
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, lowerUnderscore);
	}
	
	public static String upperCamel(String lowerUnderscore) {
		if (StringUtils.isEmpty(lowerUnderscore)) {
			return "";
		}
		
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, lowerUnderscore);
	}
	
	public static String lowerHyphen(String lowerUnderscore) {
		if (StringUtils.isEmpty(lowerUnderscore)) {
			return "";
		}
		
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, lowerUnderscore);
	}
	
	public static String lowerCase(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		
		return str.toLowerCase();
	}
	
	public static String upperCase(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		
		return str.toUpperCase();
	}

	public static String title(String lowerUnderscore) {
		if (StringUtils.isEmpty(lowerUnderscore)) {
			return "";
		}
		String[] words = lowerUnderscore.split("_");
		words[0] = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, words[0]);
		return String.join(" ", words);
	}
}
