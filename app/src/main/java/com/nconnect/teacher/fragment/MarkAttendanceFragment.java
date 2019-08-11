package com.nconnect.teacher.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.AttendanceAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.attendance.Attendance;
import com.nconnect.teacher.model.attendance.Data;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.gradeandsection.Section;
import com.nconnect.teacher.model.gradeandsection.Student;
import com.nconnect.teacher.model.gradeandsection.StudentGradesAndSection;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MarkAttendanceFragment extends Fragment {
    private static final String TAG = "Mark Attendance ";
    private TextView tvTodayDate;
    private Spinner spnClass, spnSection;
    private RecyclerView recyclerStudents;
    private List<Grade> grades;
    private List<Section> sectionList;
    private int sectionId = 0, gradeId = 0;
    private Button btnSubmit;
    private List<Student> students;
    private List<Data> attendancelist;
    private Data data;
    private AttendanceAdapter adapter;
    private TextView tvNoStudents;

    public MarkAttendanceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark_attendance, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
//        containerSchoolBoard = ((Dashboard)getActivity()).getContainerSchoolBoard();
        grades = new ArrayList<>();
        attendancelist = new ArrayList<>();
        students = new ArrayList<>();
        sectionList = new ArrayList<>();
        tvNoStudents = (TextView) view.findViewById(R.id.tagNoStudents);
        btnSubmit = (Button) view.findViewById(R.id.mark_attendance_submit);
        tvTodayDate = (TextView) view.findViewById(R.id.mark_attendance_currentDate);
        spnClass = (Spinner) view.findViewById(R.id.mark_attendance_classSpinner);
        spnSection = (Spinner) view.findViewById(R.id.mark_attendance_sectionSpinner);
        recyclerStudents = (RecyclerView) view.findViewById(R.id.mark_attendance_reyclerStudends);
        adapter = new AttendanceAdapter(getContext(), students);
        recyclerStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerStudents.setHasFixedSize(true);
        recyclerStudents.setAdapter(adapter);
        initializeListeners();
        initializeClasseAndSection();
        intializeCurrentDate();
    }

    private void intializeCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sf.format(currentDate);
        tvTodayDate.setText(formattedDate);
    }

    private void initializeClasseAndSection() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt("user_token", 0);
        Params params = new Params();
        if (userToken != 0) {
            params.setUserToken(userToken);
        }
        StudentGradesAndSection gradesAndSection = new StudentGradesAndSection();
        gradesAndSection.setJsonrpc("2.0");
        gradesAndSection.setParams(params);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString("session_id", "");
        if (sessionId != "") {
            sessionIdValue = "session_id" + "=" + sessionId;
        }
        (Utils.httpService(getContext()).setGradeAndSection(gradesAndSection, sessionIdValue)).enqueue(new Callback<StudentGradesAndSection>() {
            @Override
            public void onResponse(Call<StudentGradesAndSection> call, Response<StudentGradesAndSection> response) {
//                Log.e(TAG, "reposnse body : " + new Gson().toJson(response.body()));
                try {
                    StudentGradesAndSection studentGradesAndSection = response.body();
                    Result result = studentGradesAndSection.getResult();
                    if (result != null) {
                        if (result.getResponse().equalsIgnoreCase("success")) {
                            grades.clear();
                            grades = result.getGrade();
//                            Log.e(TAG, "grades : list " + grades);
//                            Log.e(TAG, "grades : list " + new Gson().toJson(grades));
                            ArrayAdapter<Grade> adapter = new ArrayAdapter<Grade>(getContext(), R.layout.spinner_filter_item, grades);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            adapter.notifyDataSetChanged();
                            spnClass.setAdapter(adapter);

                        } else {
                            Utils.showToast(getContext(), "Cannot get class and section please try again");
                        }
                    } else {
                        Utils.showToast(getContext(), "Something went wrong please try again");
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "Nullpointer exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<StudentGradesAndSection> call, Throwable t) {
//                Log.e(TAG, "Failure response : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToast(getContext(), "Please check your intenet connection");
                }
            }
        });

    }

    private void initializeListeners() {

        spnSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.e(TAG, "section Id : " + sectionList.get(position).getSectionId());
                sectionId = sectionList.get(position).getSectionId();
                getStudentList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < grades.size(); i++) {
                    sectionList = grades.get(position).getSection();
                }
                ArrayAdapter<Section> sectionArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_filter_item, sectionList);
                sectionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sectionArrayAdapter.notifyDataSetChanged();
                spnSection.setAdapter(sectionArrayAdapter);
