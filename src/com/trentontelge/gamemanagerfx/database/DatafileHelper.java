package com.trentontelge.gamemanagerfx.database;

import com.trentontelge.gamemanagerfx.Main;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DatafileHelper {
    public static boolean isFirstRun(){
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        decodedPath = decodedPath.substring(0, path.lastIndexOf("/") + 1) + "GAMES.dat";
        return !DatabaseHelper.tableExists("GAMES") && !(new File(decodedPath).exists());
    }
}
