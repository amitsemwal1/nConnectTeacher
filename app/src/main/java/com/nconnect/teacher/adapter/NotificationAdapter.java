package com.nconnect.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.Notifications.Notifications;

public class NotificationAdapter extends BaseAdapter {
    private Context context; //context
    private List<Notifications> data_list; //data source of the list adapter

    //public constructor 
    public NotificationAdapter(Context context, List<Notifications> data_list) {
        this.context = context;
        this.data_list = data_list;
    }

    @Override
    public int getCount() {
        return data_list.size(); //returns total of data_list in the list
    }

    @Override
    public Object getItem(int position) {
        return data_list.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItems(List<Notifications> _list) {
        if (data_list == null) {
            data_list = new ArrayList<>();
        }
        data_list.addAll(_list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.notification_item, parent, false);
        }

        // get current item to be displayed

        Notifications notifications = (Notifications) getItem(position);

        // get the TextView for item name and item description
        TextView messageView = convertView.findViewById(R.id.messageView);
        TextView dateView = convertView.findViewById(R.id.dateView);
        RelativeLayout mainLayout = convertView.findViewById(R.id.mainLayout);
        ImageView statusImageView = convertView.findViewById(R.id.statusImageView);

        mainLayout.setBackgroundColor(context.getResources().getColor(R.color.white_five));
        statusImageView.setVisibility(View.VISIBLE);

        if (notifications.isRead()){
            mainLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            statusImageView.setVisibility(View.GONE);
        }

        //sets the text for item name and item description from the current item object
        messageView.setText(notifications.getBody());
        dateView.setText(notifications.getDate());

        // returns the view for the current row
        return convertView;
    }
}