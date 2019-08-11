package com.nconnect.teacher.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.adapter.ApprovalAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.listener.RecyclerTouchListener;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApprovalStoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Approval Stories";
    private SwipeRefreshLayout swipeToRefreshApprovalStories;
    private RecyclerView recyclerApproval;
    private Toolbar toolbar;
    private TextView toolbartitle;
    private ImageView toolbarlogo;
    private BottomNavigationView navigationView;
    private LinearLayout containerSchoolBoard;
    private List<Story> storiesLIst;
    private LinearLayoutManager linearLayoutManager;
//    private ApprovalAdapter mAdapter;

    public ApprovalStoriesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_approval_stories, container, false);
        initializeViews(rootView);
        return rootView;
    }

    private void initializeViews(View rootView) {
        storiesLIst = new ArrayList<>();
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        toolbar = ((Dashboard) getActivity()).getToolbar();
        toolbarlogo = ((Dashboard) getActivity()).getToolBarLogo();
        toolbartitle = ((Dashboard) getActivity()).getToolbarTitle();
        navigationView = ((Dashboard) getActivity()).getNavigationView();
        swipeToRefreshApprovalStories = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_Approval_stories);
        recyclerApproval = (RecyclerView) rootView.findViewById(R.id.approval_stories_recycler_view);
        swipeToRefreshApprovalStories.setOnRefreshListener(this);
        swipeToRefreshApprovalStories.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        intializeListener();

    }

    private void intializeListener() {
        recyclerApproval.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerApproval, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
        recyclerApproval.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    swipeToRefreshApprovalStories.setEnabled(true);
                else
                    swipeToRefreshApprovalStories.setEnabled(false);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

    private void loadFragment() {
        try {
            navigationView.setSelectedItemId(R.id.nav_home);
            Menu menuNav = toolbar.getMenu();
            menuNav.findItem(R.id.notification).setVisible(true);
            menuNav.findItem(R.id.create_issue).setVisible(false);
            toolbarlogo.setVisibility(View.VISIBLE);
            toolbartitle.setVisibility(View.GONE);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new DashboardFragment());
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (IllegalStateException e) {
            Log.e(TAG, "exception : " + e);
        }
    }


    private void getApprovalStories() {

        swipeToRefreshApprovalStories.setRefreshing(true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        int userTOken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setStatus("pending");
        params.setLoginType("teacher");
        params.setUserToken(userTOken);
        Stories stories = new Stories();
        stories.setStoriesParams(params);
        stories.setJsonrpc(Constants.JSON_RPC);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e(TAG, "json model data : " + new Gson().toJson(stories));
        (Utils.httpService(getContext()).getPendingStories(stories, sessionIdValue)).enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, Response<Stories> response) {
//                Log.e(TAG, "response json : " + new Gson().toJson(response.body()));
//                Log.e(TAG, "error body ; " + new Gson().toJson(response.errorBody()));
                try {
                    Result result = null;
                    if (response.body() != null) {
                        swipeToRefreshApprovalStories.setRefreshing(false);
                        result = response.body().getResult();
                        ApprovalAdapter mAdapter = new ApprovalAdapter(getActivity(), result.getStories(), 0);
                        linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerApproval.setLayoutManager(linearLayoutManager);
                        recyclerApproval.setHasFixedSize(true);
                        recyclerApproval.setItemAnimator(new DefaultItemAnimator());
                        recyclerApproval.setAdapter(mAdapter);
                    }
                } catch (NullPointerException e) {
                    swipeToRefreshApprovalStories.setRefreshing(false);
//                    Log.e(TAG, "Null pointer exceoption : " + e);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
//                Log.e(TAG, "Error : " + t);
                swipeToRefreshApprovalStories.setRefreshing(false);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToast(getContext(), "Please check your network connection");
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
        getApprovalStories();
    }

    @Override
    public void onRefresh() {
        getApprovalStories();
    }

}
