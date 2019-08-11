package com.nconnect.teacher.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Vals;
import com.nconnect.teacher.model.announcements.Announcements;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.stories.Class;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PostAnnouncement extends Fragment implements DialogVpClasses.VpClassesListener {


    private static final String TAG = "Post Announcement";
    private EditText edTitle, edDEscription;
    private Button btnPostAnnouncement;
    private Toolbar toolbar;
    private BottomNavigationView navigationView;
    private TextView toolbarTitle;
    private ImageView toolBarLogo;
    private TextView tvSelectStandards, tvTExtPublish;
    private ImageView ivArrowSelectStandards;
    private RelativeLayout containerClasses;
    private List<Class> classList;
    private LinearLayout containerSchoolBoard;
    private String postTo = "";

    public PostAnnouncement() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_announcement, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        classList = new ArrayList<>();
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        tvTExtPublish = (TextView) view.findViewById(R.id.textPublish);
        tvSelectStandards = (TextView) view.findViewById(R.id.select_standards);
        ivArrowSelectStandards = (ImageView) view.findViewById(R.id.arrowSelectstandards);
        containerClasses = (RelativeLayout) view.findViewById(R.id.containerPusblishClasses);
        edTitle = (EditText) view.findViewById(R.id.post_announcement_title);
        edDEscription = (EditText) view.findViewById(R.id.post_announcement_description);
        btnPostAnnouncement = (Button) view.findViewById(R.id.post_announcement_submit);
        toolbar = ((Dashboard) getActivity()).getToolbar();
        navigationView = ((Dashboard) getActivity()).getNavigationView();
        toolBarLogo = ((Dashboard) getActivity()).getToolBarLogo();
        toolbarTitle = ((Dashboard) getActivity()).getToolbarTitle();
        initializeListeners();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String loginType = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
       /* if (loginType.equalsIgnoreCase("vice_principal")) {
            tvSelectStandards.setVisibility(View.VISIBLE);
            containerClasses.setVisibility(View.GONE);
        } else {
            tvSelectStandards.setVisibility(View.GONE);
            containerClasses.setVisibility(View.GONE);
        }*/
        btnPostAnnouncement.setVisibility(View.GONE);
        containerClasses.setVisibility(View.GONE);
        checkFieldsNotEmpty();
    }

    public void checkFieldsNotEmpty() {
        if (!edTitle.getText().toString().isEmpty() && !edDEscription.getText().toString().isEmpty()) {
            btnPostAnnouncement.setEnabled(true);
            btnPostAnnouncement.setBackground(getResources().getDrawable(R.drawable.ncp_button_back));
        } else {
            btnPostAnnouncement.setEnabled(false);
            btnPostAnnouncement.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
        }
    }

    private void initializeListeners() {

        tvSelectStandards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectStandards.setVisibility(View.GONE);
                containerClasses.setVisibility(View.VISIBLE);
                selectStandard();
            }
        });
        ivArrowSelectStandards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStandard();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();
            }
        });
        btnPostAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postAnnoucementByVp();
            }
        });
        edTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsNotEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edDEscription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsNotEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void selectStandard() {
        DialogVpClasses vpClasses = DialogVpClasses.newInstance();
        vpClasses.setTargetFragment(PostAnnouncement.this, 400);
        vpClasses.show(getActivity().getSupportFragmentManager(), "show classes");
    }


    @Override
    public void getVpClasses(List<Grade> grades, String postTo) {
        String text = "";
        classList.clear();
        if (classList.isEmpty() && postTo.isEmpty()) {
            btnPostAnnouncement.setVisibility(View.GONE);
        }

        if (postTo.equalsIgnoreCase("true")) {
            tvTExtPublish.setText("Post to All Parents");
            btnPostAnnouncement.setVisibility(View.VISIBLE);
            this.postTo = postTo;
        } else {
            btnPostAnnouncement.setVisibility(View.VISIBLE);
            for (int i = 0; i < grades.size(); i++) {
                text = text + grades.get(i).getName() + ",";
                Class classes = new Class();
                classes.setGradeId(grades.get(i).getId());
                classList.add(classes);
            }
            text = "grades : " + text;
            tvTExtPublish.setText(text);
        }
    }

    private void loadFragment() {
        try {
            navigationView.setSelectedItemId(R.id.nav_home);
            Menu menuNav = toolbar.getMenu();
            menuNav.findItem(R.id.notification).setVisible(true);
            menuNav.findItem(R.id.create_issue).setVisible(false);
            toolBarLogo.setVisibility(View.VISIBLE);
            toolbarTitle.setVisibility(View.GONE);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new DashboardFragment());
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (IllegalStateException e) {
//            Log.e(TAG, "exception : " + e);
        }
    }

    private void postAnnoucementByVp() {
        if (classList.isEmpty() && postTo.isEmpty()) {
            Toast.makeText(getContext(), "Please select class and section", Toast.LENGTH_SHORT).show();
            return;
        }
        Vals vals = new Vals();
        vals.setName(edTitle.getText().toString());
        vals.setDescription(edDEscription.getText().toString());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String loginType = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
       /* if (loginType.equalsIgnoreCase("vice_principal")) {
            if (classList.isEmpty()) {
                vals.setClasses(classList);
                vals.setPostTo("all");
            } else {
                vals.setClasses(classList);
                vals.setPostTo("");
            }
        } else {
            vals.setClasses(classList);
            vals.setPostTo("all");
        }*/
        if (classList.isEmpty()) {
            vals.setClasses(classList);
            vals.setPostTo("all");
        } else {
            vals.setClasses(classList);
            vals.setPostTo("");
        }
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        vals.setDate(date);
        Params params = new Params();
        params.setVals(vals);
        Announcements postAnnounce = new Announcements();
        postAnnounce.setParams(params);
        postAnnounce.setJsonrpc(Constants.JSON_RPC);
//        Log.e(TAG, "json model data : " + new Gson().toJson(postAnnounce));
        String sessionId = sharedPreferences.getString("session_id", "");
        String sessionIdValue = "";
        if (sessionId != "") {
            sessionIdValue = "session_id" + "=" + sessionId;
        }
        ProgressDialog dialog = Utils.showDialog(getContext(), "Please wait while creating announcements...");
        dialog.show();
        (Utils.httpService(getContext()).postAnnouncementVp(postAnnounce, sessionIdValue)).enqueue(new Callback<Announcements>() {
            @Override
            public void onResponse(Call<Announcements> call, Response<Announcements> response) {
//                Log.e(TAG, "responmse : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "announcement created successfully", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Cannot create announcements", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    dialog.dismiss();
//                    Log.e(TAG, "exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<Announcements> call, Throwable t) {
//                Log.e(TAG, "Failure : " + t);
                dialog.dismiss();
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
//                    Log.e(TAG, "Please check your network Connection");
                }
            }
        });

    }