//                Log.e(TAG, "grade id : " + grades.get(position).getId());
                gradeId = grades.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAttendance();
            }
        });

    }

    private void submitAttendance() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout, null);
        TextView tvClass = alertLayout.findViewById(R.id.dialog_class);
        TextView tvTotalStudents = alertLayout.findViewById(R.id.dialog_students);
        TextView tvAbsent = alertLayout.findViewById(R.id.dialog_absent);
        TextView tvPresent = alertLayout.findViewById(R.id.dialog_present);
        TextView tvCancel = alertLayout.findViewById(R.id.dialog_cancel);
        TextView tvSubmit = alertLayout.findViewById(R.id.dialog_submit);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(alertLayout);
        alert.setCancelable(false);
        AlertDialog dialog = alert.create();
        int totalStudents = students.size();
        int presentCount = 0, absentCount = 0;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getIsPresent()) {
                presentCount = presentCount + 1;
            } else {
                absentCount = absentCount + 1;
            }
        }
        tvAbsent.setText("" + absentCount);
        tvPresent.setText("" + presentCount);
        tvClass.setText("Class : " + gradeId + "  ,  " + sectionId);
        tvTotalStudents.setText("Total Students : " + totalStudents);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                int usertoken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
                List<Data> studentList = new ArrayList<>();
                for (int i = 0; i < students.size(); i++) {
                    Data student = new Data();
                    student.setIsPresent(students.get(i).getIsPresent());
                    student.setStudentId(students.get(i).getStudentId());
                    studentList.add(student);
                }
                Params params = new Params();
                params.setUserToken(usertoken);
                params.setDatas(studentList);
                params.setDate(tvTodayDate.getText().toString());
                Attendance attendance = new Attendance();
                attendance.setJsonrpc(Constants.JSON_RPC);
                attendance.setParams(params);
                Log.e(TAG, "json model data : " + new Gson().toJson(attendance));
                String sessionIdValue = "";
                String sessionId = sharedPreferences.getString("session_id", "");
                if (sessionId != "") {
                    sessionIdValue = "session_id" + "=" + sessionId;
                }
                ProgressDialog dialog = Utils.showDialog(getContext(), "Please wait . . .");
                dialog.show();
                (Utils.httpService(getContext()).markAttendance(attendance, sessionIdValue)).enqueue(new Callback<Attendance>() {
                    @Override
                    public void onResponse(Call<Attendance> call, Response<Attendance> response) {
//                        Log.e(TAG, "response :  " + new Gson().toJson(response.body()));
                        try {
                            if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Attendance created successfully", Toast.LENGTH_SHORT).show();
                                getStudentList();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Cannot create attendance please try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
                            dialog.dismiss();
//                            Log.e(TAG, "exception : " + e);
                            Toast.makeText(getContext(), "Cannot create attendance please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Attendance> call, Throwable t) {
                        dialog.dismiss();
//                        Log.e(TAG, "Failure : " + t);
                        if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                            Utils.showToastCustom(getContext(), "Please check your network connection");
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void getStudentList() {

        if (sectionId == 0) {
            Utils.showToastCustom(getContext(), "Something went wrong please try again");
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userTOken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setLoginType(Constants.LOGIN_TYPE_VALUE);
        params.setUserToken(userTOken);
        params.setSectionId(sectionId);
        params.setGradeId(gradeId);
        StudentGradesAndSection gradesAndSection = new StudentGradesAndSection();
        gradesAndSection.setJsonrpc(Constants.JSON_RPC);
        gradesAndSection.setParams(params);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString("session_id", "");
        if (sessionId != "") {
            sessionIdValue = "session_id" + "=" + sessionId;
        }
//        Log.e(TAG, "json model data : " + new Gson().toJson(gradesAndSection));
        (Utils.httpService(getContext()).getStudentList(gradesAndSection, sessionIdValue)).enqueue(new Callback<StudentGradesAndSection>() {
            @Override
            public void onResponse(Call<StudentGradesAndSection> call, Response<StudentGradesAndSection> response) {
//                Log.e(TAG, "response : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
//                        Log.e(TAG, "response data : " + new Gson().toJson(response.body().getResult()));
                        students.clear();
                        int attendanceCount = 0;
                        List<Student> studList = response.body().getResult().getStudents();
                        students.addAll(studList);
                        for (int pos = 0; pos < students.size(); pos++) {
                            if (students.get(pos).getIsPresent()) {
                                attendanceCount++;
                            } else {
                                if (attendanceCount != 0) {
                                    attendanceCount--;
                                }
                            }
                        }
                        if (attendanceCount == 0) {
                            for (int pos = 0; pos < students.size(); pos++) {
                                students.get(pos).setIsPresent(true);
                            }
                        }
                        if (students.isEmpty()) {
                            recyclerStudents.setVisibility(View.GONE);
                            tvNoStudents.setVisibility(View.VISIBLE);
                        } else {
                            tvNoStudents.setVisibility(View.GONE);
                            recyclerStudents.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Utils.showToastCustom(getContext(), "Cannot get student list please try again later");
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "Excpetion : " + e);
                }
            }

            @Override
            public void onFailure(Call<StudentGradesAndSection> call, Throwable t) {
//                Log.e(TAG, "Failure : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(getContext(), "Please check your network connection");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
