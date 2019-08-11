package com.nconnect.teacher.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jzvd.Jzvd;
import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.AttachmentAdapter;
import com.nconnect.teacher.helper.CameraUtils;
import com.nconnect.teacher.helper.FilePath;
import com.nconnect.teacher.model.stories.Attachment;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class PostStoryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PostStory";
    private EditText edContent;
    private ImageView ivProfileImage, ivAttachDocument, ivAttachFromGallery, ivAttachFromCamera, ivNext;
    private RecyclerView recyclerAttachements;
    private TextView tvProfileName, tvProfileDesignation;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 101;
    private static final int DOCUMENT_REQUEST_CODE = 103;
    private static final int GALLERY_IMAGE = 106;
    private static final int GALLERY_VIDEO = 107;
    private static final int WRITE_REQUEST_CODE = 43;
    private Uri fileUri;
    private List<Attachment> attachments;
    private Toolbar toolbar;
    private String selectedFilePath;
    private static final int CAPTURE_MEDIA_RESULT_CODE = 200;
    //    private LinearLayout tvClassTags;
//    private ImageView ivEditClasses;
    private AttachmentAdapter mAdapter;
    private List<Attachment> attachmentList;
    public static List<Attachment> storyAttachPreviewExtra = new ArrayList<>();
    private int seekPosition = 0;
    private String path = "";
    public static boolean isClassSelected = false;
    private String mediaPath = "";
    private NestedScrollView containerScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_story);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initializeViews();
        checkDeviceHasCamera();
        requestMultiplePermissions();
    }

    private void initializeViews() {
        attachments = new ArrayList<>();
        attachmentList = new ArrayList<>();
        attachments.clear();
        containerScroll = (NestedScrollView) findViewById(R.id.containerPostStory);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerAttachements = (RecyclerView) findViewById(R.id.post_stories_attachment);
        recyclerAttachements.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mAdapter = new AttachmentAdapter(this, attachmentList);
        mAdapter.setHasStableIds(true);
        recyclerAttachements.setHasFixedSize(true);
        recyclerAttachements.setItemAnimator(new DefaultItemAnimator());
        recyclerAttachements.getItemAnimator().setChangeDuration(0);
        recyclerAttachements.setAdapter(mAdapter);
        edContent = (EditText) findViewById(R.id.post_stories_content);
        ivProfileImage = (ImageView) findViewById(R.id.post_stories_profile_image);
        ivAttachDocument = (ImageView) findViewById(R.id.post_stories_attachdocument);
        ivAttachFromCamera = (ImageView) findViewById(R.id.post_stories_attachfromcamera);
        ivAttachFromGallery = (ImageView) findViewById(R.id.post_stories_attachfromgallery);
        ivNext = (ImageView) findViewById(R.id.post_stories_next);
        tvProfileName = (TextView) findViewById(R.id.post_stories_profile_name);
        tvProfileDesignation = (TextView) findViewById(R.id.post_stories_designation);
        ivAttachFromGallery.setOnClickListener(this);
        ivAttachFromCamera.setOnClickListener(this);
        ivAttachDocument.setOnClickListener(this);
        ivNext.setOnClickListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String profileName = sharedPreferences.getString(Constants.NAME, "");
        String profileDesig = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        String profileImage = sharedPreferences.getString(Constants.PROFILE_IMAGE, "");
        if (profileImage != null && profileImage != "") {
//                Picasso.get().load(new File(profileImage)).transform(new CircleTransform()).into(ivProfileImage);
            Glide.with(getApplicationContext()).load(profileImage).apply(new RequestOptions().placeholder(R.drawable.ncp_avator)).into(ivProfileImage);
        }
        tvProfileName.setText(profileName);
        String designation = "";
        if (profileDesig.equalsIgnoreCase("principal")) {
            designation = "Principal";
        } else if (profileDesig.equalsIgnoreCase("vice_principal")) {
            designation = "Vice Principal";
        } else if (profileDesig.equalsIgnoreCase("teacher")) {
            designation = "Teacher";
        }
        tvProfileDesignation.setText(designation);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!edContent.getText().toString().isEmpty()) {
            ivNext.setEnabled(true);
            Picasso.get().load(R.drawable.ncp_next).error(R.drawable.ncp_next).into(ivNext);
        } else {
            ivNext.setEnabled(false);
            Picasso.get().load(R.drawable.ncp_next_inactive).error(R.drawable.ncp_next_inactive).into(ivNext);
        }
        edContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edContent.getText().toString().isEmpty()) {
                    ivNext.setEnabled(true);
                    Picasso.get().load(R.drawable.ncp_next).error(R.drawable.ncp_next).into(ivNext);
                } else {
                    ivNext.setEnabled(false);
                    Picasso.get().load(R.drawable.ncp_next_inactive).error(R.drawable.ncp_next_inactive).into(ivNext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkDeviceHasCamera() {
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
        }
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
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
                            Toast.makeText(PostStoryActivity.this, "Please accept this permission to use this application", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        if (v == ivAttachDocument) {
            attachDocument();
        } else if (v == ivAttachFromCamera) {
            selectCameraOrVideo();
        } else if (v == ivAttachFromGallery) {
            selectGalleryImageOrVideo();
        } else if (v == ivNext) {
            storyAttachPreviewExtra.clear();
            storyAttachPreviewExtra.addAll(attachmentList);
            startActivity(new Intent(this, PreviewPostStoryActivity.class)
                    .putExtra("story_content", edContent.getText().toString()));
        }
    }

    private void selectGalleryImageOrVideo() {
        CharSequence items[] = {"Gallery Image", "Gallery Video"};
        String title = "Select Image or Video from Gallery";
        showDialog(items, title);
    }

    private void selectCameraOrVideo() {
        CharSequence items[] = {"Camera", "Video"};
        String title = "Select Camera or Video";
        showDialog(items, title);
    }

    public void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(galleryIntent, GALLERY_IMAGE);
    }

    public void selectVideo() {
        Intent videoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        videoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(videoIntent, GALLERY_VIDEO);
    }

    public void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = CameraUtils.getOutputMediaFileUri(Constants.MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = CameraUtils.getOutputMediaFileUri(Constants.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void attachDocument() {
        /*Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
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

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Attachment attachment = new Attachment();
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                attachment.setImageUri(null);
                attachment.setImagePath(mediaPath);
                attachment.setContentype(Constants.IMAGE);
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
                attachment.setImageUri(null);
                attachment.setImagePath(mediaPath);
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
                String url = "", file_name = "";
                if (data != null) {
                    Uri selectedDocument = data.getData();
                    url = FilePath.getPath(this, selectedDocument);

                    if (Utils.replaceNull(url).equalsIgnoreCase("")) {
                        url = Utils.getDriveFilePath(selectedDocument, PostStoryActivity.this);
                    }

                    if (!Utils.replaceNull(url).equalsIgnoreCase("")) {
                        file_name = Utils.getFileNameFromUrl(url);
                    }

                    attachment.setImagePath(url);
                    attachment.setContentype(Constants.DOCUMENT);
                    attachment.setUri(new File(url));
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User cancel upload document", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to get document", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        attachments.add(attachment);
        attachmentList.addAll(attachments);
        attachments.clear();
        mAdapter.notifyDataSetChanged();
    }

    private String getParentDirectory(@NonNull Uri uri) {
        String uriPath = uri.getPath();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (uriPath != null) {
            filePath = new File(filePath.concat("/" + uriPath.split(":")[1])).getParent();
        }
        return filePath;
    }

    private String getAbsolutePath(@NonNull Uri uri) {
        String uriPath = uri.getPath();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (uriPath != null) {
            filePath = filePath.concat("/" + uriPath.split(":")[1]);
        }
        return filePath;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
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
                    Uri photoURI = FileProvider.getUriForFile(this, "com.nconnect.teacher.fileprovider", photoFile);
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


