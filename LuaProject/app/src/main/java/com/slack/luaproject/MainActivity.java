package com.slack.luaproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.slack.luaproject.androidlua.AndroidLuaActivity;
import com.slack.luaproject.luajavademo.LuaJavaActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void LuaJava(View view) {
        startActivity(new Intent(this, LuaJavaActivity.class));
    }

    public void AndroidLua(View view) {
        startActivity(new Intent(this, AndroidLuaActivity.class));
    }
}
