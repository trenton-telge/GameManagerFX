package com.trentontelge.gamemanagerfx.prototypes;

@SuppressWarnings("unused")
public class Image {
    private int imageid, gameid;
    private boolean islistimage, iscoverimage;
    private String imagepath;

    public Image(){}

    public Image(int imageid, int gameid, boolean islistimage, boolean iscoverimage, String imagepath) {
        this.imageid = imageid;
        this.gameid = gameid;
        this.islistimage = islistimage;
        this.iscoverimage = iscoverimage;
        this.imagepath = imagepath;
    }

    public Image(int gameid, boolean islistimage, boolean iscoverimage, String imagepath) {
        this.gameid = gameid;
        this.islistimage = islistimage;
        this.iscoverimage = iscoverimage;
        this.imagepath = imagepath;
    }

    public int getImageid() {
        return imageid;
    }

    public void setImageid(int imageid) {
        this.imageid = imageid;
    }

    public int getGameid() {
        return gameid;
    }

    public void setGameid(int gameid) {
        this.gameid = gameid;
    }

    public boolean isIslistimage() {
        return islistimage;
    }

    public void setIslistimage(boolean islistimage) {
        this.islistimage = islistimage;
    }

    public boolean isIscoverimage() {
        return iscoverimage;
    }

    public void setIscoverimage(boolean iscoverimage) {
        this.iscoverimage = iscoverimage;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
