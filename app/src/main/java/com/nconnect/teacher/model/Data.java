package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.nconnect.teacher.model.stories.Story;

public class Data {

    @SerializedName("story")
    @Expose
    private Story story;
    @SerializedName("parent")
    @Expose
    private Parent parent;
    @SerializedName("issue")
    @Expose
    private Issue issue;
    @SerializedName("teacher")
    @Expose
    private Teacher teacher;

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("exception_type")
    @Expose
    private String exceptionType;
    @SerializedName("arguments")
    @Expose
    private List<String> arguments = null;
    @SerializedName("debug")
    @Expose
    private String debug;
    @SerializedName("message")
    @Expose
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("attendance")
    @Expose
    private List<Atnc> attendance = null;
    @SerializedName("working_days")
    @Expose
    private Integer workingDays;

    public List<Atnc> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<Atnc> attendance) {
        this.attendance = attendance;
    }

    public Integer getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
    }
}
