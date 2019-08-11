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
import com.nconnect.teacher.model.Comment;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import static android.content.Context.MODE_PRIVATE;

public class StoryCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<Comment> data = Collections.emptyList();
    String sessionIdValue;
    private boolean isfirst = true;
    private String loginType = "";


    public StoryCommentsAdapter(Context context, List<Comment> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.issue_comments_item, parent, false);
        MyholderStoryComments holder = new MyholderStoryComments(view);
        return holder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyholderStoryComments myHolder = (MyholderStoryComments) holder;
        Comment comment = data.get(position);
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
        if (comment.getLogin_type().equalsIgnoreCase(login)) {
            myHolder.containerParent.setVisibility(View.GONE);
            myHolder.containerTeacher.setVisibility(View.VISIBLE);
            myHolder.tvTeacherComment.setText(comment.getMessage());
            myHolder.tvTeacherDesignation.setText(comment.getLogin_type());
            myHolder.tvTeacherProfilename.setText(comment.getName());
            long createdAt = comment.getCreatedAt();
            String date = Utils.getDate(createdAt);
            myHolder.tvTimeStamp.setText(date);
//            loadProfileImage(myHolder.ivProfileImageTeacher);
            if (comment.getProfileImage().isEmpty()) {
                Glide
                        .with(context)
                        .load(comment.getProfileImage())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ncp_avator)
                                .error(R.drawable.ncp_avator))
                        .into(myHolder.ivProfileImageTeacher);
            } else {
                Glide
                        .with(context)
                        .load(R.drawable.ncp_avator)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ncp_avator)
                                .error(R.drawable.ncp_avator))
                        .into(myHolder.ivProfileImageTeacher);
            }
        } else {
            myHolder.containerTeacher.setVisibility(View.GONE);
            myHolder.containerParent.setVisibility(View.VISIBLE);
            myHolder.tvParentComment.setText(comment.getMessage());
            myHolder.tvParentChildName.setText(comment.getLogin_type());
            myHolder.tvParentProfilename.setText(comment.getName());
            long createdAt = comment.getCreatedAt();
            String date = Utils.getDate(createdAt);
            myHolder.tvTimeStamp.setText(date);
            if (comment.getProfileImage().isEmpty()) {
                Glide
                        .with(context)
                        .load(comment.getProfileImage())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ncp_avator)
                                .error(R.drawable.ncp_avator))
                        .into(myHolder.ivProfileImageParent);
            } else {
                Glide
                        .with(context)
                        .load(R.drawable.ncp_avator)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ncp_avator)
                                .error(R.drawable.ncp_avator))
                        .into(myHolder.ivProfileImageParent);
            }
        }
    }


    private void loadProfileImage(ImageView imageView) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String profileImage = preferences.getString(Constants.PROFILE_IMAGE, "");
        if (profileImage != null && profileImage != "") {
            Glide.with(context).load(profileImage).apply(new RequestOptions().placeholder(R.drawable.ncp_avator).error(R.drawable.ncp_avator)).into(imageView);
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

    class MyholderStoryComments extends RecyclerView.ViewHolder {

        TextView tvTeacherProfilename, tvTeacherDesignation, tvTeacherComment;
        ImageView ivProfileImageTeacher;
        TextView tvParentProfilename, tvParentChildName, tvParentComment;
        ImageView ivProfileImageParent;
        LinearLayout containerParent, containerTeacher;
        TextView tvTimeStamp;

        public MyholderStoryComments(View itemView) {
            super(itemView);
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
            tvTimeStamp = (TextView) itemView.findViewById(R.id.timeStampOfIssueComment);
        }

    }
}
