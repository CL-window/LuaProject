package com.slack.luaproject.androidlua;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.slack.luaproject.R;
import com.slack.luaproject.androidlua.engine.LuaExecutor;
import com.slack.luaproject.androidlua.utils.FileUtils;

import java.io.File;

public class AndroidLuaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_lua);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        File scriptPkg = new File("data/data/com.slack.luaproject/script.zip");
        if (!scriptPkg.exists()) {
            copyFileToDir("script.zip", scriptPkg);
        }
    }

    public void copyFileToDir(String file, File dstFile) {
        AssetManager assetManager = getAssets();
        try {
            FileUtils.writeFile(dstFile, assetManager.open(file));
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    public void start(View view) {
        if (!LuaExecutor.instance.isScriptRunning()) {
            File scriptPkg = new File("data/data/com.slack.luaproject/script.zip");
            String config = "config";
            LuaExecutor.instance.runScriptPkg(scriptPkg, config);
        }
    }

    public void stop(View view) {
        if (LuaExecutor.instance.isScriptRunning()) {
            LuaExecutor.instance.stopScript();
        }
    }
}
