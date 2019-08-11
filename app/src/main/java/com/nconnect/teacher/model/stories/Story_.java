package com.nconnect.teacher.model.stories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Story_ {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("story_id")
    @Expose
    private Integer storyId;
    @SerializedName("media")
    @Expose
    private List<String> media = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getStoryId() {
        return storyId;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getFromThumpImg() {
        return fromThumpImg;
    }

    public void setFromThumpImg(String fromThumpImg) {
        this.fromThumpImg = fromThumpImg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromDesignation() {
        return fromDesignation;
    }

    public void setFromDesignation(String fromDesignation) {
        this.fromDesignation = fromDesignation;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    @SerializedName("like")
    @Expose
    private Boolean like;
    @SerializedName("documents")
    @Expose
    private List<String> documents = null;
    @SerializedName("comments")
    @Expose
    private Integer comments;
    @SerializedName("from_thumpImg")
    @Expose
    private String fromThumpImg;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("from_designation")
    @Expose
    private String fromDesignation;
    @SerializedName("short_desc")
    @Expose
    private String shortDesc;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("images")
    @Expose
    private List<String> images = null;
    @SerializedName("from_name")
    @Expose
    private String fromName;
}
