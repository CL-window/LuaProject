package com.slack.luaproject.androidlua.engine;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.slack.luaproject.androidlua.utils.ZipFileUtils;
import com.slack.luaproject.luajava.CPtr;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipFile;

/**
 * Created by slack on 2020/5/27 下午3:43.
 */
public class LuaExecutor {
    public static LuaExecutor instance = new LuaExecutor();

    static {
        try {
            System.loadLibrary("luajava");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = null;
    private ExecutorService mThreadPool = null;
    public ScriptPkgDataFetcher scriptDataFetcher = null;
    private LuaExecutor() {
        mThreadPool = Executors.newSingleThreadExecutor();
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     *     function getStringFromJava()
     *     	return getString()
     *     end
     *
     *     function __TRACKBACK__(msg)
     *     	lua_log("----------------------------------------")
     *         lua_log("LUA ERROR: " .. tostring(msg) .. "\n")
     *         lua_log(debug.traceback())
     *         lua_log("----------------------------------------")
     *     end
     *
     *     function lua_log(fmt, ...)
     *     	local args = {...}
     *     	CPrintMsg(string.format(fmt, table.unpack(args)))
     *     end
     *
     *     function lua_sleepSeconds(seconds)
     *     	sleepSeconds(seconds)
     *     end
     *
     *     function lua_sleepMilliseconds(milliseconds)
     *     	sleepMilliseconds(milliseconds)
     *     end
     *
     *     function main()
     *     	while true do
     *     		for i=1,10 do
     *     			lua_log('%s %d', getStringFromJava(), i)
     *     			lua_log('getData: %s at JNI', getData('res/0028'))
     *     			lua_sleepMilliseconds(500)
     *     		end
     *     		lua_sleepSeconds(1)
     *     	end
     *     end
     */
    public void runScriptPkg(final File scriptPkg, final String configFile) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    initScriptPkg(scriptPkg);
                    ZipFile zipFile = new ZipFile(scriptPkg);
                    String config = ZipFileUtils.getFileContentFromZipFile(zipFile, configFile);
                    String[] luaScriptPaths = config.split("\r\n");
                    final String luaScript = ZipFileUtils.getFilesContentFromZipFile(zipFile, luaScriptPaths);
                    Log.i("LuaEngine", "luaScript: "+Thread.currentThread().getName()+"\n" + luaScript);
                    startScript(luaScript);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initScriptPkg(File scriptPkg) {
        scriptDataFetcher = new ScriptPkgDataFetcher(scriptPkg);
    }

    public void runScript(String script) {
        startScript(script);
    }

    public native boolean startScript(String luaString);
    public native boolean stopScript();
    public native boolean isScriptRunning();

}
