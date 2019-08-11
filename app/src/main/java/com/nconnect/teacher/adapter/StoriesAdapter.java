package com.nconnect.teacher.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.ViewStoryActivity;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.model.stories.StoryLIke;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = StoriesAdapter.class.getSimpleName();
    private Activity context;
    private LayoutInflater inflater;
    List<Story> data = Collections.emptyList();
    private String sessionIdValue = "";
    int flag = 0;

    public StoriesAdapter(Activity context, List<Story> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.stories_fragment_item, parent, false);
        MyHolderStories holder = new MyHolderStories(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderStories myHolder = (MyHolderStories) holder;
        myHolder.story = data.get(position);
        loadProfileImage(data.get(position).getFromThumpImg(), myHolder.ivprofileImage);
        myHolder.tv_class_section.setText(myHolder.story.getTo());
        myHolder.tv_content.setText(myHolder.story.getShortDesc());
        myHolder.tv_date.setText(myHolder.story.getDate());
        myHolder.tv_designation.setText(myHolder.story.getFromDesignation());
        myHolder.tv_teacherName.setText(myHolder.story.getFromName());
        myHolder.tv_title.setText(myHolder.story.getTitle());
        MultipleImageAdapter itemListDataAdapter = new MultipleImageAdapter(context, myHolder.story.getMedia(), "stories");
        myHolder.recyclerMultiImage.setHasFixedSize(true);
        myHolder.recyclerMultiImage.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        myHolder.recyclerMultiImage.setLayoutManager(layoutManager);
        myHolder.recyclerMultiImage.setAdapter(itemListDataAdapter);
        List<String> documents = myHolder.story.getDocuments();
        DocumentsAdapter documentsAdapter = new DocumentsAdapter(context, documents);
        myHolder.recyclerDocuments.setLayoutManager(new LinearLayoutManager(context));
        myHolder.recyclerDocuments.setAdapter(documentsAdapter);
        myHolder.tvCommentsCount.setText(myHolder.story.getComments() + " " + Constants.COMMENTS);
        myHolder.tvLikesCount.setText(myHolder.story.getLikes() + " " + Constants.LIKES);

        if (!myHolder.story.getLike()) {
            myHolder.ivLike.setChecked(false);
        } else {
            myHolder.ivLike.setChecked(true);
        }
        int likes = myHolder.story.getLikes();
        int comments = myHolder.story.getComments();
        if (likes == 0 || likes == 1) {
            myHolder.tvLikesCount.setText(likes + " like");
            myHolder.tvCommentsCount.setText(comments + " comment");
        } else {
            myHolder.tvLikesCount.setText(likes + " likes");
            myHolder.tvCommentsCount.setText(comments + " comments");
        }

        myHolder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myHolder.ivLike.isChecked()) {//already liked so unlike
                    myHolder.ivLike.setChecked(false);
                    String lastIndexStr = myHolder.tvLikesCount.getText().toString();
                    lastIndexStr = lastIndexStr.split(" ")[0];
                    int likes = Integer.parseInt(lastIndexStr);
                    likes = likes - 1;
                    if (likes > 1) {
                        myHolder.tvLikesCount.setText(likes + " Likes");
                        unLikePost(myHolder.story, myHolder);
                    } else {
                        myHolder.tvLikesCount.setText(likes + " Like");
                        unLikePost(myHolder.story, myHolder);
                    }
                } else {//already unliked so like
                    myHolder.ivLike.setChecked(true);
                    String lastIndexStr = myHolder.tvLikesCount.getText().toString();
                    lastIndexStr = lastIndexStr.split(" ")[0];
                    int likes = Integer.parseInt(lastIndexStr);
                    likes = likes + 1;
                    if (likes > 1) {
                        myHolder.tvLikesCount.setText(likes + " Likes");
                        likePost(myHolder.story, myHolder);
                    } else {
                        myHolder.tvLikesCount.setText(likes + " Like");
                        likePost(myHolder.story, myHolder);
                    }
                }
            }
        });
        myHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Story story = data.get(position);
                if (myHolder.ivLike.isChecked()) {
                    story.setLike(true);
                } else {
                    story.setLike(false);
                }
                String data = myHolder.tvLikesCount.getText().toString();
                data = data.substring(0, data.lastIndexOf(" "));
                story.setLikes(Integer.parseInt(data));
                String singlePostData = new Gson().toJson(story);
                context.startActivity(new Intent(context, ViewStoryActivity.class)
                        .putExtra(Constants.STORIES, singlePostData)
                        .putExtra("isScreen", "storiesFragment")
                        .putExtra("cur_position", position));
                Constants.isPreviewCliked = false;
            }
        });
    }


    private void loadProfileImage(String url, ImageView imageView) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String profileImage = preferences.getString(Constants.PROFILE_IMAGE, "");
        String sessionId = preferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        if (url != null) {
            if (url.isEmpty()) {
                Glide.with(context).load(R.drawable.ncp_avator).into(imageView);
            } else {
                GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder().addHeader("Cookie", sessionIdValue).build());
                Glide.with(context).load(glideUrl).apply(new RequestOptions().circleCrop().placeholder(R.drawable.ncp_avator).error(R.drawable.ncp_avator)).into(imageView);
            }
        }
    }

    private void unLikePost(Story story, MyHolderStories myHolderStories) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        Params params = new Params();
        params.setStory_id(story.getStoryId());
        params.setLike(false);
        StoryLIke unlikeStory = new StoryLIke();
        unlikeStory.setParams(params);
