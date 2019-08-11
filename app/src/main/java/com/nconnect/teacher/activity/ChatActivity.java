package com.nconnect.teacher.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.nconnect.teacher.OttoEvents.AndroidBusProvider;
import com.nconnect.teacher.OttoEvents.OfflineToOnlineEvent;
import com.nconnect.teacher.OttoEvents.RefreshListEvent;
import com.nconnect.teacher.OttoEvents.RestartServiceEvent;
import com.nconnect.teacher.R;
import com.nconnect.teacher.adapter.ChatAdapter;
import com.nconnect.teacher.database.Database;
import com.nconnect.teacher.helper.AndroidMultiPartEntity;
import com.nconnect.teacher.helper.CameraUtils;
import com.nconnect.teacher.helper.FilePath;
import com.nconnect.teacher.model.S3Upload;
import com.nconnect.teacher.model.chat.ChatMessage;
import com.nconnect.teacher.services.LocalBinder;
import com.nconnect.teacher.services.NetworkService;
import com.nconnect.teacher.services.SmackService;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import com.nconnect.teacher.xmpp.MyXMPP;

public class ChatActivity extends AppCompatActivity {


    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int TAKE_PHOTO = 2;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 101;
    private static final int DOCUMENT_REQUEST_CODE = 103;
    private static final int GALLERY_IMAGE = 106;
    private static final int GALLERY_VIDEO = 107;
    private static final int SELECT_AUDIO = 2;
    public static ChatAdapter mChatAdapter;
    public static ArrayList<ChatMessage> chatlist;
    public static LinearLayout loadingLayout;
    public static View loadingDialog;
    public static RecyclerView recyclerMessages;
    public static Activity activity;
    boolean isSplash = false;
    private String screen = "";

