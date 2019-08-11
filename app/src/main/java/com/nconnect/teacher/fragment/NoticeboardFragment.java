package com.nconnect.teacher.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.adapter.ViewPagerAdapter;

public class NoticeboardFragment extends Fragment {

    private static final String TAG = NoticeboardFragment.class.getSimpleName();
    private TabLayout tabNoticeboard;
    private ViewPager pagerNoticeboard;
    private int type;
    private TextView toolbarText;
    private Toolbar toolbar;
    private LinearLayout containerSchoolBoard;


    public NoticeboardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_noticeboard, container, false);
        initializeViews(rootView);
        return rootView;
    }

    private void initializeViews(View rootView) {
        toolbar = ((Dashboard) getActivity()).getToolbar();
        toolbarText = ((Dashboard) getActivity()).getToolbarTitle();
        toolbarText.setText("Noticeboard");
        type = getArguments().getInt("pos");
        pagerNoticeboard = (ViewPager) rootView.findViewById(R.id.noticeboard_viewpager);
        setupViewPager(pagerNoticeboard);
        tabNoticeboard = (TabLayout) rootView.findViewById(R.id.noticeboard_tabs);
        tabNoticeboard.setupWithViewPager(pagerNoticeboard);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupViewPager(ViewPager pagerNoticeboard) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new EventsFragment(), "Events");
        adapter.addFragment(new AnnoucnementsFragment(), "Announcements");
        pagerNoticeboard.setAdapter(adapter);
        if (type == 1) {
            pagerNoticeboard.setCurrentItem(1);
//            Log.e(TAG, "setcurent item : " + type);
        } else {
            pagerNoticeboard.setCurrentItem(0);
//            Log.e(TAG, "Set current item : " + type);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
