package com.nconnect.teacher.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.ViewPagerAdapter;
import com.nconnect.teacher.fragment.InstallsData;
import com.nconnect.teacher.fragment.UsageData;

public class AnalyticalDashboard extends AppCompatActivity {

    private static final String TAG = AnalyticalDashboard.class.getSimpleName();
    private Toolbar toolbar;
    private ViewPager pagerAnalyticDashboard;
    private TabLayout tabAnalyticDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytical_dashboard);
        init();
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pagerAnalyticDashboard = (ViewPager) findViewById(R.id.analyticalDashboard_viewpager);
        setupViewPager(pagerAnalyticDashboard);
        tabAnalyticDashboard = (TabLayout) findViewById(R.id.analyticalDashboard_tabs);
        tabAnalyticDashboard.setupWithViewPager(pagerAnalyticDashboard);
    }

    private void setupViewPager(ViewPager pagerAnalyticDashboard) {
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        pagerAdapter.addFragment(new UsageData(), "USAGE DATA");
        pagerAdapter.addFragment(new InstallsData(), "INSTALLS DATA");
        pagerAnalyticDashboard.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