    String total_file_size;
    public static String title_name = "";
    public static String NAME = "";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    private SmackService mService;
    public static boolean active = false;
    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            mService = ((LocalBinder<SmackService>) service).getService();
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            Log.d(TAG, "onServiceDisconnected");
        }
    };
    private int CAMERA_REQUEST = 10;
    private EditText edMessageText;
    private ImageView ivSendMessage;
    private Random random;
    private LinearLayoutManager layoutManager;
    private ImageView attachIcon;
    private ImageView cameraImageView;
    private ImageView pictureImageView;
    private ImageView documentImageView;
    private ImageView audioImageView;
    private ImageView backButton;
    private boolean backButtonPressed = false;
    private RelativeLayout attachmentLayout;
    private TextView tvProgresstext;
    private ProgressBar progressBar;
    //private ImageView ivSuccess;
    private String mediaPath;
    private String file_name = "";
    public static String name = "";
    public static String RECEIVERNAME = "";
    LinearLayout messageMainLayout, emptyLayout;

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            file_name = "";
            attachmentLayout.setVisibility(View.GONE);
            switch (view.getId()) {

                case R.id.cameraImageView:

                    //method to take picture or video
                    //selectCameraOrVideo();

                    //method to check camera availability
                    if (checkDeviceHasCamera()) {
                        CharSequence items[] = {"Camera", "Video"};
                        String title = "Select Camera or Video";
                        showDialog(items, title);
                    }

                    break;
                case R.id.pictureImageView:

                    //method to check camera availability
                    if (checkDeviceHasCamera()) {

                        //method to handle image or video
                        //selectGalleryImageOrVideo();
                        CharSequence g_items[] = {"Gallery Image", "Gallery Video"};
                        showDialog(g_items, "Select Image or Video from Gallery");

                    }

                    break;
                case R.id.documentImageView:

                    //method to attach document
                    //attachDocument();
                    attachDocument();

                    break;
                case R.id.audioImageView:

                    //method to attach audio
                    //openGalleryAudio();
                    openGalleryAudio();

                    break;
                case R.id.backButton:

                    backButtonPressed = true;
                    pressBack();

                    break;
                case R.id.attachIcon:

                    if (attachmentLayout.getVisibility() == View.VISIBLE) {

                        attachmentLayout.setVisibility(View.GONE);
                    } else {
                        attachmentLayout.setVisibility(View.VISIBLE);
                    }

                    break;
                case R.id.sendMessageButton:

                    if (edMessageText.getText().toString().isEmpty()) {
                        Toast.makeText(ChatActivity.this, "Text messages cannot be blank", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendMessage();

                    break;
            }
        }
    };

    //method to store offline message
    public void handleOfflineOnlineMessage(ChatMessage chatMessage) {
        try {

            Database database = new Database(activity);
//            Log.e(TAG, "Table name : " + name);
            if (!database.isTableExists(chatMessage.getReceiver(), activity)) {
                database.createTable(name);
            }

            chatMessage.setReceiver_name(title_name);
            chatMessage.setSender_name(NAME);

            /*ChatAdapter.mChatList.add(chatMessage);
            //Database.getMessagesFromOffline(user2, this, mChatAdapter);
            mChatAdapter.notifyDataSetChanged();
            recyclerMessages.smoothScrollToPosition(mChatAdapter.getItemCount());*/

            //method to change the adapter
            updateChatListAdapter(chatMessage);

            XMPPTCPConnection xmpptcpConnection = MyXMPP.getConnection();

            if (!Utils.isOnline(activity) || MyXMPP.getConnection() == null || !MyXMPP.getConnection().isAuthenticated() || SmackService.xmppConnection == null) {

                if (Utils.isOnline(activity)) {

                    //method to start services
                    RestartServices();
                }
                //if (!Utils.isOnline(activity) || !xmpptcpConnection.isAuthenticated()) {
                database.insertMessageToOffline(name, chatMessage.getMsgid(), name, RECEIVERNAME, chatMessage.getBody(), true, chatMessage.getDate(), chatMessage.getTime(), "", "");
            } else {
                SmackService.xmppConnection.sendMessage(chatMessage, "", "", activity);
            }

            //method to insert last chat values for main display purpose
            Database.insertToDB(chatMessage, activity, title_name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method to change the adapter
    public static void updateChatListAdapter(ChatMessage chatMessage) {

        try {

            if (chatMessage != null) {
                ChatAdapter.mChatList.add(chatMessage);
                ChatActivity.mChatAdapter.notifyItemRangeInserted(ChatAdapter.mChatList.size(), 1);
                //mChatAdapter.notifyDataSetChanged();
                recyclerMessages.smoothScrollToPosition(ChatAdapter.mChatList.size());
                loadingDialog.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);


        //method to handle permission for all attachments
        try {
            requestMultiplePermissions();
        } catch (Exception e) {
        }

        try {
            sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
            name = sharedPreferences.getString(Constants.SENDER, "");
            RECEIVERNAME = sharedPreferences.getString(Constants.RECEIVER_NAME, "");
            NAME = sharedPreferences.getString(Constants.NAME, "");


            Constants.Chat1 = name;
            Constants.Chat2 = RECEIVERNAME;

            try {

                Intent intent = getIntent();
                screen = intent.getStringExtra("screen");
                if ("Notification".equalsIgnoreCase(Utils.replaceNull(screen))) {
                    String receiver_name = intent.getStringExtra("receiver_name");
                    Constants.Chat2 = receiver_name;
                    RECEIVERNAME = receiver_name;
                    title_name = intent.getStringExtra("name");
                } else if ("ChatFragment".equalsIgnoreCase(Utils.replaceNull(screen)) || "ParentsListAdapter".equalsIgnoreCase(Utils.replaceNull(screen))) {
                    title_name = intent.getStringExtra("name");
                } else if (Constants.CHAT.equalsIgnoreCase(Utils.replaceNull(screen))) {
                    title_name = intent.getStringExtra("name");
                }
                isSplash = intent.getBooleanExtra("isSplash", false);

            } catch (Exception e) {
            }

            intializeViews();

            /*doBindService();
            SmackService.start(this);*/

            //method to start services
            RestartServices();

            /*doBindService();
            SmackService.start(this);*/
            /*if (MyXMPP.getConnection() == null || !MyXMPP.getConnection().isAuthenticated()) {
                try {
                    doUnbindService();
                } catch (Exception e) {
                }
                doBindService();
                SmackService.start(this);
            } else {
                MyXMPP.getArchievedMessage(RECEIVERNAME, mChatAdapter);
                loadingDialog.setVisibility(View.GONE);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            //stopService(new Intent(ChatActivity.this, NetworkService.class));
            startService(new Intent(ChatActivity.this, NetworkService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*Boolean firstTimeAppOpen = sharedPreferences.getBoolean(Constants.firstTimeAppOpen, false);

        if (!firstTimeAppOpen){

            //method to get chat history and inserted it to local db
            MyXMPP.getArchievedMessage(user2);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.firstTimeAppOpen, true);
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void intializeViews() {

        activity = this;
        //loadingLayout = findViewById(R.id.loadingLayout);
        edMessageText = (EditText) findViewById(R.id.messageEditText);
        ivSendMessage = (ImageView) findViewById(R.id.sendMessageButton);
        recyclerMessages = (RecyclerView) findViewById(R.id.msgListView);

        TextView titleView = findViewById(R.id.titleView);
        if (!Utils.replaceNull(title_name).equalsIgnoreCase("")) {
            titleView.setText(title_name);
        }

        loadingDialog = Utils.initProgress(this, "Please wait . . .");
        loadingDialog.setVisibility(View.VISIBLE);
        loadingDialog.setClickable(false);
        tvProgresstext = loadingDialog.findViewById(R.id.progressText);
        progressBar = loadingDialog.findViewById(R.id.progressbar);
        //ivSuccess = loadingDialog.findViewById(R.id.succesCheck);

        if (!Utils.isNetworkAvailable(ChatActivity.this)) {
            Toast.makeText(ChatActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            loadingDialog.setVisibility(View.GONE);
        }

        attachIcon = findViewById(R.id.attachIcon);

        attachmentLayout = findViewById(R.id.attachmentLayout);

        cameraImageView = findViewById(R.id.cameraImageView);
        pictureImageView = findViewById(R.id.pictureImageView);
        documentImageView = findViewById(R.id.documentImageView);
        audioImageView = findViewById(R.id.audioImageView);
        backButton = findViewById(R.id.backButton);

        messageMainLayout = findViewById(R.id.messageMainLayout);
        emptyLayout = findViewById(R.id.emptyLayout);

        /*attachIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (attachmentLayout.getVisibility() == View.VISIBLE) {

                    attachmentLayout.setVisibility(View.GONE);
                } else {
                    attachmentLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        cameraImageView.setOnClickListener(clickListener);
        pictureImageView.setOnClickListener(clickListener);
        documentImageView.setOnClickListener(clickListener);
        audioImageView.setOnClickListener(clickListener);
        backButton.setOnClickListener(clickListener);*/

        random = new Random();
        chatlist = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);
        mChatAdapter = new ChatAdapter(this, chatlist);
        recyclerMessages.setAdapter(mChatAdapter);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(0);
        Intent intent = getIntent();
        //String notification_text = intent.getStringExtra(Constants.NOTIFICATIONS);
        String screen = intent.getStringExtra("screen");
        //ChatMessage message = new Gson().fromJson(notification_text, ChatMessage.class);

        if (screen != null && screen.equalsIgnoreCase("Notification")) {
            //MyXMPP.getArchievedMessage(user2, mChatAdapter);
        }

        //method to change the adapter
        //updateChatListAdapter(message);

        /*if (Database.isTableExists(user1, this)) {
            ChatAdapter.mChatList.clear();
            if (message != null) {
                Database database = new Database(this);
                database.insertMessageToOffline(message.getSender(), message.getMsgid(), message.getSender(), message.getReceiver(), message.getBody(), false, message.getDate(), message.getTime());
            }
            Database.getMessagesFromOffline(user1, this, mChatAdapter);
        }*/
        //initializeListeners();

        /*if (!Utils.chatAccessTime()) {
            messageMainLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

        } else {
            messageMainLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

        }
*/
        messageMainLayout.setVisibility(View.VISIBLE);

        edMessageText.setEnabled(true);
        edMessageText.setFocusable(true);
        attachIcon.setOnClickListener(clickListener);
        cameraImageView.setOnClickListener(clickListener);
        pictureImageView.setOnClickListener(clickListener);
        documentImageView.setOnClickListener(clickListener);
        audioImageView.setOnClickListener(clickListener);
        backButton.setOnClickListener(clickListener);
        ivSendMessage.setOnClickListener(clickListener);

        //Log.e(TAG, "notification text : " + notification_text);
    }

    private void initializeListeners() {
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edMessageText.getText().toString().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Text messages cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        try {
            String message = edMessageText.getEditableText().toString();
            if (!message.equalsIgnoreCase("")) {

//                Utils.hideKeyboard(ChatActivity.this);

                final ChatMessage chatMessage = new ChatMessage(name, RECEIVERNAME, message,
                        "" + random.nextInt(1000), true, "", "");
                chatMessage.setMsgID();
                chatMessage.setBody(message);

                String utc_date = Utils.convertLocalToUtcTime();

                chatMessage.setDate(utc_date);
                chatMessage.setTime(Utils.getCurrentTime());
                edMessageText.setText("");
                long id = 0;

                handleOfflineOnlineMessage(chatMessage);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        try {

            /*Presence presence = new Presence(Presence.Type.unavailable);
            MyXMPP.connection.sendPacket(presence);*/

            if (!backButtonPressed) {
                //method to start services
                RestartServices();
            }
            //doUnbindService();
            if (ChatAdapter.mediaPlayer != null && ChatAdapter.mediaPlayer.isPlaying()) {
                ChatAdapter.mediaPlayer.reset();
            }
        } catch (Exception e) {
        }
    }

    void doBindService() {
        try {

            if (mConnection != null) {
                bindService(new Intent(this, SmackService.class), mConnection, Context.BIND_AUTO_CREATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void doUnbindService() {
        try {
            if (mConnection != null) {
                unbindService(mConnection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception e) {
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        AndroidBusProvider.getInstance().register(this);

        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter(Constants.CHAT));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            title_name = intent.getStringExtra("name");
            RECEIVERNAME = intent.getStringExtra("receiver_name");
            //Constants.Chat1 = name;
            Constants.Chat2 = RECEIVERNAME;

            //method to start services
            RestartServices();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        AndroidBusProvider.getInstance().unregister(this);
    }

    public SmackService getmService() {
        return mService;
    }

    @Subscribe
    public void processChange(OfflineToOnlineEvent event) {
        try {

            if (ChatActivity.this != null) {
                if (event.isProcess) {
                    try {

                        Database database = new Database(this);
                        if (!database.isTableExists(name, this)) {
                            database.createTable(name);
                        }
                        if (database.isTableExists(name, this)) {
                            //process offline messages
                            database.getMessagesFromOffline(name, this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void processChange(RefreshListEvent event) {
        try {

            if (ChatActivity.this != null && active) {
                if (event.refresh) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                mChatAdapter.notifyDataSetChanged();
                                /*LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                ChatActivity.recyclerMessages.setLayoutManager(mLinearLayoutManager);
                                mLinearLayoutManager.scrollToPosition(mChatAdapter.getItemCount()-1);*/
                                //ChatActivity.recyclerMessages.getLayoutManager().smoothScrollToPosition(ChatActivity.recyclerMessages, new RecyclerView.State(), mChatAdapter.getItemCount()-1);
                                ChatActivity.loadingDialog.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void processChange(RestartServiceEvent event) {
        try {

            if (ChatActivity.this != null) {
                if (event.restart) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            //method to start services
                            RestartServices();

                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method to attach document
    private void attachDocument() {

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
            String mime = mimeTypesStr.substring(0, mimeTypesStr.length() - 1);
//            Log.e(TAG, "mime type of document : " + mime);
            intent.setType(mime);
        }
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), DOCUMENT_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
//            Log.e(TAG, "exception : " + ex);
            Toast.makeText(this, "File manager not found", Toast.LENGTH_SHORT).show();
        }
    }

    //method to attach audio
    private void openGalleryAudio() {

        Intent intent = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        }
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Select Audio "), SELECT_AUDIO);
    }

    //method to open dialog for attachment
    public void showDialog(final CharSequence items[], String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_background);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {

                    //method to take photo or video
                    takePictureOrVideo(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[i].equals("Video")) {

                    //method to take photo or video
                    takePictureOrVideo(CAMERA_CAPTURE_VIDEO_REQUEST_CODE);

                } else if (items[i].equals("Gallery Image")) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(galleryIntent, GALLERY_IMAGE);
                } else if (items[i].equals("Gallery Video")) {
                    Intent videoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    videoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(videoIntent, GALLERY_VIDEO);
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    //method to take photo or video
    private void takePictureOrVideo(int REQUEST_TAKE_PHOTO) {

        //method to check camera availability
        checkDeviceHasCamera();
        Intent takePictureIntent = null;
        if (REQUEST_TAKE_PHOTO == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //takePictureIntent.setType("image/jpg");
        } else if (REQUEST_TAKE_PHOTO == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //takePictureIntent.setType("video/mp4");
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
                    Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                    //Uri photoURI = FileProvider.getUriForFile(this, "nconnect.pappaya.education.nconnectparent.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            } catch (IllegalArgumentException e) {
//                Log.e(TAG, "Illegal argument exception " + e);
            }
        }
    }

    //method to check camera availability
    private boolean checkDeviceHasCamera() {

        boolean hasCamera = true;
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
            hasCamera = false;
        }

        return hasCamera;
    }

    //method to handle permission for all attachments
    void requestMultiplePermissions() {
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
                            Toast.makeText(ChatActivity.this, "Please accept this permission to use this application", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String url = "";
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                url = mediaPath;
                try {
                    //Utils.ResizeImages(url, url);
                } catch (Exception e) {
                }
                //Toast.makeText(this, "image captures", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancelled image capture", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                url = mediaPath;
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancelled video recording", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == GALLERY_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    url = CameraUtils.getVideoPath(this, contentURI);
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancelled video recording", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    url = CameraUtils.getImagePath(this, contentURI);
                    try {
                        //Utils.ResizeImages(url, url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancelled image capture", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == DOCUMENT_REQUEST_CODE || requestCode == SELECT_AUDIO) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedDocument = data.getData();
                    url = FilePath.getPath(this, selectedDocument);
                    if (Utils.replaceNull(url).equalsIgnoreCase("")) {
                        url = Utils.getDriveFilePath(selectedDocument, ChatActivity.this);
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled upload document", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to get document", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!Utils.replaceNull(url).equalsIgnoreCase("")) {
//            Log.e(TAG, "FIlename : " + url);
            file_name = Utils.getFileNameFromUrl(url);
//            Log.e(TAG, "getFilename from urll : " + file_name);
            new UploadMediaFileToS3(url).execute();
        }

    }

    //method to update list adapter with imageview
    void fileListUpdate(String url) {

        ChatMessage chatMessage = new ChatMessage(name, RECEIVERNAME, "",
                "" + random.nextInt(1000), true, url, file_name);
        chatMessage.setMsgID();
        chatMessage.setBody("");

        String utc_date = Utils.convertLocalToUtcTime();

        chatMessage.setDate(utc_date);
        chatMessage.setTime(Utils.getCurrentTime());
        chatMessage.setFile_size(total_file_size);
        /*ChatActivity.mChatAdapter.add(chatMessage);
        ChatActivity.mChatAdapter.notifyDataSetChanged();*/

        handleOfflineOnlineMessage(chatMessage);
    }

    //method to start services
    void RestartServices() {
        try {

            if (MyXMPP.getConnection() == null || !MyXMPP.getConnection().isAuthenticated() || SmackService.xmppConnection == null) {
                //doUnbindService();
//                stopService(new Intent(ChatActivity.this, ChatService.class));
                //startService(new Intent(ChatActivity.this, ChatService.class));
                //stopService(new Intent(ChatActivity.this, SmackService.class));
                //doBindService();
                //SmackService.start(ChatActivity.this);
//                Intent intent = new Intent(ChatActivity.this, ChatService.class);
                //Intent s_intent = new Intent(ChatActivity.this, SmackService.class);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                    startForegroundService(s_intent);
                } else {
                    startService(intent);
                    startService(s_intent);
                }*/

//                startService(intent);
                //startService(s_intent);

                try {

                    //chat service action
                    Utils.chatServiceActions(ChatActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                /*Presence presence = new Presence(Presence.Type.available);
                MyXMPP.connection.sendPacket(presence);*/

                MyXMPP.getArchievedMessage(RECEIVERNAME, mChatAdapter);
                loadingDialog.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadMediaFileToS3 extends AsyncTask<Void, Integer, String> {
        private long totalSize = 0;
        private String url;


        public UploadMediaFileToS3(String url) {
            this.url = url;

//            Log.e(TAG, "getFilename from urll : " + url);
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.setVisibility(View.VISIBLE);
            //ivSuccess.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvProgresstext.setText("Uploading media files ...");
//            progressDialog.setTitle("Uploading media files....");
//            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            loadingDialog.setVisibility(View.VISIBLE);
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
                File sourceFile = new File(url);
                //sourceFile = Utils.saveBitmapToFile(sourceFile);
                entity.addPart("file_", new FileBody(sourceFile));
                totalSize = entity.getContentLength();

                if (totalSize > 50000000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this, "File more than 50 MB is not allowed!", Toast.LENGTH_SHORT).show();
                            loadingDialog.setVisibility(View.GONE);
                        }
                    });
                    return null;
                }

                total_file_size = String.valueOf(totalSize) + "Bytes";
                if (totalSize > 1024) {
                    long fileInKb = totalSize / 1024;
                    total_file_size = String.valueOf(fileInKb) + "KB";

                    if (fileInKb > 1024) {
                        long fileInMb = fileInKb / 1024;
                        total_file_size = String.valueOf(fileInMb) + "MB";
                    }
                }


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
            loadingDialog.setVisibility(View.GONE);
            try {
                S3Upload resUrl = new Gson().fromJson(result, S3Upload.class);
                if (resUrl.getUrl() != null) {
                    //method to update list adapter with imageview
                    fileListUpdate(resUrl.getUrl());
                }
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e(TAG, "exception : " + e);
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pressBack();
    }

    private void pressBack() {
        if (screen != null && screen != "") {
            if (screen.equalsIgnoreCase(Constants.notification)) {
                startActivity(new Intent(this, Dashboard.class)
                        .putExtra("isNotifyChat", true));
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

}