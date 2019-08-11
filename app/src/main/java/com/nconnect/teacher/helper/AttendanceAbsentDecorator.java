package com.nconnect.teacher.helper;

import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.nconnect.teacher.R;


public class AttendanceAbsentDecorator implements DayViewDecorator {
    //    private  HashSet<CalendarDay> dates;
    private Context context;
    private List<CalendarDay> dates;

    public AttendanceAbsentDecorator(Collection<CalendarDay> dates, Context context) {

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
        view.setSelectionDrawable(context.getResources().getDrawable(R.drawable.ncp_absentcolor));
    }
}
