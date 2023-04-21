package com.example.network.data;

import com.google.gson.Gson;

public class JsonHandler {
    public static Object decode(String json, Class classToCast) {
        // must cast the return value to classType
        Gson gson = new Gson();
        return gson.fromJson(json, classToCast);
    }

    public static String encode(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
