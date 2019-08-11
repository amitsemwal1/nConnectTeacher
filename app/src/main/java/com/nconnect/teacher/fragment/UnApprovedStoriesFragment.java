package com.nconnect.teacher.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.adapter.ApprovalAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnApprovedStoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = UnApprovedStoriesFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeToRefreshPendingStories;
    private RecyclerView recyclerPendingStories;
    private TextView tvRejectedStories;
    private List<Story> storiesList;
    private ApprovalAdapter mAdapter;
    private View placeHolderUnapproved;
    private Toolbar toolbar;
    private LinearLayout containerSchoolBoard;
    private LinearLayoutManager linearlayoutmanager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unapproved_stories, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        storiesList = new ArrayList<>();
        toolbar = ((Dashboard) getActivity()).getToolbar();
        toolbar.setVisibility(View.VISIBLE);
        placeHolderUnapproved = view.findViewById(R.id.unapprovedstories_placeholder);
        swipeToRefreshPendingStories = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_pending_stories);
        recyclerPendingStories = (RecyclerView) view.findViewById(R.id.pending_stories_recycler_view);
        linearlayoutmanager = new LinearLayoutManager(getActivity());
        tvRejectedStories = (TextView) view.findViewById(R.id.rejectedStories);
        swipeToRefreshPendingStories.setOnRefreshListener(this);

        mAdapter = new ApprovalAdapter(getActivity(), storiesList, 1);
        recyclerPendingStories.setLayoutManager(linearlayoutmanager);
        recyclerPendingStories.setAdapter(mAdapter);

        initListeners();
    }

    private void initListeners() {
        swipeToRefreshPendingStories.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));

        recyclerPendingStories.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (linearlayoutmanager.findFirstCompletelyVisibleItemPosition() == 0) {
                    swipeToRefreshPendingStories.setEnabled(true);
                } else {
                    swipeToRefreshPendingStories.setEnabled(false);
                }
            }
        });
        swipeToRefreshPendingStories.post(new Runnable() {
            @Override
            public void run() {
                loadPendingStories();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        tvRejectedStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Dashboard) getActivity()).loadFragment(new RejectedStoriesFragment(), "REJECTED_STORIES");
            }
        });
    }

    private void loadPendingStories() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPendingStories();
            }
        }, 200);
    }

    private void getPendingStories() {
        swipeToRefreshPendingStories.setRefreshing(true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        int userTOken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setUserToken(userTOken);
        params.setLoginType("teacher");
        params.setStatus("pending");
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
                try {
                    Result result = null;
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        result = response.body().getResult();
                        storiesList.clear();
                        List<Story> stories = result.getStories();
                        storiesList.addAll(stories);
                        if (storiesList.isEmpty()) {
                            placeHolderUnapproved.setVisibility(View.VISIBLE);
                            recyclerPendingStories.setVisibility(View.GONE);
                            swipeToRefreshPendingStories.setRefreshing(false);
                        } else {
                            placeHolderUnapproved.setVisibility(View.GONE);
                            recyclerPendingStories.setVisibility(View.VISIBLE);
                            swipeToRefreshPendingStories.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        swipeToRefreshPendingStories.setRefreshing(false);
                        Toast.makeText(getContext(), "Cannot get Un Approved Stories", Toast.LENGTH_SHORT).show();
                        placeHolderUnapproved.setVisibility(View.VISIBLE);
                        recyclerPendingStories.setVisibility(View.GONE);
                    }
                } catch (NullPointerException e) {
                    placeHolderUnapproved.setVisibility(View.VISIBLE);
                    recyclerPendingStories.setVisibility(View.GONE);
                    swipeToRefreshPendingStories.setRefreshing(false);
//                    Log.e(TAG, "Null pointer exceoption : " + e);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
//                Log.e(TAG, "Error : " + t);
                recyclerPendingStories.setVisibility(View.GONE);
                placeHolderUnapproved.setVisibility(View.VISIBLE);
                swipeToRefreshPendingStories.setRefreshing(false);
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
    }

    @Override
    public void onRefresh() {
        loadPendingStories();
    }

}
