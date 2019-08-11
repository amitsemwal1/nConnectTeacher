package com.nconnect.teacher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.events.Event;
import com.nconnect.teacher.util.Constants;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Event> eventList = Collections.emptyList();
    private String sessionIdValue;

    public EventsAdapter(Context context, List<Event> storyList) {
        this.context = context;
        this.eventList = storyList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.events_item, parent, false);
        TrendingEventsHolder eventsHolder = new TrendingEventsHolder(view);
        return eventsHolder;
    }

    @SuppressLint({"ResourceAsColor", "NewApi"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrendingEventsHolder holderEvents = (TrendingEventsHolder) holder;
        Event event = eventList.get(position);
        holderEvents.tvDate.setText(event.getDateFrom() + " - " + event.getDateTo());
        holderEvents.tvTitle.setText(event.getTitle());

        try {
            if (event.getDp().isEmpty()) {
                Glide.with(context).load(R.drawable.kid).apply(new RequestOptions().error(R.drawable.kid)).into(holderEvents.ivContentImage);
            } else {
                loadImageUsingGlide(event.getDp(), holderEvents.ivContentImage);
            }
        } catch (NullPointerException e) {
            Log.e("Exception : ", "" + e);
        }

    }

    private void loadImageUsingGlide(@NonNull String path, ImageView imageView) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        GlideUrl glideUrl = new GlideUrl(path, new LazyHeaders.Builder().addHeader("Cookie", sessionIdValue).build());
        Glide.with(context).load(glideUrl).apply(new RequestOptions().placeholder(R.drawable.ncp_placeholder).centerCrop()).into(imageView);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class TrendingEventsHolder extends RecyclerView.ViewHolder {
        ImageView ivContentImage;
        TextView tvKnowMore, tvTitle, tvDate;
        CardView container;

        public TrendingEventsHolder(@NonNull View itemView) {
            super(itemView);
            ivContentImage = (ImageView) itemView.findViewById(R.id.trendingEventsContentImage);
            tvKnowMore = (TextView) itemView.findViewById(R.id.trendingEventsKnowMore);
            tvTitle = (TextView) itemView.findViewById(R.id.trendingEventsTitle);
            tvDate = (TextView) itemView.findViewById(R.id.trendingEventsDate);
            container = (CardView) itemView.findViewById(R.id.eventsCard);
        }
    }
}
