package com.trentontelge.gamemanagerfx.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class DBFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String ext = getExtension(f);
        if (ext != null){
            return ext.equals("db");
        } else {return false;}
    }

    @Override
    public String getDescription() {
        return null;
    }
    private static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
