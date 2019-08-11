package com.nconnect.teacher.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.NotificationAdapter;
import com.nconnect.teacher.database.Database;
import com.nconnect.teacher.helper.MySingleton;
import com.nconnect.teacher.model.Notifications.NotificationRequest;
import com.nconnect.teacher.model.Notifications.NotificationRoot;
import com.nconnect.teacher.model.Notifications.Notifications;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    ListView listView;
    Database database;
    int pageNumber = 1;
    int totalCount = 20;
    NotificationAdapter notificationAdapter;
    private List<Notifications> data_list;
    private View loadingDialog;
    private View nodataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        database = new Database(NotificationActivity.this);
        initializeview();

        data_list = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(NotificationActivity.this, data_list);
        listView.setAdapter(notificationAdapter);

        loadingDialog = Utils.initProgress(this, "Please wait . . .");
        loadingDialog.setVisibility(View.VISIBLE);
        loadingDialog.setClickable(false);

        //method to load the data
        loadData(pageNumber);
    }

    void initializeview() {
        nodataLayout = findViewById(R.id.nodataLayout);
        listView = findViewById(R.id.listView);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("Notification");
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalCount) {

                    //here the section to increase the page number to get next 20 items
                    totalCount += 20;
                    pageNumber += 1;
                    loadData(pageNumber);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try{
                    if (Dashboard.notifyItemCount >= 1){

                        Dashboard.notifyItemCount -= 1;
                        Dashboard.setupBadge();
                    }
                }catch (Exception e){}

                RelativeLayout itemLayout = view.findViewById(R.id.mainLayout);
                ImageView statusImageView = view.findViewById(R.id.statusImageView);
                statusImageView.setVisibility(View.GONE);
                itemLayout.setBackgroundColor(getResources().getColor(R.color.white));
                Notifications notifications = data_list.get(i);
                String type = notifications.getRequest().getModel();

                //method to update the read status
                updateReadStatus(notifications.getId());

                if (type.equalsIgnoreCase("story")) {

                    Intent intent = new Intent(NotificationActivity.this, ViewStoryActivity.class);
                    intent.putExtra("isScreen", Constants.notification);
                    intent.putExtra("id", String.valueOf(notifications.getRequest().getValue()));
                    startActivity(intent);
                } else if (type.equalsIgnoreCase("issue")) {
                    Intent intent = new Intent(NotificationActivity.this, ViewIssueActivity.class);
                    intent.putExtra("isScreen", Constants.notification);
                    intent.putExtra("id", String.valueOf(notifications.getRequest().getValue()));
                    startActivity(intent);
                } else if (type.equalsIgnoreCase("announcement")) {
                    Intent intent = new Intent(NotificationActivity.this, ViewAnnouncementActivity.class);
                    intent.putExtra("isScreen", Constants.notification);
                    intent.putExtra("id", String.valueOf(notifications.getRequest().getValue()));
                    startActivity(intent);

                } else if (type.equalsIgnoreCase("event")) {
                    Intent intent = new Intent(NotificationActivity.this, ViewEventsActivity.class);
                    intent.putExtra("isScreen", Constants.notification);
                    intent.putExtra("id", String.valueOf(notifications.getRequest().getValue()));
                    startActivity(intent);
                }
            }
        });
    }

    //method to load the data
    void loadData(int pageNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        NotificationRequest.Params params = new NotificationRequest.Params(pageNumber);
        NotificationRequest notificationRequest = new NotificationRequest(Constants.JSON_RPC, params);

        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        (Utils.httpService(NotificationActivity.this).getNotification(notificationRequest, sessionIdValue)).enqueue(new Callback<NotificationRoot>() {
            @Override
            public void onResponse(Call<NotificationRoot> call, Response<NotificationRoot> response) {
                try {
                    if (response.body() == null){
                        nodataLayout.setVisibility(View.VISIBLE);
                        loadingDialog.setVisibility(View.GONE);
                        return;
                    }
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {

                        List<Notifications> _list = response.body().getResult().getNotifications();
                        data_list.addAll(_list);
                        loadingDialog.setVisibility(View.GONE);
                        notificationAdapter.notifyDataSetChanged();

                        /*NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this, data_list);
                        listView.setAdapter(notificationAdapter);*/

                        /*if (!database.isTableExists(database.NOTIFICATIONTABLENAME, NotificationActivity.this)) {
                            database.createNotificationTable();
                        }

                        Collections.sort(_list, new Comparator<Notifications>(){
                            public int compare(Notifications obj1, Notifications obj2) {
                                // ## Ascending order
                                return Integer.valueOf(obj1.getId()).compareTo(Integer.valueOf(obj2.getId())); // To compare integer values
                            }
                        });

                        for (int i = 0; i < _list.size(); i++) {

                            Notifications notifications = _list.get(i);

                            String read_status = "0";
                            int notification_id = notifications.getId();
                            String message = notifications.getBody();
                            String model = notifications.getRequest().getModel();
                            String date = notifications.getDate();

                            ContentValues values = new ContentValues();
                            values.put(DBModel.Notification.read_status, read_status);
                            values.put(DBModel.Notification.notification_id, notification_id);
                            values.put(DBModel.Notification.message, message);
                            values.put(DBModel.Notification.model, model);
                            values.put(DBModel.Notification.date, date);

                            long local_dbId = database.insertTableValues(database.NOTIFICATIONTABLENAME, values);

                            NotificationDetail notificationDetail = new NotificationDetail(read_status, local_dbId, notification_id, message, model, date);
                            data_list.add(notificationDetail);
                        }
                        NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this, data_list);
                        listView.setAdapter(notificationAdapter);*/
                    } else {

                    }
                } catch (NullPointerException e) {
                    loadingDialog.setVisibility(View.GONE);
                    nodataLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<NotificationRoot> call, Throwable t) {
                loadingDialog.setVisibility(View.GONE);
                nodataLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    //method to update the read status
    private void updateReadStatus(int id) {

        JSONObject jsonObject = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put("notification_id", id);
            params.put("read", true);
            jsonObject.put(Constants.PARAMS, params);
        } catch (JSONException e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, Constants.UPDATENOTIFICATION, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
}
