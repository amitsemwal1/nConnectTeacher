package com.nconnect.teacher.model.Profile;

public class Data {
    private String dob;

    private String address;

    private String work_email;

    private String mobile_phone;

    private String dp;

    private String about;

    public void setDob(String dob){
        this.dob = dob;
    }
    public String getDob(){
        return this.dob;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return this.address;
    }
    public void setWork_email(String work_email){
        this.work_email = work_email;
    }
    public String getWork_email(){
        return this.work_email;
    }
    public void setMobile_phone(String mobile_phone){
        this.mobile_phone = mobile_phone;
    }
    public String getMobile_phone(){
        return this.mobile_phone;
    }
    public void setDp(String dp){
        this.dp = dp;
    }
    public String getDp(){
        return this.dp;
    }
    public void setAbout(String about){
        this.about = about;
    }
    public String getAbout(){
        return this.about;
    }
}