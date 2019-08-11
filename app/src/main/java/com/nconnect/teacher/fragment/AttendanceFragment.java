package com.nconnect.teacher.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.nconnect.teacher.activity.Dashboard;
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

public class AttendanceFragment extends Fragment {

    private static final String TAG = "Attendance Fragment";
    private MaterialCalendarView calender;
    private TextView tvAttendanceGuardianName, tvPresent, tvAbsent, tvToolbarTitle;
    private String fromDateStr, toDateStr;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private LinearLayout containerSchoolBoard;

    public AttendanceFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        calender = (MaterialCalendarView) view.findViewById(R.id.attendance_calendar);
        tvAttendanceGuardianName = (TextView) view.findViewById(R.id.attendance_guardian_name);
        tvPresent = (TextView) view.findViewById(R.id.attendance_present_days);
        tvAbsent = (TextView) view.findViewById(R.id.attendance_absent_days);
        tvToolbarTitle = ((Dashboard) getActivity()).getToolbarTitle();
        tvToolbarTitle.setText("Attendance");
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
    }

    private void initializeData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        int studentToken = sharedPreferences.getInt(Constants.STUDENT_TOKENS, 0);
        Params attendanceParam = new Params();
        if (userToken != 0) {
            attendanceParam.setUserToken(userToken);
        }
        if (studentToken != 0) {
            attendanceParam.setStudentToken(studentToken);
        }
//        Log.e(TAG, "from date : " + fromDateStr + " - " + "todate str : " + toDateStr);
        attendanceParam.setLoginType(Constants.LOGIN_TYPE_VALUE);
        attendanceParam.setFromDate(fromDateStr);
        attendanceParam.setToDate(toDateStr);
        Attendance attendance = new Attendance();
        attendance.setJsonrpc(Constants.JSON_RPC);
        attendance.setParams(attendanceParam);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e(TAG, "Attendance json model : " + new Gson().toJson(attendance));
        (Utils.httpService(getContext()).attendance(attendance, sessionIdValue)).enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(Call<Attendance> call, Response<Attendance> response) {
//                Log.e(TAG, "Attendance response : " + new Gson().toJson(response.body()));
                Attendance attendance1 = response.body();
                try {
                    Result result = attendance1.getResult();
                    String responseStr = result.getResponse();
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        String[] presentResponse = result.getPresentDates();
//                        Log.e(TAG, "present response size : " + presentResponse.length);
                        if (presentResponse != null) {
                            tvPresent.setText("" + presentResponse.length);
                        }
                        String[] absentResponse = result.getAbsentDates();
//                        Log.e(TAG, "Absent  response size : " + absentResponse.length);
                        if (absentResponse != null) {
                            tvAbsent.setText("" + absentResponse.length);
                        }
                        intializePresentandAbsent(presentResponse, absentResponse);
                    } else {
                        Utils.showToastCustom(getContext(), "Cannot get attendance Please try again");
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "Excpetion : " + e);
                }
            }

            @Override
            public void onFailure(Call<Attendance> call, Throwable t) {
                Log.e(TAG, "Failure response : " + t.getMessage());
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(getContext(), Constants.NO_CONNECTION);
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
        calender.addDecorators(new AttendancePresentDecorator(presentCal, getContext()));
        calender.addDecorators(new AttendanceAbsentDecorator(absentCal, getContext()));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (containerSchoolBoard.getVisibility() == View.VISIBLE){
            containerSchoolBoard.setVisibility(View.GONE);
        }
    }
}
