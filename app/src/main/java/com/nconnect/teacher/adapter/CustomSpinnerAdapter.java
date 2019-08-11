package com.nconnect.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.stories.ClassSectionList;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context context;
    List<ClassSectionList> classSectionLists;
    LayoutInflater inflter;

    public CustomSpinnerAdapter(Context applicationContext, List<ClassSectionList> classSectionLists) {
        this.context = applicationContext;
        this.classSectionLists = classSectionLists;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return classSectionLists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        TextView className = (TextView) view.findViewById(R.id.class_name);
        TextView sectionName = (TextView) view.findViewById(R.id.section_name);
        className.setText(classSectionLists.get(i).grade);
        sectionName.setText(classSectionLists.get(i).section);
        return view;
    }
}