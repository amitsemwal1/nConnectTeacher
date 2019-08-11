package com.nconnect.teacher.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.VpClassAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.gradeandsection.StudentGradesAndSection;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class DialogVpClasses extends DialogFragment {

    private static final String TAG = DialogVpClasses.class.getSimpleName();
    private RecyclerView recyclerViewClasses;
    private CheckedTextView cTvPosttoAllParent;
    private Button btnCancel, btnSave;
    private List<Grade> grades;

    public interface VpClassesListener {
        void getVpClasses(List<Grade> grades, String postTo);
    }

    public static DialogVpClasses newInstance() {
        DialogVpClasses classes = new DialogVpClasses();
        return classes;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_vp_classes, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        grades = new ArrayList<>();
        btnCancel = (Button) view.findViewById(R.id.vpClassCancel);
        btnSave = (Button) view.findViewById(R.id.vpClassSave);
        recyclerViewClasses = (RecyclerView) view.findViewById(R.id.recyclerClasses);
        cTvPosttoAllParent = (CheckedTextView) view.findViewById(R.id.publishto_all);
        initListener();
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String loginType = preferences.getString(Constants.LOGIN_TYPE, "");
        if (loginType.equalsIgnoreCase("principal")) {
            getClassesByPrinicpal();
        } else {
            getClassesByVp();
        }
    }

    private void initListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishClasses();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().detach(DialogVpClasses.this).commit();
            }
        });
        cTvPosttoAllParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cTvPosttoAllParent.isChecked()) {
                    cTvPosttoAllParent.setChecked(false);
                } else {
                    cTvPosttoAllParent.setChecked(true);
                }
            }
        });
    }


    private void publishClasses() {

        List<Grade> gradeList = new ArrayList<>();

        for (int i = 0; i < grades.size(); i++) {
            Grade grade = new Grade();
            grade.setName(grades.get(i).getName());
            grade.setId(grades.get(i).getId());
            grade.setGradeChecked(grades.get(i).getGradeChecked());
            gradeList.add(grade);
        }

//        Log.e(TAG, "grades and section : " + new Gson().toJson(gradeList));

        List<Grade> filteredGrades = new ArrayList<>();
        for (int i = 0; i < gradeList.size(); i++) {
            if (gradeList.get(i).getGradeChecked().equalsIgnoreCase("true")) {
                Grade grade = new Grade();
                grade.setId(gradeList.get(i).getId());
                grade.setName(gradeList.get(i).getName());
                filteredGrades.add(grade);
            }
        }
        if (filteredGrades.isEmpty() && !cTvPosttoAllParent.isChecked()) {
            Toast.makeText(getContext(), "Please select atleast one section", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!filteredGrades.isEmpty() && cTvPosttoAllParent.isChecked()) {
            Toast.makeText(getContext(), "Please select any one option ", Toast.LENGTH_SHORT).show();
            return;
        }

        String status = "";
        if (filteredGrades.isEmpty() && cTvPosttoAllParent.isChecked()) {
            status = "true";
        } else if (!filteredGrades.isEmpty() && cTvPosttoAllParent.isChecked()) {
            status = "false";
        }
        VpClassesListener listener = (VpClassesListener) getTargetFragment();
        listener.getVpClasses(filteredGrades, status);
        dismiss();
    }

    private void getClassesByPrinicpal() {
        Params params = new Params();
        StudentGradesAndSection gradesAndSection = new StudentGradesAndSection();
        gradesAndSection.setParams(params);
//        Log.e(TAG, "model data : " + new Gson().toJson(gradesAndSection));
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String session = preferences.getString(Constants.SESSION_ID, "");
        session = Constants.SESSION_ID + "=" + session;
        (Utils.httpService(getContext()).getClassByPrinicpal(gradesAndSection, session)).enqueue(new Callback<StudentGradesAndSection>() {
            @Override
            public void onResponse(Call<StudentGradesAndSection> call, Response<StudentGradesAndSection> response) {
//                Log.e(TAG, "response : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        grades.clear();
                        grades = response.body().getResult().getGradesList();
                        VpClassAdapter classAdapter = new VpClassAdapter(getContext(), grades);
                        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerViewClasses.setAdapter(classAdapter);
                    } else {
                        Toast.makeText(getContext(), "Cannot get classes", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "excpetion : " + e);
                }
            }

            @Override
            public void onFailure(Call<StudentGradesAndSection> call, Throwable t) {
//                Log.e(TAG, "Response ; " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Toast.makeText(getContext(), "Please check your network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getClassesByVp() {
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
//                            Log.e(TAG, "grades : list " + new Gson().toJson(grades));
                            VpClassAdapter classAdapter = new VpClassAdapter(getContext(), grades);
                            recyclerViewClasses.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerViewClasses.setAdapter(classAdapter);
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
}
