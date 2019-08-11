package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.nconnect.teacher.model.announcements.Announcement;
import com.nconnect.teacher.model.events.Event;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.gradeandsection.Student;
import com.nconnect.teacher.model.login.Child;
import com.nconnect.teacher.model.registration.LoginStatus;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.util.Constants;

public class Result {
    @SerializedName(Constants.LOGIN_TYPE)
    @Expose
    private String login_type;

    @SerializedName(Constants.RESULT)
    @Expose
    private Result_ result_;

    public Result_ getResult_() {
        return result_;
    }

    public void setResult_(Result_ result_) {
        this.result_ = result_;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    @SerializedName(Constants.LOGIN_STATUS)
    @Expose
    private List<LoginStatus> loginStatus = null;
    @SerializedName(Constants.RESPONSE)
    @Expose
    private String response;
    @SerializedName(Constants.EMAIL)
    @Expose
    private String email;
    @SerializedName(Constants.LOGIN)
    @Expose
    private String login;
    @SerializedName(Constants.NAME)
    @Expose
    private String name;
    @SerializedName(Constants.ADDRESS)
    @Expose
    private String address;
    @SerializedName(Constants.PASSWORD)
    @Expose
    private String password;
    @SerializedName(Constants.MOBILE)
    @Expose
    private String mobile;
    @SerializedName(Constants.USER_TOKEN)
    @Expose
    private Integer userToken;
    @SerializedName(Constants.STUDENT_TOKENS)
    @Expose
    private List<Integer> studentTokens;
    @SerializedName(Constants.STORIES_LIST)
    @Expose
    private List<Story> stories;
    @SerializedName("student_token")
    @Expose
    private Integer studentTkon;

    public Integer getStudentTkon() {
        return studentTkon;
    }

    public void setStudentTkon(Integer studentTkon) {
        this.studentTkon = studentTkon;
    }

    @SerializedName(Constants.ANNOUNCEMENT_LIST)
    @Expose
    private List<Announcement> announcements = null;
    @SerializedName(Constants.EVENTS_LIST)
    @Expose
    private List<Event> events = null;

    @SerializedName(Constants.ISSUES_COUNT)
    @Expose
    private Integer issuesCount;
    @SerializedName("child_name")
    @Expose
    private String childName;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public Integer getIssuesCount() {
        return issuesCount;
    }

    public void setIssuesCount(Integer issuesCount) {
        this.issuesCount = issuesCount;
    }

    /* public List<StudentProfile> getStudentProfile() {
             return studentProfile;
         }

         public void setStudentProfile(List<StudentProfile> studentProfile) {
             this.studentProfile = studentProfile;
         }

         @SerializedName(Constants.STUDENT_PROFILE)
         @Expose
         private List<StudentProfile> studentProfile = null;*/
    @SerializedName(Constants.PRESENT_DATES)
    @Expose
    private String[] presentDates;
    @SerializedName(Constants.ABSENT_DATES)
    @Expose
    private String[] absentDates;

    public String[] getPresentDates() {
        return presentDates;
    }

    public void setPresentDates(String[] presentDates) {
        this.presentDates = presentDates;
    }

    public String[] getAbsentDates() {
        return absentDates;
    }

    public void setAbsentDates(String[] absentDates) {
        this.absentDates = absentDates;
    }




    @SerializedName(Constants.DATES_PRESENT)
    @Expose
    private String[] datesPresent;
    @SerializedName(Constants.DATES_ABSENT)
    @Expose
    private String[] datesAbsent;

    public void setDatesPresent(String[] datesPresent) {
        this.datesPresent = datesPresent;
    }

    public String[] getDatesPresent() {
        return datesPresent;
    }

    public void setDatesAbsent(String[] datesAbsent) {
        this.datesAbsent = datesAbsent;
    }

    public String[] getDatesAbsent() {
        return datesAbsent;
    }

    public Result() {

    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Integer> getStudentTokens() {
        return studentTokens;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public void setStudentTokens(List<Integer> studentTokens) {
        this.studentTokens = studentTokens;
    }

    public Integer getUserToken() {
        return userToken;
    }

    public void setUserToken(Integer userToken) {
        this.userToken = userToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    @SerializedName(Constants.SESSION_ID)
    @Expose
    private String sessionId;

    public Result(String response) {
        this.response = response;
    }

    public List<LoginStatus> getLoginStatus() {
        return loginStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setLoginStatus(List<LoginStatus> loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @SerializedName(Constants.CHILDREN)
    @Expose
    private List<Child> children = null;
    @SerializedName(Constants.ESCALATED_TO)
    @Expose
    private String escalateTo;
    @SerializedName(Constants.ISSUES_ID)
    @Expose
    private Integer issueId;
    @SerializedName(Constants.STUDENT)
    @Expose
    private String student;
    @SerializedName(Constants.ISSUE_STATUS)
    @Expose
    private String status;
    @SerializedName(Constants.ISSUES_DATE)
    @Expose
    private String date;
    @SerializedName(Constants.RAISED_BY)
    @Expose
    private String raisedBy;
    @SerializedName("id")
    @Expose
    private Integer id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEscalateTo() {
        return escalateTo;
    }

    public void setEscalateTo(String escalateTo) {
        this.escalateTo = escalateTo;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SerializedName("assign_to")

    @Expose
    private String assignTo;

    public List<Grade> getGrade() {
        return grade;
    }

    public void setGrade(List<Grade> grade) {
        this.grade = grade;
    }

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("grade")
    @Expose
    private List<Grade> grade = null;

    @SerializedName("grades")
    @Expose
    private List<Grade> gradesList;

    public List<Grade> getGradesList() {
        return gradesList;
    }

    public void setGradesList(List<Grade> gradesList) {
        this.gradesList = gradesList;
    }

    @SerializedName("students")
    @Expose
    private List<Student> students = null;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName(Constants.MESSAGE)
    @Expose
    private String message;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = null;

    @SerializedName(Constants.URL)
    @Expose
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("dp")
    @Expose
    private String dp;

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    @SerializedName("days_absent")
    @Expose
    private Integer daysAbsent;
    @SerializedName("days_present")
    @Expose
    private Integer daysPresent;

    @SerializedName("attendance")
    @Expose
    private List<Atnc> attendance = null;

    public Integer getDaysAbsent() {
        return daysAbsent;
    }

    public void setDaysAbsent(Integer daysAbsent) {
        this.daysAbsent = daysAbsent;
    }

    public Integer getDaysPresent() {
        return daysPresent;
    }

    public void setDaysPresent(Integer daysPresent) {
        this.daysPresent = daysPresent;
    }

    public List<Atnc> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<Atnc> attendance) {
        this.attendance = attendance;
    }

    public class Atnc {

        @SerializedName("is_present")
        @Expose
        private Boolean isPresent;
        @SerializedName("date")
        @Expose
        private String date;

        public Boolean getPresent() {
            return isPresent;
        }

        public void setPresent(Boolean present) {
            isPresent = present;
        }

        public Boolean getIsPresent() {
            return isPresent;
        }

        public void setIsPresent(Boolean isPresent) {
            this.isPresent = isPresent;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

}
