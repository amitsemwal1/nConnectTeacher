package com.nconnect.teacher.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.activity.ViewIssueActivity;
import com.nconnect.teacher.adapter.IssueAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.listener.RecyclerTouchListener;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.issues.GetIssuesResponse;
import com.nconnect.teacher.model.issues.Issues;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssuesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Issue fragment";
    private Spinner spnFilter;
    private SwipeRefreshLayout swipeToRefreshIssues;
    private RecyclerView recyclerIssues;
    private String[] filters = {"All", "Active", "Escalate", "Resolved"};
    private String status = "";
    private List<GetIssuesResponse.IssueDetails> issueList;
    private Toolbar toolbar;
    private LinearLayout containerSchoolBoard;
    private IssueAdapter mAdapter;
    private View issuePlaceHolder;
    private LinearLayoutManager layoutManager;

    private int totalCount = 10;
    private int pageNumber = 1;
    public static String stats;


    private static String value = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_issues, container, false);
        initializeViews(rootView);
        return rootView;
    }

    private void initializeViews(View view) {
        issueList = new ArrayList<>();
        issuePlaceHolder = view.findViewById(R.id.issue_placeHolder);
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        spnFilter = (Spinner) view.findViewById(R.id.issues_filter);
        swipeToRefreshIssues = (SwipeRefreshLayout) view.findViewById(R.id.issues_refresh_container);
        recyclerIssues = (RecyclerView) view.findViewById(R.id.issues_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());

        toolbar = ((Dashboard) getActivity()).getToolbar();
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_filter_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilter.setAdapter(adapter);
        swipeToRefreshIssues.setOnRefreshListener(this);
        swipeToRefreshIssues.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        mAdapter = new IssueAdapter(getContext(), issueList);
        recyclerIssues.setItemAnimator(new DefaultItemAnimator());
        recyclerIssues.setHasFixedSize(true);
        recyclerIssues.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerIssues.setAdapter(mAdapter);
        if (stats != null) {
            status = stats;
            spnFilter.setSelection(getIndex(spnFilter, status));

        }
        initializeListeners();
    }

    private void initializeListeners() {
        spnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerStatus = filters[position];
                if (spinnerStatus.equalsIgnoreCase("Active")) {
                    status = "open";
                } else if (spinnerStatus.equalsIgnoreCase("Escalate")) {
                    status = "escalate";
                } else if (spinnerStatus.equalsIgnoreCase("Resolved")) {
                    status = "closed_by_parent";
                } else if (spinnerStatus.equalsIgnoreCase("All")) {
                    status = "All";
                }
                loadIssues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recyclerIssues.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerIssues, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                GetIssuesResponse.IssueDetails issues = issueList.get(position);
                String singlePostData = new Gson().toJson(issues);
                startActivity(new Intent(getContext(), ViewIssueActivity.class)
                        .putExtra(Constants.ISSUES, singlePostData)
                        .putExtra("isScreen", "issuesFragment"));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerIssues.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                if (lastInScreen == totalCount) {

                    //here the section to increase the page number to get next 20 items
                    totalCount += 10;
                    pageNumber += 1;
                    loadIssues();
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadIssues() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getIssues();
            }
        }, 200);
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private void getIssues() {
        swipeToRefreshIssues.setRefreshing(true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        int studentToken = sharedPreferences.getInt(Constants.STUDENT_TOKENS, 0);
        Params issuesParams = new Params();
        if (userToken != 0) {
            issuesParams.setUserToken(userToken);
        }

        if (status.equalsIgnoreCase("escalate")) {
            issuesParams.setStatus(status);
        } else if (status.equalsIgnoreCase("open")) {
            issuesParams.setStatus(status);
        } else if (status.equalsIgnoreCase(Constants.CLOSE_BY_PARENT)) {
            issuesParams.setStatus(status);
        } else if (status.equalsIgnoreCase("All")) {
//            Log.e(TAG, "Do nothing");
        }
        String loginType = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        issuesParams.setLoginType("teacher");
        issuesParams.setPage(pageNumber);
        final Issues issues = new Issues();
        issues.setJsonrpc(Constants.JSON_RPC);
        issues.setParams(issuesParams);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e(TAG, "issues model json data : " + new Gson().toJson(issues));
        (Utils.httpService(getContext()).issuesView(issues, sessionIdValue)).enqueue(new Callback<GetIssuesResponse>() {
            @Override
            public void onResponse(Call<GetIssuesResponse> call, Response<GetIssuesResponse> response) {
//                Log.e(TAG, "issues response :" + new Gson().toJson(response.body()));
                try {
                    swipeToRefreshIssues.setRefreshing(false);
                    issueList.clear();
                    issueList.addAll(response.body().getResult().getIssues());
                    if (issueList.isEmpty()) {
                        issuePlaceHolder.setVisibility(View.VISIBLE);
                        recyclerIssues.setVisibility(View.GONE);
                    } else {
                        recyclerIssues.setVisibility(View.VISIBLE);
                        issuePlaceHolder.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (NullPointerException e) {
                    swipeToRefreshIssues.setRefreshing(false);
//                    Log.e(TAG, "exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<GetIssuesResponse> call, Throwable t) {
//                Log.e(TAG, "error : " + t.getMessage());
                swipeToRefreshIssues.setRefreshing(false);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToast(getContext(), "Please check your internet connection");
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (containerSchoolBoard.getVisibility() == View.VISIBLE) {
            containerSchoolBoard.setVisibility(View.GONE);
        }
        loadIssues();
    }

    @Override
    public void onRefresh() {
        loadIssues();
    }
}
