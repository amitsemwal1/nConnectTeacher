package com.nconnect.teacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.announcements.Announcement;

public class AnnouncnementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private LayoutInflater inflater;
    List<Announcement> data = Collections.emptyList();
    private String sessionIdValue = "";

    public AnnouncnementAdapter(Context context, List<Announcement> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.announcement_fragment_item, parent, false);
        MyHolderAnnoucnements myHolderAnnoucnements = new MyHolderAnnoucnements(view);
        return myHolderAnnoucnements;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderAnnoucnements holderAnnoucnements = (MyHolderAnnoucnements) holder;
        Announcement announcement = data.get(position);
        holderAnnoucnements.tvTitle.setText(announcement.getTitle());
        holderAnnoucnements.tvCreatedBy.setText("Posted by : " + announcement.getPostedBy());
        holderAnnoucnements.tvCOntent.setPaintFlags(0);
        holderAnnoucnements.tvCOntent.setText(announcement.getDesc());

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

    class MyHolderAnnoucnements extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCOntent, tvCreatedBy, tvSeemore;
        LinearLayout container;

        public MyHolderAnnoucnements(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.announcements_title);
            tvCOntent = (TextView) itemView.findViewById(R.id.announcements_content);
            tvCreatedBy = (TextView) itemView.findViewById(R.id.announcemets_postedBy);
            container = (LinearLayout) itemView.findViewById(R.id.announcementCard);
            tvSeemore = (TextView) itemView.findViewById(R.id.announcement_seemore);
        }
    }
}
