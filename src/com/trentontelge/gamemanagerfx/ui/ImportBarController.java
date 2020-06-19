package com.trentontelge.gamemanagerfx.ui;

import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.database.DatabaseHelper;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class ImportBarController implements Initializable {
    public ProgressBar importBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                DatabaseHelper.importTables(Main.param, importBar::setProgress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
