package com.nconnect.teacher.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.fragment.EditProfileFragment;
import com.nconnect.teacher.helper.AndroidMultiPartEntity;
import com.nconnect.teacher.helper.CameraUtils;
import com.nconnect.teacher.helper.CircleTransform;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.S3Upload;
import com.nconnect.teacher.model.Vals;
import com.nconnect.teacher.model.attendance.Data;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.util.Constants;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfileBackPress, ivEditProfile, ivEditProfileImage, ivProfileImage;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE = 106;
    private TextView tvAbout, tvPhone, tvAddress, tvEmail, tvProfileName, tvDesign, tvDob;
    private String mediaPath = "";
    private TextView tvAddressProfile;
    private String profileImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();
    }

    private void initializeViews() {
        tvAddressProfile = (TextView) findViewById(R.id.addressPRofile);
        tvAbout = (TextView) findViewById(R.id.aboutTeacher);
        tvAddress = (TextView) findViewById(R.id.aboutAddress);
        tvEmail = (TextView) findViewById(R.id.aboutteacherMail);
        tvPhone = (TextView) findViewById(R.id.aboutPhoneNumber);
        ivProfileBackPress = (ImageView) findViewById(R.id.profileBackPress);
        ivEditProfile = (ImageView) findViewById(R.id.editProfile);
        ivEditProfileImage = (ImageView) findViewById(R.id.editProfileImage);
        ivProfileImage = (ImageView) findViewById(R.id.profileimage);
        tvProfileName = (TextView) findViewById(R.id.profileNAme);
        tvDesign = (TextView) findViewById(R.id.profileDesig);
        tvDob = (TextView) findViewById(R.id.aboutteacherDob);
        initializeListeners();
        init();
        initdata();
        checkDeviceHasCamera();
        requestMultiplePermissions();
    }

    private void init() {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String profileImage = preferences.getString(Constants.PROFILE_IMAGE, "");
        if (profileImage != null && profileImage != "") {
//            Picasso.get().load(new File(profileImage)).placeholder(R.drawable.ncp_avator).into(ivProfileImage);
            Glide.with(this).load(profileImage).apply(new RequestOptions().placeholder(R.drawable.ncp_avator).centerCrop()).into(ivProfileImage);
        }
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
                            Toast.makeText(ProfileActivity.this, "Please accept this permission to use this application", Toast.LENGTH_SHORT).show();
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

    public void initdata() {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String name = sharedPreferences.getString(Constants.NAME, "");
        String loginType = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        if (loginType.equalsIgnoreCase("teacher")) {
            tvDesign.setText("Teacher");
        }
        tvProfileName.setText(name);
        Stories stories = new Stories();
        Params params = new Params();
        stories.setStoriesParams(params);
        stories.setJsonrpc(Constants.JSON_RPC);
        JSONObject object = null;
        try {
            object = new JSONObject(new Gson().toJson(stories));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + "web/nConnect/teacher/get/profile", object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.e(TAG, "response : " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject resutl = jsonObject.getJSONObject("result");
                    String res = resutl.getString("response");
                    if (res.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {

                        JSONObject dataJSon = resutl.getJSONObject("data");
                        Data data = new Gson().fromJson(dataJSon.toString(), Data.class);
                        tvAbout.setText("" + data.getAbout());
                        tvAddress.setText("" + data.getAddress());
                        tvAddressProfile.setText("" + data.getAddress());
                        tvEmail.setText("" + data.getWorkEmail());
                        tvPhone.setText("" + data.getMobilePhone());
                        tvDob.setText("" + data.getDob());
                    } else {
                        Toast.makeText(ProfileActivity.this, "Cannot get profile details", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "exception  : " + e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "eroor : " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
                if (sessionId != "") {
                    sessionId = Constants.SESSION_ID + "=" + sessionId;
                }
                headers.put(Constants.COOKIE, sessionId);
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void initializeListeners() {
        ivProfileBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ProfileActivity.this, Dashboard.class));
                finish();
            }
        });
        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                EditProfileFragment fragment = new EditProfileFragment().newInstance();
                SharedPreferences preferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                Data data = new Data();
                data.setDp(preferences.getString(Constants.PROFILE_IMAGE, ""));
                data.setAbout(tvAbout.getText().toString());
                data.setAddress(tvAddress.getText().toString());
                data.setDob(tvDob.getText().toString());
                data.setWorkEmail(tvEmail.getText().toString());
                data.setMobilePhone(tvPhone.getText().toString());
                String editableData = new Gson().toJson(data);
                Bundle bundle = new Bundle();
                bundle.putString("editable_profile", editableData);
                fragment.setArguments(bundle);
                fragment.show(transaction, "editprofile");
            }
        });
        ivEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCameraOrGallery();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, Dashboard.class));
        finish();
    }

    private void updateProfile() {
        Vals vals = new Vals();
        vals.setAbout(tvAbout.getText().toString());
        vals.setMobilePhone(tvPhone.getText().toString());
        vals.setWorkEmail(tvEmail.getText().toString());
        vals.setAddress(tvAddress.getText().toString());
        vals.setBirthday(tvDob.getText().toString());
        vals.setDp(profileImage);
        Params params = new Params();
        params.setVals(vals);
        Stories stories = new Stories();
        stories.setStoriesParams(params);
//        Log.e(TAG, "model data ; " + new Gson().toJson(stories));
        JSONObject json = null;
        try {
            json = new JSONObject(new Gson().toJson(stories));
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + "web/nConnect/teacher/update/profile",
                json, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject resonseJSon = null;
//                Log.e(TAG, "response : " + response.toString());
                try {
                    resonseJSon = new JSONObject(response.toString());
                    JSONObject resultJson = resonseJSon.getJSONObject(Constants.RESULT);
                    String responseStr = resultJson.getString(Constants.RESPONSE);
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        Toast.makeText(ProfileActivity.this, "Your profile updated succesfully", Toast.LENGTH_SHORT).show();
                        initdata();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Cannot update profile Details", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Log.e(TAG, "response : " + e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, " error " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
                headers.put(Constants.COOKIE, Constants.SESSION_ID + "=" + sharedPreferences.getString(Constants.SESSION_ID, ""));
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (mediaPath != null && mediaPath != "") {
                    Picasso.get().load(new File(mediaPath)).transform(new CircleTransform()).error(R.drawable.ncp_avator).into(ivProfileImage);
                    new UploadProfileImage(mediaPath).execute();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri contentURI = data.getData();
                String selectedImagePath = CameraUtils.getImagePath(this, contentURI);

                Picasso.get().load(new File(selectedImagePath)).transform(new CircleTransform()).error(R.drawable.ncp_avator).into(ivProfileImage);
                new UploadProfileImage(selectedImagePath).execute();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled upload image", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to open image", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void selectCameraOrGallery() {
        CharSequence items[] = {"Camera", "Gallery"};
        String title = "Select Camera or Gallery";
        showDialog(items, title);
    }

    public void showDialog(CharSequence items[], String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_background);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (items[i].equals("Camera")) {
                    takePictureOrVideo(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[i].equals("Gallery")) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(galleryIntent, GALLERY_IMAGE);
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void takePictureOrVideo(int REQUEST_TAKE_PHOTO) {
        Intent takePictureIntent = null;
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
        } else {
            return null;
        }
        mediaPath = media.getAbsolutePath();
        return media;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class UploadProfileImage extends AsyncTask<Void, Integer, String> {
        private String uploadedImageUri;
        private long totalSize = 0;
        private ProgressDialog dialog = null;


        public UploadProfileImage(String path) {
            this.uploadedImageUri = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(ProfileActivity.this, R.style.dialog_background);
            this.dialog.setCancelable(false);
            this.dialog.setTitle("Updating Profile Please Wait");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
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
                File sourceFile = new File(uploadedImageUri);
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
        protected void onProgressUpdate(Integer... progress) {

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
//            Log.e(TAG, "Response from server: " + s);
            S3Upload resUrl = new Gson().fromJson(s, S3Upload.class);
            if (resUrl.getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
//                Log.e(TAG, "profdile updated");
                profileImage = resUrl.getUrl();
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.PROFILE_IMAGE, profileImage);
                editor.commit();

                updateProfile();
            }
        }
    }
}
