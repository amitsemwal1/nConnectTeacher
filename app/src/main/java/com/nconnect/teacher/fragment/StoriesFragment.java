package com.nconnect.teacher.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.adapter.StoriesAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Stories Fragment";
    private RecyclerView recyclerStories;
    private SwipeRefreshLayout swipeToRefreshStories;
    private View placeHolderStories;
    private List<Story> storiesList;
    private LinearLayoutManager layoutManager;
    private StoriesAdapter mAdapter;
    private Toolbar toolbar;
    private String type = "";
    private Spinner spnSchoolBoard;
    private LinearLayout containerSchoolBoard;
    private String base_url = "";
    private String login = "";
    private int totalCount = 10;
    private int pageNumber = 1;


    public StoriesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_stories, container, false);
        initializeViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void initializeViews(View rootview) {
        storiesList = new ArrayList<>();
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        containerSchoolBoard.setVisibility(View.GONE);
        toolbar = ((Dashboard) getActivity()).getToolbar();
        spnSchoolBoard = (Spinner) rootview.findViewById(R.id.spnSchoolBoard);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_filter_item, Constants.FILTERS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSchoolBoard.setAdapter(adapter);
        placeHolderStories = rootview.findViewById(R.id.stories_placeholder);
        recyclerStories = (RecyclerView) rootview.findViewById(R.id.stories_recycler_view);
        swipeToRefreshStories = (SwipeRefreshLayout) rootview.findViewById(R.id.stories_refresh_container);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerStories.setLayoutManager(layoutManager);
        mAdapter = new StoriesAdapter(getActivity(), storiesList);
        recyclerStories.setAdapter(mAdapter);

        swipeToRefreshStories.setOnRefreshListener(this);
        swipeToRefreshStories.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        initListener();

        type = getArguments().getString("type");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        if (type != null && type != "") {
            if (type.equalsIgnoreCase("my_story")) {
                base_url = Constants.MY_STORIES;
                spnSchoolBoard.setVisibility(View.GONE);
                loadStories();
            } else if (type.equalsIgnoreCase("school_stories")) {
                base_url = Constants.SCHOOL_BOARD_STORIES;
                spnSchoolBoard.setVisibility(View.VISIBLE);
            }
        }

        likeCommentListener();
    }

    private void likeCommentListener() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("like_comment_count");
        bManager.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.e(TAG, "details captured");
            if (intent.getAction().equals("like_comment_count")) {
                boolean getIsCount = intent.getBooleanExtra("sendCount", false);
                if (getIsCount) {
                    int currentPosition = intent.getIntExtra("sendPosition", -1);
                    int likes = intent.getIntExtra("sendLikes", -1);
                    int comments = intent.getIntExtra("sendComments", -1);
                    boolean isLike = intent.getBooleanExtra("sendIsLiked", false);
//                    Log.e(TAG, "current position : " + currentPosition + "\n " + "likes : " + likes + "\n" + "comments : " + comments);
                    storiesList.get(currentPosition).setLikes(likes);
                    storiesList.get(currentPosition).setComments(comments);
                    storiesList.get(currentPosition).setLike(isLike);
                    mAdapter.notifyItemChanged(currentPosition);
                }
            }
        }
    };

    private void initListener() {
        recyclerStories.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    swipeToRefreshStories.setEnabled(true);
                } else {
                    swipeToRefreshStories.setEnabled(false);
                }
                int visibleItemCount = layoutManager.getChildCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                if (lastInScreen == totalCount) {
                    //here the section to increase the page number to get next 20 items
                    totalCount += 10;
                    pageNumber += 1;
                    loadStories();
                }
            }
        });
        spnSchoolBoard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = Constants.FILTERS[position];
                if (selectedFilter.equalsIgnoreCase("Events")) {
                    ((Dashboard) getActivity()).openSchoolBoardEvents();
                } else if (selectedFilter.equalsIgnoreCase("Announcements")) {
                    ((Dashboard) getActivity()).openSchoolBoardAnnoucements();
                } else if (selectedFilter.equalsIgnoreCase("Stories")) {
                    loadStories();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadStories() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getStories();
            }
        }, 200);
    }

    public void getStories() {
        swipeToRefreshStories.setRefreshing(true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        int studentToken = sharedPreferences.getInt(Constants.STUDENT_TOKENS, 0);
//        Log.e(TAG, "student token : " + studentToken);
        Params storiesParams = new Params();
        if (userToken != 0) {
            storiesParams.setUserToken(userToken);
        }
        storiesParams.setPage(pageNumber);

//        storiesParams.setStatus("approved");
//        storiesParams.setLoginType("");
        final Stories stories = new Stories();
        stories.setJsonrpc(Constants.JSON_RPC);
        stories.setStoriesParams(storiesParams);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e(TAG, "base_url : " + base_url);
//        Log.e(TAG, "Stories model json data : " + new Gson().toJson(stories));
        (Utils.httpService(getContext()).stories(base_url, stories, sessionIdValue)).enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, Response<Stories> response) {
//                Log.e(TAG, "Stories json :" + new Gson().toJson(response.body()));

                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        List<Story> stories = response.body().getResult().getStories();
//                        storiesList.clear();
                        storiesList.addAll(stories);
                        if (storiesList.isEmpty()) {
                            swipeToRefreshStories.setRefreshing(false);
                            placeHolderStories.setVisibility(View.VISIBLE);
                            recyclerStories.setVisibility(View.GONE);
                        } else {
                            recyclerStories.setVisibility(View.VISIBLE);
                            placeHolderStories.setVisibility(View.GONE);
                            swipeToRefreshStories.setRefreshing(false);
//                            mAdapter.notifyDataSetChanged();
                            mAdapter.notifyItemInserted(storiesList.size() - 1);
                        }
                    } else {
                        swipeToRefreshStories.setRefreshing(false);
                        recyclerStories.setVisibility(View.GONE);
                        placeHolderStories.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    recyclerStories.setVisibility(View.GONE);
//                    Log.e(TAG, "Nullpointer : " + e);
                    Toast.makeText(getContext(), "Cannot load stories please try again", Toast.LENGTH_SHORT).show();
                    placeHolderStories.setVisibility(View.VISIBLE);
                    swipeToRefreshStories.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
                swipeToRefreshStories.setRefreshing(false);
                placeHolderStories.setVisibility(View.VISIBLE);
                recyclerStories.setVisibility(View.GONE);
//                Log.e(TAG, "Failure response : " + t.getMessage());
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(getContext(), Constants.NO_CONNECTION);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        storiesList.clear();
        mAdapter.notifyDataSetChanged();
        pageNumber = 1;
        totalCount = 10;
        loadStories();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (containerSchoolBoard.getVisibility() == View.VISIBLE) {
            containerSchoolBoard.setVisibility(View.GONE);
        }
    }

}
