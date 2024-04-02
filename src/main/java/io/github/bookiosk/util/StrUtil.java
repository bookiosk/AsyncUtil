package io.github.bookiosk.util;

/**
 * @author bookiosk 2024-02-29
 */
public class StrUtil {

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return null;
        }
        if (ArrayUtil.isEmpty(params) || isBlank(template)) {
            return template.toString();
        }
        return StrFormatter.format(template.toString(), params);
    }

    public static boolean isBlank(CharSequence str) {
        if ((str == null) || ((str.length()) == 0)) {
            return true;
        }
        return str.toString().trim().isEmpty();
    }
}
