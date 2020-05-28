package com.slack.luaproject.androidlua.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by slack on 2020/5/27 下午3:49.
 */
public class FileUtils {

    public static String readInputStream(InputStream ins) {
        try {
            InputStreamReader isr = new InputStreamReader(ins);

            char[] buffer = new char[64*1024];
            int c;
            StringBuilder result = new StringBuilder();
            while((c = isr.read(buffer)) > 0){
                result.append(buffer, 0, c);
            }
            isr.close();
            ins.close();
            return result.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String readFile(File file) {
        try {
            return readInputStream(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static boolean writeFile(File to, InputStream is) {
        try (FileOutputStream os = new FileOutputStream(to)) {
            int length;
            byte[] buffer = new byte[64*1024];
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            is.close();
        } catch(Exception e) {
            e.printStackTrace();

            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        return true;
    }
}
