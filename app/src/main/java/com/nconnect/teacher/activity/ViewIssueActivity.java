package com.nconnect.teacher.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.IssueCommentsAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.helper.MySingleton;
import com.nconnect.teacher.listener.RecyclerTouchListener;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.issues.CloseIssue;
import com.nconnect.teacher.model.issues.GetIssuesResponse;
import com.nconnect.teacher.model.issues.IssueComment;
import com.nconnect.teacher.model.issues.IssueComments;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewIssueActivity extends AppCompatActivity {

    private static final String TAG = " ViewIssueActivity";
    private android.support.v7.widget.Toolbar toolbar;
    private TextView tvContent, tvProfileName, tvDesignation, tvToolbarTitle;
    private ImageView ivProfileImage, ivSend;
    private RecyclerView recyclerComments;
    private LinearLayout btnReply, btnCloseIssue, btnCallParent, containerReply, containerComment;
    private GetIssuesResponse.IssueDetails issues;
    private EditText edCommendText;
    private IssueCommentsAdapter mAdapter;
    private List<IssueComment> commentList;
    private int issueID;
    private LinearLayout containerIssueMsgResolved;
    private ImageView ivIssueMsgIcon;
    private TextView tvIssueMessage;
    private String isScreen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_issue);
        initializeViews();
    }

    private void initializeViews() {
        tvIssueMessage = (TextView) findViewById(R.id.issue_message);
        ivIssueMsgIcon = (ImageView) findViewById(R.id.issue_message_icon);
        containerIssueMsgResolved = (LinearLayout) findViewById(R.id.issue_message_resolved);
        tvToolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ivSend = (ImageView) findViewById(R.id.issues_send);
        edCommendText = (EditText) findViewById(R.id.issues_comment);
        tvContent = (TextView) findViewById(R.id.issues_content);
        tvProfileName = (TextView) findViewById(R.id.issues_profile_name);
        tvDesignation = (TextView) findViewById(R.id.issues_designation);
        ivProfileImage = (ImageView) findViewById(R.id.issues_profile_image);
        recyclerComments = (RecyclerView) findViewById(R.id.issues_comment_recycler);
        btnReply = (LinearLayout) findViewById(R.id.issues_reply);
        btnCloseIssue = (LinearLayout) findViewById(R.id.issues_close);
        btnCallParent = (LinearLayout) findViewById(R.id.issues_call_parent);
        containerReply = (LinearLayout) findViewById(R.id.issues_reply_container);
        containerComment = (LinearLayout) findViewById(R.id.issues_comment_container);
        getPhoneCallPermission();
        init();
        initializeListenter();
    }

    private void getPhoneCallPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CALL_PHONE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Permission granted");
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                            Toast.makeText(ViewIssueActivity.this, "Please accept this permission to use this application", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void init() {
        commentList = new ArrayList<>();
        Intent intent = getIntent();
        isScreen = intent.getStringExtra("isScreen");
        if (isScreen != null && isScreen != "") {
            if (isScreen.equalsIgnoreCase(Constants.notification)) {
                String id = intent.getStringExtra("id");
                getData(id);
            } else if (isScreen.equalsIgnoreCase("issuesFragment")) {
                String singlePostJson = intent.getStringExtra(Constants.ISSUES);
                Gson gson = new Gson();
                issues = gson.fromJson(singlePostJson, GetIssuesResponse.IssueDetails.class);
                initializeData();
            }
        }
        initializeListenter();
    }

    private void initializeData() {
        issueID = issues.getIssueId();
        tvContent.setText(issues.getDescription());
        tvProfileName.setText(issues.getRaisedBy());
        tvDesignation.setText("Parent of : " + issues.getStudent());
        tvToolbarTitle.setText(issues.getName());
        if (issues.getStatus().equalsIgnoreCase("Open")) {
            containerIssueMsgResolved.setVisibility(View.GONE);
            containerReply.setVisibility(View.VISIBLE);
            containerComment.setVisibility(View.GONE);
        }
        if (issues.getStatus().equalsIgnoreCase("reopen_by_parent")) {
            containerReply.setVisibility(View.VISIBLE);
            containerComment.setVisibility(View.GONE);
            containerIssueMsgResolved.setVisibility(View.VISIBLE);
            containerIssueMsgResolved.setBackgroundColor(getResources().getColor(R.color.issue_unresolved));
            tvIssueMessage.setText("This issue is still open.\n Parent has marked this issue as \"unresolved\"");
        } else if (issues.getStatus().equalsIgnoreCase("escalate")) {
            containerIssueMsgResolved.setVisibility(View.GONE);
            containerReply.setVisibility(View.VISIBLE);
            containerComment.setVisibility(View.GONE);
        } else if (issues.getStatus().equalsIgnoreCase("Close")) {
            containerIssueMsgResolved.setVisibility(View.VISIBLE);
            containerReply.setVisibility(View.GONE);
            containerComment.setVisibility(View.GONE);
            containerIssueMsgResolved.setBackgroundColor(getResources().getColor(R.color.issue_resolved));
            Picasso.get().load(R.drawable.ncp_unlock).error(R.drawable.ncp_unlock).into(ivIssueMsgIcon);
            tvIssueMessage.setText("Issue Closed. \n Waiting for Parent's Confirmation...");
        } else if (issues.getStatus().equalsIgnoreCase(Constants.CLOSE_BY_PARENT)) {
            containerReply.setVisibility(View.GONE);
            containerComment.setVisibility(View.GONE);
            containerIssueMsgResolved.setVisibility(View.VISIBLE);
            containerIssueMsgResolved.setBackgroundColor(getResources().getColor(R.color.issue_resolved));
            Picasso.get().load(R.drawable.ncp_unlock).error(R.drawable.ncp_unlock).into(ivIssueMsgIcon);
            tvIssueMessage.setText("This issue has beed resolved");
        }
        viewMessages();
    }

    private void initializeListenter() {
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerReply.setVisibility(View.GONE);
                containerComment.setVisibility(View.VISIBLE);
            }
        });
        btnCallParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = issues.getMobileRaisedBy();
                phone = "tel:" + phone;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(phone));
                startActivity(intent);
            }
        });
        btnCloseIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeIssue();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBack();
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        recyclerComments.addOnItemTouchListener(new RecyclerTouchListener(ViewIssueActivity.this, recyclerComments, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                IssueComment comment = commentList.get(position);
                int messageId = comment.getMessageId();
                int userId = comment.getUserId();
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                int userIdPref = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
                if (userId == userIdPref) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewIssueActivity.this, R.style.dialog_background);
                    builder.setMessage("Do you want to delete this comment");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Params params = new Params();
                            params.setMessage_id(messageId);
                            Stories stories = new Stories();
                            stories.setStoriesParams(params);
                            stories.setJsonrpc(Constants.JSON_RPC);
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                            String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
                            sessionId = Constants.SESSION_ID + "=" + sessionId;
//                            Log.e(TAG, "json model data : " + new Gson().toJson(stories));
                            (Utils.httpService(ViewIssueActivity.this).deleteStoryComment(stories, sessionId)).enqueue(new Callback<Stories>() {
                                @Override
                                public void onResponse(Call<Stories> call, retrofit2.Response<Stories> response) {
                                    try {
                                        Stories storiesModel = response.body();
//                                        Log.e(TAG, "moedl : " + new Gson().toJson(storiesModel));
                                        Result result = storiesModel.getResult();
                                        if (result.getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                                            viewMessages();
                                        } else {
                                            Toast.makeText(ViewIssueActivity.this, "Cannot Delete Comment", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (NullPointerException e) {
//                                        Log.e(TAG, "Exception : " + e);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Stories> call, Throwable t) {

                                    Log.e(TAG, " error : " + t);
                                    if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                                        Toast.makeText(ViewIssueActivity.this, "PLease check your network connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }));
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

    private void closeIssue() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setStatus("close");
        params.setUserToken(userToken);
        params.setIssueId(issueID);
        CloseIssue issues = new CloseIssue();
        issues.setJsonrpc(Constants.JSON_RPC);
        issues.setParams(params);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e("TGA", "close issue : " + new Gson().toJson(issues));
        final ProgressDialog dialog = Utils.showDialog(ViewIssueActivity.this, "Please wait...");
        dialog.show();
        (Utils.httpService(ViewIssueActivity.this).closeIssue(issues, sessionIdValue)).enqueue(new Callback<CloseIssue>() {
            @Override
            public void onResponse(Call<CloseIssue> call, Response<CloseIssue> response) {
//                Log.e(TAG, "response : " + new Gson().toJson(response.body()));
                dialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResult().getStatus().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                            Utils.showToast(ViewIssueActivity.this, "Issue has been closed. Waiting for parent confirmation");
                            if (isScreen != null && isScreen != "") {
                                if (isScreen.equalsIgnoreCase(Constants.notification)) {
                                    startActivity(new Intent(ViewIssueActivity.this, Dashboard.class));
                                } else {
                                    finish();
                                }
                            } else {
                                finish();
                            }
                        } else {
                            Utils.showToast(ViewIssueActivity.this, "Cannot close issue please try again");
                        }

                    } else {
                        Utils.showToast(ViewIssueActivity.this, "cannot close issue please try again");
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<CloseIssue> call, Throwable t) {
                dialog.dismiss();
//                Log.e(TAG, "failure : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(ViewIssueActivity.this, "Please check your network connection ");
                }
            }
        });

    }

    private void postComment() {
        if (!validate()) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setUserToken(userToken);
        params.setIssueId(issueID);
        params.setMessage(edCommendText.getText().toString());
        IssueComments comments = new IssueComments();
        comments.setJsonrpc(Constants.JSON_RPC);
        comments.setParams(params);
        edCommendText.setText("");
//        Log.e(TAG, "json model data comments : " + new Gson().toJson(comments));
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        (Utils.httpService(ViewIssueActivity.this).postIssueMsgComment(comments, sessionIdValue)).enqueue(new Callback<IssueComments>() {
            @Override
            public void onResponse(retrofit2.Call<IssueComments> call, Response<IssueComments> response) {
                try {
//                    Log.e(TAG, "Resposne : +" + new Gson().toJson(response.body()));
//                    Log.e(TAG, "Error : " + new Gson().toJson(response.errorBody()));
                    if (response.body() != null && response.body().getResult().getStatus().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        viewMessages();
                    } else {
                        Utils.showToastCustom(ViewIssueActivity.this, "Cannot load IssueComments");
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "Exception : " + e);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<IssueComments> call, Throwable t) {
//                Log.e(TAG, "Failure response : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToast(ViewIssueActivity.this, "Please check your internet connection");
                }
            }
        });

    }

    public void viewMessages() {
        JSONObject requestJSon = setupJSon();
//        Log.e(TAG, "request json : " + requestJSon);
        new SendJsonDataToServer().execute(requestJSon.toString());
    }

    private JSONObject setupJSon() {
        JSONObject baseJson = new JSONObject();
        JSONObject paramsJson = new JSONObject();
        try {
            paramsJson.put(Constants.ISSUES_ID, issueID);
            paramsJson.put(Constants.LAST_UPDATE, 0);
            baseJson.put(Constants.JSON_RPC_, Constants.JSON_RPC);
            baseJson.put(Constants.PARAMS, paramsJson);
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e("TAG", "Exception json : " + e);
        }
        return baseJson;
    }

    @SuppressLint("StaticFieldLeak")
    class SendJsonDataToServer extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String JsonResponse = null;
            String JsonDATA = strings[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                String BASEURL = Constants.BASE_URL + Constants.VIEW_COMMENTS;
                URL url = new URL(BASEURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);// is output buffer writter
                urlConnection.setRequestMethod("POST"); //set headers and method
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Cookie", "session_id=" + sharedPreferences.getString(Constants.SESSION_ID, ""));
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();//input stream
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;// Nothing to do.
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine).append("\n");
                if (buffer.length() == 0) {// Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
//                Log.e("TAG", JsonResponse);  //response data
                return JsonResponse;//send to post execute
            } catch (IOException e) {
                e.printStackTrace();
//                Log.e(TAG, "IO EXCEPTION : " + e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
//                        Log.e("TAG", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
//            Log.e(TAG, "s : " + s);
            try {
                commentList.clear();
                JSONObject jsonObject = new JSONObject(s);
                JSONObject resultJson = jsonObject.getJSONObject("result");
                JSONArray jsonArray = resultJson.getJSONArray("comments");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    IssueComment comment = new IssueComment();
                    comment.setCreatedAt(jsonObject.getInt(Constants.CREATED_AT));
                    comment.setMessage(jsonObject.getString(Constants.MESSAGE));
                    comment.setMessageId(jsonObject.getInt(Constants.MESSAGE_ID));
                    comment.setName(jsonObject.getString(Constants.NAME));
                    comment.setUserId(jsonObject.getInt(Constants.USERID));
                    comment.setLogin_type(jsonObject.getString(Constants.LOGIN_TYPE));
                    comment.setProfileImage(jsonObject.getString("profile_image"));
//                    Log.e(TAG, "messages : " + comment.getMessage());
                    commentList.add(comment);
                }
//                Log.e(TAG, " list of messages : " + new Gson().toJson(commentList));
                LinearLayoutManager manager = new LinearLayoutManager(ViewIssueActivity.this);
                manager.setStackFromEnd(true);
                manager.setReverseLayout(true);
                recyclerComments.setLayoutManager(manager);
                mAdapter = new IssueCommentsAdapter(ViewIssueActivity.this, commentList);
                mAdapter.notifyDataSetChanged();
                recyclerComments.setAdapter(mAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
//                Log.e(TAG, "JSon Exception result Array : " + e);
            }
        }
    }

    //method to get data from api
    void getData(String id) {

        JSONObject singleStory = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.ISSUES_ID, Integer.valueOf(id));
            singleStory.put(Constants.PARAMS, params);
        } catch (JSONException e) {
//            Log.e(TAG, "josn exceptiion : " + e);
        }
//        Log.e(TAG, "model data : " + singleStory);
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, Constants.GETSINGLE_ISSUES_URL, singleStory, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.e(TAG, "response : " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject resultJson = jsonObject.getJSONObject("result");
                    String responseStr = resultJson.getString("response");
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        issues = new Gson().fromJson(resultJson.getString("issue"), GetIssuesResponse.IssueDetails.class);
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
//                Log.e(TAG, "error : " + error);
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
    protected void onResume() {
        super.onResume();
        viewMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.ISSUES));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            viewMessages();
        }
    };

    private boolean validate() {
        if (edCommendText.getText().toString().isEmpty()) {
            Toast.makeText(this, "IssueComment should not be empty ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
