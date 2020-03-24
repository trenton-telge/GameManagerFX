package com.trentontelge.gamemanagerfx;

import com.trentontelge.gamemanagerfx.database.DatabaseHelper;
import com.trentontelge.gamemanagerfx.database.DatafileHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.trentontelge.gamemanagerfx.database.DatabaseHelper.cleanTable;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        cleanTable(DatabaseHelper.KnownTable.GAMES.getSql());
        cleanTable(DatabaseHelper.KnownTable.CIRCLES.getSql());
        cleanTable(DatabaseHelper.KnownTable.IMAGES.getSql());
            DatabaseHelper.createTable(DatabaseHelper.KnownTable.GAMES);
            DatabaseHelper.createTable(DatabaseHelper.KnownTable.CIRCLES);
            DatabaseHelper.createTable(DatabaseHelper.KnownTable.IMAGES);
        if (DatafileHelper.isFirstRun()){
            DatafileHelper.getParent().mkdirs();
        }
        Parent root = FXMLLoader.load(getClass().getResource("ui/mainlayout.fxml"));
        primaryStage.setTitle("GameManagerFX");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.show();
        DatabaseHelper.writeDB();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
