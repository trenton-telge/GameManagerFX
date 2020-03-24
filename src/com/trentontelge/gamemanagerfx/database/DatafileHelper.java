package com.trentontelge.gamemanagerfx.database;

import java.io.File;

public class DatafileHelper {
    public static File getParent(){
        return new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".GameManagerFX" + System.getProperty("file.separator"));
    }
    public static boolean isFirstRun(){
        return !getFile(DatabaseHelper.KnownTable.GAMES).exists();
    }
    public static File getFile(DatabaseHelper.KnownTable table){
        String decodedPath;
        switch (table){
            case GAMES:{
                decodedPath = getParent().toString() + System.getProperty("file.separator") + "games.dat";
                break;
            }
            case CIRCLES:{
                decodedPath = getParent().toString() + System.getProperty("file.separator") + "circles.dat";
                break;
            }
            case IMAGES:{
                decodedPath = getParent().toString() + System.getProperty("file.separator") + "images.dat";
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + table);
            }
        }
        return new File(decodedPath);
    }
}
