package com.nconnect.teacher.model.stories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Class {

    @SerializedName("grade_id")
    @Expose
    private Integer gradeId;
    @SerializedName("section_id")
    @Expose
    private Integer sectionId;

    public Class() {

    }

    public Integer getGradeId() {
        return gradeId;
    }

    public Class(Integer gradeId, Integer sectionId) {
        this.gradeId = gradeId;
        this.sectionId = sectionId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }
}
