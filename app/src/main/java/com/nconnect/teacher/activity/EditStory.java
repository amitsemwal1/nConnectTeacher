package com.nconnect.teacher.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.Jzvd;

import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.AttachmentAdapter;
import com.nconnect.teacher.fragment.PublishStoryFragment;
import com.nconnect.teacher.helper.AndroidMultiPartEntity;
import com.nconnect.teacher.helper.CameraUtils;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.helper.FilePath;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.S3Upload;
import com.nconnect.teacher.model.Vals;
import com.nconnect.teacher.model.stories.Attachment;
import com.nconnect.teacher.model.stories.Class;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditStory extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditStory.class.getSimpleName();
    private ImageView ivProfileImage, ivEditClasses;
    private EditText edContent;
    private TextView tvProfileName, tvProfileDesig;
    private RecyclerView recyclerMedias, recyclerDocuments;
    private FlexboxLayout flexClassTags;
    private Button btnUpdateStory;
    private Story story;
    private List<Attachment> attachmentList;
    private List<Attachment> attachmentsTemp;
    private List<Attachment> attachmentsInit;
    private AttachmentAdapter mAttachementAdapter;
    private ImageView ivUploadCamera, ivUploadGallery, ivUploadDocument;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 101;
    private static final int DOCUMENT_REQUEST_CODE = 103;
    private static final int GALLERY_IMAGE = 106;
    private static final int GALLERY_VIDEO = 107;
    private static final int WRITE_REQUEST_CODE = 43;
    private Uri fileUri;
    private List<Class> classList = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();
    private int storiesCount = 0, documentsCount = 0;
    private List<String> mediaUrls = new ArrayList<>();
    private List<String> documentUrls = new ArrayList<>();
    private List<String> uploadMediaList = new ArrayList<>();
    private List<String> uploadDocumetnList = new ArrayList<>();

    private View dialog;
    private AlertDialog progress;
    private TextView tvProgresstext;
    private ProgressBar progressBar;
    private ImageView ivSuccess;
    ProgressDialog progressDialog;
    private String session;
    private String mediaPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);
        init();
    }

    private void init() {
        progressDialog = new ProgressDialog(EditStory.this, R.style.dialog_background);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        dialog = this.findViewById(R.id.progress_layout);
        tvProgresstext = dialog.findViewById(R.id.progressText);
        progressBar = dialog.findViewById(R.id.progressbar);
        ivSuccess = dialog.findViewById(R.id.succesCheck);

        progress = Utils.showProgress(this, "");

        ivProfileImage = (ImageView) findViewById(R.id.edit_stories_profile_image);
        ivEditClasses = (ImageView) findViewById(R.id.editStoryClasses);
        tvProfileName = (TextView) findViewById(R.id.edit_stories_profile_name);
        tvProfileDesig = (TextView) findViewById(R.id.edit_stories_designation);
        edContent = (EditText) findViewById(R.id.edit_stories_content);
        recyclerDocuments = (RecyclerView) findViewById(R.id.editRecyclerDocumentAttachement);
        recyclerMedias = (RecyclerView) findViewById(R.id.edit_stories_attachment);
        flexClassTags = (FlexboxLayout) findViewById(R.id.edit_class_tags);
        btnUpdateStory = (Button) findViewById(R.id.updateStory);
        ivUploadCamera = (ImageView) findViewById(R.id.edit_stories_attachfromcamera);
        ivUploadDocument = (ImageView) findViewById(R.id.edit_stories_attachdocument);
        ivUploadGallery = (ImageView) findViewById(R.id.edit_stories_attachfromgallery);
        attachmentList = new ArrayList<>();
        attachmentsTemp = new ArrayList<>();
        attachmentsInit = new ArrayList<>();
        ivUploadGallery.setOnClickListener(this);
        ivUploadCamera.setOnClickListener(this);
        ivUploadDocument.setOnClickListener(this);
        ivEditClasses.setOnClickListener(this);
        btnUpdateStory.setOnClickListener(this);
        recyclerMedias.setLayoutManager(new LinearLayoutManager(this));
        recyclerDocuments.setLayoutManager(new LinearLayoutManager(this));
        mAttachementAdapter = new AttachmentAdapter(this, attachmentList);
        recyclerMedias.setAdapter(mAttachementAdapter);
        initData();
    }

    private void initData() {

        Intent intent = getIntent();
        String singleEditJSon = intent.getStringExtra(Constants.STORIES_LIST);
        story = new Gson().fromJson(singleEditJSon, Story.class);
//        Log.e(TAG, "model data : " + new Gson().toJson(story));
        List<String> mediaUrls = story.getMedia();
        List<String> documentUrls = story.getDocuments();
        if (!mediaUrls.isEmpty()) {
            for (int i = 0; i < mediaUrls.size(); i++) {
                String extension = mediaUrls.get(i).substring(mediaUrls.get(i).lastIndexOf(".") + 1);
                if (extension.equalsIgnoreCase("mp4")) {
                    Attachment attachment = new Attachment();
                    attachment.setContentype(Constants.VIDEO);
                    attachment.setImagePath(mediaUrls.get(i));
                    attachmentList.add(attachment);
                } else {
                    Attachment attachment = new Attachment();
                    attachment.setContentype(Constants.IMAGE);
                    attachment.setImagePath(mediaUrls.get(i));
                    attachmentList.add(attachment);
                }
            }

            if (!documentUrls.isEmpty()) {
                for (int i = 0; i < documentUrls.size(); i++) {
                    Attachment attachment = new Attachment();
                    attachment.setImagePath(documentUrls.get(i));
                    attachment.setContentype(Constants.DOCUMENT);
                    attachmentList.add(attachment);
                }

            }
        }

        String sampleString = story.getTo();
        String items[] = sampleString.split(",");
        List<String> itemList = Arrays.asList(items);
        mAttachementAdapter.notifyDataSetChanged();
        tvProfileName.setText("" + story.getFromName());
        tvProfileDesig.setText("" + story.getFromDesignation());
        edContent.setText("" + story.getShortDesc());
        classList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            addClassAndSection(itemList.get(i));
        }
        List<Class> classes = story.getClasses();
        this.classList = classes;
    }


    @Override
    public void onClick(View v) {

        if (v == btnUpdateStory) {
            updateStory();
        } else if (v == ivEditClasses) {
            editClasses();
        } else if (v == ivUploadCamera) {
            selectCameraOrVideo();
        } else if (v == ivUploadGallery) {
            selectGalleryImageOrVideo();
        } else if (v == ivUploadDocument) {
            uploadDocument();
        }
    }

    private void uploadDocument() {
       /* Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), DOCUMENT_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(TAG, "exception : " + ex);
            Toast.makeText(this, "File manager not found", Toast.LENGTH_SHORT).show();
        }*/
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), DOCUMENT_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
//            Log.e(TAG, "exception : " + ex);
            Toast.makeText(this, "File manager not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectGalleryImageOrVideo() {
        CharSequence items[] = {"Gallery Image", "Gallery Video"};
        String title = "Select image or video from Gallery";
        showDialog(items, title);
    }

    private void selectCameraOrVideo() {
        CharSequence items[] = {"Camera", "Video"};
        String title = "Select camera or Gallery";
        showDialog(items, title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Attachment attachment = new Attachment();
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                attachment.setImagePath(mediaPath);
                attachment.setContentype(Constants.IMAGE);
                attachment.setImageUri(null);
                Toast.makeText(this, "image captures", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                attachment.setContentype(Constants.VIDEO);
                attachment.setImagePath(mediaPath);
                attachment.setImageUri(null);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled video recording", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == GALLERY_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    String selectedVideoPath = CameraUtils.getVideoPath(this, contentURI);
                    attachment.setContentype(Constants.VIDEO);
                    attachment.setImagePath(selectedVideoPath);
                    attachment.setImageUri(contentURI);
//                    Log.e(TAG, "path : " + selectedVideoPath);
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled video recording", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    String selectedImagePath = CameraUtils.getImagePath(this, contentURI);
                    attachment.setContentype(Constants.IMAGE);
                    attachment.setImagePath(selectedImagePath);
                    attachment.setImageUri(data.getData());
//                    Log.e(TAG, "path : " + selectedImagePath);
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == DOCUMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedDocument = data.getData();
                    String docpath = FilePath.getPath(this, selectedDocument);
                    attachment.setImagePath(docpath);
                    attachment.setContentype(Constants.DOCUMENT);
                    attachment.setUri(new File(docpath));
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User cancel upload document", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to get document", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        attachmentsTemp.add(attachment);
        attachmentList.addAll(attachmentsTemp);
        attachmentsTemp.clear();
        mAttachementAdapter.notifyDataSetChanged();
    }

    public void showDialog(CharSequence items[], String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_background);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (items[i].equals("Camera")) {
                    takePictureOrVideo(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[i].equals("Video")) {
                    takePictureOrVideo(CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                } else if (items[i].equals("Gallery Image")) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(galleryIntent, GALLERY_IMAGE);
                } else if (items[i].equals("Gallery Video")) {
                    Intent videoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    videoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(videoIntent, GALLERY_VIDEO);
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    private void takePictureOrVideo(int REQUEST_TAKE_PHOTO) {
        Intent takePictureIntent = null;
        if (REQUEST_TAKE_PHOTO == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        } else if (REQUEST_TAKE_PHOTO == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        }
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(REQUEST_TAKE_PHOTO);
            } catch (IOException ex) {
//                Log.e("TAG", ex.getMessage(), ex);
            }
            try {
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "nconnect.pappaya.education.nconnectteachers.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            } catch (IllegalArgumentException e) {
//                Log.e(TAG, "Illegal argument exception " + e);
            }
        }
    }

    private File createImageFile(int REQUEST) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File media = null;
        File storageDir = null;
        String fileName = "";
        storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.GALLERY_DIRECTORY_NAME);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        if (REQUEST == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            fileName = "JPEG_" + "" + timeStamp + "_";
            media = File.createTempFile(fileName, ".jpg", storageDir);
        } else if (REQUEST == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            fileName = "MP4_" + "" + timeStamp + "_";
            media = File.createTempFile(fileName, ".mp4", storageDir);
        } else {
            return null;
        }
        mediaPath = media.getAbsolutePath();
        return media;
    }

    private void editClasses() {
        Bundle bundle = new Bundle();
        bundle.putString("type", "edit");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PublishStoryFragment newFragment = PublishStoryFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(transaction, "filter_class");
    }

    private void updateStory() {

        //init data
        for (int i = 0; i < attachmentList.size(); i++) {
            if (!attachmentList.get(i).getImagePath().startsWith(Constants.S3BASE_URL)) {
                if (attachmentList.get(i).getContentype().equalsIgnoreCase(Constants.DOCUMENT)) {
                    documentUrls.add(attachmentList.get(i).getImagePath());
                } else if (attachmentList.get(i).getContentype().equalsIgnoreCase(Constants.IMAGE)) {
                    mediaUrls.add(attachmentList.get(i).getImagePath());
                } else if (attachmentList.get(i).getContentype().equalsIgnoreCase(Constants.VIDEO)) {
                    mediaUrls.add(attachmentList.get(i).getImagePath());
                }
            } else {
                if (attachmentList.get(i).getContentype().equalsIgnoreCase(Constants.DOCUMENT)) {
                    uploadDocumetnList.add(attachmentList.get(i).getImagePath());
                } else if (attachmentList.get(i).getContentype().equalsIgnoreCase(Constants.IMAGE)) {
                    uploadMediaList.add(attachmentList.get(i).getImagePath());
                } else if (attachmentList.get(i).getContentype().equalsIgnoreCase(Constants.VIDEO)) {
                    uploadMediaList.add(attachmentList.get(i).getImagePath());
                }
            }
        }

//        Log.e(TAG, "media urls  " + new Gson().toJson(mediaUrls));
//        Log.e(TAG, "Docuemtn urls : " + new Gson().toJson(documentUrls));

        if (classList.isEmpty()) {
            Toast.makeText(EditStory.this, "Please select class and section", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaUrls.isEmpty() && documentUrls.isEmpty()) {
            postStory(mediaUrls, documentUrls);
        }
        if (!mediaUrls.isEmpty() && !documentUrls.isEmpty()) {
            storiesCount = 0;
            new UploadMediaFileToS3(storiesCount).execute();
        }
        if (mediaUrls.isEmpty() && !documentUrls.isEmpty()) {
            documentsCount = 0;
            new UploadDocumentToS3(documentsCount).execute();
        }
        if (documentUrls.isEmpty() && !mediaUrls.isEmpty()) {
            storiesCount = 0;
            new UploadMediaFileToS3(storiesCount).execute();
        }
    }

    public void setClassAndSection(List<Class> classes, Map<String, String> map, String postTo) {
        this.classList.clear();
        this.map.clear();
        flexClassTags.removeAllViews();
        this.classList = classes;
        this.map = map;
//        Log.e(TAG, "list : " + new Gson().toJson(this.classList));
//        Log.e(TAG, "map : " + this.map);
        int size = this.map.size();
        for (Map.Entry<String, String> entry : this.map.entrySet()) {
            String value = entry.getValue();
            value = Utils.removeCommas(value);
            value = entry.getKey() + value;
            addClassAndSection(value);
        }
    }

    private void addClassAndSection(String value) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        TextView tv = new TextView(this);
        tv.setPadding(5, 5, 5, 5);
        tv.setTextSize(14);
        tv.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
        tv.setLayoutParams(params);
        tv.setText(value);
        flexClassTags.addView(tv);
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

        private UploadMediaFileToS3(int position) {
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
                File sourceFile = new File(mediaUrls.get(position));
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
            S3Upload resUrl = new Gson().fromJson(result, S3Upload.class);
            if (resUrl.getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                uploadMediaList.add(resUrl.getUrl());
                storiesCount++;
                if (storiesCount < mediaUrls.size()) {
                    new UploadMediaFileToS3(storiesCount).execute();
                } else {
                    if (!documentUrls.isEmpty()) {
                        documentsCount = 0;
                        new UploadDocumentToS3(documentsCount).execute();
                    } else {
                        postStory(uploadMediaList, uploadDocumetnList);
                    }
                }
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

        private UploadDocumentToS3(int position) {
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
                File sourceFile = new File(documentUrls.get(position));
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
            S3Upload resUrl = new Gson().fromJson(result, S3Upload.class);
            if (resUrl.getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                uploadDocumetnList.add(resUrl.getUrl());
                documentsCount++;
                if (documentsCount < documentUrls.size()) {
                    new UploadDocumentToS3(documentsCount).execute();
                } else {
                    postStory(uploadMediaList, uploadDocumetnList);
                }
            }
            super.onPostExecute(result);
        }

    }

    private void postStory(List<String> uploadMediaList, List<String> uploadDocumetnList) {

//        Log.e(TAG, "uplaod media urls : " + uploadMediaList + "\nupload document list : " + uploadDocumetnList);
        SharedPreferences preferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        session = preferences.getString(Constants.SESSION_ID, "");
        session = Constants.SESSION_ID + "=" + session;
        Vals vals = new Vals();
        vals.setClasses(this.classList);
        vals.setDocuments(uploadDocumetnList);
        vals.setMedia(uploadMediaList);
        vals.setName("");
        vals.setDescription(edContent.getText().toString());
        Params params = new Params();
        params.setStory_id(story.getStoryId());
        params.setVals(vals);
        Stories stories = new Stories();
        stories.setStoriesParams(params);
        stories.setJsonrpc(Constants.JSON_RPC);
//        Log.e(TAG, "json model data : " + new Gson().toJson(stories));
        tvProgresstext.setText("Publishing Stories...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                (Utils.httpService(EditStory.this).updateStory(stories, session)).enqueue(new Callback<Stories>() {
                    @Override
                    public void onResponse(Call<Stories> call, Response<Stories> response) {
                        try {
                            Stories storiesModel = response.body();
                            Result resModel = storiesModel.getResult();
//                            Log.e(TAG, "response : " + new Gson().toJson(resModel));
                            if (resModel.getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                                progressBar.setVisibility(View.GONE);
                                ivSuccess.setVisibility(View.VISIBLE);
                                tvProgresstext.setText("Story Updated");
                                Toast.makeText(EditStory.this, "Story Updated Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditStory.this, Dashboard.class));
                                finish();
                            } else {
                                dialog.setVisibility(View.GONE);
                                Toast.makeText(EditStory.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
//                            Log.e(TAG, "Exception : " + e);
                            dialog.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Stories> call, Throwable t) {

//                        Log.e(TAG, "error : " + t);
                        if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                            Toast.makeText(EditStory.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                        }
                        dialog.setVisibility(View.GONE);
                    }
                });
            }
        }, 500);
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
