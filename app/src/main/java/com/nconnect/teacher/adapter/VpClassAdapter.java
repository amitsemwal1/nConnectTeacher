package com.nconnect.teacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.gradeandsection.Grade;

public class VpClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<Grade> data = Collections.emptyList();
    private String sessionIdValue = "";

    public VpClassAdapter(Context context, List<Grade> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vp_class_item, parent, false);
        MyHolderVpClass vpClass = new MyHolderVpClass(view);
        return vpClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderVpClass vpClass = (MyHolderVpClass) holder;
        vpClass.grade = data.get(position);
        vpClass.ctvClasses.setText("Class " + vpClass.grade.getName());
        vpClass.grade.setGradeChecked("false");
        vpClass.ctvClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vpClass.ctvClasses.isChecked()) {
                    vpClass.ctvClasses.setChecked(false);
                    vpClass.grade.setId(data.get(position).getId());
                    vpClass.grade.setName(data.get(position).getName());
                    vpClass.grade.setGradeChecked("false");
                } else {

                    vpClass.ctvClasses.setChecked(true);
                    vpClass.grade.setId(data.get(position).getId());
                    vpClass.grade.setName(data.get(position).getName());
                    vpClass.grade.setGradeChecked("true");
                }
//                Log.e("clss ","e kgndsg"+ new Gson().toJson(data));
            }
        });
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

    class MyHolderVpClass extends RecyclerView.ViewHolder {
        CheckedTextView ctvClasses;
        Grade grade;

        public MyHolderVpClass(View itemView) {
            super(itemView);
            ctvClasses = (CheckedTextView) itemView.findViewById(R.id.vpClasses);
        }
    }
}
