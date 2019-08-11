package com.nconnect.teacher.model.announcements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;


public class Announcement {


    @SerializedName(Constants.ANNOUNCEMENT_POSTED_BY)
    @Expose
    private String postedBy;
    @SerializedName(Constants.ANNOUNCEMENT_DESCRIPTION)
    @Expose
    private String desc;
    @SerializedName(Constants.ANNOUNCEMENT_TITLE)
    @Expose
    private String title;
    @SerializedName(Constants.ANNOUNCEMENT_POSTED_BY_DESIGNATION)
    @Expose
    private String postedByDesig;
    @SerializedName(Constants.ANNOUNCEMENT_DATE)
    @Expose
    private String date;
    @SerializedName(Constants.ANNOUNCEMENT_ID)
    @Expose
    private int announcementId;

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostedByDesig() {
        return postedByDesig;
    }

    public void setPostedByDesig(String postedByDesig) {
        this.postedByDesig = postedByDesig;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }
}
