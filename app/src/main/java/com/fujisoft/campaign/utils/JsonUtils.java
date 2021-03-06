package com.fujisoft.campaign.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * json工具类
 */
public class JsonUtils {

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description: cannot be instantiated
     * </p>
     */
    public JsonUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * @param @param  list
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: javaBeanToJson
     * @Description: 生成json字符串
     */
    public static <T> String listToJson(List<T> list) {
        if (null == list || list.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * @param @param  object
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: javaBeanToJson
     * @Description: java对象转换成json
     */
    public static String javaBeanToJson(Object object) {
        try {
            Gson gson = new Gson();
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param JsonStr
     * @param name
     * @return String 返回类型
     * @throws
     * @Title: getJsonStr
     * @Description: 取出json字符串中指定内容
     */
    public static String getJsonStr(String JsonStr, String name) {
        if (TextUtils.isEmpty(JsonStr) || TextUtils.isEmpty(name)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(JsonStr);
            if (jsonObject != null && jsonObject.has(name))
                return jsonObject.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * @param jsonStr
     * @param c
     * @return T 返回类型
     * @throws
     * @Title: jsonStrToBean
     * @Description: json解析成java bean可用于多层解析
     */
    public static <T> T jsonStrToBean(String jsonStr, Class<T> c) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, c);
    }

    /**
     * @return List<T> 返回类型
     * @throws
     * @Title: jsonStrToList
     * @Description: 将json数组解析成list [{},{},{}]
     */
    public static <T> List<T> jsonStrToList(String jsonStr, Type type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param c
     * @return List<T> 返回类型
     * @throws
     * @Title: jsonStrToList
     * @Description: 将json数组解析成list [{},{},{}]
     */
    public static <T> List<T> jsonStrToList(String jsonStr, Class<T> c) {
        Gson gson = new Gson();
        Type objectType = new TypeToken<List<T>>() {
        }.getType();
        return gson.fromJson(jsonStr, objectType);
    }
}
