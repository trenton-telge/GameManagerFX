package com.trentontelge.gamemanagerfx.prototypes;

import javafx.scene.image.Image;

import java.sql.Date;

public class Game {
    private int id, circleid, size;
    private String RJCode, title, path, category, tags, comments, language;
    private boolean rating, isRPGMaker;
    private java.sql.Date releaseDate, addedDate;

    public Game() {
    }

    public Game(int id, int circleid, int size, String RJCode, String title, String path, String category, String tags, String comments, String language, boolean rating, boolean isRPGMaker, Date releaseDate, Date addedDate) {
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

    public Game(int circleid, int size, String RJCode, String title, String path, String category, String tags, String comments, String language, boolean rating, boolean isRPGMaker, Date releaseDate, Date addedDate) {
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

    public Image getVisibleImage() {
        //TODO query images table by game ID and load to Image object
        return null;
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

    public boolean getRating() {
        return rating;
    }

    public void setRating(boolean rating) {
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
}
