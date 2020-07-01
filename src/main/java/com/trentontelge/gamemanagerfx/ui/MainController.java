package com.trentontelge.gamemanagerfx.ui;

import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.database.DatabaseHelper;
import com.trentontelge.gamemanagerfx.prototypes.Game;
import com.trentontelge.gamemanagerfx.util.DBFileFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public MenuItem closeMenu;
    public MenuItem addGameMenu;
    public MenuItem importDBMenu;
    public MenuItem exportCSVMenu;
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
    private Game previousSelection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameTable.setRowFactory( tv -> {
            TableRow<Game> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    if (!gameTable.getSelectionModel().getSelectedItem().equals(previousSelection)){
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
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    if (new File(gameTable.getSelectionModel().getSelectedItem().getPath()).exists()) {
                        System.out.println("Attempt to run " + gameTable.getSelectionModel().getSelectedItem().getPath());
                        try {
                            Runtime.getRuntime().exec(gameTable.getSelectionModel().getSelectedItem().getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //TODO show error dialog
                        System.out.println("Game executable not found");
                    }
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
            //TODO open add game modal
        });
        exportCSVMenu.setOnAction( e -> {
            //TODO export games table to csv
        });
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
}
