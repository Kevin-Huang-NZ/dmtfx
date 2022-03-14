package com.mahara.stocker.util;

import org.slf4j.helpers.MessageFormatter;

public class MessageUtil {
    public static String format(String format, Object... params) {
        return MessageFormatter.arrayFormat(format, params).getMessage();
    }
}
