package io.github.bookiosk.util;

import cn.hutool.core.io.resource.ResourceUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Reader;

/**
 * @author bookiosk
 */
public class JsonUtil {

    private static Gson gson = new Gson();

    /**
     *
     * @param jsonStr Json字符串
     * @return Json对象
     */
    public static JsonObject parseObj(String jsonStr) {
        return gson.fromJson(jsonStr, JsonObject.class);
    }

    public static JsonObject parseObj(Reader reader) {
        return gson.fromJson(reader, JsonObject.class);
    }

}
