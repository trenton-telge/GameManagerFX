package com.trentontelge.gamemanagerfx;

import com.trentontelge.gamemanagerfx.database.DatabaseHelper;
import com.trentontelge.gamemanagerfx.database.DatafileHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.trentontelge.gamemanagerfx.database.DatabaseHelper.cleanTable;

public class Main extends Application {

    public static File param;
    public static final Stage importBarStage = new Stage();
    public static Runnable callback = () -> {};

    @Override
    public void start(Stage primaryStage) throws Exception{
        cleanTable(DatabaseHelper.KnownTable.GAMES.getSql());
        cleanTable(DatabaseHelper.KnownTable.CIRCLES.getSql());
        cleanTable(DatabaseHelper.KnownTable.IMAGES.getSql());
        if (DatafileHelper.isFirstRun()){
            DatafileHelper.getParent().mkdirs();
        }
        DatabaseHelper.createTable(DatabaseHelper.KnownTable.GAMES);
        DatabaseHelper.createTable(DatabaseHelper.KnownTable.CIRCLES);
        DatabaseHelper.createTable(DatabaseHelper.KnownTable.IMAGES);
        DatabaseHelper.readDBFromFile();
        Parent root = FXMLLoader.load(getClass().getResource("ui/mainlayout.fxml"));
        primaryStage.setTitle("GameManagerFX");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.show();
        DatabaseHelper.writeDBToFile();
    }

    public static void showImportBar(Runnable updater){
        Parent root;
        try {
            callback = updater;
            root = FXMLLoader.load(Main.class.getResource("ui/importbarlayout.fxml"));
            importBarStage.setTitle("Import DB File");
            importBarStage.setScene(new Scene(root, 500,150));
            importBarStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
