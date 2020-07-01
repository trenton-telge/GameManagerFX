package com.trentontelge.gamemanagerfx.prototypes;

import com.trentontelge.gamemanagerfx.database.DatabaseHelper;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Date;
import java.util.Vector;

public class Game {
    private int id = 0, circleid = 0, size = 0, rating=0;
    private String RJCode, title, path, category, tags, comments, language;
    private boolean isRPGMaker;
    private java.sql.Date releaseDate, addedDate;

    public Game() {
    }

    public Game(int id, int circleid, int size, String RJCode, String title, String path, String category, String tags, String comments, String language, int rating, boolean isRPGMaker, Date releaseDate, Date addedDate) {
        this.id = id;
        this.circleid = circleid;
        this.size = size;
        this.RJCode = RJCode;
        this.title = title;
        this.path = path;
        this.category = category;
        this.tags = tags;
        this.comments = comments;
        this.language = language;
        this.rating = rating;
        this.isRPGMaker = isRPGMaker;
        this.releaseDate = releaseDate;
        this.addedDate = addedDate;
    }

    public Game(int circleid, int size, String RJCode, String title, String path, String category, String tags, String comments, String language, int rating, boolean isRPGMaker, Date releaseDate, Date addedDate) {
        this.circleid = circleid;
        this.size = size;
        this.RJCode = RJCode;
        this.title = title;
        this.path = path;
        this.category = category;
        this.tags = tags;
        this.comments = comments;
        this.language = language;
        this.rating = rating;
        this.isRPGMaker = isRPGMaker;
        this.releaseDate = releaseDate;
        this.addedDate = addedDate;
    }

    public Image getListImage() {
        return DatabaseHelper.getListImage(this.id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCircleid() {
        return circleid;
    }

    public void setCircleid(int circleid) {
        this.circleid = circleid;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getRJCode() {
        return RJCode;
    }

    public void setRJCode(String RJCode) {
        this.RJCode = RJCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isRPGMaker() {
        return isRPGMaker;
    }

    public void setRPGMaker(boolean RPGMaker) {
        isRPGMaker = RPGMaker;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getCircleName() {
        return DatabaseHelper.getCircle(circleid).getName();
    }

    public Image getRatingImage() {
        switch (getRating()){
            case 1: {
                return new Image("img\\1.png");
            }
            case 2: {
                return new Image("img\\2.png");
            }
            case 3: {
                return new Image("img\\3.png");
            }
            case 4: {
                return new Image("img\\4.png");
            }
            case 5: {
                return new Image("img\\5.png");
            }
            default: {
                return new Image("img\\0.png");
            }
        }
    }

    public Vector<Image> getImages(){
        return DatabaseHelper.getGameImages(this.id);
    }
}
