package com.nconnect.teacher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.issues.IssueComment;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import static android.content.Context.MODE_PRIVATE;


public class IssueCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<IssueComment> data = Collections.emptyList();
    private String sessionIdValue;

    public IssueCommentsAdapter(Context context, List<IssueComment> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.issue_comments_item, parent, false);
        MyHolderIssuesComments holder = new MyHolderIssuesComments(view);
        return holder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderIssuesComments myHolder = (MyHolderIssuesComments) holder;
        IssueComment issues = data.get(position);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String logintype = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        String login = "";
        if (logintype.equalsIgnoreCase("principal")) {
            login = "Principal";
        } else if (logintype.equalsIgnoreCase("teacher")) {
            login = "Teacher";
        } else if (logintype.equalsIgnoreCase("parent")) {
            login = "Parent";
        } else if (logintype.equalsIgnoreCase("vice_principal")) {
            login = "Vice Principal";
        }
        if (issues.getLogin_type().equalsIgnoreCase(login)) {
            myHolder.containerParent.setVisibility(View.GONE);
            myHolder.containerTeacher.setVisibility(View.VISIBLE);
            myHolder.tvTeacherComment.setText(issues.getMessage());
            myHolder.tvTeacherDesignation.setText(issues.getLogin_type());
            myHolder.tvTeacherProfilename.setText(issues.getName());
            long createdAt = issues.getCreatedAt();
            String date = Utils.getDate(createdAt);
            myHolder.tvTimestamp.setText("" + date);
            loadProfileImage(myHolder.ivProfileImageParent);
        } else {
            myHolder.containerTeacher.setVisibility(View.GONE);
            myHolder.containerParent.setVisibility(View.VISIBLE);
            myHolder.tvParentComment.setText(issues.getMessage());
            myHolder.tvParentChildName.setText(issues.getLogin_type());
            myHolder.tvParentProfilename.setText(issues.getName());
            long createdAt = issues.getCreatedAt();
            String date = Utils.getDate(createdAt);
            myHolder.tvTimestamp.setText("" + date);
            if (issues.getProfileImage().isEmpty()) {
                Glide.with(context)
                        .load(R.drawable.ncp_avator)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .error(R.drawable.placeholder_stories)
                                .placeholder(R.drawable.ncp_avator))
                        .into(myHolder.ivProfileImageTeacher);
            } else {
                Glide.with(context)
                        .load(issues.getProfileImage())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .error(R.drawable.placeholder_stories)
                                .placeholder(R.drawable.ncp_avator))
                        .into(myHolder.ivProfileImageTeacher);
            }
        }
    }

    private void loadProfileImage(ImageView imageView) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String profileImage = preferences.getString(Constants.PROFILE_IMAGE, "");
        if (profileImage != null && profileImage != "") {
            Glide.with(context).load(profileImage).apply(new RequestOptions().circleCrop().placeholder(R.drawable.ncp_avator).error(R.drawable.ncp_avator)).into(imageView);
        }
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

    class MyHolderIssuesComments extends RecyclerView.ViewHolder {

        TextView tvTeacherProfilename, tvTeacherDesignation, tvTeacherComment;
        ImageView ivProfileImageTeacher;
        TextView tvParentProfilename, tvParentChildName, tvParentComment;
        ImageView ivProfileImageParent;
        LinearLayout containerParent, containerTeacher;
        TextView tvTimestamp;

        public MyHolderIssuesComments(View itemView) {
            super(itemView);
            tvTimestamp = (TextView) itemView.findViewById(R.id.timeStampOfIssueComment);
            tvParentProfilename = (TextView) itemView.findViewById(R.id.comment_fathername);
            tvParentChildName = (TextView) itemView.findViewById(R.id.comment_childname);
            tvParentComment = (TextView) itemView.findViewById(R.id.comment_content_parent);
            ivProfileImageParent = (ImageView) itemView.findViewById(R.id.comment_parent_image);
            tvTeacherProfilename = (TextView) itemView.findViewById(R.id.comment_teachername);
            tvTeacherDesignation = (TextView) itemView.findViewById(R.id.comment_teacherdesignation);
            tvTeacherComment = (TextView) itemView.findViewById(R.id.comment_content_teacher);
            ivProfileImageTeacher = (ImageView) itemView.findViewById(R.id.comment_teacher_iamge);
            containerParent = (LinearLayout) itemView.findViewById(R.id.comment_parent_container);
            containerTeacher = (LinearLayout) itemView.findViewById(R.id.comment_teacher_container);
        }

    }
}