package com.slack.luaproject.androidlua.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by slack on 2020/5/27 下午3:53.
 */
public class ZipFileUtils {
    public static String getFileContentFromZipFile(ZipFile zipFile, String targetFile) {
        InputStream ins = null;
        try {
            ZipEntry ze = zipFile.getEntry(targetFile);
            if (ze != null) {
                ins = zipFile.getInputStream(ze);
                return FileUtils.readInputStream(ins);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static String getFilesContentFromZipFile(ZipFile zipFile, String[] targetFiles) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String file : targetFiles) {
            String content = getFileContentFromZipFile(zipFile, file);
            stringBuilder.append(content).append('\n');
        }
        return stringBuilder.toString();
    }
}
