package com.nconnect.teacher.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.ChatActivity;
import com.nconnect.teacher.fragment.ChatFragment;
import com.nconnect.teacher.model.announcements.Announcement;
import com.nconnect.teacher.model.chat.Father;
import com.nconnect.teacher.model.chat.Mother;
import com.nconnect.teacher.model.chat.Parents;
import com.nconnect.teacher.util.Constants;

import okhttp3.internal.connection.RealConnection;

import static android.content.Context.MODE_PRIVATE;

public class ParentsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LinearLayout selectionLayout, inviteLayout;
    private Activity context; //context
    private List<Parents> data_list; //data source of the list
    private LayoutInflater inflater;

    //public constructor
    public ParentsListAdapter(Activity context, List<Parents> data_list, LinearLayout selectionLayout, LinearLayout inviteLayout) {
        this.context = context;
        this.data_list = data_list;
        this.selectionLayout = selectionLayout;
        this.inviteLayout = inviteLayout;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.newchat_select_item, parent, false);
        MyHolderParentsList holder = new MyHolderParentsList(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolderParentsList holderParents = (MyHolderParentsList) holder;
        Parents parents = data_list.get(position);

        holderParents.tvNameView.setText(parents.getStudent_name());
        holderParents.tvIdView.setText(parents.getStudent_id());

        holderParents.ivChatWithFather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Father father = parents.getFather();
                String student_id = parents.getStudent_id();
                ChatFragment.student_name = parents.getStudent_name();
                ChatFragment.infoTextview.setText(ChatFragment.student_name + "’s father has not installed the nConnect app, please send him an invite, if you want to connect with him.");
                if (father.getInstalled()) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.RECEIVER_NAME, student_id + "F");
                    editor.commit();
                    String studentName = parents.getStudent_name();
                    studentName = studentName + "’s Father";
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("screen", "ParentsListAdapter");
                    intent.putExtra("name", studentName);
                    context.startActivity(intent);
                } else {
                    selectionLayout.setVisibility(View.GONE);
                    inviteLayout.setVisibility(View.VISIBLE);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("student_id", student_id);
                    hashMap.put("object", father);
                    inviteLayout.setTag(hashMap);
                }
            }
        });
        holderParents.ivChatWithMother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mother mother = parents.getMother();
                String student_id = parents.getStudent_id();
                ChatFragment.student_name = parents.getStudent_name();
                ChatFragment.infoTextview.setText(parents.getStudent_name() + "’s mother has not installed the nConnect app, please send him an invite, if you want to connect with him.");
                if (mother.getInstalled()) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.RECEIVER_NAME, student_id + "M");
                    editor.commit();
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("screen", "ParentsListAdapter");
                    intent.putExtra("name", parents.getStudent_name() + "’s Mother");
                    context.startActivity(intent);
                } else {
                    selectionLayout.setVisibility(View.GONE);
                    inviteLayout.setVisibility(View.VISIBLE);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("student_id", student_id);
                    hashMap.put("object", mother);
                    inviteLayout.setTag(hashMap);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class MyHolderParentsList extends RecyclerView.ViewHolder {
        TextView tvNameView, tvIdView;
        ImageView ivChatWithFather, ivChatWithMother;

        public MyHolderParentsList(View itemView) {
            super(itemView);
            tvNameView = itemView.findViewById(R.id.nameView);
            tvIdView = itemView.findViewById(R.id.idView);
            ivChatWithFather = itemView.findViewById(R.id.chatWithFatherView);
            ivChatWithMother = itemView.findViewById(R.id.chatWithMotherView);

        }
    }
}