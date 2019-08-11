package com.nconnect.teacher.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.EditStory;
import com.nconnect.teacher.helper.CircleTransform;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class ApprovalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ApprovalAdapter.class.getSimpleName();
    private Activity context;
    private LayoutInflater inflater;
    List<Story> data = Collections.emptyList();
    private String sessionIdValue;
    int type;
    private String storyStatus = "";

    public ApprovalAdapter(Activity context, List<Story> lists, int type) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = lists;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.approval_items, parent, false);
        MyHolderApprovalStories holder = new MyHolderApprovalStories(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyHolderApprovalStories myHolder = (MyHolderApprovalStories) holder;
        final Story story = data.get(position);
        if (type == 0) {
            myHolder.ivoptionMenu.setVisibility(View.GONE);
            myHolder.containerApprovalStories.setVisibility(View.VISIBLE);
            myHolder.containerPendingStories.setVisibility(View.GONE);
            myHolder.containerRejectedStories.setVisibility(View.GONE);
        } else if (type == 1) {
            myHolder.ivoptionMenu.setVisibility(View.VISIBLE);
            myHolder.containerPendingStories.setVisibility(View.VISIBLE);
            myHolder.containerApprovalStories.setVisibility(View.GONE);
            myHolder.containerRejectedStories.setVisibility(View.GONE);
        } else {
            myHolder.ivoptionMenu.setVisibility(View.GONE);
            myHolder.containerRejectedStories.setVisibility(View.VISIBLE);
            myHolder.containerPendingStories.setVisibility(View.GONE);
            myHolder.containerApprovalStories.setVisibility(View.GONE);
        }
        myHolder.tvContent.setText(story.getShortDesc());
        myHolder.tvClassOne.setText("Class " + story.getTo());
        myHolder.tvCreatedDate.setText("Story created On : " + story.getDate());
        myHolder.tvProfileDesignation.setText(story.getFromDesignation());
        myHolder.tvProfileName.setText(story.getFromName());
        myHolder.containerApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStoryStatus(story, "approved", myHolder);
            }
        });
        myHolder.containerRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStoryStatus(story, "rejected", myHolder);
            }
        });
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String logintype = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        String login = "";
        if (logintype.equalsIgnoreCase("principal")) {
            login = "Principal";
        } else if (logintype.equalsIgnoreCase("teacher")) {
            login = "Teacher";
        } else if (logintype.equalsIgnoreCase("parent")) {
            login = "Parent";
        } else if (logintype.equalsIgnoreCase("vice_principal")) {
            login = "Vice Principal";
        }
        if (login.equals("Teacher")) {
            myHolder.tvRejectTextview.setText("Rejected");

        } else if (login.equals("Principal") || login.equals("Vice Principal")) {
            myHolder.tvRejectTextview.setText("Reject");

        }
        if (story.getFromThumpImg().isEmpty()) {
            Picasso.get().load(R.drawable.ncp_avator).transform(new CircleTransform()).error(R.drawable.ncp_avator)
                    .into(myHolder.ivProfileImage);
        } else {
            loadProfileImageusingPicasso(story.getFromThumpImg(), myHolder);
        }
        MultipleImageAdapter itemListDataAdapter = new MultipleImageAdapter(context, story.getMedia(), "approval");
        myHolder.recyclerImages.setHasFixedSize(true);
        myHolder.recyclerImages.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        myHolder.recyclerImages.setLayoutManager(layoutManager);
        ViewCompat.setNestedScrollingEnabled(myHolder.recyclerImages, true);
        myHolder.recyclerImages.setAdapter(itemListDataAdapter);

        List<String> documents = story.getDocuments();
