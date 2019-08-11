package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Atnc {
    @SerializedName("absent")
    @Expose
    private Integer absent;
    @SerializedName("student_id")
    @Expose
    private String studentId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("present")
    @Expose
    private Integer present;
    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getAbsent() {
        return absent;
    }

    public void setAbsent(Integer absent) {
        this.absent = absent;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPresent() {
        return present;
    }

    public void setPresent(Integer present) {
        this.present = present;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