/*

    private void postAnnouncement() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt("user_token", 0);
        Arg arg = new Arg();
        arg.setName(edTitle.getText().toString());
        arg.setDescription(edDEscription.getText().toString());
        arg.setPostTo("all");
        Params params = new Params();
        List<Arg> argList = new ArrayList<>();
        argList.add(arg);
        Kwargs kwargs = new Kwargs();
        params.setArgs(argList);
        params.setKwargs(kwargs);
        params.setMethod("create");
        params.setModel("pappaya.announcement");
        PostAnnounce announcement = new PostAnnounce();
        announcement.setJsonrpc("2.0");
        announcement.setParams(params);
        Log.e(TAG, "Create announcement Model : " + new Gson().toJson(announcement));
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString("session_id", "");
        if (sessionId != "") {
            sessionIdValue = "session_id" + "=" + sessionId;
        }
        ProgressDialog dialog = Utils.showDialog(getContext(), "Please wait while creating announcement...");
        dialog.show();
        (Utils.httpService(getContext()).postAnnouncement(announcement, sessionIdValue)).enqueue(new Callback<PostAnnounce>() {
            @Override
            public void onResponse(Call<PostAnnounce> call, Response<PostAnnounce> response) {

                Log.e(TAG, "response  : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResultStr() != 0) {
                        dialog.dismiss();
                        Utils.showToast(getContext(), "Announcement created");
                        getActivity().onBackPressed();
                    } else {
                        dialog.dismiss();
                        Utils.showToast(getContext(), "Cannot create announcement");
                    }
                } catch (NullPointerException e) {
                    dialog.dismiss();
                    Log.e(TAG, "Exception ; " + e);
                }
            }

            @Override
            public void onFailure(Call<PostAnnounce> call, Throwable t) {
                dialog.dismiss();
                Log.e(TAG, "Failure : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Log.e(TAG, "Please check your network Connection");
                }
            }
        });

    }
*/

    @Override
    public void onResume() {
        super.onResume();
        if (containerSchoolBoard.getVisibility() == View.VISIBLE) {
            containerSchoolBoard.setVisibility(View.GONE);
        }
    }
}
