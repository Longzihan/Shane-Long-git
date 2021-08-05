package com.fenda.ai.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lavon.liyuanfang on 2019/5/28.
 */

public class Jsonparse {
    private final static String TAG = "Jsonparse";

    public static String parseInputText(String input)
    {
        try {
        JSONObject jo = new JSONObject(input);
        String text = jo.optString("text", "");
        String var = jo.optString("var", "");
        Log.v(TAG,text+"|||"+var);
        return text+var;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v(TAG,"json err");
        }
         return null;
    }

}
