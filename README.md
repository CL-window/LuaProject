### Lua 
* [lua 官网](https://www.lua.org/download.html)
* 使用参考1: https://zhuanlan.zhihu.com/p/76560253
* 使用参考2: https://github.com/liangchenhe55/Android-Lua
* 虚拟栈的参考：https://blog.csdn.net/zhongdong00/article/details/80237592


#### 编译步骤
1. 新建一个工程
1. local.properties 配置ndk.dir路径 & gradle.properties 配置使用ndk
    ```
    ndk.dir=/Users/slack/Library/Android/sdk/ndk-bundle

    android.useDeprecatedNdk=true
    ```
1. 复制lua源码到cpp/lua下，使用的5.3.5的源码， 删掉luac.c， lua.c里面的main方法注释掉
1. liolib.c 编译时会报错，luaconf.h内替换 lua_getlocaledecpoint()
    ```
    #if !defined(lua_getlocaledecpoint)
    //#define lua_getlocaledecpoint()		(localeconv()->decimal_point[0])
    #define lua_getlocaledecpoint()		('.')
    #endif
    ```
1. 编译时还会报一堆错，提示lua的资源找不到，头文件lua.h替换为引用lua.hpp头文件就可以了
1. 学习测试使用，只编译了 armeabi-v7a 和 arm64-v8a 的包
1. 有两套资源，一个是 andoirdlua, 在 luabridge.cpp 下有JNI_OnLoad；一个是luajava, 在luajava.cpp 下有JNI_OnLoad；需要注意测试时只能有一个该方法，另外一个需要注释掉

#### androidlua
1. 介绍java通过调用c代码调用lua, lua通过调用c代码来间接调用java代码;
1. Android 和 C++的桥梁是 luabridge.cpp 文件 和 LuaExecutor.java 以及 Shell.java文件;
1. LuaExecutor.java是Android调用C，Shell.java是C调用Android;
1. 调用步骤
    1. 创建lua_State栈， mScriptContext = luaL_newstate();
    1. 加载标准库 luaL_openlibs(mScriptContext);
    1. 注册lua脚本里需要方法，lua脚本大概是这样的，以lua_xx格式命名的function都是lua脚本里的，需要注册的方法在 shell.h头文件mCFunctions里定义，shell.cpp/shell.h为c和lua交互的桥梁；getString()方法是 lua调用c, c 再调用java 获得结果
    ```
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
    ```
    1. 加载lua脚本 luaL_loadbuffer
    1. 找到需要执行的方法（测试脚本是执行main方法），先 lua_getglobal() 找到main方法，在 lua_pcall() 执行

#### luajava
1. luajava 对lua的使用介绍比较详细， 很多lua的方法都封装为对应的java可调用的方法，可以直观看到整个lua执行的流程
1. 脚本运行举例
    ```
    --filename: luafile.lua  
    function LuaFunc()  
        return 1,2,3,4;  
    end 
    ```
1. 调用的c代码
    ```
    int main (int argc, char **argv) {
    int status, result;
    lua_State *L = luaL_newstate();  // 新建虚拟栈
    if (L == NULL) {
        l_message(argv[0], "cannot create state: not enough memory");
        return EXIT_FAILURE;
    }
    luaL_openlibs(L); // 加载标准库
    luaL_dofile(L, "luafile.lua");// 这里执行 test.lua  Lua文件
    lua_settop(L, 0);//清空栈,是为了确认栈底是空的，以便后面的操作是按照顺序入栈的且从1号栈位开始
    lua_getglobal(L,  "LuaFunc");// 通过lua脚本内方法名字，获取到该方法, "LuaFunc"入栈
    lua_pcall(L, 0, 4, 0);// 表示函数有0个参数，4个返回值，0个错误信息
    // 获取方法的返回值，方法 return 1,2,3,4; 1先入栈，2入栈，3入栈，4入栈， 4位于盏顶，
    // 栈的正数标号以栈底为1开始，栈底倒数第二个为2号；负数标号以栈顶为-1开始，栈顶第二个为-2号
    printf("%s\n", lua_tostring(L, 1)); // out: 1
    printf("%s\n", lua_tostring(L, -4)); // out: 1
    printf("%s\n", lua_tostring(L, 2)); // out: 2
    printf("%s\n", lua_tostring(L, 3)); // out: 3
    printf("%s\n", lua_tostring(L, 4)); // out: 4
    printf("%s\n", lua_tostring(L, -1)); // out: 4
    lua_close(L); // 关闭
    return EXIT_SUCCESS;
    }
    ```
1. luajava 里提供了 从lua读取非 local 变量，注意local 标示的变量无法被读取
1. Java 设置 lua 变量，并读取
1. lua 调用Java方法，luajava.cpp ,jni_pushJavaFunction() -> luaJavaFunctionCall(),看一下注入java函数后栈内数据， 所以java 这边获取传入的参数，要从下标2开始，
    ```
    注入Java函数时：
    D/LuaUtils: Default 1: userdata
    调用Java注入的函数时：
    D/LuaUtils: String 3:`2020-05-28 15:28:29 - passing by lua'
    D/LuaUtils: String 2:` - passing by lua'
    D/LuaUtils: Default 1: userdata
    ```
1. 获取lua内table数据
1. 设置lua内table数据
1. 调用lua内比较大小的方法
1. 传入 Java 对象,我们可以直接将对象作为参数传入，并在 Lua 中直接执行对象的成员函数
1. Lua回调，先java调用lua, 注册一个方法，在lua调用java的注入方法，然后java再调用lua的回调；
    ```
    注入时：
    D/LuaUtils: Default 1: userdata
    调用时：
    D/LuaUtils: Default 2: function
    D/LuaUtils: Default 1: userdata
    ```

#### JNINativeMethod解读
```
typedef struct {
const char* name;
const char* signature;
void* fnPtr;
} JNINativeMethod;

第一个变量name是Java中函数的名字。
第二个变量signature，用字符串是描述了函数的参数和返回值
第三个变量fnPtr是函数指针，指向C函数。
第二个参数:
"()" 中的字符表示参数，后面的则代表返回值。例如"()V" 就表示void Func();
"(II)V" 表示 void Func(int, int);

字符   Java类型        C类型
V      void           void
Z      jboolean       boolean
I      jint           int
J      jlong          ong
D      jdouble        double
F      jfloat         float
B      jbyte          byte
C      jchar          char
S      jshort         short

数组则以"["开始，用两个字符表示
[I     jintArray      int[]
[F     jfloatArray    float[]
[B     jbyteArray     byte[]
[C     jcharArray     char[]
[S     jshortArray    short[]
[D     jdoubleArray   double[]
[J     jlongArray     long[]
[Z     jbooleanArray  boolean[]

如果Java函数的参数是class，则以"L"开头，以";"结尾中间是用"/" 隔开的包及类名。而其对应的C函数名的参数则为jobject. 一个例外是String类，其对应的类为jstring
例如 Ljava/lang/String; String jstring
例如 Ljava/net/Socket; Socket jobject

如果JAVA函数位于一个嵌入类，则用$作为类名间的分隔符。
例如 "(Ljava/lang/String;Landroid/os/FileUtils$FileStatus;)Z"
```