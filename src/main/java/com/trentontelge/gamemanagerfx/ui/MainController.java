package com.trentontelge.gamemanagerfx.ui;

import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.database.DatabaseHelper;
import com.trentontelge.gamemanagerfx.database.DatafileHelper;
import com.trentontelge.gamemanagerfx.prototypes.Circle;
import com.trentontelge.gamemanagerfx.prototypes.Game;
import com.trentontelge.gamemanagerfx.util.DBFileFilter;
import com.trentontelge.gamemanagerfx.util.GameFileFilter;
import com.trentontelge.gamemanagerfx.util.OSChecker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
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
    public ComboBox<Circle> circleSelector;
    public Button addCircleButton;
    public TextField pathField;
    public Button pathBrowseButton;
    public TextField sizeField;
    public Button sizeCalculateButton;
    public ComboBox<String> ratingSelector;
    public DatePicker releaseDateSelector;
    public TextArea tagField;
    public Button saveButton;
    public VBox dataPane;
    public VBox editPane;
    public Label statusText;
    private Game previousSelection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataPane.managedProperty().bind(dataPane.visibleProperty());
        editPane.managedProperty().bind(editPane.visibleProperty());
        ObservableList<Circle> circleList = FXCollections.observableList(DatabaseHelper.getAllCircles());
        circleSelector.setItems(circleList);
        circleSelector.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Circle> call(ListView<Circle> p) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Circle t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        ratingSelector.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> p) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon;
                            try {
                                int iconNumber = this.getIndex();
                                String iconPath = "img\\" + iconNumber + ".png";
                                icon = new Image(iconPath);
                            } catch (NullPointerException ex) {
                                String iconPath = "img\\0.png";
                                icon = new Image(iconPath);
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(30);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        });
        final String[] ratings = {"X", "Unplayable", "Bad", "Decent", "Good", "Great"};
        ratingSelector.setItems(FXCollections.observableList(Arrays.asList(ratings)));
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
                    checkAndChangeDetails(row.getItem());
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
                        checkAndChangeDetails(gameTable.getSelectionModel().getSelectedItem());
                    }
                }
                editGame();
            }
        });
        saveButton.setOnAction(e -> saveEdit());
        editGameButton.setOnAction(e -> editGame());
        addCircleButton.setOnAction(e -> {
            //TODO add circle interface
        });
        sizeCalculateButton.setOnAction(e -> {
            //TODO calculate size of containing folder
        });
        exportJSONMenu.setOnAction( e -> DatafileHelper.saveDBAsJSON());
        preferencesMenu.setOnAction( e-> Main.showPrefs());
        refreshData();
        gameTable.getSelectionModel().select(0);
        if (gameTable.getSelectionModel().getSelectedItem() != null) {
            checkAndChangeDetails(gameTable.getSelectionModel().getSelectedItem());
        }
    }
    protected void refreshData(){
        System.out.println("Refreshing data...");
        ObservableList<Game> data = FXCollections.observableList(DatabaseHelper.getAllGames());
        gameTable.setItems(data);
        gameTable.getSortOrder().add(titleCol);
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
    protected void checkAndChangeDetails(Game g){
        if (!g.equals(previousSelection)){
            //viewMode();
            previousSelection = g;
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
            setStatusText("Attempt to run " + g.getPath());
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
            setStatusText("Attempt to run " + g.getPath());
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
            setStatusText("Game executable not found.");
            System.out.println("Game executable not found");
        }
    }
    protected void editGame(){
        dataPane.setMaxHeight(0);
        dataPane.setPrefHeight(0);
        dataPane.setVisible(false);
        editPane.setVisible(true);
        editPane.setPrefHeight(200);
        editPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
        saveButton.setVisible(true);
        editGameButton.setVisible(false);
        rjCodeField.setText(previousSelection.getRJCode());
        titleField.setText(previousSelection.getTitle());
        circleSelector.getSelectionModel().select(circleSelector.getItems().indexOf(DatabaseHelper.getCircle(previousSelection.getCircleid())));
        pathField.setText(previousSelection.getPath());
        sizeField.setText(String.valueOf(previousSelection.getSize()));
        releaseDateSelector.setValue(previousSelection.getReleaseDate().toLocalDate());
        tagField.setText(previousSelection.getTags());
        ratingSelector.getSelectionModel().select(previousSelection.getRating());
        int circleIndex = -1;
        System.out.println(previousSelection.getCircleName());
        for (Circle c : circleSelector.getItems()) {
            if (c.getName().equals(previousSelection.getCircleName())) {
                circleIndex = circleSelector.getItems().indexOf(c);
                System.out.println(circleIndex);
            }
        }
        if (circleIndex >= 0) {
            circleSelector.getSelectionModel().select(circleIndex);
        }
    }
    protected void saveEdit(){
        if (!previousSelection.getTitle().equals(titleField.getText())) {
            DatabaseHelper.setTitle(previousSelection.getId(), titleField.getText());
        }
        if (!previousSelection.getPath().equals(pathField.getText())) {
            DatabaseHelper.setPath(previousSelection.getId(), pathField.getText());
        }
        if (!previousSelection.getCircleName().equals(circleSelector.getSelectionModel().getSelectedItem().getName())) {
            DatabaseHelper.setCircle(previousSelection.getId(), circleSelector.getSelectionModel().getSelectedItem().getName());
        }
        if (previousSelection.getRating() != ratingSelector.getSelectionModel().getSelectedIndex()) {
            DatabaseHelper.setRating(previousSelection.getId(), ratingSelector.getSelectionModel().getSelectedIndex());
        }
        //TODO size, rjcode, releasedate, tags
        DatabaseHelper.writeDBToFile();
        checkAndChangeDetails(previousSelection);
        viewMode();
    }
    protected void viewMode(){
        editPane.setPrefHeight(0);
        editPane.setMaxHeight(0);
        editPane.setVisible(false);
        refreshData();
        dataPane.setVisible(true);
        dataPane.setPrefHeight(200);
        dataPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
        saveButton.setVisible(false);
        editGameButton.setVisible(true);
    }
    protected void setStatusText(String text) {
        this.statusText.setText(text);
    }
}
