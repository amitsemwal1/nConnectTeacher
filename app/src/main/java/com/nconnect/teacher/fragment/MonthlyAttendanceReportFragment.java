package com.nconnect.teacher.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.adapter.CustomSpinnerAdapter;
import com.nconnect.teacher.adapter.MonthlyAttendanceAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Atnc;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.TeacherClassSectionCombinedApi;
import com.nconnect.teacher.model.TeacherCombinedClassSectionCallback;
import com.nconnect.teacher.model.attendance.Attendance;
import com.nconnect.teacher.model.gradeandsection.StudentGradesAndSection;
import com.nconnect.teacher.model.stories.ClassSectionList;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MonthlyAttendanceReportFragment extends Fragment implements TeacherCombinedClassSectionCallback {

    private static final String TAG = "monthly attendance ";
    private Toolbar toolbar;
    private Spinner spnMonth,spnClass;
    private TextView noReportText,workingDays;
    private RecyclerView recyclerView;
    private String sectionId = "0", gradeId = "0";
    private Button showResult;
    private int slectedPos = 0;

    private List<Atnc> monthlyAttendance;
    private MonthlyAttendanceAdapter adapter;
    List<ClassSectionList> classSectionLists;

    private String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_attendance_report, container, false);
        initializeViews(view);
        initializeClasseAndSection();
        initializeListeners();
//        getAttendanceReport();

        return view;
    }

    private void initializeViews(View view) {
        monthlyAttendance = new ArrayList<>();
        toolbar = ((Dashboard) getActivity()).getToolbar();
        spnMonth = view.findViewById(R.id.attendance_report_months);
        spnClass = view.findViewById(R.id.attendance_report_classandsection);
        recyclerView = view.findViewById(R.id.monthly_attendance_recycler_view);
        noReportText = view.findViewById(R.id.no_report_text);
        workingDays = view.findViewById(R.id.working_days);
        showResult = view.findViewById(R.id.show_result);
        adapter = new MonthlyAttendanceAdapter(getContext(),monthlyAttendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ArrayAdapter<String> spinnerArrayAdapter =
                new ArrayAdapter<String>(getContext(), R.layout.spinner_filter_item, months); //selected item will look like a spinner set from XML
        //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spnMonth.setAdapter(spinnerArrayAdapter);
    }
    private void initializeListeners(){
       spnClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showResult.setBackgroundResource(R.drawable.ncp_appbar);
                sectionId = classSectionLists.get(position).sectionId;
                gradeId = classSectionLists.get(position).gradeID;

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
       spnMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showResult.setBackgroundResource(R.drawable.ncp_appbar);
                slectedPos =  position+1 ;
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAttendanceReport();

            }
        });

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
        // Calling the teacher class section comined api
        TeacherClassSectionCombinedApi teacherClassSectionCombinedApi = new
                TeacherClassSectionCombinedApi(getActivity(),this,sessionIdValue);
        teacherClassSectionCombinedApi.execute();
    }
    private void getAttendanceReport(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt("user_token", 0);
        Params params = new Params();
//        if (userToken != 0) {
//            params.setUserToken(userToken);
//        }
        params.setMonth(slectedPos);
        params.setGradeId(Integer.parseInt(gradeId));
        params.setSectionId(Integer.parseInt(sectionId));
        Attendance attendance = new Attendance();
//        attendance.setJsonrpc(Constants.JSON_RPC);
        attendance.setParams(params);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString("session_id", "");
        if (sessionId != "") {
            sessionIdValue = "session_id" + "=" + sessionId;
        }


        (Utils.httpService(getContext()).viewStudentAttendanceMonth(attendance, sessionIdValue)).enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(Call<Attendance> call, Response<Attendance> response) {
//                Log.e(TAG, "reposnse body : " + new Gson().toJson(response.body()));
                try {
                    Attendance attendance1 = response.body();
                    Result result = attendance1.getResult();
                    if (result != null) {
                        if (result.getResponse().equalsIgnoreCase("success")) {

                            monthlyAttendance.clear();
                            List<Atnc> studList = response.body().getResult().getData().getAttendance();
                            workingDays.setText(response.body().getResult().getData().getWorkingDays().toString());
                            monthlyAttendance.addAll(studList);
                            if (monthlyAttendance.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                noReportText.setVisibility(View.VISIBLE);
                            } else {
                                noReportText.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }

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
            public void onFailure(Call<Attendance> call, Throwable t) {
//                Log.e(TAG, "Failure response : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToast(getContext(), "Please check your intenet connection");
                }
            }
        });


    }

    @Override
    public void teacherClassCombinedCallback(List<ClassSectionList> classSectionLists) {
        this.classSectionLists = classSectionLists;
        Log.i("size",""+classSectionLists.size());

        CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getContext(),classSectionLists);

        spnClass.setAdapter(customAdapter);

    }

    @Override
    public void teacherClassCombinedErrorCallback(int responseCode, String responseMessage) {
        Utils.showToastCustom(getContext(), "Something went wrong please try again");

    }
}

