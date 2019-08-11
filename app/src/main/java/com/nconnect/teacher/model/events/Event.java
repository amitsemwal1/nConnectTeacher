package com.nconnect.teacher.model.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;

public class Event {

    @SerializedName(Constants.ORGANIZER)
    @Expose
    private String organizer;
    @SerializedName(Constants.EVENT_ID)
    @Expose
    private String eventId;
    @SerializedName(Constants.EVENT_TITLE)
    @Expose
    private String title;
    @SerializedName(Constants.EVENTS_SHORT_DESCRIPTION)
    @Expose
    private String shortDesc;
    @SerializedName(Constants.LOCATION)
    @Expose
    private String location;
    @SerializedName(Constants.EVENTS_DATE_TO)
    @Expose
    private String dateTo;
    @SerializedName(Constants.EVENTS_DATE_FROM)
    @Expose
    private String dateFrom;

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    @SerializedName(Constants.EVENTS_IMAGE)
    @Expose
    private String image;
    @SerializedName(Constants.EVENTS_TIME)
    @Expose
    private String time;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("dp")
    @Expose
    private String dp;

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}