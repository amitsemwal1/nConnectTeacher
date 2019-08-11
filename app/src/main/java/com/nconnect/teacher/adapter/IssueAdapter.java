package com.nconnect.teacher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.issues.GetIssuesResponse;
import com.nconnect.teacher.util.Constants;


public class IssueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<GetIssuesResponse.IssueDetails> data = Collections.emptyList();

    public IssueAdapter(Context context, List<GetIssuesResponse.IssueDetails> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.issues_fragment_items, parent, false);
        MyHolderIssues holder = new MyHolderIssues(view);
        return holder;
    }

    @SuppressLint({"ResourceAsColor", "NewApi"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderIssues myHolder = (MyHolderIssues) holder;
        GetIssuesResponse.IssueDetails issues = data.get(position);
        myHolder.tvTitle.setText(issues.getName());
        myHolder.tvContent.setText(issues.getDescription());
        myHolder.tvCount.setText(issues.getComments() + " comments");
        String content = "raised by : " + issues.getRaisedBy() +
                " parent of " + issues.getStudent() + "\n" +
                "Issue created on : " + issues.getDate();
        myHolder.tvRaisedBy.setText(content);

        if (issues.getStatus().equalsIgnoreCase("Open") || issues.getStatus().equalsIgnoreCase("reopen_by_parent") || issues.getStatus().equalsIgnoreCase("Close")) {
            myHolder.tvStatus.setText("Active");
            myHolder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.ncp_active));
        } else if (issues.getStatus().equalsIgnoreCase("Escalate")) {
            myHolder.tvStatus.setText("Escalated");
            myHolder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.ncp_escalated));
        } else if (issues.getStatus().equalsIgnoreCase(Constants.CLOSE_BY_PARENT)) {
            myHolder.tvStatus.setText("Resolved");
            myHolder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.ncp_resolved));
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

    class MyHolderIssues extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvContent, tvRaisedBy, tvStatus, tvCount;

        public MyHolderIssues(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.issue_title);
            tvContent = (TextView) itemView.findViewById(R.id.issue_content);
            tvRaisedBy = (TextView) itemView.findViewById(R.id.issue_raised_by);
            tvStatus = (TextView) itemView.findViewById(R.id.issue_status);
            tvCount = (TextView) itemView.findViewById(R.id.issue_count);
        }
    }


}


