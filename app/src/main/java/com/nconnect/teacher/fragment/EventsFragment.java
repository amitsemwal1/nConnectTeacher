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
import android.widget.TableRow;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.activity.ViewEventsActivity;
import com.nconnect.teacher.adapter.EventsAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.listener.RecyclerTouchListener;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.events.Event;
import com.nconnect.teacher.model.events.Events;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Events Fragment";
    private RecyclerView recyclerEvents;
    private SwipeRefreshLayout swipeToRefreshEvents;
    private View placeHolderEvents;
    private Spinner spnSchoolBoard;
    private TableRow spinnerBar;
    private LinearLayout containerSchoolBoard;
    private Toolbar toolbar;
    private List<Event> eventsList;

    private EventsAdapter mAdapter;
    private String type = "";
    private String base_url = "";
    private LinearLayoutManager linearLayoutManager;
    private int pageNumber = 1;
    private int totalCount = 10;


    public EventsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        initilizeVies(rootView);
        return rootView;
    }

    private void initilizeVies(View rootView) {
        toolbar = ((Dashboard) getActivity()).getToolbar();
        spinnerBar = (TableRow) rootView.findViewById(R.id.spinner_bar);
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        placeHolderEvents = rootView.findViewById(R.id.events_placeholder);
        spnSchoolBoard = (Spinner) rootView.findViewById(R.id.spnSchoolBoard);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_filter_item, Constants.FILTERS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSchoolBoard.setAdapter(adapter);
        spnSchoolBoard.setSelection(1);
        recyclerEvents = (RecyclerView) rootView.findViewById(R.id.events_recycler_view);
        swipeToRefreshEvents = (SwipeRefreshLayout) rootView.findViewById(R.id.events_refresh_container);
        swipeToRefreshEvents.setOnRefreshListener(this);
        swipeToRefreshEvents.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        eventsList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAdapter = new EventsAdapter(getActivity(), eventsList);
        recyclerEvents.setLayoutManager(linearLayoutManager);
        recyclerEvents.setItemAnimator(new DefaultItemAnimator());
        recyclerEvents.setAdapter(mAdapter);

        initListener();

        type = getArguments().getString("type");
        if (type != null && type != "") {
            if (type.equalsIgnoreCase("my_events")) {
                base_url = Constants.MY_EVENTS;
                spnSchoolBoard.setVisibility(View.GONE);
                loadEvents();
            } else if (type.equalsIgnoreCase("school_events")) {
                base_url = Constants.SCHOOL_BOARD_EVENTS;
                spnSchoolBoard.setVisibility(View.VISIBLE);
            }
        }


    }

    private void initListener() {
        recyclerEvents.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerEvents, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Event event = eventsList.get(position);
                String singlePostData = new Gson().toJson(event);
                startActivity(new Intent(getContext(), ViewEventsActivity.class).putExtra(Constants.EVENTS, singlePostData)
                        .putExtra("isScreen", "eventsFragment"));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        /*recyclerEvents.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    swipeToRefreshEvents.setEnabled(true);
                } else {
                    swipeToRefreshEvents.setEnabled(false);
                }


                int visibleItemCount = linearLayoutManager.getChildCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                Log.e(TAG, "scrolled");
                if (lastInScreen == totalCount) {

                    //here the section to increase the page number to get next 20 items
                    totalCount += 10;
                    pageNumber += 1;
                    getEvents(pageNumber);
                }
            }
        });*/
        recyclerEvents.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    swipeToRefreshEvents.setEnabled(true);
                } else {
                    swipeToRefreshEvents.setEnabled(false);
                }
                int visibleItemCount = linearLayoutManager.getChildCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
//                Log.e("TAG", "scrolled");
//                Log.e("TAG", "lsat in screen : " + lastInScreen);
//                Log.e("TAG", "firstVisibleItemPosition : " + firstVisibleItemPosition);
//                Log.e("TAG", "page number : " + pageNumber);
//                Log.e("TAG", "total countt : " + totalCount);
                if (lastInScreen == totalCount) {
                    //here the section to increase the page number to get next 20 items
                    totalCount += 10;
                    pageNumber += 1;
                    getEvents(pageNumber);
                }
            }
        });

        spnSchoolBoard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = Constants.FILTERS[position];
                if (selectedFilter.equalsIgnoreCase("Events")) {
                    loadEvents();
                } else if (selectedFilter.equalsIgnoreCase("Announcements")) {
                    ((Dashboard) getActivity()).openSchoolBoardAnnoucements();
                } else if (selectedFilter.equalsIgnoreCase("Stories")) {
                    ((Dashboard) getActivity()).openSchoolBoard();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadEvents() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getEvents(pageNumber);
            }
        }, 200);
    }

    private void getEvents(int page) {
//        Log.e(TAG, "page : " + page);
        swipeToRefreshEvents.setRefreshing(true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
//        int studentToken = sharedPreferences.getInt(Constants.STUDENT_TOKENS, 0);
        Params eventsParams = new Params();
        if (userToken != 0) {
            eventsParams.setUserToken(userToken);
        }
        eventsParams.setPage(page);
//        eventsParams.setLoginType("");
        Events events = new Events();
        events.setJsonrpc(Constants.JSON_RPC);
        events.setParams(eventsParams);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e(TAG, "base_url : " + base_url);
//        Log.e(TAG, "Events model data : " + new Gson().toJson(events));
        (Utils.httpService(getContext()).events(base_url, events, sessionIdValue)).enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
//                Log.e(TAG, "Events : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        swipeToRefreshEvents.setRefreshing(false);
                        List<Event> eventslist = response.body().getResult().getEvents();
                        eventsList.addAll(eventslist);
                        swipeToRefreshEvents.setRefreshing(false);
                       /* if (eventsList.isEmpty()) {
                            placeHolderEvents.setVisibility(View.VISIBLE);
                            recyclerEvents.setVisibility(View.GONE);
                        } else {
                            placeHolderEvents.setVisibility(View.GONE);
                            recyclerEvents.setVisibility(View.VISIBLE);
                            mAdapter.notifyItemInserted(eventsList.size() - 1);
                        }*/
                        if (!eventsList.isEmpty()) {
                            placeHolderEvents.setVisibility(View.GONE);
                            recyclerEvents.setVisibility(View.VISIBLE);
                            mAdapter.notifyItemInserted(eventsList.size() - 1);
                        } else {
                            placeHolderEvents.setVisibility(View.VISIBLE);
                            recyclerEvents.setVisibility(View.GONE);
                        }
                    } else {
                        placeHolderEvents.setVisibility(View.VISIBLE);
                        recyclerEvents.setVisibility(View.GONE);
                        swipeToRefreshEvents.setRefreshing(false);
                        Toast.makeText(getContext(), "Cannot load events please try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    recyclerEvents.setVisibility(View.GONE);
                    placeHolderEvents.setVisibility(View.VISIBLE);
//                    Log.e(TAG, "null pointer exception : " + e);
                    swipeToRefreshEvents.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {
//                Log.e(TAG, "Failure response : " + t.getMessage());
                swipeToRefreshEvents.setRefreshing(false);
                recyclerEvents.setVisibility(View.GONE);
                placeHolderEvents.setVisibility(View.VISIBLE);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {

                    Utils.showToastCustom(getContext(), Constants.NO_CONNECTION);
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
        eventsList.clear();
        mAdapter.notifyDataSetChanged();
        totalCount = 10;
        getEvents(1);
    }
}
