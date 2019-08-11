package com.nconnect.teacher.helper;

import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Collection;

import com.nconnect.teacher.R;


public class AttendancePresentDecorator implements DayViewDecorator {

    //    private HashSet<CalendarDay> dates;
    private ArrayList<CalendarDay> dates;
    private Context context;

    public AttendancePresentDecorator(Collection<CalendarDay> dates, Context context) {

        this.dates = new ArrayList<>(dates);
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
        view.setSelectionDrawable(context.getResources().getDrawable(R.drawable.ncp_presentcolor));
    }
}