package com.nconnect.teacher.model.gradeandsection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Grade {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("section")
    @Expose
    private List<Section> section = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("grade_checked")
    @Expose
    private String gradeChecked;

    public Grade() {

    }

    public String getGradeChecked() {
        return gradeChecked;
    }

    public void setGradeChecked(String gradeChecked) {
        this.gradeChecked = gradeChecked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Section> getSection() {
        return section;
    }

    public void setSection(List<Section> section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}