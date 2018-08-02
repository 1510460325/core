package org.cn.wzy.util;

import org.bson.Document;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by Wzy
 * on 2018/7/29 16:42
 * 不短不长八字刚好
 */
public class MapUtil {


    public static final <Q> Map<String, Object> parseEntity(Q record, String... strings) {
        if (record == null)
            return null;
        String pre = "";
        if (strings != null && strings.length > 0)
            pre = strings[0];
        Map<String, Object> cond = new HashMap<>();
        Class clazz = record.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(record);
                if (value != null)
                    cond.put(pre + field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return cond;
    }


    public static final <Q> Q castToEntity(Map<String, Object> document, Class<Q> target) {
        try {
            Q result = target.newInstance();
            Field[] fields = target.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                Object value = document.get(f.getName());
                if (value == null)
                    continue;
                else if (value instanceof Document) {
                    f.set(result, castToEntity((Document) value, f.getType()));
                } else if (f.getType() == Integer.class)
                    f.set(result, ((Number) value).intValue());
                else if (f.getType() == Long.class)
                    f.set(result, ((Number) value).longValue());
                else
                    f.set(result, f.getType().cast(document.get(f.getName())));
            }
            return result;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
