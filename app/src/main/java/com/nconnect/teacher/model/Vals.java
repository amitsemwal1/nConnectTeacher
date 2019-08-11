package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.nconnect.teacher.model.stories.Class;
import com.nconnect.teacher.util.Constants;

public class Vals {

    @SerializedName("classes")
    @Expose
    private List<Class> classes = null;
    @SerializedName("documents")
    @Expose
    private List<String> documents = null;
    @SerializedName("media")
    @Expose
    private List<String> media = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SerializedName("date_from")
    @Expose
    private String datefrom;
    @SerializedName("date_to")
    @Expose
    private String dateTo;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("address1")
    @Expose
    private String address;
    @SerializedName("post_to")
    @Expose
    private String postTo;

    public String getDatefrom() {
        return datefrom;
    }

    public void setDatefrom(String datefrom) {
        this.datefrom = datefrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostTo() {
        return postTo;
    }

    public void setPostTo(String postTo) {
        this.postTo = postTo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @SerializedName(Constants.EVENTS_IMAGE)
    @Expose
    private String image;

    @SerializedName("date")
    @Expose
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("mobile_phone")
    @Expose
    private String mobilePhone;
    @SerializedName("work_email")
    @Expose
    private String workEmail;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("dp")
    private String dp;

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
