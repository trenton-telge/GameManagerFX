package com.trentontelge.gamemanagerfx.ui;

import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.database.DatabaseHelper;
import com.trentontelge.gamemanagerfx.database.DatafileHelper;
import com.trentontelge.gamemanagerfx.prototypes.Game;
import com.trentontelge.gamemanagerfx.util.DBFileFilter;
import com.trentontelge.gamemanagerfx.util.GameFileFilter;
import com.trentontelge.gamemanagerfx.util.OSChecker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public MenuItem closeMenu;
    public MenuItem addGameMenu;
    public MenuItem importDBMenu;
    public MenuItem exportJSONMenu;
    public MenuItem preferencesMenu;
    public TableView<Game> gameTable;
    public TableColumn<Game, Image> iconCol;
    public TableColumn<Game, String> titleCol;
    public TableColumn<Game, String> circleCol;
    public TableColumn<Game, Image> ratingCol;
    public TableColumn<Game, String> sizeCol;
    public TableColumn<Game, String> tagsCol;
    public Label rjCodeDisplay;
    public Label titleDisplay;
    public Label circleDisplay;
    public Label pathDisplay;
    public Label sizeDisplay;
    public ImageView ratingDisplay;
    public Label releaseDateDisplay;
    public Label tagsDisplay;
    public Button editGameButton;
    public AnchorPane imageScrollpane;
    public TextField rjCodeField;
    public TextField titleField;
    public ComboBox circleSelector;
    public Button addCircleButton;
    public TextField pathField;
    public Button pathBrowseButton;
    public TextField sizeField;
    public Button sizeCalculateButton;
    public ChoiceBox ratingSelector;
    public DatePicker releaseDateSelector;
    public TextArea tagField;
    public Button saveButton;
    private Game previousSelection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewMode();
        gameTable.setRowFactory( tv -> {
            TableRow<Game> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();
            MenuItem runItem = new MenuItem("Run game");
            runItem.setOnAction(e-> runGame(gameTable.getSelectionModel().getSelectedItem()));
            MenuItem openInFilesystemItem = new MenuItem("Open in filesystem");
            switch (OSChecker.getOperatingSystemType()){
                case WINDOWS ->  openInFilesystemItem = new MenuItem("Open in Explorer");
                case MAC_OS ->  openInFilesystemItem = new MenuItem("Open in Finder");
            }
            openInFilesystemItem.setOnAction(e->{
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(new File(new File(gameTable.getSelectionModel().getSelectedItem().getPath()).getParent() + System.getProperty("file.separator")));
                    System.out.println("Opened " + new File(new File(gameTable.getSelectionModel().getSelectedItem().getPath()).getParent()) + " in filesystem");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            MenuItem deleteFromList = new MenuItem("Remove from list");
            deleteFromList.setOnAction(e->{
                String name = gameTable.getSelectionModel().getSelectedItem().getTitle();
                DatabaseHelper.deleteGame(gameTable.getSelectionModel().getSelectedItem().getId());
                refreshData();
                System.out.println("Deleted " + name + " from list");
                gameTable.getSelectionModel().select(1);
            });
            MenuItem deleteItem = new MenuItem("Delete from disk");
            deleteItem.setOnAction(e->{
                String name = gameTable.getSelectionModel().getSelectedItem().getTitle();
                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + gameTable.getSelectionModel().getSelectedItem().getTitle() + " permanently from the disk?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION){
                    DatabaseHelper.deleteGame(gameTable.getSelectionModel().getSelectedItem().getId());
                    refreshData();
                    new File(new File(gameTable.getSelectionModel().getSelectedItem().getPath()).getParent()).delete();
                    System.out.println("Deleted " + name + " from disk");
                    gameTable.getSelectionModel().select(1);
                }
            });
            rowMenu.getItems().addAll(runItem, openInFilesystemItem, deleteFromList, deleteItem);
            row.setContextMenu(rowMenu);
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    checkAndChangeDetails();
                }
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    runGame(gameTable.getSelectionModel().getSelectedItem());
                }
            });
            return row ;
        });
        iconCol.setCellFactory(param -> {
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(20);
            imageview.setFitWidth(20);
            TableCell<Game, Image> cell = new TableCell<>() {
                public void updateItem(Image item, boolean empty) {
                    if (item != null) {
                        imageview.setImage(item);
                    }
                }
            };
            cell.setGraphic(imageview);
            return cell;
        });
        iconCol.setCellValueFactory(new PropertyValueFactory<>("listImage"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        circleCol.setCellValueFactory(new PropertyValueFactory<>("circleName"));
        ratingCol.setCellFactory(param -> {
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(20);
            imageview.setFitWidth(20);
            TableCell<Game, Image> cell = new TableCell<>() {
                public void updateItem(Image item, boolean empty) {
                    if (item != null) {
                        imageview.setImage(item);
                    }
                }
            };
            cell.setGraphic(imageview);
            return cell;
        });
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("ratingImage"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("tags"));

        importDBMenu.setOnAction(e -> {
            JFileChooser dbChooser = new JFileChooser();
            dbChooser.addChoosableFileFilter(new DBFileFilter());
            dbChooser.setAcceptAllFileFilterUsed(false);
            int returnval = dbChooser.showDialog(new JFrame("Import DB File"), "Import DB File");
            if (returnval == JFileChooser.APPROVE_OPTION) {
                Main.param = dbChooser.getSelectedFile();
                Runnable updater = this::refreshData;
                Main.showImportBar(updater);
            }
        });
        addGameMenu.setOnAction(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new GameFileFilter());
            int returnVal = fileChooser.showDialog(new JFrame("Add Game"), "Add Game");
            if (returnVal == JFileChooser.APPROVE_OPTION){
                int id = DatabaseHelper.writeGame(new Game(0, 0, "", fileChooser.getSelectedFile().getParent().substring(fileChooser.getSelectedFile().getParent().lastIndexOf("\\")+1), fileChooser.getSelectedFile().toString(), "", "", "", "", 0, false, java.sql.Date.valueOf("2001-01-01"), java.sql.Date.valueOf(LocalDate.now())));
                refreshData();
                for (Game g : gameTable.getItems()){
                    if (g.getId() == id){
                        gameTable.getSelectionModel().select(gameTable.getItems().indexOf(g));
                    }
                }
                checkAndChangeDetails();
                editGame();
            }
        });
        saveButton.setOnAction(e -> {
            saveEdit();
        });
        editGameButton.setOnAction(e -> {
            editGame();
        });
        exportJSONMenu.setOnAction( e -> DatafileHelper.saveDBAsJSON());
        preferencesMenu.setOnAction( e-> Main.showPrefs());
        refreshData();
    }
    protected void refreshData(){
        System.out.println("Refreshing data...");
        ObservableList<Game> data = FXCollections.observableList(DatabaseHelper.getAllGames());
        gameTable.setItems(data);
        System.out.println("Data refreshed.");
    }
    protected void resetLabels(){
        rjCodeDisplay.setText("     ");
        titleDisplay.setText("     ");
        circleDisplay.setText("     ");
        pathDisplay.setText("     ");
        sizeDisplay.setText("     ");
        ratingDisplay.setImage(new Image("img\\0.png"));
        releaseDateDisplay.setText("     ");
        tagsDisplay.setText("     ");
    }
    protected void checkAndChangeDetails(){
        if (!gameTable.getSelectionModel().getSelectedItem().equals(previousSelection)){
            viewMode();
            previousSelection = gameTable.getSelectionModel().getSelectedItem();
            resetLabels();
            rjCodeDisplay.setText(previousSelection.getRJCode());
            titleDisplay.setText(previousSelection.getTitle());
            circleDisplay.setText(previousSelection.getCircleName());
            pathDisplay.setText(previousSelection.getPath());
            sizeDisplay.setText(previousSelection.getSize() + " Mb");
            switch (previousSelection.getRating()){
                case 1: {
                    ratingDisplay.setImage(new Image("img\\1.png"));
                    break;
                }
                case 2: {
                    ratingDisplay.setImage(new Image("img\\2.png"));
                    break;
                }
                case 3: {
                    ratingDisplay.setImage(new Image("img\\3.png"));
                    break;
                }
                case 4: {
                    ratingDisplay.setImage(new Image("img\\4.png"));
                    break;
                }
                case 5: {
                    ratingDisplay.setImage(new Image("img\\5.png"));
                    break;
                }
                default: {
                    ratingDisplay.setImage(new Image("img\\0.png"));
                    break;
                }
            }
            releaseDateDisplay.setText(previousSelection.getReleaseDate().toString());
            tagsDisplay.setText(previousSelection.getTags());
            imageScrollpane.getChildren().clear();
            int x = 0;
            for (Image i : previousSelection.getImages()){
                ImageView iv = new ImageView(i);
                iv.setPreserveRatio(true);
                iv.setFitHeight(200);
                iv.setFitWidth(300);
                iv.setY(x);
                imageScrollpane.setPrefHeight(x);
                imageScrollpane.getChildren().add(iv);
                x+=iv.getFitHeight()+5;
            }
        }
    }
    protected void runGame(Game g){
        //TODO add run support for non-Windows and HTML games, and add dynamic pathing
        if (new File(g.getPath()).exists()) {
            System.out.println("Attempt to run " + g.getPath());
            if (g.getPath().toLowerCase().endsWith(".exe")) {
                try {
                    Runtime.getRuntime().exec(g.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (g.getPath().toLowerCase().endsWith(".app")) {
                try {
                    Desktop.getDesktop().open(new File(g.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (g.getPath().toLowerCase().endsWith(".html")) {
                try {
                    Desktop.getDesktop().open(new File(g.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (new File(Main.prefs.getLibraryHome() + System.getProperty("file.separator") + g.getPath()).exists()) {
            System.out.println("Attempt to run " + g.getPath());
            if (g.getPath().toLowerCase().endsWith(".exe")) {
                try {
                    Runtime.getRuntime().exec(Main.prefs.getLibraryHome() + System.getProperty("file.separator") + g.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (g.getPath().toLowerCase().endsWith(".app")) {
                try {
                    Desktop.getDesktop().open(new File(Main.prefs.getLibraryHome() + System.getProperty("file.separator") + g.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (g.getPath().toLowerCase().endsWith(".html")) {
                try {
                    Desktop.getDesktop().open(new File(Main.prefs.getLibraryHome() + System.getProperty("file.separator") + g.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //TODO show error dialog
            System.out.println("Game executable not found");
        }
    }
    protected void editGame(){
        rjCodeDisplay.setVisible(false);
        rjCodeDisplay.setMaxWidth(0);
        titleDisplay.setVisible(false);
        titleDisplay.setMaxWidth(0);
        circleDisplay.setVisible(false);
        circleDisplay.setMaxWidth(0);
        pathDisplay.setVisible(false);
        pathDisplay.setMaxWidth(0);
        sizeDisplay.setVisible(false);
        sizeDisplay.setMaxWidth(0);
        ratingDisplay.setVisible(false);
        releaseDateDisplay.setVisible(false);
        releaseDateDisplay.setMaxWidth(0);
        tagsDisplay.setVisible(false);
        tagsDisplay.setMaxWidth(0);
        editGameButton.setVisible(false);
        editGameButton.setMaxWidth(0);
        //TODO populate
        rjCodeField.setVisible(true);
        titleField.setVisible(true);
        circleSelector.setVisible(true);
        addCircleButton.setVisible(true);
        pathField.setVisible(true);
        pathBrowseButton.setVisible(true);
        sizeField.setVisible(true);
        sizeCalculateButton.setVisible(true);
        ratingSelector.setVisible(true);
        releaseDateSelector.setVisible(true);
        tagField.setVisible(true);
        saveButton.setVisible(true);
    }
    protected void saveEdit(){
        //TODO send to DB
        checkAndChangeDetails();
        viewMode();
    }
    protected void viewMode(){
        rjCodeField.setVisible(false);
        titleField.setVisible(false);
        circleSelector.setVisible(false);
        addCircleButton.setVisible(false);
        pathField.setVisible(false);
        pathBrowseButton.setVisible(false);
        sizeField.setVisible(false);
        sizeCalculateButton.setVisible(false);
        ratingSelector.setVisible(false);
        releaseDateSelector.setVisible(false);
        tagField.setVisible(false);
        saveButton.setVisible(false);
        rjCodeDisplay.setVisible(true);
        rjCodeDisplay.setMaxWidth(250);
        rjCodeDisplay.setPrefWidth(250);
        titleDisplay.setVisible(true);
        titleDisplay.setMaxWidth(250);
        titleDisplay.setPrefWidth(250);
        circleDisplay.setVisible(true);
        circleDisplay.setMaxWidth(250);
        circleDisplay.setPrefWidth(250);
        pathDisplay.setVisible(true);
        pathDisplay.setMaxWidth(250);
        pathDisplay.setPrefWidth(250);
        sizeDisplay.setVisible(true);
        sizeDisplay.setMaxWidth(250);
        sizeDisplay.setPrefWidth(250);
        ratingDisplay.setVisible(true);
        releaseDateDisplay.setVisible(true);
        releaseDateDisplay.setMaxWidth(250);
        releaseDateDisplay.setPrefWidth(250);
        tagsDisplay.setVisible(true);
        tagsDisplay.setMaxWidth(250);
        tagsDisplay.setPrefWidth(250);
        editGameButton.setVisible(true);
    }
}
