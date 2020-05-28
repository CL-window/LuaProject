package com.slack.luaproject.luajavademo;

import com.slack.luaproject.luajava.JavaFunction;
import com.slack.luaproject.luajava.LuaException;
import com.slack.luaproject.luajava.LuaState;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这是 Lua 调用 java 函数的 demo. 首先把要执行的函数包装成一个类，继承 {@link JavaFunction}，
 * 在 {@link #execute()} 中执行操作，可获取 Lua 传入的参数或压入返回值。
 * <p>
 * This is a demo that Lua calls the java function.
 * First wrap the function to be a class which extends {@link JavaFunction}.
 * Then do something in {@link #execute()}.
 * You can get the parameters passed in by Lua or press the return value.
 */
public class MyJavaFunction extends JavaFunction {

    MyJavaFunction(LuaState luaState) {
        super(luaState);
    }

    /**
     * 调用时：
     * lua.LdoString("return getTime('hello','slack',' - passing by lua')");
     * String str = L.toString(2); 获得 'hello'
     * String str = L.toString(1); 获得 null
     * String str = L.toString(3); 获得 'slack'
     * String str = L.toString(4); 获得 ' - passing by lua'
     * 测试发现第一个参数需要从下标 '2' 开始获取， 原理目前未知，待理解源码再来注释
     *
     */
    @Override
    public int execute() {
        // 获取Lua传入的参数，注意第一个参数固定为上下文环境。
        // Getting the parameters passed in by Lua
        // Notice that the first argument is lua context.
        String str = L.toString(2);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        L.pushString(simpleDateFormat.format(date) + str);
        return 1; // 返回值的个数 // Number of return values
    }

    void register() {
        try {
            // 注册为 Lua 全局函数
            // Register as a Lua global function
            register("getTime");
        } catch (LuaException e) {
            e.printStackTrace();
        }
    }

}
