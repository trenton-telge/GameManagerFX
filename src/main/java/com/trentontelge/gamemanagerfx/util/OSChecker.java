package com.trentontelge.gamemanagerfx.util;

import java.util.Locale;

public class OSChecker {
    public enum OSType {
        WINDOWS, MAC_OS, LINUX, OTHER
    };
    protected static OSType detectedOS;
    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                detectedOS = OSType.MAC_OS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.WINDOWS;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.LINUX;
            } else {
                detectedOS = OSType.OTHER;
            }
        }
        return detectedOS;
    }
}
