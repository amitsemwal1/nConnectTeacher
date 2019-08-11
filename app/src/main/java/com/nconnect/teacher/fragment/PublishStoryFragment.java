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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.EditStory;
import com.nconnect.teacher.activity.PreviewPostStoryActivity;
import com.nconnect.teacher.adapter.ClassAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.gradeandsection.Section;
import com.nconnect.teacher.model.gradeandsection.StudentGradesAndSection;
import com.nconnect.teacher.model.stories.Args;
import com.nconnect.teacher.model.stories.Class;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PublishStoryFragment extends DialogFragment {

    private static final String TAG = "PublishStory Fragment ";
    private RecyclerView recyclerPublishTo;
    private Button btnCancel, btnDone;
    private List<Grade> grades;
    private boolean isTouched = false;
    List<Args> argsList;
    private CheckedTextView tvCheckAll;
    private String type = "";

    public static PublishStoryFragment newInstance() {
        PublishStoryFragment f = new PublishStoryFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish_story, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        type = getArguments().getString("type");
        grades = new ArrayList<>();
        argsList = new ArrayList<>();
        recyclerPublishTo = (RecyclerView) view.findViewById(R.id.publishto_recycler);
        btnCancel = (Button) view.findViewById(R.id.publishto_cancel);
        btnDone = (Button) view.findViewById(R.id.publishto_save);
        tvCheckAll = (CheckedTextView) view.findViewById(R.id.publishto_all);
        tvCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvCheckAll.isChecked()) {
                    tvCheckAll.setChecked(false);
                } else {
                    tvCheckAll.setChecked(true);
                }
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStory();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().detach(PublishStoryFragment.this).commit();
            }
        });
        recyclerPublishTo.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String login = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        if (login.equalsIgnoreCase("teacher")) {
            tvCheckAll.setVisibility(View.GONE);
        } else {
            tvCheckAll.setVisibility(View.VISIBLE);
        }
        getClassAndSection();
    }

    private void postStory() {
        List<Grade> gradeList = new ArrayList<>();

        for (int i = 0; i < grades.size(); i++) {
            Grade grade = new Grade();
            grade.setGradeChecked(grades.get(i).getGradeChecked());
            grade.setSection(grades.get(i).getSection());
            grade.setName(grades.get(i).getName());
            grade.setId(grades.get(i).getId());
            gradeList.add(grade);
        }
//        Log.e(TAG, "grades and section : " + new Gson().toJson(gradeList));

        String grade = "", section = " ";
        Map<String, String> map = new HashMap<>();
        List<Class> classes = new ArrayList<>();
        int classesSize = gradeList.size();
        for (int i = 0; i < classesSize; i++) {
            List<Section> sections = gradeList.get(i).getSection();
            int sectionSize = sections.size();
            for (int j = 0; j < sectionSize; j++) {
                if (sections.get(j).getStatus()) {
                    grade = gradeList.get(i).getName();
                    Class class_ = new Class();
                    class_.setSectionId(sections.get(j).getSectionId());
                    class_.setGradeId(gradeList.get(i).getId());
                    section = section + gradeList.get(i).getSection().get(j).getSectionName() + ",";
                    classes.add(class_);
                    map.put("class " + grade, " " + section);
                }
            }
            section = "";
        }
//        Log.e(TAG, "map : " + map);
//        Log.e(TAG, "filterd classes list : " + new Gson().toJson(classes));

        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String loginType = preferences.getString(Constants.LOGIN_TYPE, "");
        if (loginType != null && loginType != "") {
            if (loginType.equalsIgnoreCase("teacher")) {

                if (classes.isEmpty()) {
                    Toast.makeText(getContext(), "Please select atleast one section", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                if (classes.isEmpty() && !tvCheckAll.isChecked()) {
                    Toast.makeText(getContext(), "Please select atleast one section", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!classes.isEmpty() && tvCheckAll.isChecked()) {
                    Toast.makeText(getContext(), "Please select any one option ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        String status = "";
        if (classes.isEmpty() && tvCheckAll.isChecked()) {
            status = "true";
        } else if (!classes.isEmpty() && tvCheckAll.isChecked()) {
            status = "false";
        }
        getActivity().getSupportFragmentManager().beginTransaction().detach(PublishStoryFragment.this).commit();
        if (type != "") {
            if (type.equalsIgnoreCase("create")) {
                ((PreviewPostStoryActivity) getActivity()).setClassAndSection(classes, map, status);
            } else if (type.equalsIgnoreCase("edit")) {
                ((EditStory) getActivity()).setClassAndSection(classes, map, status);
            }

        }

    }

    private void getClassAndSection() {

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
//        Log.e(TAG, "grade an section : " + new Gson().toJson(gradesAndSection));
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
                            ClassAdapter classAdapter = new ClassAdapter(getContext(), grades);
                            recyclerPublishTo.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerPublishTo.setAdapter(classAdapter);
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