//        Log.e(TAG, "document : " + documents);
        DocumentsAdapter documentsAdapter = new DocumentsAdapter(context, documents);
        myHolder.recyclerDocuments.setLayoutManager(new LinearLayoutManager(context));
        ViewCompat.setNestedScrollingEnabled(myHolder.recyclerImages, true);
        myHolder.recyclerDocuments.setAdapter(documentsAdapter);
        myHolder.ivoptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeMenuOption(myHolder, story);
            }
        });

    }

    private void initializeMenuOption(MyHolderApprovalStories myHolderStories, Story story) {
        PopupMenu popup = new PopupMenu(context, myHolderStories.ivoptionMenu);
        popup.inflate(R.menu.optionmenu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editStory:
                        String bundleStoriesData = new Gson().toJson(story);
                        context.startActivity(new Intent(context, EditStory.class)
                                .putExtra(Constants.STORIES_LIST, bundleStoriesData));
                        return true;
                    case R.id.deleteStory:

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                        builder.setMessage("Are you sure you want to Delete this story");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                deleteStories(story);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        android.support.v7.app.AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void deleteStories(Story story) {
        String url = Constants.BASE_URL + Constants.END_POINT_DELETE;
        Params params = new Params();
        params.setStory_id(story.getStoryId());
        Stories stories = new Stories();
        stories.setStoriesParams(params);
        stories.setMethod("call");
        stories.setJsonrpc(Constants.JSON_RPC);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e("TAG", "json objet : " + new Gson().toJson(stories));
        final ProgressDialog dialog = Utils.showDialog(context, "Please wait...");
        dialog.show();

        (Utils.httpService(context).deleteStory(stories, sessionIdValue)).enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, retrofit2.Response<Stories> response) {
                dialog.dismiss();
//                Log.e("TAG", "reposns : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    try {
                        if (response.body().getResult().getStatus().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                            Utils.showToast(context, "story deleted");
                            loadApi();
                        } else {
                            Utils.showToast(context, "Cannot delete stories please try again later");
                        }
                    } catch (NullPointerException e) {
//                        Log.e("TGA", "exception : " + e);
                    }
                } else {
//                    Log.e("TAG", "response : " + response);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
                dialog.dismiss();
//                Log.e("TAg", "Eror : " + t);
            }
        });
    }


    private void setStoryStatus(Story story, final String status, MyHolderApprovalStories container) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, context.MODE_PRIVATE);
        int userToken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        final JSONObject base = new JSONObject();

        JSONObject params = new JSONObject();
        try {
            params.put(Constants.USER_TOKEN, userToken);
            params.put("story_id", story.getStoryId());
            params.put("status", status);
            base.put(Constants.PARAMS, params);
            base.put(Constants.JSON_RPC_, Constants.JSON_RPC);
        } catch (JSONException e) {
//            Log.e("TAg", "josn exception : " + e);
        }
//        Log.e("TAG", "Json approval model : " + base);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog_background);
        if (status.equalsIgnoreCase("approved")) {
            storyStatus = "Approve";
        } else {
            storyStatus = "Reject";
        }
        builder.setMessage("Do you want to " + storyStatus + " this story");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final ProgressDialog progressDialog = Utils.showDialog(context, "Please wait..");
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        Constants.BASE_URL + "web/nConnect/storyStatus", base, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
//                        Log.e("TAG", "json response : " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONObject result = jsonObject.getJSONObject(Constants.RESULT);
                            String message = result.getString("status");
                            if (message.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                                Utils.showToast(context, "Your Story has been " + status);
                                if (storyStatus.equalsIgnoreCase("Approve")) {
                                    container.containerRejected.setVisibility(View.GONE);
                                    container.tvApprove.setText("" + "Approved");
                                    container.containerApprove.setBackground(context.getResources().getDrawable(R.drawable.ncp_approved));
                                } else {
                                    container.containerApprove.setVisibility(View.GONE);
                                    container.tvReject.setText("" + "Rejected");
                                    container.containerRejected.setBackground(context.getResources().getDrawable(R.drawable.ncp_rejected));
                                }
//                                loadApi();
                            } else {
                                Utils.showToast(context, "Some thing went wrong please try again later");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
//                            Log.e("TAG", "Json Exception : " + e);
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("TAg", "Error : " + error);
                        progressDialog.dismiss();
                        Utils.showToast(context, "Something went wrong please try again later");
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Cookie", Constants.SESSION_ID + "=" + sharedPreferences.getString(Constants.SESSION_ID, ""));
                        return headers;
                    }
                };
                VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.TimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void loadApi() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        int userTOken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
        Params params = new Params();
        params.setLoginType("teacher");
        params.setStatus("pending");
        params.setUserToken(userTOken);
        Stories stories = new Stories();
        stories.setStoriesParams(params);
        stories.setJsonrpc(Constants.JSON_RPC);
        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
