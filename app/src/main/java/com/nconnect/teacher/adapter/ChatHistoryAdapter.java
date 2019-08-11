package com.nconnect.teacher.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.chat.ChatMessage;
import com.nconnect.teacher.util.Utils;

import org.jivesoftware.smack.chat2.Chat;

public class ChatHistoryAdapter extends BaseAdapter {
    private Context context; //context
    private List<ChatMessage> data_list; //data source of the list adapter

    //public constructor 
    public ChatHistoryAdapter(Context context, List<ChatMessage> data_list) {
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

    public void addItems(List<ChatMessage> _list) {
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
                    inflate(R.layout.sender_list_item, parent, false);
        }

        // get current item to be displayed

        ChatMessage ChatMessage = (ChatMessage) getItem(position);

        String message = ChatMessage.getBody();
        String file_url = ChatMessage.getFile_url();
        if (!file_url.equals("")) {
            message = "Files";
        }

        String _name = ChatMessage.getReceiver_name();
        // get the TextView for item name and item description
        TextView nameView = convertView.findViewById(R.id.nameView);
        TextView messageView = convertView.findViewById(R.id.messageView);

//        Log.e("TAG", "name : " + _name);
        //sets the text for item name and item description from the current item object
        nameView.setText(_name);
        messageView.setText(message);

        // returns the view for the current row
        return convertView;
    }
}