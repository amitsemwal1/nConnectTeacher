package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.nconnect.teacher.model.attendance.Data;
import com.nconnect.teacher.model.issues.Arg;
import com.nconnect.teacher.model.issues.Kwargs;
import com.nconnect.teacher.util.Constants;

public class Params {

    public Integer getStory_id() {
        return story_id;
    }

    public void setStory_id(Integer story_id) {
        this.story_id = story_id;
    }

    @SerializedName(Constants.STATUS)
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName(Constants.USER_TOKEN)
    @Expose
    private Integer userToken;
    @SerializedName(Constants.STUDENT_TOKEN)
    @Expose
    private Integer studentToken;
    @SerializedName(Constants.FROM_DATE)
    @Expose
    private String fromDate;
    @SerializedName(Constants.TO_DATE)
    @Expose
    private String toDate;
    @SerializedName(Constants.ARGS)
    @Expose
    private List<Arg> args = null;
    @SerializedName(Constants.MODEL)
    @Expose
    private String model;
    @SerializedName(Constants.METHOD)
    @Expose
    private String method;
    @SerializedName(Constants.KWARGS)
    @Expose
    private Kwargs kwargs;
    @SerializedName(Constants.ISSUES_ID)
    @Expose
    private Integer issueId;
    @SerializedName(Constants.MESSAGE)
    @Expose
    private String message;
    @SerializedName("month")
    @Expose
    private Integer month;
    @SerializedName(Constants.DATE_FROM)
    @Expose
    private String dateFrom;
    @SerializedName("datas")
    @Expose
    private List<Data> datas = null;

    public List<Data> getDatas() {
        return datas;
    }

    public void setDatas(List<Data> datas) {
        this.datas = datas;
    }

    public void setMonth(Integer month){
        this.month = month;

    }
    public Integer getMonth(){
        return month;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @SerializedName(Constants.DATE_TO)
    @Expose
    private String dateTo;
    @SerializedName(Constants.NOTE)
    @Expose
    private String note;

    @SerializedName("student_id")
    @Expose
    private String studentID;

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStudentToken(Integer studentToken) {
        this.studentToken = studentToken;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public List<Arg> getArgs() {
        return args;
    }

    public void setArgs(List<Arg> args) {
        this.args = args;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Kwargs getKwargs() {
        return kwargs;
    }

    public void setKwargs(Kwargs kwargs) {
        this.kwargs = kwargs;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Params(int userToken, Integer studentToken, String loginType) {
        this.userToken = userToken;
        this.studentToken = studentToken;
        this.loginType = loginType;
    }

    public Params() {

    }

    public int getUserToken() {

        return userToken;
    }

    public void setUserToken(Integer userToken) {
        this.userToken = userToken;
    }

    public Integer getStudentToken() {
        return studentToken;
    }

    public void setStudentToken(int studentToken) {
        this.studentToken = studentToken;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @SerializedName("mobile_number")
    @Expose
    private Integer mobileNo;

    public Integer getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(Integer mobileNo) {
        this.mobileNo = mobileNo;
    }

    @SerializedName(Constants.LOGIN_TYPE)
    @Expose
    private String loginType;
    @SerializedName("last_update")
    @Expose
    private Integer lastUpdate;

    public Integer getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Integer lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @SerializedName("grade_id")
    @Expose
    private Integer gradeId;

    public Integer getGradeId() {
        return gradeId;
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

    @SerializedName("section_id")
    @Expose
    private Integer sectionId;
    @SerializedName("date")
    @Expose
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @SerializedName(Constants.LIKE)
    @Expose
    private Boolean like;
    @SerializedName(Constants.STORY_ID)
    @Expose
    private Integer story_id;

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public Vals getVals() {
        return vals;
    }

    public void setVals(Vals vals) {
        this.vals = vals;
    }

    @SerializedName("vals")
    @Expose
    private Vals vals;

    @SerializedName("message_id")
    @Expose
    private Integer message_id;
    @SerializedName("page")
    @Expose
    private Integer page;

    @SerializedName(Constants.STUDENT_INFO)
    @Expose
    private String studentInfo;

    public String getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(String studentInfo) {
        this.studentInfo = studentInfo;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    @SerializedName("device_token")
    @Expose
    private String device_token;

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
