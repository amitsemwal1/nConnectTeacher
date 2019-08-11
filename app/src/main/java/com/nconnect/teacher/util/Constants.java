package com.nconnect.teacher.util;

import android.os.Environment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constants {

    public static final String USERS = "USERS_LIST";

    public static final String CHANNEL_ID = "my_channel_teacher";
    public static final String CHANNEL_NAME = "nconnect teacher";
    public static final String CHANNEL_DESCRIPTION = "devnconnect.pappaya.education.teacher";
    public static final String FCM_PREF = "fcm_pref_teacher";
    public static final String NO_CONNECTION = "Please check your network connection";
    // public static final String BASE_URL = "https://uatnconnect.pappaya.education/";

    // public static final String BASE_URL = "https://devops.pappaya.education/";
    //devops.pappaya.education
//        public static final String BASE_URL = "https://testnrynconnect.pappaya.education/";
  //      public static final String BASE_URL = "https://devnconnect.pappaya.education/";
        public static final String BASE_URL = "https://testnrynconnect.pappaya.education/";
//    public static final String BASE_URL = "http://192.168.1.55/";
//   public static final String BASE_URL = "https://nconnect.pappaya.education/";
    public static final String JSON_RPC = "2.0";
    public static final String JSON_RPC_ = "jsonrpc";
    public static final String STUDENT_INFO = "student_info";
    public static final String SUCCESS_MESSAGE = "success";
    public static final String STUDENT_ID = "student_id";
    public static final String TEACHER_ID = "teacher_id";
    public static final String PARAMS = "params";
    public static final String FAIL_MESSAGE = "fail";
    public static final String[] MONTHS = {"months", "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final int REQUEST_CODE_PERMISSION_FOR_OTP_VERIFICATION = 100;
    public static final String LOGIN_TYPE_VALUE = "teacher";
    public static final String OTP_FROM_SMS = "OTP";
    public static final String REGEX_GET_OTP = "[^0-9]";
    public static final String SMS_SUFFIX = "OLMHSC";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String SERVER_ERROR = "Odoo Server Error";
    public static final String MODEL = "model";
    public static final int DASHBOARD_STORIES_TYPE = 0;
    public static final int DASHBOARD_EVENTS_TYPE = 1;
    public static final int DASHBOARD_ANNOUNCEMENTS_TYPE = 2;
    public static final int DASHBOARD_ISSUES_TYPE = 3;
    public static final String STORIES = "stories";
    public static final String ISSUES = "issues";
    public static final String EVENTS = "events";
    public static final String ANNOUNCEMENTS = "annoucnements";
    public static final int TimeOut = 12000;
    public static final String CLOSE_BY_PARENT = "Closed_By_Parent";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String DOCUMENT = "document";
    public static final String POSITION = "position";

    //preference parametes
    public static final String PREFNAME = "pappaya_preference_teacher";
    public static final String URL = "url";
    public static final String GALLERY_DIRECTORY_NAME = "nConnect";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";
    private static final String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/signature/";
    private static final String PIC_NAME = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    public static final String STORED_PATH = DIRECTORY + PIC_NAME + ".png";

    //registration parameters
    public static final String END_POINT_REGISTER_TEACHER = "web/nConnect/registerTeacher";
    public static final String ID = "id";
    public static final String RESULT = "result";
    public static final String REGISTER_MOBILE_NUMBER = "reg_mobile";
    public static final String USERNAME = "user_name";
    public static final String PROFILE_IMAGE = "Profile_image";
    public static final String USER_TOKEN = "user_token";
    public static final String USER_TYPE = "user_type";
    public static final String STUDENT_TOKEN = "student_token";
    public static final String OTP = "Otp";
    public static final String STATUS = "status";
    public static final String LOGIN_STATUS = "login_status";
    public static final String RESPONSE = "response";

    //setpassword parameters
    public static final String END_POINT_SET_PASSWORD = "web/nConnect/setPassword";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String ADDRESS = "Address";
    public static final String MOBILE = "mobile";
    public static final String SET_PASSWORD = "set_password";
    public static final String RESEND_OTP = "web/nConnect/sendOtp";

    //login parameters
    public static final String END_POINT_LOGIN = "web/nConnect/login";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String SESSION_ID = "session_id";
    public static final String LOGIN_TYPE = "login_type";
    public static final String STUDENT_TOKENS = "student_tokens";
    public static final String STUDENT_TOKENS_JSON = "student_tokens_json";

    //stories parameters
    public static final String END_POINT_STORIES = "web/nConnect/stories";
    public static final String STORIES_LIST = "stories";
    public static final String STORIES_ID = "story_id";
    public static final String STORIES_TITLE = "title";
    public static final String STORIES_SHORT_DESCRIPTION = "short_desc";
    public static final String STORIED_IMAGES = "images";
    public static final String STORIES_FROM_NAME = "from_name";
    public static final String STORIES_FROM_DESIGNATION = "from_designation";
    public static final String STORIES_FROM_THUMP_IMAGE = "from_thumpImg";
    public static final String STORIES_TO = "to";
    public static final String STORIES_DATE = "date";

    //events parameters
    public static final String END_POINT_EVENTS = "web/nConnect/events";
    public static final String EVENTS_LIST = "events";
    public static final String ORGANIZER = "oragnizer";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_TITLE = "title";
    public static final String EVENTS_SHORT_DESCRIPTION = "short_desc";
    public static final String LOCATION = "location";
    public static final String EVENTS_DATE_TO = "date_to";
    public static final String EVENTS_DATE_FROM = "date_from";
    public static final String EVENTS_IMAGE = "image";
    public static final String EVENTS_TIME = "time";

    //announcements parameter
    public static final String END_POINT_ANNOUNCEMENTS = "web/nConnect/announcements";
    public static final String ANNOUNCEMENT_LIST = "announcements";
    public static final String ANNOUNCEMENT_ID = "announcement_id";
    public static final String ANNOUNCEMENT_TITLE = "title";
    public static final String ANNOUNCEMENT_DESCRIPTION = "desc";
    public static final String ANNOUNCEMENT_DATE = "date";
    public static final String ANNOUNCEMENT_POSTED_BY = "posted_by";
    public static final String ANNOUNCEMENT_POSTED_BY_DESIGNATION = "posted_by_desig";
    public static final String ANNOUNCEMENT_TIME = "time";
    public static final String POST_TO = "post_to";
    public static final String END_POINT_CREATE_ANNOUNCEMENT = "web/dataset/call_kw";

    //logout
    public static final String END_POINT_LOGOUT = "web/nConnect/logout";
    public static final String COOKIE = "Cookie";
    public static final String METHOD = "method";
    public static final String METHOD_VAL = "call";

    //StudentProfile
    public static final String END_POINT_HOME = "web/nConnect/homeParent";
    public static final String STUDENT_NAME = "student_name";
    public static final String PROFILE_IMAGE_HOME = "profile_image";
    public static final String STUDENT_PROFILE = "student_profile";
    public static final String ISSUES_COUNT = "issues_count";
    public static final String STUDENT_IMAGE = "stud_image";
    public static final String CHILDREN = "children";

    //AttendancePresents
    public static final String END_POINT_ATTENDANCE = "web/nConnect/viewAttendance";
    public static final String PRESENT_DATES = "present_dates";
    public static final String ABSENT_DATES = "absent_dates";
    public static final String DATES_PRESENT = "dates_present";
    public static final String DATES_ABSENT = "dates_absent";
    public static final String FROM_DATE = "from_date";
    public static final String TO_DATE = "to_date";
    public static final String LEAVE_NOTE_ENDPOINT = "web/nConnect/createLeaveNote";
    public static final String NOTE = "note";
    public static final String DATE_FROM = "date_from";
    public static final String DATE_TO = "date_to";


    //view profile
    public static final String END_POINT_PROFILE = "web/nConnect/viewProfile";

    //issues
    public static final String END_POINT_ISSUES = "web/nConnect/issues";
    public static final String ESCALATED_TO = "escalate_to";
    public static final String ISSUES_ID = "issue_id";
    public static final String STUDENT = "student";
    public static final String ISSUE_STATUS = "status";
    public static final String ISSUES_DATE = "date";
    public static final String RAISED_BY = "raised_by";
    public static final String DESCRIPTION = "description";
    public static final String ARGS = "args";
    public static final String KWARGS = "kwargs";
    public static final String END_POINT_CREATE_ISSUE = "web/dataset/call_kw";
    public static final String POST_COMMENTS = "web/nConnect/postComment";
    public static final String VIEW_COMMENTS = "web/nConnect/comments";
    public static final String CREATED_AT = "createdAt";
    public static final String USERID = "userId";
    public static final String MESSAGE_ID = "messageId";
    public static final String LAST_UPDATE = "last_update";

    public static final String ENDPOINT_LIKE_STORY = "web/nConnect/likeStory";
    public static final String STORY_ID = "story_id";
    public static final String LIKE = "like";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";
    public static final String END_POINT_CREATE_STORY = "web/nConnect/createStory";

    public static final String END_POINT_POST_STORY_COMMENT = "web/nConnect/story/postComment";
    public static final String END_POINT_VIEW_STORY_COMMENT = "web/nConnect/story/comments";
    public static final String END_POINT_DELETE = "web/nConnect/deleteStory";

    public static final String END_POINT_S3_UPLOAD = "web/nConnect/s3upload";

    public static final int MIN_BUFFER_DURATION = 3000;
    //Max Video you want to buffer during PlayBack
    public static final int MAX_BUFFER_DURATION = 5000;
    //Min Video you want to buffer before start Playing it
    public static final int MIN_PLAYBACK_START_BUFFER = 1500;
    //Min video You want to buffer when user resumes video
    public static final int MIN_PLAYBACK_RESUME_BUFFER = 5000;

    public static boolean isPreviewCliked = false;
    //    public static final String S3UPLOAD = "https://devnconnect.pappaya.education/web/nConnect/s3upload";
    public static final String S3UPLOAD = "web/nConnect/s3upload";

    public static final int DASH_ANALYTICAL = 100;
    public static final int DASH_STORIES = 101;
    public static final int DASH_ATTENDANCE = 102;
    public static final int DASH_CHAT = 103;
    public static final int DASH_EVENTS = 104;
    public static final int DASH_ANNOUNCEMENTS = 105;
    public static final int DASH_ISSUES = 106;
    public static final String S3BASE_URL = "https://s3.us-west-1.amazonaws.com";
    public static final String S3BASE_URL_1 = "https:/aws";


    public static String[] FILTERS = {"Stories", "Events", "Announcements"};
    public static final String USER_POS = "userPosition";

    public static final String PAPPAYA_PREF = "pappaya";
    public static DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    public static DateFormat timeFormat = new SimpleDateFormat("K:mma");
    public static final String RECEIVER_NAME = "receiver";
    public static final String ISLOGIN = "false";
    public static final String NOTIFICATIONS = "notification";
    public static final String firstTimeAppOpen = "firstTimeAppOpen";
    public static final String FILE_PATH = android.os.Environment.getExternalStorageDirectory().toString();
    public static String Chat1 = "";
    public static String Chat2 = "";
    public static String demo_pass = "123";
    public static final String date = "d MMM yyyy K:mma";

    public static final String notification = "Notification";
    public static final String GETSINGLE_STORY_URL = BASE_URL + "web/nConnect/story";
    public static final String GETSINGLE_ISSUES_URL = BASE_URL + "web/nConnect/issue";
    public static final String GETSINGLE_ANNOUNCEMENT_URL = BASE_URL + "web/nConnect/announcement";
    public static final String GETSINGLE_EVENT_URL = BASE_URL + "web/nConnect/event";
    public static final String SENDINVITE = BASE_URL + "web/nConnect/send/invite";
    public static final String INVITE = "invite";
    public static final String SENDER = "sender";
    public static final String UPDATENOTIFICATION = BASE_URL + "web/nConnect/read/notification";

    public static final String SCHOOL_BOARD_STORIES = "web/nConnect/school/stories";
    public static final String SCHOOL_BOARD_EVENTS = "web/nConnect/school/events";
    public static final String SCHOOL_BOARD_ANNOUNCEMENTS = "web/nConnect/school/announcements";

    public static final String MY_STORIES = "web/nConnect/my/stories";
    public static final String MY_EVENTS = "web/nConnect/my/events";
    public static final String MY_ANNOUNCEMENTS = "web/nConnect/my/announcements";


    public static final String GO_TO_DASHBOARD = "2";
    public static final String GO_TO_SETPASSWORD = "0";
    public static final String GO_TO_ENTER_DETAIL = "1";
    public static final String GO_TO_FORGOT_PASSWORD = "3";
    public static final String STATUS_CODE = "status";
    public static final String DATE_HOURS_MINI_FORMAT = "yyyy-MM-dd' 'HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String HH_MM = "hh:mm aa";

    public static final String PROGRESS_UPDATE = "progress_update";


    public static final String CHATNOTIFICATION = BASE_URL + "web/nConnect/notify/user";
    public static final String CHAT = "chat";
    public static final int PORT = 5222;
}