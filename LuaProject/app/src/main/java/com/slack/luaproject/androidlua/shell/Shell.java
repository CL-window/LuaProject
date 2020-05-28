package com.slack.luaproject.androidlua.shell;

import com.slack.luaproject.androidlua.engine.LuaExecutor;

import androidx.annotation.Keep;

/**
 * Created by slack on 2020/5/27 下午3:57.
 */
public class Shell {
    private static final String TAG = Shell.class.getName();

    @Keep
    public static String getStringFromJavaLayer() {
        return "Hello World From Java Layer";
    }

    @Keep
    public static byte[] getScriptPkgData(String dataPath) {
        return LuaExecutor.instance.scriptDataFetcher.getContentByEntryName(dataPath).getBytes();
    }
}
