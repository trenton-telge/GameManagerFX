package com.trentontelge.gamemanagerfx.ui;

import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.util.DBFileFilter;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public MenuItem closeMenu;
    public MenuItem addGameMenu;
    public MenuItem importDBMenu;
    public MenuItem exportCSVMenu;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        importDBMenu.setOnAction(e -> {
            JFileChooser dbChooser = new JFileChooser();
            dbChooser.addChoosableFileFilter(new DBFileFilter());
            dbChooser.setAcceptAllFileFilterUsed(false);
            int returnval = dbChooser.showDialog(new JFrame("Import DB File"), "Import DB File");
            if (returnval == JFileChooser.APPROVE_OPTION) {
                File db = dbChooser.getSelectedFile();
                Main.param = db;
                Main.showImportBar();
            }
        });
        addGameMenu.setOnAction(e -> {
            //TODO open add game modal
        });
        exportCSVMenu.setOnAction( e -> {
            //TODO export games table to csv
        });
    }
}
