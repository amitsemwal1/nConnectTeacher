package com.nconnect.teacher.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.Jzvd;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.AttachmentAdapter;
import com.nconnect.teacher.adapter.MultipleImageAdapter;
import com.nconnect.teacher.fragment.PublishStoryFragment;
import com.nconnect.teacher.helper.AndroidMultiPartEntity;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.MultipleImageVideo;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.S3Upload;
import com.nconnect.teacher.model.Vals;
import com.nconnect.teacher.model.stories.Attachment;
import com.nconnect.teacher.model.stories.Class;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviewPostStoryActivity extends AppCompatActivity {

    private static final String TAG = PreviewPostStoryActivity.class.getSimpleName();
    private TextView tvContent;
    private ImageView ivProfileImage;
    private RecyclerView recyclerAttachements, recyclerDocuments;
    private TextView tvProfileName, tvProfileDesignation;
    private Toolbar toolbar;
    private String selectedFilePath;
    private static final int WRITE_REQUEST_CODE = 43;
    private static final int CAPTURE_MEDIA_RESULT_CODE = 200;
    private FlexboxLayout tvClassTags;
    private ImageView ivEditClasses;
    private int seekPosition = 0;
    public static boolean isClassSelected = false;
    private List<Attachment> attachments = new ArrayList<>();
    private List<MultipleImageVideo> imageVideoList = new ArrayList<>();
    private List<Attachment> documents = new ArrayList<>();
    private List<Class> classList = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();
    private boolean isFirst = false;
    private Button btnPublish;
    private List<String> mediaUrls = new ArrayList<>();
    private List<String> documentsUrls = new ArrayList<>();
    private String sessionIdValue = "";
    private int storiesCount = 0, documentsCount = 0;
    private TextView tvSelectStandards;
    private RelativeLayout containerClassSection;
    private String postTO = "";
    private List<String> media;

    //init dilaog
    private View dialog;
    private TextView tvProgresstext;
    private ProgressBar progressBar;
    private ImageView ivSuccess;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_post_story);
        init();
    }

    private void init() {
        media = new ArrayList<>();
        //init dialog
        progressDialog = new ProgressDialog(PreviewPostStoryActivity.this, R.style.dialog_background);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        dialog = this.findViewById(R.id.progress_layout);
        tvProgresstext = dialog.findViewById(R.id.progressText);
        progressBar = dialog.findViewById(R.id.progressbar);
        ivSuccess = dialog.findViewById(R.id.succesCheck);

        containerClassSection = (RelativeLayout) findViewById(R.id.classSectionContainer);
        tvSelectStandards = (TextView) findViewById(R.id.select_standards);
        btnPublish = (Button) findViewById(R.id.publishStory);
        ivEditClasses = (ImageView) findViewById(R.id.editClasses);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerDocuments = (RecyclerView) findViewById(R.id.recyclerDocumentAttachement);
        tvClassTags = (FlexboxLayout) findViewById(R.id.class_tags);
        recyclerAttachements = (RecyclerView) findViewById(R.id.post_stories_attachment);
        tvContent = (TextView) findViewById(R.id.post_stories_content);
        ivProfileImage = (ImageView) findViewById(R.id.post_stories_profile_image);
        tvProfileName = (TextView) findViewById(R.id.post_stories_profile_name);
        tvProfileDesignation = (TextView) findViewById(R.id.post_stories_designation);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String profileName = sharedPreferences.getString(Constants.NAME, "");
        String profileDesig = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        String designation = "";
        if (profileDesig.equalsIgnoreCase("principal")) {
            designation = "Principal";
        } else if (profileDesig.equalsIgnoreCase("vice_pricipal")) {
            designation = "Vice Principal";
        } else if (profileDesig.equalsIgnoreCase("teacher")) {
            designation = "Teacher";
        }
        tvProfileName.setText(profileName);
        tvProfileDesignation.setText(designation);
        initListener();
        initData();
        filterClassAndSection();
    }

    private void initData() {
        media.clear();
        attachments.clear();
        attachments.addAll(PostStoryActivity.storyAttachPreviewExtra);
//        Log.e(TAG, "attachement : " + new Gson().toJson(attachments));
        //attach documents
        for (int i = 0; i < attachments.size(); i++) {
            if (attachments.get(i).getContentype().equalsIgnoreCase(Constants.DOCUMENT)) {
                Attachment attachment = new Attachment();
                attachment.setImagePath(attachments.get(i).getImagePath());
                attachment.setImageUri(attachments.get(i).getImageUri());
                attachment.setContentype(Constants.DOCUMENT);
                documents.add(attachment);
            } else {
                media.add(attachments.get(i).getImagePath());
            }
        }
//        Log.e(TAG, "media : " + new Gson().toJson(media));

        MultipleImageAdapter multipleImageAdapter = new MultipleImageAdapter(this, media, "previewStory");
        recyclerAttachements.setLayoutManager(new LinearLayoutManager(this));
        recyclerAttachements.setAdapter(multipleImageAdapter);

        AttachmentAdapter adapter = new AttachmentAdapter(this, documents);
        recyclerDocuments.setLayoutManager(new LinearLayoutManager(this));
        recyclerDocuments.setAdapter(adapter);

        Intent intent = getIntent();
        String content = intent.getStringExtra("story_content");
        tvContent.setText(content);
    }

    public void setClassAndSection(List<Class> classes, Map<String, String> map, String postTo) {
        this.classList.clear();
        this.map.clear();
        tvClassTags.removeAllViews();
        this.classList = classes;
        this.map = map;
        this.postTO = postTo;
        if (postTo.equalsIgnoreCase("true")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            TextView tv = new TextView(this);
            tv.setPadding(5, 5, 5, 5);
            tv.setTextSize(14);
            tv.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
            tv.setLayoutParams(params);
            tv.setText("All Parents");
            tvClassTags.addView(tv);
        } else {
            int size = this.map.size();
            for (Map.Entry<String, String> entry : this.map.entrySet()) {
                String value = entry.getValue();
                value = Utils.removeCommas(value);
                value = entry.getKey() + value;
                addClassAndSection(value, size);
            }
        }
    }

    public void addClassAndSection(String value, int size) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        TextView tv = new TextView(this);
        tv.setPadding(5, 5, 5, 5);
        tv.setTextSize(14);
        tv.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
        tv.setLayoutParams(params);
        tv.setText(value);
        tvClassTags.addView(tv);
    }

    private void initListener() {

        ivEditClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterClassAndSection();
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classList.isEmpty() && postTO.isEmpty()) {
                    Toast.makeText(PreviewPostStoryActivity.this, "Please select class and section", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (media.isEmpty() && documents.isEmpty()) {
                    postStory(mediaUrls, documentsUrls);
                }
                if (!media.isEmpty() && !documents.isEmpty()) {
                    storiesCount = 0;
                    new UploadMediaFileToS3(storiesCount).execute();
                }
                if (media.isEmpty() && !documents.isEmpty()) {
                    documentsCount = 0;
                    new UploadDocumentToS3(documentsCount).execute();
                }
                if (documents.isEmpty() && !media.isEmpty()) {
                    storiesCount = 0;
                    new UploadMediaFileToS3(storiesCount).execute();
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void filterClassAndSection() {
        Bundle bundle = new Bundle();
        bundle.putString("type", "create");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PublishStoryFragment newFragment = PublishStoryFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(transaction, "filter_class");
    }


    @SuppressLint("StaticFieldLeak")
    private class UploadMediaFileToS3 extends AsyncTask<Void, Integer, String> {
        private long totalSize = 0;
        private int position;


        @Override
        protected void onPreExecute() {
            dialog.setVisibility(View.VISIBLE);
            ivSuccess.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvProgresstext.setText("Uploading media files ...");
//            progressDialog.setTitle("Uploading media files....");
//            progressDialog.show();
            super.onPreExecute();
        }

        public UploadMediaFileToS3(int position) {
            this.position = position;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            dialog.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
//            progressDialog.setProgress(progress[0]);
            tvProgresstext.setText("Uploading media files ..." + String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.BASE_URL + Constants.S3UPLOAD);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                File sourceFile = new File(media.get(position));
                entity.addPart("file_", new FileBody(sourceFile));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
//                Log.e(TAG, "Client protocol exception : " + e);
            } catch (IOException e) {
                responseString = e.toString();
//                Log.e(TAG, "io excetpiton : " + e);
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
//            Log.e(TAG, "Response from server: " + result);
            try{
                S3Upload resUrl = new Gson().fromJson(result, S3Upload.class);
                mediaUrls.add(resUrl.getUrl());
                storiesCount++;
                if (storiesCount < media.size()) {
                    new UploadMediaFileToS3(storiesCount).execute();
                } else {
                    if (!documents.isEmpty()) {
                        documentsCount = 0;
                        new UploadDocumentToS3(documentsCount).execute();
                    } else {
                        postStory(mediaUrls, documentsUrls);
                    }
                }

            }catch (NullPointerException e){

            }
            super.onPostExecute(result);
        }

    }

    private class UploadDocumentToS3 extends AsyncTask<Void, Integer, String> {
        private long totalSize = 0;
        private int position;


        @Override
        protected void onPreExecute() {
            dialog.setVisibility(View.VISIBLE);
            ivSuccess.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        public UploadDocumentToS3(int position) {
            this.position = position;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (dialog.getVisibility() == View.GONE) {
                dialog.setVisibility(View.VISIBLE);
            }
            tvProgresstext.setText("Uploading Docuements ..." + String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.BASE_URL + Constants.S3UPLOAD);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                File sourceFile = new File(documents.get(position).getImagePath());
                entity.addPart("file_", new FileBody(sourceFile));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
//                Log.e(TAG, "Client protocol exception : " + e);
            } catch (IOException e) {
                responseString = e.toString();
//                Log.e(TAG, "io excetpiton : " + e);
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
//            Log.e(TAG, "Response from server: " + result);
            try {
                S3Upload resUrl = new Gson().fromJson(result, S3Upload.class);
                documentsUrls.add(resUrl.getUrl());
                documentsCount++;
                if (documentsCount < documents.size()) {
                    new UploadDocumentToS3(documentsCount).execute();
                } else {
                    postStory(mediaUrls, documentsUrls);
                }
            } catch (NullPointerException e) {
//                Log.e(TAG, "excpetion : " + e);
            }
            super.onPostExecute(result);
        }

    }

    public void postStory(List<String> mediaUrls, List<String> documentsUrls) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        Vals vals = new Vals();
        vals.setMedia(mediaUrls);
        vals.setDocuments(documentsUrls);
        vals.setName("");
        vals.setDescription(tvContent.getText().toString());
        String login = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        if (login.equalsIgnoreCase("teacher")) {
            vals.setPostTo("");
            vals.setClasses(PreviewPostStoryActivity.this.classList);
        } else {
            if (classList.isEmpty()) {
                vals.setPostTo("all");
                vals.setClasses(this.classList);
            } else {
                vals.setPostTo("");
                vals.setClasses(this.classList);
            }
        }

        Params params = new Params();
        params.setVals(vals);
        Stories stories = new Stories();
        stories.setStoriesParams(params);
        stories.setJsonrpc(Constants.JSON_RPC);
//        Log.e(TAG, "json model data : " + new Gson().toJson(stories));
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        dialog.setVisibility(View.VISIBLE);
        ivSuccess.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvProgresstext.setText("Publishing Stories...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                (Utils.httpService(PreviewPostStoryActivity.this).postStory(stories, sessionIdValue)).enqueue(new Callback<Stories>() {
                    @Override
                    public void onResponse(Call<Stories> call, Response<Stories> response) {
//                        Log.e(TAG, "response : " + new Gson().toJson(response.body()));
                        try {
                            if (response.body().getResult().getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                                progressBar.setVisibility(View.GONE);
                                ivSuccess.setVisibility(View.VISIBLE);
                                tvProgresstext.setText("Story Published");
                                Toast.makeText(PreviewPostStoryActivity.this, "Story Published Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PreviewPostStoryActivity.this, Dashboard.class));
                                finish();
                            } else if (response.body().getResult().getResponse().equalsIgnoreCase("fail")) {
                                dialog.setVisibility(View.GONE);
                                Toast.makeText(PreviewPostStoryActivity.this, "Cannot post story please try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
                            dialog.setVisibility(View.GONE);
//                            Log.e(TAG, "exception : " + e);
                        }

                    }

                    @Override
                    public void onFailure(Call<Stories> call, Throwable t) {
//                        Log.e(TAG, "error : " + t);
                        if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                            Toast.makeText(PreviewPostStoryActivity.this, "Plesae check your internet connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.setVisibility(View.GONE);
                    }
                });
            }
        }, 1000);

    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}
