package com.nconnect.teacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.gradeandsection.Section;
import com.nconnect.teacher.model.stories.Args;

public class ClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ClassAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    List<Grade> data = Collections.emptyList();
    private String sessionIdValue = "";
    ArrayList<Integer> counter = new ArrayList<Integer>();


    public ClassAdapter(Context context, List<Grade> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
        for (int i = 0; i < data.size(); i++) {
            counter.add(0);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_class_item, parent, false);
        MyHolderClass holderClass = new MyHolderClass(view);
        return holderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderClass holderClass = (MyHolderClass) holder;
        holderClass.grade = data.get(position);
        holderClass.sections = holderClass.grade.getSection();
        holderClass.tvClassName.setText(holderClass.grade.getName());
        holderClass.grade.setGradeChecked("false");
        int size = holderClass.sections.size();
        for (int i = 0; i < size; i++) {
            Section section = new Section();
            section.setStatus(false);
            section.setSectionId(holderClass.sections.get(i).getSectionId());
            section.setSectionName(holderClass.sections.get(i).getSectionName());
            holderClass.sections.set(i, section);
        }
//        Log.e(TAG, "section list : " + new Gson().toJson(holderClass.sections));
        SectionAdapter sectionAdapter = new SectionAdapter(context, holderClass.sections);
        holderClass.tvClassName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holderClass.tvClassName.isChecked()) {
                    holderClass.tvClassName.setChecked(false);
                    holderClass.ivClassChecked.setVisibility(View.INVISIBLE);
                    holderClass.grade.setGradeChecked("false");
                    holderClass.grade.setId(data.get(position).getId());
                    holderClass.grade.setName(data.get(position).getName());
                    holderClass.grade.setSection(holderClass.sections);
                } else {
                    holderClass.tvClassName.setChecked(true);
                    holderClass.ivClassChecked.setVisibility(View.VISIBLE);
                    holderClass.grade.setGradeChecked("true");
                    holderClass.grade.setId(data.get(position).getId());
                    holderClass.grade.setName(data.get(position).getName());
                    holderClass.grade.setSection(holderClass.sections);
                }
                if (counter.get(position) % 2 == 0) {
                    holderClass.recyclerSection.setVisibility(View.VISIBLE);
                } else {
                    holderClass.recyclerSection.setVisibility(View.GONE);
                }
                counter.set(position, counter.get(position) + 1);
            }
        });
        holderClass.recyclerSection.setLayoutManager(new LinearLayoutManager(context));
        holderClass.recyclerSection.setAdapter(sectionAdapter);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class MyHolderClass extends RecyclerView.ViewHolder {

        CheckedTextView tvClassName;
        RecyclerView recyclerSection;
        ImageView ivClassChecked;
        Grade grade;
        Args args;
        List<Section> sections;

        public MyHolderClass(View itemView) {
            super(itemView);
            tvClassName = (CheckedTextView) itemView.findViewById(R.id.class_name);
            recyclerSection = (RecyclerView) itemView.findViewById(R.id.recycler_section);
            ivClassChecked = (ImageView) itemView.findViewById(R.id.classChecked);
            sections = new ArrayList<>();
        }
    }
}
