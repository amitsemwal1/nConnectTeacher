package com.nconnect.teacher.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.helper.MySingleton;
import com.nconnect.teacher.model.announcements.Announcement;
import com.nconnect.teacher.util.Constants;

public class ViewAnnouncementActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private TextView toolbarTitle, tvContent, tvPostedBy;
    private Announcement announcement;
    private String isScreen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_announcement);
        initializeViews();
    }

    private void initializeViews() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        tvContent = (TextView) findViewById(R.id.view_annoucnement_content);
        tvPostedBy = (TextView) findViewById(R.id.view_annoucnement_postedby);
        Intent intent = getIntent();

        isScreen = intent.getStringExtra("isScreen");
        if (isScreen != null && isScreen != "") {
            if (isScreen.equalsIgnoreCase(Constants.notification)) {
                String id = intent.getStringExtra("id");
                getData(id);
            } else if (isScreen.equalsIgnoreCase("announcementFragment")) {
                String singlePostData = intent.getStringExtra(Constants.ANNOUNCEMENTS);
                Gson gson = new Gson();
                announcement = gson.fromJson(singlePostData, Announcement.class);
                initializeData();
            }
        }
    }

    private void initializeData() {
        tvContent.setText(announcement.getDesc());
        toolbarTitle.setText(announcement.getTitle());
        tvPostedBy.setText(announcement.getPostedBy() + " on " + announcement.getDate());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBack();
            }
        });
    }

    //method to get data from api
    void getData(String id) {

        JSONObject singleStory = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.ANNOUNCEMENT_ID, Integer.valueOf(id));
            singleStory.put(Constants.PARAMS, params);
        } catch (JSONException e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, Constants.GETSINGLE_ANNOUNCEMENT_URL, singleStory, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject resultJson = jsonObject.getJSONObject("result");
                    String responseStr = resultJson.getString("response");
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        announcement = new Gson().fromJson(resultJson.getString("announcement"), Announcement.class);
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
