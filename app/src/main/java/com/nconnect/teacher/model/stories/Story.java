package com.nconnect.teacher.model.stories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.nconnect.teacher.util.Constants;

public class Story {
    @SerializedName(Constants.STATUS)
    private String status;
    @SerializedName(Constants.STORIES_ID)
    @Expose
    private Integer storyId;
    @SerializedName(Constants.STORIES_TITLE)
    @Expose
    private String title;
    @SerializedName(Constants.STORIES_SHORT_DESCRIPTION)
    @Expose
    private String shortDesc;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @SerializedName(Constants.STORIED_IMAGES)
    @Expose
    private List<String> images = null;
    @SerializedName(Constants.STORIES_FROM_NAME)
    @Expose
    private String fromName;
    @SerializedName(Constants.STORIES_FROM_DESIGNATION)
    @Expose
    private String fromDesignation;
    @SerializedName(Constants.STORIES_FROM_THUMP_IMAGE)
    @Expose
    private String fromThumpImg;
    @SerializedName(Constants.STORIES_TO)
    @Expose
    private String to;
    @SerializedName(Constants.STORIES_DATE)
    @Expose
    private String date;
    @SerializedName("media")
    @Expose
    private List<String> media;
    @SerializedName("documents")
    @Expose
    private List<String> documents;
    @SerializedName("classes")
    @Expose
    private List<Class> classes;

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public Integer getStoryId() {
        return storyId;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
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

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromDesignation() {
        return fromDesignation;
    }

    public void setFromDesignation(String fromDesignation) {
        this.fromDesignation = fromDesignation;
    }

    public String getFromThumpImg() {
        return fromThumpImg;
    }

    public void setFromThumpImg(String fromThumpImg) {
        this.fromThumpImg = fromThumpImg;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    @SerializedName(Constants.LIKE)
    @Expose
    private Boolean like;
    @SerializedName(Constants.LIKES)
    @Expose
    private Integer likes;
    @SerializedName(Constants.COMMENTS)
    @Expose
    private Integer comments;

    @SerializedName("like_per_story")
    @Expose
    private Double likePerStory;
    @SerializedName("per_teacher")
    @Expose
    private Double perTeacher;
    @SerializedName("total")
    @Expose
    private Integer total;

    public Double getLikePerStory() {
        return likePerStory;
    }

    public void setLikePerStory(Double likePerStory) {
        this.likePerStory = likePerStory;
    }

    public Double getPerTeacher() {
        return perTeacher;
    }

    public void setPerTeacher(Double perTeacher) {
        this.perTeacher = perTeacher;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
