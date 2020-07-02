package com.trentontelge.gamemanagerfx.prototypes;

@SuppressWarnings("unused")
public class Circle {
    int circleID;
    String rgCode, name;

    public Circle() {

    }

    public Circle(int circleID, String rgCode, String name) {
        this.circleID = circleID;
        this.rgCode = rgCode;
        this.name = name;
    }

    public Circle(String rgCode, String name) {
        this.rgCode = rgCode;
        this.name = name;
    }

    public int getCircleID() {
        return circleID;
    }

    public void setCircleID(int circleID) {
        this.circleID = circleID;
    }

    public String getRgCode() {
        return rgCode;
    }

    public void setRgCode(String rgCode) {
        this.rgCode = rgCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
