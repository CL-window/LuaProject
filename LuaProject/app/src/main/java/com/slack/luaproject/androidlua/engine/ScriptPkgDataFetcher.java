package com.slack.luaproject.androidlua.engine;

import com.slack.luaproject.androidlua.utils.ZipFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Created by slack on 2020/5/27 下午4:00.
 */
public class ScriptPkgDataFetcher {

    private final File scriptPkg;
    public ScriptPkgDataFetcher(File scriptPkg) {
        this.scriptPkg = scriptPkg;
    }

    public String getContentByEntryName(String entryName) {
        try {
            return ZipFileUtils.getFileContentFromZipFile(new ZipFile(scriptPkg), entryName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
