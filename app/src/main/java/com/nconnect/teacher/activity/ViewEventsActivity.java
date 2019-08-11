package com.nconnect.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.helper.FullScreenPreview;
import com.nconnect.teacher.helper.MySingleton;
import com.nconnect.teacher.model.events.Event;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class ViewEventsActivity extends AppCompatActivity {

    private ImageView ivContentImage, ivBookmark;
    private TextView tvDate, tvTime, tvPlace, tvPlaceAddress, tvContent;
    private android.support.v7.widget.Toolbar toolbarTitle;
    private Event event;
    private String sessionIdValue;
    private TextView tvTitle;
    private AppBarLayout appBarLayout;
    private int flag = 0;
    private boolean isImageFitToScreen;
    private String isScreen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);
        initializeViews();
    }

    private void initializeViews() {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbarTitle = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbarTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBack();
            }
        });
        tvTitle = (TextView) findViewById(R.id.view_events_title);
        ivBookmark = (ImageView) findViewById(R.id.view_events_bookmark);
        ivContentImage = (ImageView) findViewById(R.id.view_events_content_image);
        tvDate = (TextView) findViewById(R.id.view_events_date);
        tvTime = (TextView) findViewById(R.id.view_events_time);
        tvPlace = (TextView) findViewById(R.id.view_events_place_name);
        tvPlaceAddress = (TextView) findViewById(R.id.view_events_place_address);
        tvContent = (TextView) findViewById(R.id.view_events_content);
        Intent intent = getIntent();
        isScreen = intent.getStringExtra("isScreen");

        if (isScreen != null && isScreen != "") {
            if (isScreen.equalsIgnoreCase(Constants.notification)) {
                String id = intent.getStringExtra("id");
                getData(id);
            } else if (isScreen.equalsIgnoreCase("eventsFragment")) {
                String singlePostData = intent.getStringExtra(Constants.EVENTS);
                Gson gson = new Gson();
                event = gson.fromJson(singlePostData, Event.class);
                initializeData();
            }
        }

        initializeListener();
    }

    private void initializeListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                float offsetAlpha = (appBarLayout.getY() / appBarLayout.getTotalScrollRange());
                ivContentImage.setAlpha(1 - (offsetAlpha * -1));
            }
        });
        ivContentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("code", "events");
                bundle.putString("image", event.getDp());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                FullScreenPreview newFragment = FullScreenPreview.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(transaction, "imagepreview");
            }
        });

        ivBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    ivBookmark.setImageDrawable(getResources().getDrawable(R.drawable.ncp_bookmark));
                    Utils.showToastCustom(ViewEventsActivity.this, "You Bookmarked this image");
                    flag = 1;
                } else if (flag == 1) {
                    ivBookmark.setImageDrawable(getResources().getDrawable(R.drawable.ncp_bookmark_outline));
                    flag = 0;
                }
            }
        });
    }

    private void initializeData() {
        toolbarTitle.setTitle(event.getTitle());
        if (event.getDp() != null && !event.getDp().isEmpty()) {
            loadImageUsingGlide(event.getDp());
        }
        tvTitle.setText(event.getTitle());
        String fromDate = event.getDateFrom().substring(0, event.getDateFrom().indexOf(","));
        String toDate = event.getDateTo().substring(0, event.getDateTo().indexOf(","));
        tvDate.setText(fromDate + " - " + toDate);
        String resultFromTime = event.getDateFrom().substring(event.getDateFrom().lastIndexOf(',') + 1).trim();
        String resultToTime = event.getDateTo().substring(event.getDateTo().lastIndexOf(',') + 1).trim();
        String fromTime = ((resultFromTime.substring(4, resultFromTime.length())).trim());
        String toTime = ((resultToTime.substring(4, resultToTime.length())).trim());
        tvTime.setText(fromTime + " - " + toTime);
        tvPlace.setText(event.getLocation());
        tvPlaceAddress.setText(event.getAddress());
        tvContent.setText(event.getShortDesc());
    }

    private void loadImageUsingGlide(@NonNull String path) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        GlideUrl glideUrl = new GlideUrl(path, new LazyHeaders.Builder().addHeader("Cookie", sessionIdValue).build());
        Glide
                .with(this)
                .load(glideUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ncp_placeholder)
                        .centerCrop())
                .into(ivContentImage);
    }


    //method to get data from api
    void getData(String id) {

        JSONObject singleStory = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.EVENT_ID, Integer.valueOf(id));
            singleStory.put(Constants.PARAMS, params);
        } catch (JSONException e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, Constants.GETSINGLE_EVENT_URL, singleStory, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject resultJson = jsonObject.getJSONObject("result");
                    String responseStr = resultJson.getString("response");
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        event = new Gson().fromJson(resultJson.getString("event"), Event.class);
                        initializeData();
                    } else {
                        //Toast.makeText(ViewIssueActivity.this, "Cannot get single story", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("volley", "error : " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                String session = sharedPreferences.getString(Constants.SESSION_ID, "");
                headers.put("Cookie", Constants.SESSION_ID + "=" + session);
                return headers;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pressBack();
    }
    private void pressBack() {
        if (isScreen != null && isScreen != "") {
            if (isScreen.equalsIgnoreCase(Constants.notification)) {
                startActivity(new Intent(this, Dashboard.class));
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

}
