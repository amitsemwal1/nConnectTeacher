package com.nconnect.teacher.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.gradeandsection.Student;
import com.nconnect.teacher.model.stories.AttendanceItemOnClickCallback;

public class StudentSearchAdapter extends RecyclerView.Adapter<StudentSearchAdapter.StudentsViewHolder> {
    private List<Student> studentList = Collections.emptyList();
    private static AttendanceItemOnClickCallback attendanceItemOnClickCallback;

    public StudentSearchAdapter(List<Student> exampleList,AttendanceItemOnClickCallback attendanceItemOnClickCallback) {
        studentList = exampleList;
        this.attendanceItemOnClickCallback = attendanceItemOnClickCallback;
    }

    @Override
    public StudentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_search_item, parent, false);
        StudentsViewHolder evh = new StudentsViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(StudentsViewHolder holder, int position) {
        Student currentItem = studentList.get(position);
        StudentsViewHolder viewHolder = (StudentsViewHolder) holder;
        viewHolder.name.setText(currentItem.getName());
        viewHolder.studentId.setText(currentItem.getStudentId());

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void filterList(List<Student> filteredList) {
        studentList = filteredList;
        notifyDataSetChanged();
    }

    public static class StudentsViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile;
        public TextView name;
        public TextView studentId;
        private RelativeLayout container;

        public StudentsViewHolder(View itemView) {
            super(itemView);
            profile = (ImageView) itemView.findViewById(R.id.search_student_profile_image);
            name = (TextView) itemView.findViewById(R.id.search_student_name);
            studentId = (TextView) itemView.findViewById(R.id.search_student_id);
            container = (RelativeLayout) itemView.findViewById(R.id.search_student_profile_container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attendanceItemOnClickCallback.onclickCallback(name.getText().toString(),studentId.getText().toString());

                }
            });
        }
    }

}
