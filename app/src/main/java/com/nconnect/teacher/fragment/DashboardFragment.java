package com.nconnect.teacher.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.AnalyticalDashboard;
import com.nconnect.teacher.activity.AttendanceActivity;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.activity.PostStoryActivity;
import com.nconnect.teacher.adapter.DashBoardAdapter;
import com.nconnect.teacher.listener.RecyclerTouchListener;
import com.nconnect.teacher.model.DashBoardModel;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import static com.nconnect.teacher.util.Constants.DASH_ANALYTICAL;
import static com.nconnect.teacher.util.Constants.DASH_ANNOUNCEMENTS;
import static com.nconnect.teacher.util.Constants.DASH_ATTENDANCE;
import static com.nconnect.teacher.util.Constants.DASH_CHAT;
import static com.nconnect.teacher.util.Constants.DASH_EVENTS;
import static com.nconnect.teacher.util.Constants.DASH_ISSUES;
import static com.nconnect.teacher.util.Constants.DASH_STORIES;

public class DashboardFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Dashboard Fragement";
    private View view;
    private CardView cardApprovalStories;
    private android.support.v7.widget.Toolbar toolbar;
    private ImageView toolbarLogo;
    private TextView toolbarTitle;
    private BottomNavigationView navigationView;
    private ImageView ivProfileImage, ivprofileImagePrincipal;
    private TextView tvProfileName, tvProfileDesig;
    private TextView tvProfileNamePrincipal, tvProfileDesigPrincipal;
    private LinearLayout containerProfileNametecher, containerProfileNamePrincipal, containerApproveStories;
    private List<DashBoardModel> dashBoardModelList;
    private RecyclerView dashBoard;
    private NestedScrollView parentContainer;
    DashBoardAdapter adapter;
    private LinearLayout containerSchoolBoard;
    public static NestedScrollView parentScrollView;

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = ((Dashboard) getActivity()).getToolbar();
        toolbarLogo = ((Dashboard) getActivity()).getToolBarLogo();
        toolbarTitle = ((Dashboard) getActivity()).getToolbarTitle();
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        containerSchoolBoard.setVisibility(View.VISIBLE);
    }

    private void initializeViews(View view) {
        dashBoardModelList = new ArrayList<>();
        parentScrollView = view.findViewById(R.id.parentScrollView);
        tvProfileNamePrincipal = (TextView) view.findViewById(R.id.profilename_principal);
        tvProfileDesig = (TextView) view.findViewById(R.id.profileDesignation);
        tvProfileDesigPrincipal = (TextView) view.findViewById(R.id.profileDesignation_principal);
        navigationView = ((Dashboard) getActivity()).getNavigationView();
        containerApproveStories = (LinearLayout) view.findViewById(R.id.approve_stories_container);
        containerProfileNamePrincipal = (LinearLayout) view.findViewById(R.id.profile_name_container_principal);
        containerProfileNametecher = (LinearLayout) view.findViewById(R.id.profile_name_container);
        ivprofileImagePrincipal = (ImageView) view.findViewById(R.id.profileImagePrinicipal);
        cardApprovalStories = (CardView) view.findViewById(R.id.card_approval_Stories);
        ivProfileImage = (ImageView) view.findViewById(R.id.profileImage);
        tvProfileName = (TextView) view.findViewById(R.id.profilename);
        cardApprovalStories.setOnClickListener(this);

        dashBoard = (RecyclerView) view.findViewById(R.id.recyclerDashBoard);
        dashBoard.setLayoutManager(new GridLayoutManager(getContext(), 2));
        dashBoard.setHasFixedSize(true);
        dashBoard.setItemAnimator(new DefaultItemAnimator());
        adapter = new DashBoardAdapter(getContext(), dashBoardModelList);
        adapter.setHasStableIds(true);
        dashBoard.setAdapter(adapter);
        dashBoard.addOnItemTouchListener(new RecyclerTouchListener(getContext(), dashBoard, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                DashBoardModel model = dashBoardModelList.get(position);
                Fragment fragment = null;
                Menu menuNav = toolbar.getMenu();
                Bundle bundle = new Bundle();
                switch (model.getId()) {
                    case DASH_ANALYTICAL:
                        startActivity(new Intent(getContext(), AnalyticalDashboard.class));
                        break;
                    case DASH_ANNOUNCEMENTS:
                        containerSchoolBoard.setVisibility(View.GONE);
                        menuNav.findItem(R.id.notification).setVisible(true);
                        menuNav.findItem(R.id.create_issue).setVisible(false);
                        toolbar.setNavigationIcon(R.drawable.ncp_back);
                        toolbarLogo.setVisibility(View.GONE);
                        toolbarTitle.setVisibility(View.VISIBLE);
                        toolbarTitle.setText("Post Announcement");
                        fragment = new PostAnnouncement();
                        loadFragment(fragment);
                        break;
                    case DASH_ATTENDANCE:
                        startActivity(new Intent(getContext(), AttendanceActivity.class));
                        break;
                    case DASH_CHAT:

                        /*if(Utils.chatAccessTime()){
                            containerSchoolBoard.setVisibility(View.GONE);
                            navigationView.getMenu().getItem(2).setChecked(true);
                            parentScrollView.fullScroll(View.FOCUS_UP);
                            loadFragment(new ChatFragment());
                        }else {
                            Toast.makeText(getContext(), getString(R.string.error_chat_available), Toast.LENGTH_LONG).show();
                        }*/

                        containerSchoolBoard.setVisibility(View.GONE);
                        navigationView.getMenu().getItem(2).setChecked(true);
                        parentScrollView.fullScroll(View.FOCUS_UP);
                        loadFragment(new ChatFragment());

                        break;
                    case DASH_EVENTS:
                        containerSchoolBoard.setVisibility(View.GONE);
                        menuNav.findItem(R.id.notification).setVisible(true);
                        menuNav.findItem(R.id.create_issue).setVisible(false);
                        toolbar.setNavigationIcon(R.drawable.ncp_back);
                        toolbarLogo.setVisibility(View.GONE);
                        toolbarTitle.setVisibility(View.VISIBLE);
                        toolbar.setNavigationIcon(R.drawable.ncp_back);
                        toolbarTitle.setText("Post Events");
                        fragment = new PostEvents();
                        loadFragment(fragment);
                        break;
                    case DASH_ISSUES:
                        containerSchoolBoard.setVisibility(View.GONE);
                        toolbar.setNavigationIcon(R.drawable.ncp_back);
                        menuNav.findItem(R.id.notification).setVisible(true);
                        menuNav.findItem(R.id.create_issue).setVisible(false);
                        toolbarLogo.setVisibility(View.GONE);
                        toolbarTitle.setVisibility(View.VISIBLE);
                        toolbarTitle.setText("Issues");
                        loadFragment(new IssuesFragment());
                        break;
                    case DASH_STORIES:
                        startActivity(new Intent(getContext(), PostStoryActivity.class));
                        break;
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }



    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        Menu menuNav = toolbar.getMenu();
        switch (v.getId()) {
            case R.id.card_approval_Stories:
                menuNav.findItem(R.id.notification).setVisible(true);
                menuNav.findItem(R.id.create_issue).setVisible(false);
                toolbar.setNavigationIcon(R.drawable.ncp_back);
                toolbarLogo.setVisibility(View.GONE);
                toolbarTitle.setVisibility(View.VISIBLE);
                toolbarTitle.setText("Approve Stories");
                fragment = new ApprovalStoriesFragment();
                loadFragment(fragment);
                break;
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.setTransition(transaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        toolbar.setVisibility(View.VISIBLE);
        if (containerSchoolBoard.getVisibility() == View.GONE) {
            containerSchoolBoard.setVisibility(View.VISIBLE);
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String loginType = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        String profileName = sharedPreferences.getString(Constants.NAME, "");
        String profileImage = sharedPreferences.getString(Constants.PROFILE_IMAGE, "");
//        Log.e(TAG, "profile name : " + profileName);
//        Log.e(TAG, "login_type = " + loginType);
        if (loginType.equalsIgnoreCase(Constants.LOGIN_TYPE_VALUE)) {
            containerApproveStories.setVisibility(View.GONE);
            containerProfileNametecher.setVisibility(View.VISIBLE);
            containerProfileNamePrincipal.setVisibility(View.GONE);
            ivProfileImage.setVisibility(View.VISIBLE);
            if (profileImage != null && profileImage != "") {
//                Picasso.get().load(new File(profileImage)).transform(new CircleTransform()).into(ivProfileImage);
                Glide.with(getContext()).load(profileImage).apply(new RequestOptions().placeholder(R.drawable.ncp_avator)).into(ivProfileImage);
            }
            ivprofileImagePrincipal.setVisibility(View.GONE);
            tvProfileName.setText(profileName);
            tvProfileDesig.setText("Teacher");
            dashBoardModelList.clear();
            dashBoardModelList.addAll(Utils.getTeacherDashBoard());
            adapter.notifyDataSetChanged();
        } else if (loginType.equalsIgnoreCase("principal") || loginType.equalsIgnoreCase("vice_principal")) {
            containerApproveStories.setVisibility(View.VISIBLE);
            containerProfileNametecher.setVisibility(View.GONE);
            containerProfileNamePrincipal.setVisibility(View.VISIBLE);
            ivProfileImage.setVisibility(View.GONE);
            ivprofileImagePrincipal.setVisibility(View.VISIBLE);
            if (profileImage != null && profileImage != "") {
                Glide.with(getContext()).load(profileImage).apply(new RequestOptions().placeholder(R.drawable.ncp_avator)).into(ivprofileImagePrincipal);
            }
            tvProfileNamePrincipal.setText(profileName);
            if (loginType.equalsIgnoreCase("principal")) {
                tvProfileDesigPrincipal.setText("Principal");
            } else {
                tvProfileDesigPrincipal.setText("Vice Principal");
            }
            dashBoardModelList.clear();
            dashBoardModelList.addAll(Utils.getPrincipalDashBoard());
            adapter.notifyDataSetChanged();
        }
    }

}
