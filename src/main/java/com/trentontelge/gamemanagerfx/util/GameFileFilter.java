package com.trentontelge.gamemanagerfx.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class GameFileFilter extends FileFilter {
    public String getDescription() {
        return "Games (.exe, .html)";
    }
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        } else {
            return (f.getName().toLowerCase().endsWith(".exe") || f.getName().toLowerCase().endsWith(".html"));
        }
    }
}
