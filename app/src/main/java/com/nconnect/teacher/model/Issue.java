package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Issue {
    @SerializedName("escalated")
    @Expose
    private Integer escalated;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("resolved")
    @Expose
    private Integer resolved;

    public Integer getEscalated() {
        return escalated;
    }

    public void setEscalated(Integer escalated) {
        this.escalated = escalated;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getResolved() {
        return resolved;
    }

    public void setResolved(Integer resolved) {
        this.resolved = resolved;
    }
}
