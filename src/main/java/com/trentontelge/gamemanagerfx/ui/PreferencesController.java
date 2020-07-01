package com.trentontelge.gamemanagerfx.ui;

import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.database.DatafileHelper;
import com.trentontelge.gamemanagerfx.prototypes.Preferences;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PreferencesController implements Initializable {
    public TextField gamesHomeLocField;
    public Button setGamesHomeButton;
    public CheckBox moveGamesCheckbox;
    public Button saveButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gamesHomeLocField.setText(Main.prefs.getLibraryHome());
        moveGamesCheckbox.setSelected(Main.prefs.isMoveGames());
        setGamesHomeButton.setOnAction(e->{
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(Main.prefs.getLibraryHome()));
            chooser.setDialogTitle("Choose Game Directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                gamesHomeLocField.setText(chooser.getSelectedFile().toString());
            }
        });
        saveButton.setOnAction(e->{
            DatafileHelper.setPrefs(new Preferences(gamesHomeLocField.getText(), moveGamesCheckbox.isSelected()));
            Main.prefsStage.close();
        });
    }
}
