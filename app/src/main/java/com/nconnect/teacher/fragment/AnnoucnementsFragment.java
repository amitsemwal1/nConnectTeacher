package com.nconnect.teacher.fragment;

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
import android.support.v7.widget.OrientationHelper;
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
import com.nconnect.teacher.activity.ViewAnnouncementActivity;
import com.nconnect.teacher.adapter.AnnouncnementAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.listener.RecyclerTouchListener;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.announcements.Announcement;
import com.nconnect.teacher.model.announcements.Announcements;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AnnoucnementsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = AnnoucnementsFragment.class.getSimpleName();
    private RecyclerView recyclerAnnoucnement;
    private SwipeRefreshLayout swipeToRefreshAnnoucnements;
    private View placeHolderAnnouncments;
    private Spinner spnSchoolBoard;
    private LinearLayout containerSchoolBoard;
    private Toolbar toolbar;
    private List<Announcement> annList;
    AnnouncnementAdapter mAdapter;
    private String type = "";
    private String base_url = "";
    private LinearLayoutManager linearLayoutManager;
    private int pageNumber = 1;
    private int totalCount = 10;

    public AnnoucnementsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_announcement, container, false);
        initializeViews(rootView);
        return rootView;
    }

    private void initializeViews(View rootView) {
        annList = new ArrayList<>();
        toolbar = ((Dashboard) getActivity()).getToolbar();
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        spnSchoolBoard = (Spinner) rootView.findViewById(R.id.spnSchoolBoard);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_filter_item, Constants.FILTERS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSchoolBoard.setAdapter(adapter);
        spnSchoolBoard.setSelection(2);
        placeHolderAnnouncments = rootView.findViewById(R.id.announcement_placeholder);
        recyclerAnnoucnement = (RecyclerView) rootView.findViewById(R.id.annoucnements_recycler_view);
        swipeToRefreshAnnoucnements = (SwipeRefreshLayout) rootView.findViewById(R.id.annoucnements_refresh_container);
        swipeToRefreshAnnoucnements.setOnRefreshListener(this);
        swipeToRefreshAnnoucnements.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        linearLayoutManager = new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false);
        mAdapter = new AnnouncnementAdapter(getContext(), annList);
        recyclerAnnoucnement.setLayoutManager(linearLayoutManager);
        recyclerAnnoucnement.setItemAnimator(new DefaultItemAnimator());
        recyclerAnnoucnement.setAdapter(mAdapter);
        initListener();
        type = getArguments().getString("type");
        if (type != null && type != "") {
            if (type.equalsIgnoreCase("my_announcements")) {
                base_url = Constants.MY_ANNOUNCEMENTS;
                spnSchoolBoard.setVisibility(View.GONE);
                loadAnnouncement();
            } else if (type.equalsIgnoreCase("school_annoucements")) {
                base_url = Constants.SCHOOL_BOARD_ANNOUNCEMENTS;
                spnSchoolBoard.setVisibility(View.VISIBLE);
            }
        }

    }

    private void initListener() {
        spnSchoolBoard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = Constants.FILTERS[position];
                if (selectedFilter.equalsIgnoreCase("Events")) {
                    ((Dashboard) getActivity()).openSchoolBoardEvents();
                } else if (selectedFilter.equalsIgnoreCase("Announcements")) {
                    loadAnnouncement();
                } else if (selectedFilter.equalsIgnoreCase("Stories")) {
                    ((Dashboard) getActivity()).openSchoolBoard();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recyclerAnnoucnement.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerAnnoucnement, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Announcement announcement = annList.get(position);
                String singlePostData = new Gson().toJson(announcement);
                startActivity(new Intent(getContext(), ViewAnnouncementActivity.class).putExtra(Constants.ANNOUNCEMENTS, singlePostData)
                        .putExtra("isScreen", "announcementFragment"));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerAnnoucnement.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    swipeToRefreshAnnoucnements.setEnabled(true);
                } else {
                    swipeToRefreshAnnoucnements.setEnabled(false);
                }
                int visibleItemCount = linearLayoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                if (lastInScreen == totalCount) {

                    //here the section to increase the page number to get next 20 items
                    totalCount += 10;
                    pageNumber += 1;
                    getAnnouncement(pageNumber);
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

    private void loadAnnouncement() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAnnouncement(1);
            }
        }, 200);
    }

    private void getAnnouncement(int page) {
        swipeToRefreshAnnoucnements.setRefreshing(true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        int studentToken = sharedPreferences.getInt(Constants.STUDENT_TOKENS, 0);
        Params announcementParams = new Params();
        if (userToken != 0) {
            announcementParams.setUserToken(userToken);
        }
//        announcementParams.setLoginType("");
        announcementParams.setPage(page);
        Announcements announcements = new Announcements();
        announcements.setJsonrpc(Constants.JSON_RPC);
        announcements.setParams(announcementParams);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e(TAG, "base_url : " + base_url);
//        Log.e(TAG, "Announcements model json data : " + new Gson().toJson(announcements));
        (Utils.httpService(getContext()).announcements(base_url, announcements, sessionIdValue)).enqueue(new Callback<Announcements>() {
            @Override
            public void onResponse(Call<Announcements> call, Response<Announcements> response) {
//                Log.e(TAG, "Announcements respose :" + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        List<Announcement> announcementList = response.body().getResult().getAnnouncements();
                        swipeToRefreshAnnoucnements.setRefreshing(false);
//                        annList.clear();
                        annList.addAll(announcementList);
                        if (annList.isEmpty()) {
                            placeHolderAnnouncments.setVisibility(View.VISIBLE);
                            recyclerAnnoucnement.setVisibility(View.GONE);
                        } else {
                            recyclerAnnoucnement.setVisibility(View.VISIBLE);
                            placeHolderAnnouncments.setVisibility(View.GONE);
//                            mAdapter.notifyDataSetChanged();
                            mAdapter.notifyItemInserted(annList.size() - 1);
                        }
                    } else {
                        recyclerAnnoucnement.setVisibility(View.GONE);
                        placeHolderAnnouncments.setVisibility(View.VISIBLE);
                        swipeToRefreshAnnoucnements.setRefreshing(false);
                        Toast.makeText(getContext(), "Cannot load Announcements Please try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    placeHolderAnnouncments.setVisibility(View.VISIBLE);
                    recyclerAnnoucnement.setVisibility(View.GONE);
                    swipeToRefreshAnnoucnements.setRefreshing(false);
//                    Log.e(TAG, "Null pointer exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<Announcements> call, Throwable t) {
//                Log.e(TAG, "Failure response : " + t.getMessage());
                placeHolderAnnouncments.setVisibility(View.VISIBLE);
                recyclerAnnoucnement.setVisibility(View.GONE);
                swipeToRefreshAnnoucnements.setRefreshing(false);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(getContext(), Constants.NO_CONNECTION);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        annList.clear();
        mAdapter.notifyDataSetChanged();
        totalCount = 10;
        getAnnouncement(1);
    }
}
