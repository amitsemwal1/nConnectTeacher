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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.adapter.ApprovalAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejectedStoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "REjected stories";
    private RecyclerView recyclerRejectedStories;
    private SwipeRefreshLayout swipeRefreshtRejectedStories;
    private SharedPreferences sharedPreferences;
    private List<Story> storiesList;
    private ApprovalAdapter mAdapter;
    private Toolbar toolbar;
    private View placeHolderRejectedstories;
    private ImageView ivOnbackPress;
    private LinearLayoutManager linearLayoutManager;

    public RejectedStoriesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rejected_stories, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        ivOnbackPress = (ImageView) view.findViewById(R.id.closeRejectedStories);
        recyclerRejectedStories = (RecyclerView) view.findViewById(R.id.rejected_stories_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        swipeRefreshtRejectedStories = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_rejected_stories);
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        storiesList = new ArrayList<>();
        toolbar = ((Dashboard) getActivity()).getToolbar();
        toolbar.setVisibility(View.GONE);
        mAdapter = new ApprovalAdapter(getActivity(), storiesList, 2);
        recyclerRejectedStories.setLayoutManager(linearLayoutManager);
        recyclerRejectedStories.setAdapter(mAdapter);
        placeHolderRejectedstories = view.findViewById(R.id.rejectedStories_placeholder);
        swipeRefreshtRejectedStories.setOnRefreshListener(this);
        swipeRefreshtRejectedStories.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        swipeRefreshtRejectedStories.post(new Runnable() {
            @Override
            public void run() {
                loadRejectedStories();
            }
        });
        initializeListener();
    }

    private void initializeListener() {

        ivOnbackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        recyclerRejectedStories.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    swipeRefreshtRejectedStories.setEnabled(true);
                else
                    swipeRefreshtRejectedStories.setEnabled(false);
            }
        });
    }

    private void loadRejectedStories() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getRejectedStories();
            }
        }, 200);
    }


    private void getRejectedStories() {
        swipeRefreshtRejectedStories.setRefreshing(true);
        int userTOken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setUserToken(userTOken);
        params.setStatus("rejected");
        params.setLoginType("teacher");
        Stories stories = new Stories();
        stories.setStoriesParams(params);
        stories.setJsonrpc(Constants.JSON_RPC);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e(TAG, "json model data : " + new Gson().toJson(stories));
        (Utils.httpService(getContext()).getRejectedStories(stories, sessionIdValue)).enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, Response<Stories> response) {
//                Log.e(TAG, "response json : " + new Gson().toJson(response.body()));
//                Log.e(TAG, "error body ; " + new Gson().toJson(response.errorBody()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        storiesList.clear();
                        List<Story> storiesData = response.body().getResult().getStories();
                        storiesList.addAll(storiesData);
                        if (storiesList.isEmpty()) {
                            swipeRefreshtRejectedStories.setRefreshing(false);
                            recyclerRejectedStories.setVisibility(View.GONE);
                            placeHolderRejectedstories.setVisibility(View.VISIBLE);
                        } else {
                            mAdapter.notifyDataSetChanged();
                            recyclerRejectedStories.setVisibility(View.VISIBLE);
                            swipeRefreshtRejectedStories.setRefreshing(false);
                            placeHolderRejectedstories.setVisibility(View.GONE);
                        }
                    } else {
                        swipeRefreshtRejectedStories.setRefreshing(false);
                        recyclerRejectedStories.setVisibility(View.GONE);
                        placeHolderRejectedstories.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    swipeRefreshtRejectedStories.setRefreshing(false);
                    recyclerRejectedStories.setVisibility(View.GONE);
                    placeHolderRejectedstories.setVisibility(View.VISIBLE);
                    swipeRefreshtRejectedStories.setRefreshing(false);
//                    Log.e(TAG, "Null pointer exceoption : " + e);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
//                Log.e(TAG, "Error : " + t);
                swipeRefreshtRejectedStories.setRefreshing(false);
                swipeRefreshtRejectedStories.setRefreshing(false);
                recyclerRejectedStories.setVisibility(View.GONE);
                placeHolderRejectedstories.setVisibility(View.VISIBLE);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Toast.makeText(getContext(), "Please check your network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        loadRejectedStories();
    }
}
