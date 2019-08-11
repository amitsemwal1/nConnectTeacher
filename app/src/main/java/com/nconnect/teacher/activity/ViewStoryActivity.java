package com.nconnect.teacher.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.DocumentsAdapter;
import com.nconnect.teacher.adapter.MultipleImageAdapter;
import com.nconnect.teacher.adapter.StoryCommentsAdapter;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.helper.MySingleton;
import com.nconnect.teacher.listener.RecyclerTouchListener;
import com.nconnect.teacher.model.Comment;
import com.nconnect.teacher.model.Comments;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.model.stories.StoryLIke;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class ViewStoryActivity extends AppCompatActivity {
    private static final String TAG = "View Stories";
    private TextView tvToolbarTitle;
    private TextView tv_teacherName, tv_designation, tv_date, tv_title, tv_class_section, tvLikesCount, tvCommentsCount;
    private ImageView ivprofileImage;
    private CheckedTextView ivLike;
    private TextView tv_content;
    private RecyclerView recyclerMultiImage, recyclerDocuments;
    private String sessionIdValue;
    private Toolbar toolbar;
    private Story story;
    private RecyclerView recyclerComments;
    private EditText edPostComment;
    private ImageView btnPostComment;
    private Timer timer;
    private LinearLayout containerComment;
    private boolean isFirst = false;
    List<Comment> commentList;
    private StoryCommentsAdapter mAdapter;
    int currentPosition = -1;
    private String isScreen = "";
    private LinearLayout containerLikeComment;
    private RelativeLayout containerLikeCommentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        initilizeViews();
    }

    private void initilizeViews() {
        commentList = new ArrayList<>();
        containerLikeCommentStatus = (RelativeLayout) findViewById(R.id.containerLikeCommentStatus);
        containerLikeComment = (LinearLayout) findViewById(R.id.likeCommentContainer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerDocuments = (RecyclerView) findViewById(R.id.view_stories_attachDocument);
        edPostComment = (EditText) findViewById(R.id.stories_comment_text);
        containerComment = (LinearLayout) findViewById(R.id.stories_comment_container);
        btnPostComment = (ImageView) findViewById(R.id.stories_send);
        recyclerComments = (RecyclerView) findViewById(R.id.view_stories_comment_recycler);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        tvToolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        tv_teacherName = (TextView) findViewById(R.id.view_stories_profile_name);
        tv_designation = (TextView) findViewById(R.id.view_stories_designation);
        tv_date = (TextView) findViewById(R.id.view_stories_date);
        tv_title = (TextView) findViewById(R.id.view_stories_title);
        tv_class_section = (TextView) findViewById(R.id.view_stories_classandsection);
        tv_content = (TextView) findViewById(R.id.view_stories_content);
        ivprofileImage = (ImageView) findViewById(R.id.view_stories_profileimage);
        recyclerMultiImage = (RecyclerView) findViewById(R.id.view_stories_images_recyclerview);
        tvLikesCount = (TextView) findViewById(R.id.view_stories_likes_count);
        tvCommentsCount = (TextView) findViewById(R.id.view_stories_comment_count);
        ivLike = (CheckedTextView) findViewById(R.id.view_stories_like);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        recyclerComments.setLayoutManager(manager);

        mAdapter = new StoryCommentsAdapter(this, commentList);
        recyclerComments.setAdapter(mAdapter);


        Intent intent = getIntent();
        isScreen = intent.getStringExtra("isScreen");

        if (isScreen != null && isScreen != "") {
            if (isScreen.equalsIgnoreCase(Constants.notification)) {
                String id = intent.getStringExtra("id");
                getStoryDetail(id);
            } else if (isScreen.equalsIgnoreCase("storiesFragment")) {
                String singlePostData = intent.getStringExtra(Constants.STORIES);
                story = new Gson().fromJson(singlePostData, Story.class);
                currentPosition = intent.getIntExtra("cur_position", 0);
                initializeData();
            }
        }

        initializeListener();

    }

    private void getStoryDetail(String id) {
/*
        Stories stories = new Stories();
        Params params = new Params();
        params.setStory_id(Integer.valueOf(id));
        stories.setStoriesParams(params);*/
        JSONObject singleStory = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.STORIES_ID, Integer.valueOf(id));
            singleStory.put(Constants.PARAMS, params);
        } catch (JSONException e) {
//            Log.e(TAG, "josn exceptiion : " + e);
        }
