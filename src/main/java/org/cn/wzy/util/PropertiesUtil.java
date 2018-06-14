package org.cn.wzy.util;

import java.util.ResourceBundle;

/**
 * Create by Wzy
 * on 2018/6/14 15:47
 * 不短不长八字刚好
 */
public class PropertiesUtil {

    private static final ResourceBundle resource = ResourceBundle.getBundle("config");


    public static final String StringValue(String key) {
        return resource.getString(key);
    }

    public static final int IntegerValue(String key) {
        return Integer.parseInt(resource.getString(key));
    }

    public static final long LongValue(String key) {
        return Long.parseLong(resource.getString(key));
    }

    public static final boolean BoolValue(String key) {
        return Boolean.parseBoolean(resource.getString(key));
    }
}