//        Log.e("TGA", "unlike json model data : " + new Gson().toJson(unlikeStory));
        sessionIdValue = sharedPreferences.getString(Constants.SESSION_ID, "");
        sessionIdValue = "session_id" + "=" + sessionIdValue;
        (Utils.httpService(context).likeStory(unlikeStory, sessionIdValue)).enqueue(new Callback<StoryLIke>() {
            @Override
            public void onResponse(Call<StoryLIke> call, retrofit2.Response<StoryLIke> response) {
//                Log.e("TAg", "response : " + new Gson().toJson(response.body()));
                try {

                    if (response.body().getResult().getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
//                        Log.e("TAG", "liked : " + story.getLikes());
                    } else {
                        myHolderStories.tvLikesCount.setText(story.getLikes() + " likes");
                        myHolderStories.ivLike.setChecked(true);
                    }
                } catch (NullPointerException e) {
                    myHolderStories.tvLikesCount.setText(story.getLikes() + " likes");
                    myHolderStories.ivLike.setChecked(true);
//                    Log.e("TAG", "exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<StoryLIke> call, Throwable t) {

                Log.e("TAg", "Failure : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(context, "Please check your network connection");
                }
            }
        });
    }

    private void likePost(Story story, MyHolderStories myHolderStories) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        Params params = new Params();
        params.setStory_id(story.getStoryId());
        params.setLike(true);
        StoryLIke likeStory = new StoryLIke();
        likeStory.setParams(params);
//        Log.e("TAG", "like json model data : " + new Gson().toJson(likeStory));
        sessionIdValue = sharedPreferences.getString(Constants.SESSION_ID, "");
        sessionIdValue = "session_id" + "=" + sessionIdValue;
        (Utils.httpService(context).likeStory(likeStory, sessionIdValue)).enqueue(new Callback<StoryLIke>() {
            @Override
            public void onResponse(Call<StoryLIke> call, retrofit2.Response<StoryLIke> response) {
//                Log.e("TAG", "response : " + new Gson().toJson(response.body()));
                try {
                    assert response.body() != null;
                    if (response.body().getResult().getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
//                        Log.e("TAG", "liked : " + story.getLikes());
                    } else {
                        myHolderStories.tvLikesCount.setText(story.getLikes() + " likes");
                        myHolderStories.ivLike.setChecked(false);
                    }
                } catch (NullPointerException e) {
                    myHolderStories.tvLikesCount.setText(story.getLikes() + " likes");
                    myHolderStories.ivLike.setChecked(false);
//                    Utils.showToastCustom(context, "Something went wrong please try again");
//                    Log.e("TAg", "Null pointer exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<StoryLIke> call, Throwable t) {
//                Log.e("TAG", "Failure  ; " + t);
                myHolderStories.ivLike.setChecked(false);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(context, "Please check your network connection");
                }
            }
        });
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

    class MyHolderStories extends RecyclerView.ViewHolder {
        TextView tv_teacherName, tv_designation, tv_date, tv_title, tv_class_section, tvLikesCount, tvCommentsCount;
        ImageView ivprofileImage, ivComment;
        CheckedTextView ivLike;
        TextView tv_content;
        RecyclerView recyclerMultiImage, recyclerDocuments;
        CardView container;
        Story story;

        public MyHolderStories(View itemView) {
            super(itemView);
            tv_teacherName = (TextView) itemView.findViewById(R.id.stories_profile_name);
            tv_designation = (TextView) itemView.findViewById(R.id.stories_designation);
            tv_date = (TextView) itemView.findViewById(R.id.stories_date);
            tv_title = (TextView) itemView.findViewById(R.id.stories_title);
            tv_class_section = (TextView) itemView.findViewById(R.id.stories_classandsection);
            tv_content = (TextView) itemView.findViewById(R.id.stories_content);
            ivprofileImage = (ImageView) itemView.findViewById(R.id.stories_profile_image);
            recyclerMultiImage = (RecyclerView) itemView.findViewById(R.id.stories_images_recyclerview);
            tvLikesCount = (TextView) itemView.findViewById(R.id.stories_likes_count);
            tvCommentsCount = (TextView) itemView.findViewById(R.id.stories_comment_count);
            ivLike = (CheckedTextView) itemView.findViewById(R.id.stories_like);
            ivComment = (ImageView) itemView.findViewById(R.id.stories_comment);
            container = (CardView) itemView.findViewById(R.id.postStoriesCard);
            recyclerDocuments = (RecyclerView) itemView.findViewById(R.id.stories_docuements_recyclerview);
        }
    }
}

