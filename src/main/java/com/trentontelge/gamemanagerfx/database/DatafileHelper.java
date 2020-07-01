package com.trentontelge.gamemanagerfx.database;

import com.google.gson.Gson;
import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.prototypes.Preferences;

import java.io.*;

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
    public static void setPrefs(Preferences prefs){
        File prefsFile = new File(getParent().toString() + System.getProperty("file.separator") + "prefs.json");
        if (prefsFile.exists()){prefsFile.delete();}
        Gson gson = new Gson();
        try {
            FileWriter writer = new FileWriter(prefsFile);
            gson.toJson(prefs, writer);
            writer.flush();
            writer.close();
            Main.prefs = prefs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saved preferences");
    }
    public static Preferences getPrefs(){
        File prefsFile = new File(getParent().toString() + System.getProperty("file.separator") + "prefs.json");
        if (prefsFile.exists()){
            Gson gson = new Gson();
            Preferences p;
            try {
                p = gson.fromJson(new FileReader(prefsFile), Preferences.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                p =  null;
            }
            System.out.println("Read preferences from file");
            return p;
        } else {
            System.out.println("No preferencess file detected");
            setPrefs(new Preferences(System.getProperty("user.home") + System.getProperty("file.separator") + "My Games" + System.getProperty("file.separator"), false));
            return getPrefs();
        }
    }
}
