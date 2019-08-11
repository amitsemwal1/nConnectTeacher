package com.nconnect.teacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.gradeandsection.Student;

public class AttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<Student> data = Collections.emptyList();


    public AttendanceAdapter(Context context, List<Student> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.mark_attendance_studens_item, parent, false);
        MyHolderAttendance attendance = new MyHolderAttendance(view);
        return attendance;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderAttendance myHolder = (MyHolderAttendance) holder;
        myHolder.student = data.get(position);
        myHolder.tvStudentNAme.setText(data.get(position).getName());
        myHolder.tvStudentId.setText(data.get(position).getStudentId());

        if (myHolder.student.getIsPresent()) {
            myHolder.togglePre.setChecked(true);
            myHolder.toggleAb.setChecked(false);
        } else {
            myHolder.toggleAb.setChecked(true);
            myHolder.togglePre.setChecked(false);
        }
        myHolder.togglePre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHolder.togglePre.setChecked(true);
                myHolder.toggleAb.setChecked(false);
                myHolder.student.setStudentId(myHolder.student.getStudentId());
                myHolder.student.setIsPresent(true);
            }
        });
        myHolder.toggleAb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHolder.toggleAb.setChecked(true);
                myHolder.togglePre.setChecked(false);
                myHolder.student.setStudentId(myHolder.student.getStudentId());
                myHolder.student.setIsPresent(false);
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

    public interface OnItemClickListener {
        void onItemClicked(int position, List<Student> list);
    }

    public class MyHolderAttendance extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvStudentNAme, tvStudentId;
        ToggleButton togglePre, toggleAb;
        LinearLayout containerMarkAttendance;
        Student student;


        public MyHolderAttendance(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.mark_attendance_student_profile_image);
            tvStudentNAme = (TextView) itemView.findViewById(R.id.mark_attendance_student_name);
            tvStudentId = (TextView) itemView.findViewById(R.id.mark_attendance_student_id);
            togglePre = (ToggleButton) itemView.findViewById(R.id.mark_attendance_present);
            toggleAb = (ToggleButton) itemView.findViewById(R.id.mark_attendance_absent);
            containerMarkAttendance = (LinearLayout) itemView.findViewById(R.id.mark_attendane_container);

        }
    }
}
