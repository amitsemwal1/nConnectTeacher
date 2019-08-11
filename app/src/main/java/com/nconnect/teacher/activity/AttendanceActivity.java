package com.nconnect.teacher.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.ViewPagerAdapter;
import com.nconnect.teacher.fragment.MarkAttendanceFragment;
import com.nconnect.teacher.fragment.ViewAttendanceFragment;

public class AttendanceActivity extends AppCompatActivity {

    private static final String TAG = "Attendance";
    private TabLayout tabAttendance;
    private ViewPager pagerAttendance;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        initializeViews();
    }

    private void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tabAttendance = (TabLayout) findViewById(R.id.attendacne_tabs);
        pagerAttendance = (ViewPager) findViewById(R.id.attendacnce_viewpager);
        setupViewPager(pagerAttendance);
        tabAttendance.setupWithViewPager(pagerAttendance);
    }

    public void setupViewPager(ViewPager pager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MarkAttendanceFragment(), "Mark Attendance");
        adapter.addFragment(new ViewAttendanceFragment(), "View Attendance");
        pagerAttendance.setAdapter(adapter);
        pagerAttendance.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabAttendance));
    }

}
