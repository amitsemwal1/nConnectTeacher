package com.nconnect.teacher.model.gradeandsection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Section {
    @SerializedName("section_id")
    @Expose
    private Integer sectionId;
    @SerializedName("section_name")
    @Expose
    private String sectionName;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    public Section() {
    }

    @Override
    public String toString() {
        return sectionName;
    }
}
