package com.nconnect.teacher.util;

import com.nconnect.teacher.model.Comments;
import com.nconnect.teacher.model.Logout;
import com.nconnect.teacher.model.Notifications.NotificationRequest;
import com.nconnect.teacher.model.Notifications.NotificationRoot;
import com.nconnect.teacher.model.Otp;
import com.nconnect.teacher.model.Profile.ProfileRequest;
import com.nconnect.teacher.model.Profile.ProfileRoot;
import com.nconnect.teacher.model.S3Upload;
import com.nconnect.teacher.model.announcements.Announcements;
import com.nconnect.teacher.model.announcements.PostAnnounce;
import com.nconnect.teacher.model.attendance.Attendance;
import com.nconnect.teacher.model.chat.ParentRequest;
import com.nconnect.teacher.model.chat.ParentsRoot;
import com.nconnect.teacher.model.events.Events;
import com.nconnect.teacher.model.gradeandsection.StudentGradesAndSection;
import com.nconnect.teacher.model.issues.CloseIssue;
import com.nconnect.teacher.model.issues.GetIssuesResponse;
import com.nconnect.teacher.model.issues.IssueComments;
import com.nconnect.teacher.model.issues.Issues;
import com.nconnect.teacher.model.login.LoginUser;
import com.nconnect.teacher.model.registration.Registration;
import com.nconnect.teacher.model.setpassword.SetPassword;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.model.stories.StoryLIke;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface RetrofitClient {

    @POST(Constants.END_POINT_REGISTER_TEACHER)
    Call<Registration> register(@Body Registration registration);

    @POST(Constants.END_POINT_SET_PASSWORD)
    Call<SetPassword> setPassword(@Body SetPassword setPassword);

    @POST(Constants.END_POINT_LOGIN)
    Call<LoginUser> login(@Body LoginUser loginUser);

    @POST(Constants.END_POINT_LOGOUT)
    Call<Logout> logout(@Body Logout logout, @Header(Constants.COOKIE) String sessionId);

    @POST(Constants.RESEND_OTP)
    Call<Otp> resendOtp(@Body Otp otp);

    @POST(Constants.POST_COMMENTS)
    Call<IssueComments> postIssueMsgComment(@Body IssueComments comments, @Header(Constants.COOKIE) String session_id);

    @POST
    Call<Stories> stories(@Url String url, @Body Stories stories, @Header(Constants.COOKIE) String sessionId);

    @POST("web/nConnect/search/student")
    Call<StudentGradesAndSection> getStudentSearchList(@Body StudentGradesAndSection gradesAndSection, @Header("Cookie") String sessionId);

    @POST
    Call<Announcements> announcements(@Url String url, @Body Announcements announcements, @Header(Constants.COOKIE) String sessionId);

    @POST
    Call<Events> events(@Url String url, @Body Events events, @Header(Constants.COOKIE) String sessionId);

    @POST(Constants.END_POINT_ATTENDANCE)
    Call<Attendance> attendance(@Body Attendance attendance, @Header(Constants.COOKIE) String sessionId);

    @POST("web/nConnect/student/attendance")
    Call<Attendance> viewStudentAttendance(@Body Attendance attendance, @Header(Constants.COOKIE) String session);

    @POST("web/nConnect/attendance/month")
    Call<Attendance> viewStudentAttendanceMonth(@Body Attendance attendance, @Header(Constants.COOKIE) String session);

    @POST(Constants.END_POINT_ISSUES)
    Call<GetIssuesResponse> issuesView(@Body Issues issues, @Header(Constants.COOKIE) String session_id);

    @POST(Constants.END_POINT_CREATE_ANNOUNCEMENT)
    Call<PostAnnounce> postAnnouncement(@Body PostAnnounce postAnnounce, @Header(Constants.COOKIE) String sessionId);

    @POST("web/dataset/call_kw")
    Call<PostAnnounce> postEvent(@Body PostAnnounce postevent, @Header("Cookie") String sessionId);

    @POST("web/nConnect/stories")
    Call<Stories> getPendingStories(@Body Stories stories, @Header("Cookie") String sessionId);

    @POST("web/nConnect/teacherClasses")
    Call<StudentGradesAndSection> setGradeAndSection(@Body StudentGradesAndSection gradesAndSection, @Header("Cookie") String sessionId);

    @POST("/web/nConnect/students/attendance")
    Call<StudentGradesAndSection> getStudentList(@Body StudentGradesAndSection gradesAndSection, @Header("Cookie") String sessionId);

    @POST("web/nConnect/issueStatus")
    Call<CloseIssue> closeIssue(@Body CloseIssue issues, @Header("Cookie") String sessionId);

    @POST("/web/nConnect/createAttendance")
    Call<Attendance> markAttendance(@Body Attendance attendance, @Header(Constants.COOKIE) String sessionId);

    @POST(Constants.ENDPOINT_LIKE_STORY)
    Call<StoryLIke> likeStory(@Body StoryLIke storyLIke, @Header(Constants.COOKIE) String sessionId);

    @POST(Constants.END_POINT_POST_STORY_COMMENT)
    Call<Stories> postStoryComment(@Body Stories stories, @Header(Constants.COOKIE) String sessionId);

    @POST("web/nConnect/stories")
    Call<Stories> getRejectedStories(@Body Stories stories, @Header("Cookie") String sessionId);

    @POST(Constants.END_POINT_DELETE)
    Call<Stories> deleteStory(@Body Stories stories, @Header("Cookie") String sessionIdValue);

    @POST(Constants.END_POINT_CREATE_STORY)
    Call<Stories> postStory(@Body Stories stories, @Header("Cookie") String sessionIdValue);

    @POST(Constants.END_POINT_S3_UPLOAD)
    Call<ResponseBody> uploadS3File(@Body RequestBody file);

    @POST("web/nConnect/createEvent")
    Call<Events> postEventByVp(@Body Events events, @Header(Constants.COOKIE) String cookie);

    @POST("web/nConnect/createAnnouncement")
    Call<Announcements> postAnnouncementVp(@Body Announcements announce, @Header(Constants.COOKIE) String session);

    @POST("web/nConnect/story/comments")
    Call<Comments> storyComments(@Body Comments comments, @Header(Constants.COOKIE) String sessionId);

    @POST("web/nConnect/usage-data")
    Call<Comments> usageData(@Body Comments comments, @Header(Constants.COOKIE) String sessionId);

    @POST("web/nConnect/install-data")
    Call<Comments> installData(@Body Comments comments, @Header(Constants.COOKIE) String sessionId);

    @POST("web/nConnect/delete/comment")
    Call<Stories> deleteStoryComment(@Body Stories stories, @Header(Constants.COOKIE) String session);


    @POST("web/nConnect/updateStory")
    Call<Stories> updateStory(@Body Stories stories, @Header(Constants.COOKIE) String sessionID);

    @POST("/web/nConnect/teacher/onboard")
    Call<LoginUser> onboard(@Body LoginUser loginUser);

    @POST("web/nConnect/teacher/update/profile")
    Call<Stories> updateProfile(@Body Stories stories);

    @POST("web/nConnect/onboard/set_password")
    Call<Stories> setPassword(@Body Stories stories, @Header(Constants.COOKIE) String sessionId);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl, @Header(Constants.COOKIE) String session);

    @Streaming
    @GET
    Call<ResponseBody> downloadImage(@Url String fileUrl);

    @POST("web/nConnect/notifications")
    Call<NotificationRoot> getNotification(@Body NotificationRequest notificationRequest, @Header(Constants.COOKIE) String sessionID);

    @POST("web/nConnect/teacher/get/profile")
    Call<ProfileRoot> getNotification(@Body ProfileRequest profileRequest, @Header(Constants.COOKIE) String sessionID);

    @POST("web/nConnect/chat/parents")
    Call<ParentsRoot> getParentList(@Body ParentRequest parentRequest, @Header(Constants.COOKIE) String sessionID);

    @POST("web/nConnect/update/deviceToken")
    Call<Stories> updateToken(@Body Stories stories, @Header(Constants.COOKIE) String session);

    @POST("web/nConnect/schoolGrades")
    Call<StudentGradesAndSection> getClassByPrinicpal(@Body StudentGradesAndSection grades, @Header(Constants.COOKIE) String session);

    @Multipart
    @POST(Constants.S3UPLOAD)
    Call<S3Upload> uploadS3File(@Part MultipartBody.Part file);
}
