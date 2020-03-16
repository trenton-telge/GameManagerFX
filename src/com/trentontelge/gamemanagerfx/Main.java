package com.trentontelge.gamemanagerfx;

import com.trentontelge.gamemanagerfx.database.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.trentontelge.gamemanagerfx.database.DatafileHelper.isFirstRun;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        if (isFirstRun()){
            DatabaseHelper.createTable(DatabaseHelper.KnownTable.GAMES);
            DatabaseHelper.createTable(DatabaseHelper.KnownTable.CIRCLES);
            DatabaseHelper.createTable(DatabaseHelper.KnownTable.IMAGES);
        }
        Parent root = FXMLLoader.load(getClass().getResource("ui/mainlayout.fxml"));
        primaryStage.setTitle("GameManagerFX");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
