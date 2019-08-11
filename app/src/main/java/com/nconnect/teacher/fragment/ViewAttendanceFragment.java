package com.nconnect.teacher.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.ViewAttendanceActivity;
import com.nconnect.teacher.adapter.StudentSearchAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.gradeandsection.Student;
import com.nconnect.teacher.model.gradeandsection.StudentGradesAndSection;
import com.nconnect.teacher.model.stories.AttendanceItemOnClickCallback;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ViewAttendanceFragment extends Fragment implements AttendanceItemOnClickCallback {

    private static final String TAG = ViewAttendanceFragment.class.getSimpleName();
    private EditText edStudentName;
    private RecyclerView recyclerStudentList;
    private TextView tvNoStudents;
    private Button searchStudents;
    private StudentSearchAdapter mAdapter;
    private List<Student> students;
    private List<Grade> grades;
    private String studentInfo = "";
    private String searchKey = "";
    private LinearLayoutManager linearLayoutManager;

    public ViewAttendanceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_attendance, container, false);
        initializeViews(view);
        return view;
    }


    private void initializeViews(View view) {
        students = new ArrayList<>();
//        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        edStudentName = (EditText) view.findViewById(R.id.enter_student_name);
        recyclerStudentList = (RecyclerView) view.findViewById(R.id.recycler_filter_student);
        tvNoStudents = (TextView) view.findViewById(R.id.tagNoStudents);
        searchStudents = (Button) view.findViewById(R.id.view_attendance_search);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerStudentList.setLayoutManager(linearLayoutManager);
        mAdapter = new StudentSearchAdapter(students, this::onclickCallback);
        recyclerStudentList.setHasFixedSize(true);
        recyclerStudentList.setAdapter(mAdapter);
        initListener();
//        getStudentList();

    }

    private void initListener() {
     /*   recyclerStudentList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerStudentList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Student student = students.get(position);
                String studentName = students.get(position).getName();
                String studentId = students.get(position).getStudentId();
                String singleStudentdata = new Gson().toJson(student);
                Intent intent = new Intent(getActivity(), ViewAttendanceActivity.class);
                intent.putExtra("student_data", singleStudentdata);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
        edStudentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                studentInfo = s.toString();
                if (studentInfo.length() > 0) {
                    searchKey = s.toString();
                    recyclerStudentList.setVisibility(View.VISIBLE);
                    searchStudents.setEnabled(true);
                    searchStudents.setBackgroundResource(R.drawable.ncp_appbar);

                } else {
                    recyclerStudentList.setVisibility(View.GONE);
                    searchStudents.setEnabled(false);
                    searchStudents.setBackgroundResource(R.color.common_google_signin_btn_text_dark_disabled);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStudentList();
                filter(searchKey);
            }
        });
    }

    private void getStudentList() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userTOken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setStudentInfo("");
        StudentGradesAndSection gradesAndSection = new StudentGradesAndSection();
        gradesAndSection.setJsonrpc(Constants.JSON_RPC);
        gradesAndSection.setParams(params);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString("session_id", "");
        if (sessionId != "") {
            sessionIdValue = "session_id" + "=" + sessionId;
        }
//        Log.e(TAG, "json model data : " + new Gson().toJson(gradesAndSection));
        (Utils.httpService(getContext()).getStudentSearchList(gradesAndSection, sessionIdValue)).enqueue(new Callback<StudentGradesAndSection>() {
            @Override
            public void onResponse(Call<StudentGradesAndSection> call, Response<StudentGradesAndSection> response) {
//                Log.e(TAG, "response for view attendance : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
//                        Log.e(TAG, "response data : " + new Gson().toJson(response.body().getResult()));
                        students.clear();
                        List<Student> studList = response.body().getResult().getStudents();
                        students.addAll(studList);
                        if (students.isEmpty()) {
                            recyclerStudentList.setVisibility(View.GONE);
                            tvNoStudents.setVisibility(View.VISIBLE);
                        } else {
                            tvNoStudents.setVisibility(View.GONE);
                            recyclerStudentList.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
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

    private void filter(String text) {
        List<Student> filteredList = new ArrayList<>();
        for (Student item : students) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getStudentId().contains(text)) {
                filteredList.add(item);
            }
        }
        mAdapter.filterList(filteredList);
    }

    @Override
    public void onclickCallback(String studentName, String studentId) {

        Intent intent = new Intent(getActivity(), ViewAttendanceActivity.class);
        intent.putExtra("student_name", studentName);
        intent.putExtra("student_id", studentId);
        startActivity(intent);

    }
}
