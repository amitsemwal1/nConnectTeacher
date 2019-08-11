package com.nconnect.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nconnect.teacher.R;
import com.nconnect.teacher.helper.AttendanceAbsentDecorator;
import com.nconnect.teacher.helper.AttendancePresentDecorator;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.attendance.Attendance;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAttendanceActivity extends AppCompatActivity {

    private static final String TAG = ViewAttendanceActivity.class.getSimpleName();
    private MaterialCalendarView calender;
    private TextView tvStudentMonthOfAttendance, tvPresent, tvAbsent, tvToolbarTitle;
    private String fromDateStr, toDateStr;
    private String studentName, studentId;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
    //    private Student student;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        init();
    }

    private void init() {
        calender = (MaterialCalendarView) findViewById(R.id.attendance_calendar);
        tvStudentMonthOfAttendance = (TextView) findViewById(R.id.attendance_guardian_name);
        tvPresent = (TextView) findViewById(R.id.attendance_present_days);
        tvAbsent = (TextView) findViewById(R.id.attendance_absent_days);
        tvToolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("student_id");
        studentName = intent.getStringExtra("student_name");
//        Log.e(TAG, "student id : " + studentId);
//        Log.e(TAG, "student name : " + studentName);
        tvToolbarTitle.setText(studentName + " Attendance");
        CalendarDay calendarDay = new CalendarDay();
        fromDateStr = "01" + "-" +
                (calendarDay.getMonth() + 1) + "-" +
                calendarDay.getYear();
        toDateStr = String.valueOf(calendarDay.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)) + "-" +
                (calendarDay.getMonth() + 1) + "-" +
                calendarDay.getYear();
        initializeData();
        calender.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                fromDateStr = "01" + "-" +
                        (date.getMonth() + 1) + "-" +
                        date.getYear();
                toDateStr = String.valueOf(date.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)) + "-" +
                        (date.getMonth() + 1) + "-" +
                        date.getYear();
                initializeData();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params attendanceParam = new Params();
        attendanceParam.setStudentID(studentId);
        attendanceParam.setDateFrom(fromDateStr);
        attendanceParam.setDateTo(toDateStr);
        Attendance attendance = new Attendance();
//        attendance.setJsonrpc(Constants.JSON_RPC);
        attendance.setParams(attendanceParam);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
            Log.i(TAG,""+sessionIdValue);
        }
//        Log.e(TAG, "Attendance json model : " + new Gson().toJson(attendance));
        (Utils.httpService(this).viewStudentAttendance(attendance, sessionIdValue)).enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(Call<Attendance> call, Response<Attendance> response) {
//                Log.e(TAG, "Attendance response : " + new Gson().toJson(response.body()));
             /*   try {
                    Result result = response.body().getResult();
                    if (result.getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        presentList = new ArrayList<>();
                        absentList = new ArrayList<>();
                        tvPresent.setText("" + result.getDaysPresent());
                        tvAbsent.setText("" + result.getDaysAbsent());
                        List<Result.Atnc> atncList = result.getAttendance();
                        for (int i = 0; i < atncList.size(); i++) {
                            Boolean presentResponse = atncList.get(i).getIsPresent();
                            if (presentResponse){
                                presentList.add(atncList.get(i).getDate());
                            }
                            else {
                                absentList.add(atncList.get(i).getDate());
                            }
                        }
                        String [] presentListArray = new String[presentList.size()];
                        presentListArray   = presentList.toArray(presentListArray);
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "Exception : " + e);
                }*/
                Attendance attendance1 = response.body();
                try {
                    Result result = attendance1.getResult();
                    String responseStr = result.getResponse();
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        String[] presentResponse = result.getDatesPresent();
//                        Log.e(TAG, "present response size : " + presentResponse.length);
                        if (presentResponse != null) {
                            tvPresent.setText("" + presentResponse.length);
                        }
                        String[] absentResponse = result.getDatesAbsent();
//                        Log.e(TAG, "Absent  response size : " + absentResponse.length);
                        if (absentResponse != null) {
                            tvAbsent.setText("" + absentResponse.length);
                        }
                        CalendarDay calendarDay = new CalendarDay();
                        tvStudentMonthOfAttendance.setText(studentId +
                                " Attendance for the month of " + Constants.MONTHS[(calendarDay.getMonth() + 1)] + " "
                                + calendarDay.getYear());
                        intializePresentandAbsent(presentResponse, absentResponse);
                    } else {
                        Utils.showToastCustom(ViewAttendanceActivity.this, "Cannot get attendance Please try again");
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "Excpetion : " + e);
                }
            }

            @Override
            public void onFailure(Call<Attendance> call, Throwable t) {
//                Log.e(TAG, "Failure response : " + t.getMessage());
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(ViewAttendanceActivity.this, Constants.NO_CONNECTION);
                }
            }
        });
    }

    private void intializePresentandAbsent(String[] present, String[] absent) {
        Set<Calendar> presentDates = null;
        Set<Calendar> absentDates = null;
        if (presentDates == null) {
            presentDates = new HashSet<Calendar>();
            try {
                for (int i = 0; i < present.length; i++) {
                    Calendar c = Calendar.getInstance();
                    Date date = sdf.parse(present[i]);
                    c.setTime(date);
                    presentDates.add(c);
                }
            } catch (ParseException e) {
//                Log.e("TAG", "Exception : " + e);
            }
        }
        if (absentDates == null) {
            absentDates = new HashSet<Calendar>();
            try {
                for (int i = 0; i < absent.length; i++) {
                    Calendar c = Calendar.getInstance();
                    Date date = sdf.parse(absent[i]);
                    c.setTime(date);
                    absentDates.add(c);
                }
            } catch (ParseException e) {
//                Log.e("TAG", "Exception : " + e);
            }

        }
        List<Calendar> presentList = new ArrayList<>();
        List<Calendar> absentList = new ArrayList<>();
        presentList.addAll(presentDates);
        absentList.addAll(absentDates);
//        Log.e(TAG, "present " + new Gson().toJson(presentList));
//        Log.e(TAG, "absent " + new Gson().toJson(absentList));
        HashSet<CalendarDay> calendarDayspresent = new HashSet<>();
        for (int cal = 0; cal < presentList.size(); cal++) {
            Calendar calendar = presentList.get(cal);
            CalendarDay calendarDay = CalendarDay.from(calendar);
            calendarDayspresent.add(calendarDay);
        }

        HashSet<CalendarDay> calendarDaysabsent = new HashSet<>();
        for (int cal = 0; cal < absentList.size(); cal++) {
            Calendar calendar = absentList.get(cal);
            CalendarDay calendarDay = CalendarDay.from(calendar);
            calendarDaysabsent.add(calendarDay);
        }
        List<CalendarDay> presentCal = new ArrayList<>();
        List<CalendarDay> absentCal = new ArrayList<>();
        presentCal.addAll(calendarDayspresent);
        absentCal.addAll(calendarDaysabsent);
//        Log.e(TAG, "present : " + new Gson().toJson(presentCal));
//        Log.e(TAG, "absent : " + new Gson().toJson(absentCal));
        calender.addDecorators(new AttendancePresentDecorator(presentCal, this));
        calender.addDecorators(new AttendanceAbsentDecorator(absentCal, this));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