//        Log.e("TAG", "json model data : " + new Gson().toJson(stories));
        (Utils.httpService(context).getPendingStories(stories, sessionIdValue)).enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, retrofit2.Response<Stories> response) {
//                Log.e("TAG", "response json : " + new Gson().toJson(response.body()));
//                Log.e("TGA", "error body ; " + new Gson().toJson(response.errorBody()));
                try {
                    Result result = null;
                    if (response.body() != null) {
                        result = response.body().getResult();
                        data.clear();
                        data = result.getStories();
                        notifyDataSetChanged();
                    }
                } catch (NullPointerException e) {
//                    Log.e("TAG", "Null pointer exceoption : " + e);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
//                Log.e("TAG", "Error : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToast(context, "Please check your network connection");
                }
            }
        });
    }

    private void loadProfileImageusingPicasso(String path, MyHolderApprovalStories myHolderStories) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");

        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }

        GlideUrl glideUrl = new GlideUrl(path, new LazyHeaders.Builder().addHeader("Cookie", sessionIdValue).build());
        if (path.isEmpty()) {
            Picasso.get().load(R.drawable.ncp_avator).error(R.drawable.ncp_avator)
                    .into(myHolderStories.ivProfileImage);
        } else {
            Glide.with(context).load(glideUrl).into(myHolderStories.ivProfileImage);
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

    class MyHolderApprovalStories extends RecyclerView.ViewHolder {
        TextView tvProfileName, tvProfileDesignation, tvCreatedDate, tvContent, tvClassOne, tvClassTwo;
        RecyclerView recyclerImages, recyclerDocuments;
        LinearLayout containerApprove, containerRejected;
        ImageView ivProfileImage, ivoptionMenu;
        LinearLayout containerApprovalStories, containerPendingStories, containerRejectedStories;
        private TextView tvApprove, tvReject, tvRejectTextview;

        public MyHolderApprovalStories(View itemView) {
            super(itemView);
            tvClassOne = (TextView) itemView.findViewById(R.id.approval_classone);
            tvClassTwo = (TextView) itemView.findViewById(R.id.approval_classtwo);
            tvContent = (TextView) itemView.findViewById(R.id.approval_content);
            tvCreatedDate = (TextView) itemView.findViewById(R.id.approval_stories_created);
            tvProfileDesignation = (TextView) itemView.findViewById(R.id.approval_designation);
            tvProfileName = (TextView) itemView.findViewById(R.id.approval_profile_name);
            recyclerImages = (RecyclerView) itemView.findViewById(R.id.approval_images_recyclerview);
            containerApprove = (LinearLayout) itemView.findViewById(R.id.approval_approve_container);
            containerRejected = (LinearLayout) itemView.findViewById(R.id.approval_reject_container);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.approval_profile_image);
            containerApprovalStories = (LinearLayout) itemView.findViewById(R.id.containerApprovalStories);
            containerPendingStories = (LinearLayout) itemView.findViewById(R.id.containerPendingStories);
            containerRejectedStories = (LinearLayout) itemView.findViewById(R.id.containerRejectedStories);
            ivoptionMenu = (ImageView) itemView.findViewById(R.id.optionMenu);
            recyclerDocuments = (RecyclerView) itemView.findViewById(R.id.stories_docuements_recyclerview);
            tvApprove = (TextView) itemView.findViewById(R.id.approve);
            tvReject = (TextView) itemView.findViewById(R.id.reject);
            tvRejectTextview = (TextView) itemView.findViewById(R.id.reject_tv);
        }

    }

}

