package com.nconnect.teacher.model;

public class DashBoardModel {

    int id;
    String dashBoardName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDashBoardName() {
        return dashBoardName;
    }

    public void setDashBoardName(String dashBoardName) {
        this.dashBoardName = dashBoardName;
    }

    public int getDrawableImage() {
        return drawableImage;
    }

    public DashBoardModel(int id, String dashBoardName, int drawableImage) {
        this.id = id;
        this.dashBoardName = dashBoardName;
        this.drawableImage = drawableImage;
    }

    public void setDrawableImage(int drawableImage) {
        this.drawableImage = drawableImage;
    }

    int drawableImage;
}