//        Log.e(TAG, "model data : " + singleStory);
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, Constants.GETSINGLE_STORY_URL, singleStory, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.e(TAG, "response : " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject resultJson = jsonObject.getJSONObject("result");
                    String responseStr = resultJson.getString("response");
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        story = new Gson().fromJson(resultJson.getString("story"), Story.class);
                        initializeData();
                    } else {
                        Toast.makeText(ViewStoryActivity.this, "Cannot get single story", Toast.LENGTH_SHORT).show();
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

    private void initializeListener() {

        recyclerComments.addOnItemTouchListener(new RecyclerTouchListener(ViewStoryActivity.this, recyclerComments, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                Comment comment = commentList.get(position);
                int messageId = comment.getMessageId();
                int userId = comment.getUserId();
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                int userIdPref = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
                if (userId == userIdPref) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewStoryActivity.this, R.style.dialog_background);
                    builder.setMessage("Do you want to delete this comment ?");
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
                            (Utils.httpService(ViewStoryActivity.this).deleteStoryComment(stories, sessionId)).enqueue(new Callback<Stories>() {
                                @Override
                                public void onResponse(Call<Stories> call, retrofit2.Response<Stories> response) {
                                    try {
                                        Stories storiesModel = response.body();
//                                        Log.e(TAG, "moedl : " + new Gson().toJson(storiesModel));
                                        Result result = storiesModel.getResult();
                                        if (result.getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                                            loadComments();
                                        } else {
                                            Toast.makeText(ViewStoryActivity.this, "Cannot Delete Comment", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (NullPointerException e) {
//                                        Log.e(TAG, "Exception : " + e);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Stories> call, Throwable t) {

//                                    Log.e(TAG, " error : " + t);
                                    if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                                        Toast.makeText(ViewStoryActivity.this, "PLease check your network connection", Toast.LENGTH_SHORT).show();
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

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivLike.isChecked()) {          //already liked so unlike
                    ivLike.setChecked(false);
                    String lastIndexStr = tvLikesCount.getText().toString();
                    lastIndexStr = lastIndexStr.split(" ")[0];
                    int likes = Integer.parseInt(lastIndexStr);
                    likes = likes - 1;
                    if (likes > 1) {
                        tvLikesCount.setText(likes + " Likes");
                        unLikePost();
                    } else {
                        tvLikesCount.setText(likes + " Like");
                        unLikePost();
                    }
                } else {            //already unliked so like
                    ivLike.setChecked(true);
                    String lastIndexStr = tvLikesCount.getText().toString();
                    lastIndexStr = lastIndexStr.split(" ")[0];
                    int likes = Integer.parseInt(lastIndexStr);
                    likes = likes + 1;
                    if (likes > 1) {
                        tvLikesCount.setText(likes + " Likes");
                        likePost();
                    } else {
                        tvLikesCount.setText(likes + " Like");
                        likePost();
                    }
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressBack();
            }
        });
        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
    }

    private void likePost() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        Params params = new Params();
        params.setStory_id(story.getStoryId());
        params.setLike(true);
        StoryLIke likeStory = new StoryLIke();
        likeStory.setParams(params);
//        Log.e("TAG", "like json model data : " + new Gson().toJson(likeStory));
        sessionIdValue = sharedPreferences.getString(Constants.SESSION_ID, "");
        sessionIdValue = "session_id" + "=" + sessionIdValue;
        (Utils.httpService(this).likeStory(likeStory, sessionIdValue)).enqueue(new Callback<StoryLIke>() {
            @Override
            public void onResponse(Call<StoryLIke> call, retrofit2.Response<StoryLIke> response) {
//                Log.e("TAG", "response : " + new Gson().toJson(response.body()));
                try {
                    assert response.body() != null;
                    if (response.body().getResult().getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
//                        Log.e("TAG", "liked : " + story.getLikes());
                    } else {
                        tvLikesCount.setText(story.getLikes() + " likes");
                        ivLike.setChecked(false);
                    }
                } catch (NullPointerException e) {
                    tvLikesCount.setText(story.getLikes() + " likes");
                    ivLike.setChecked(false);
//                    Utils.showToastCustom(ViewStoryActivity.this, "Something went wrong please try again");
//                    Log.e("TAg", "Null pointer exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<StoryLIke> call, Throwable t) {
//                Log.e("TAG", "Failure  ; " + t);
                ivLike.setChecked(false);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(ViewStoryActivity.this, "Please check your network connection");
                }
            }
        });
    }

    private void unLikePost() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        Params params = new Params();
        params.setStory_id(story.getStoryId());
        params.setLike(false);
        StoryLIke unlikeStory = new StoryLIke();
        unlikeStory.setParams(params);
//        Log.e("TGA", "unlike json model data : " + new Gson().toJson(unlikeStory));
        sessionIdValue = sharedPreferences.getString(Constants.SESSION_ID, "");
        sessionIdValue = "session_id" + "=" + sessionIdValue;
        (Utils.httpService(this).likeStory(unlikeStory, sessionIdValue)).enqueue(new Callback<StoryLIke>() {
            @Override
            public void onResponse(Call<StoryLIke> call, retrofit2.Response<StoryLIke> response) {
//                Log.e("TAg", "response : " + new Gson().toJson(response.body()));
                try {

                    if (response.body().getResult().getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
//                        Log.e("TAG", "liked : " + story.getLikes());
                    } else {
                        tvLikesCount.setText(story.getLikes() + " likes");
                        ivLike.setChecked(true);
                    }
                } catch (NullPointerException e) {
                    tvLikesCount.setText(story.getLikes() + " likes");
                    ivLike.setChecked(true);
//                    Log.e("TAG", "exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<StoryLIke> call, Throwable t) {

//                Log.e("TAg", "Failure : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(ViewStoryActivity.this, "Please check your network connection");
                }
            }
        });
    }

    private void postComment() {

        if (edPostComment.getText().toString().isEmpty()) {
            Utils.showToastCustom(this, "Comment field should not be empty");
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setStory_id(story.getStoryId());
        params.setMessage(edPostComment.getText().toString());
        if (userToken != 0) {
            params.setUserToken(userToken);
        }
        Stories stories = new Stories();
        stories.setStoriesParams(params);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");

        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        edPostComment.setText("");
//        Log.e(TAG, "json model data : " + new Gson().toJson(stories));
        (Utils.httpService(this).postStoryComment(stories, sessionIdValue)).enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, retrofit2.Response<Stories> response) {

//                Log.e(TAG, "response : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getStatus().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        String commentCount = tvCommentsCount.getText().toString();
                        commentCount = commentCount.split(" ")[0];
                        int comments = Integer.parseInt(commentCount);
                        comments = comments + 1;
                        if (comments < 1) {
                            tvCommentsCount.setText(comments + " comment");
                        } else {
                            tvCommentsCount.setText(comments + " comments");
                        }
                        loadComments();
                    } else {
                        Utils.showToastCustom(ViewStoryActivity.this, "Cannot post comment for this story");
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "Exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {

//                Log.e(TAG, "failure : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(ViewStoryActivity.this, "Please check your network connectionl");
                }
            }
        });
    }

    private void initializeData() {

        tvToolbarTitle.setText(story.getFromName() + " Post");
        int likes = story.getLikes();
        int comments = story.getComments();
        if (likes == 0 || likes == 1) {
            tvLikesCount.setText(likes + " like");
            tvCommentsCount.setText(comments + " comment");
        } else {
            tvLikesCount.setText(likes + " likes");
            tvCommentsCount.setText(comments + " comments");
        }
        String profilePAth = story.getFromThumpImg();
        if (profilePAth.isEmpty()) {
            Picasso.get().load(R.drawable.ncp_avator).error(R.drawable.ncp_avator).into(ivprofileImage);
        } else {
            loadProfileImageusingPicasso(profilePAth);
        }
        if (!story.getLike()) {
            ivLike.setChecked(false);
        } else {
            ivLike.setChecked(true);
        }
        tv_class_section.setText(story.getTo());
        tv_content.setText(story.getShortDesc());
        tv_date.setText(story.getDate());
        tv_designation.setText(story.getFromDesignation());
        tv_teacherName.setText(story.getFromName());
        tv_title.setText(story.getTitle());
        List<String> media = story.getMedia();

        MultipleImageAdapter itemListAdapter = new MultipleImageAdapter(ViewStoryActivity.this, media, "view_story");
        recyclerMultiImage.setHasFixedSize(true);
        recyclerMultiImage.setLayoutManager(new LinearLayoutManager(this));
        recyclerMultiImage.setAdapter(itemListAdapter);

        List<String> document = story.getDocuments();
        DocumentsAdapter documentsAdapter = new DocumentsAdapter(this, document);
        recyclerDocuments.setLayoutManager(new LinearLayoutManager(this));
        recyclerDocuments.setAdapter(documentsAdapter);
        String storyStatus = story.getStatus();
        if (storyStatus.equalsIgnoreCase("pending") || storyStatus.equalsIgnoreCase("rejected")) {
            containerComment.setVisibility(View.GONE);
            containerLikeComment.setVisibility(View.GONE);
            containerLikeCommentStatus.setVisibility(View.GONE);
        } else {
            containerLikeCommentStatus.setVisibility(View.VISIBLE);
            containerLikeComment.setVisibility(View.VISIBLE);
            containerComment.setVisibility(View.VISIBLE);
            loadComments();
        }
    }

    private void loadComments() {
        Params params = new Params();
        params.setStory_id(story.getStoryId());
        params.setLastUpdate(0);
        Comments comments = new Comments();
        comments.setJsonrpc(Constants.JSON_RPC);
        comments.setParams(params);
//        Log.e(TAG, "params : " + new Gson().toJson(comments));
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        (Utils.httpService(this).storyComments(comments, sessionIdValue)).enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, retrofit2.Response<Comments> response) {
//                Log.e(TAG, "response : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        List<Comment> newComments = response.body().getResult().getComments();
                        commentList.clear();
                        commentList.addAll(newComments);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ViewStoryActivity.this, "Cannot get comments", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<Comments> call, Throwable t) {

                Log.e(TAG, "failure : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Toast.makeText(ViewStoryActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                sendLikeCommentCount();
            }
        }
    }

    private void sendLikeCommentCount() {
//        Log.e(TAG, "send like & comment ");
        Intent intent = new Intent("like_comment_count");
        intent.putExtra("sendCount", true);
        intent.putExtra("sendPosition", currentPosition);
        String commentCount = tvCommentsCount.getText().toString();
        commentCount = commentCount.split(" ")[0];
        int comments = Integer.parseInt(commentCount);
        String likeCount = tvLikesCount.getText().toString();
        likeCount = likeCount.split(" ")[0];
        int likes = Integer.parseInt(likeCount);
        intent.putExtra("sendLikes", likes);
        intent.putExtra("sendComments", comments);
        if (ivLike.isChecked()) {
            intent.putExtra("sendIsLiked", true);
        } else {
            intent.putExtra("sendIsLiked", false);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void loadProfileImageusingPicasso(String path) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");

        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Cookie", sessionIdValue)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();
        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client))
                .build();
        if (path.isEmpty()) {
            picasso.load(R.drawable.ncp_avator).error(R.drawable.ncp_avator)
                    .into(ivprofileImage);
        } else {
            picasso.load(path).error(R.drawable.ncp_avator)
                    .into(ivprofileImage);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter(Constants.STORIES));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String storyId = intent.getStringExtra("story_id");
            int type = intent.getIntExtra("type", 0);
//            Toast.makeText(context, "receiver "+ type, Toast.LENGTH_SHORT).show();
            loadComments();
        }
    };
}
