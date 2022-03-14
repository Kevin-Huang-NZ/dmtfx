package com.mahara.stocker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class CheckUtil {

	public static final String PATTERN_ALPHABET_NUMBER = "[a-zA-Z0-9]+";
	public static final String PATTERN_EMAIL = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
	public static final String PATTERN_MOBILE = "(\\+\\d+)?1[3458]\\d{9}$";
	public static final String PATTERN_INT = "\\-?[1-9]\\d*|0";
	public static final String PATTERN_INT_1_99 = "[1-9][0-9]?";
	public static final String PATTERN_INT_POSITIVE = "[1-9][0-9]*";
	public static final String PATTERN_DECIMALS = "\\-?[1-9]\\d+(\\.\\d+)?";
	public static final String PATTERN_CHINESE = "^[\u4E00-\u9FA5]+$";
	public static final String PATTERN_YMD = "[1-2][0-9]{3}(-)\\d{1,2}\\1\\d{1,2}";
	public static final String PATTERN_URL = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
	public static final String PATTERN_POST_CODE = "[1-9]\\d{5}";
	public static final String PATTERN_IP = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
	public static final String PATTERN_PASSWORD = "(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,20}";
	public static final String PATTERN_YMDHMS = "[1-2][0-9]{3}(-)\\d{1,2}\\1\\d{1,2} \\d{1,2}(:)\\d{1,2}\\2\\d{1,2}";

	public static final String PATTERN_ONE_ALPHABET_NUMBER = "[a-zA-Z0-9]{1}";

	public static boolean isEmpty(String target) {
		return StringUtils.isEmpty(target);
	}

	public static boolean strEquals(String target1, String target2) {
		return StringUtils.equals(target1, target2);
	}

	public static boolean overLength(String target, int maxLength) {
		if (target == null) {
			return false;
		}
		if (target.length() > maxLength) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证page_name
	 *
	 * @param pageName 页面名称，格式：字母开头，大小写字母或数字或者中划线组成，最长50字符
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPageName(String pageName) {
		String regex = "^[a-zA-Z][a-zA-Z0-9|-]{0,49}$";
		return Pattern.matches(regex, pageName);
	}

	/**
	 * 验证fun_no
	 * 
	 * @param funNo 功能编号，格式：page_name/action_no.其中，action_no是，字母开头，大小写字母或数字或者中划线组成，最长30字符
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkFunNo(String funNo) {
		String regex = "^[a-zA-Z][a-zA-Z0-9|-]{0,49}/[a-zA-Z][a-zA-Z0-9|-]{0,29}$";
		return Pattern.matches(regex, funNo);
	}

	/**
	 * 验证action_no
	 *
	 * @param actionNo 页面名称，格式：字母开头，大小写字母或数字或者中划线组成，最长50字符
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkActionNo(String actionNo) {
		String regex = "^[a-zA-Z][a-zA-Z0-9|-]{0,28}$";
		return Pattern.matches(regex, actionNo);
	}

	/**
	 * 验证fun_no
	 *
	 * @param roleNo 权限编号，1位字母或数字
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkRoleNo(String roleNo) {
		return Pattern.matches(PATTERN_ONE_ALPHABET_NUMBER, roleNo);
	}

	public static boolean patternCheck(String pattern, String target) {
		return Pattern.matches(pattern, target);
	}

	/**
	 * <pre>
	 *   
	 * 获取网址 URL 的一级域名  
	 * http://www.zuidaima.com/share/1550463379442688.htm ->> zuidaima.com
	 * </pre>
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomain(String url) {
		Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
		// Pattern p=Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)",
		// Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(url);
		matcher.find();
		return matcher.group();
	}
}
