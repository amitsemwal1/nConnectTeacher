package com.nconnect.teacher.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.jivesoftware.smack.chat.ChatManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.database.Database;
import com.nconnect.teacher.fragment.AnnoucnementsFragment;
import com.nconnect.teacher.fragment.ApprovalStoriesFragment;
import com.nconnect.teacher.fragment.ChatFragment;
import com.nconnect.teacher.fragment.DashboardFragment;
import com.nconnect.teacher.fragment.EventsFragment;
import com.nconnect.teacher.fragment.IssuesFragment;
import com.nconnect.teacher.fragment.MonthlyAttendanceReportFragment;
import com.nconnect.teacher.fragment.NoticeboardFragment;
import com.nconnect.teacher.fragment.StoriesFragment;
import com.nconnect.teacher.fragment.UnApprovedStoriesFragment;
import com.nconnect.teacher.helper.CircularImageView;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Logout;
import com.nconnect.teacher.model.Notifications.NotificationRequest;
import com.nconnect.teacher.model.Notifications.NotificationRoot;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.login.LoginParam;
import com.nconnect.teacher.services.LocalBinder;
import com.nconnect.teacher.services.SmackService;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import com.nconnect.teacher.xmpp.MyXMPP;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nconnect.teacher.xmpp.MyXMPP.connection;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Dashboard";
    String sessionIdValue;
    public static TextView notifyCountTextview;
    public static int notifyItemCount = 0;
    private BottomNavigationView navigationView;
    private Toolbar toolbar;
    private FrameLayout fragContainer;
    private AppBarLayout appBarLayout;
    private ImageView toolBarLogo;
    private TextView toolbarTitle;
    public static Dashboard context = null;
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Menu menuNav = toolbar.getMenu();
            switch (item.getItemId()) {
                case R.id.nav_home:
                    menuNav.findItem(R.id.notification).setVisible(true);
                    menuNav.findItem(R.id.create_issue).setVisible(false);
                    item.setChecked(true);
                    toolBarLogo.setVisibility(View.VISIBLE);
                    toolbarTitle.setVisibility(View.GONE);
                    loadFragment(new DashboardFragment(), "DASHBOARD");
                    return true;
                case R.id.nav_stories:
                    menuNav.findItem(R.id.notification).setVisible(true);
                    menuNav.findItem(R.id.create_issue).setVisible(false);
                    item.setChecked(true);
                    toolBarLogo.setVisibility(View.GONE);
                    toolbarTitle.setVisibility(View.VISIBLE);
                    toolbarTitle.setText("Stories");
                    loadFragment(new StoriesFragment(), "STORIES");
                    return true;
                case R.id.nav_chat:

                    /*if(Utils.chatAccessTime()){
                        item.setChecked(true);
                        DashboardFragment.parentScrollView.scrollTo(0, 0);
                        loadFragment(new ChatFragment(), "CHAT");
                    }else {
                        Toast.makeText(Dashboard.this, getString(R.string.error_chat_available), Toast.LENGTH_LONG).show();
                    }*/

                    item.setChecked(true);
                    DashboardFragment.parentScrollView.scrollTo(0, 0);
                    loadFragment(new ChatFragment(), "CHAT");

                    return true;
                case R.id.nav_noticeboard:
                    menuNav.findItem(R.id.notification).setVisible(false);
                    menuNav.findItem(R.id.create_issue).setVisible(false);
                    item.setChecked(true);
                    toolBarLogo.setVisibility(View.GONE);
                    toolbarTitle.setVisibility(View.VISIBLE);
                    toolbarTitle.setText("Noticeboard");
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", 0);
                    Fragment fragment = new NoticeboardFragment();
                    fragment.setArguments(bundle);
                    loadFragment(fragment, "NOTICEBOARD");
                    return true;
            }
            return false;
        }
    };
    private ImageView ivSchoolBoard;
    private LinearLayout containerSchoolBoard;
    private View headerView;
    private SmackService mService;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeViews();


        //method to get notification count
        loadData(1);

        try {
            if (MyXMPP.getConnection() == null || !MyXMPP.getConnection().isAuthenticated()) {
                try {
                    //doBindService();
//                    Intent intent = new Intent(Dashboard.this, ChatService.class);
                    //Intent s_intent = new Intent(Dashboard.this, SmackService.class);
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                        startForegroundService(s_intent);
                    } else {
                        startService(intent);
                        startService(s_intent);
                    }*/

//                    startService(intent);
                    //startService(s_intent);
                    //doBindService();
                    /*startService(new Intent(Dashboard.this, ChatService.class));
                    SmackService.start(Dashboard.this);*/
                    try {

                        //chat service action
                        Utils.chatServiceActions(Dashboard.this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
//                    Log.e(TAG, "exception : " + e);
                }
            }
        } catch (Exception e) {

//            Log.e(TAG, "exception : " + e);
        }
    }

    void doBindService() {
        bindService(new Intent(this, SmackService.class), mConnection, Context.BIND_AUTO_CREATE);
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

    //method to stop services
    void stopChatServices() {
        try {

            if (MyXMPP.getConnection() != null || MyXMPP.getConnection().isAuthenticated()) {
                //  doBindService();
                MyXMPP.loginUser = "";
                MyXMPP.passwordUser = "";
                if (connection != null && MyXMPP.mChatManagerListener != null) {
                    ChatManager.getInstanceFor(connection).removeChatListener(MyXMPP.mChatManagerListener);
                }
                MyXMPP.disconnect();
//                stopService(new Intent(Dashboard.this, ChatService.class));
                stopService(new Intent(Dashboard.this, SmackService.class));

                SmackService.mActive = false;
                SmackService.mThread = null;
                SmackService.mConnection = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initializeViews() {
        ivSchoolBoard = (ImageView) findViewById(R.id.schoolBoard);
        containerSchoolBoard = (LinearLayout) findViewById(R.id.schoolBoardContainer);
        containerSchoolBoard.setVisibility(View.VISIBLE);
        toolBarLogo = (ImageView) findViewById(R.id.toolbarLogo);
        toolBarLogo.setVisibility(View.VISIBLE);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        fragContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbar.inflateMenu(R.menu.notification_menu);
        toolbar.getMenu().findItem(R.id.notification).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(Dashboard.this, NotificationActivity.class));
                return false;
            }
        });
        Menu menuNav = toolbar.getMenu();

        View actionView = MenuItemCompat.getActionView(toolbar.getMenu().findItem(R.id.notification));
        notifyCountTextview = actionView.findViewById(R.id.notification_badge);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, NotificationActivity.class));
            }
        });

        setupBadge();

        menuNav.findItem(R.id.create_issue).setVisible(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(this);
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        headerView = navigation.getHeaderView(0);
        checkTheLoginType(navigation);

        Intent intent = getIntent();
        String issuesScreen = intent.getStringExtra("value");
        if (issuesScreen != null && issuesScreen.equalsIgnoreCase("escalated")) {
            IssuesFragment.stats = "Escalate";
            loadFragment(new IssuesFragment(), "ISSUES");

        } else if (issuesScreen != null && issuesScreen.equalsIgnoreCase("resolved")) {
            IssuesFragment.stats = "Resolved";
            loadFragment(new IssuesFragment(), "ISSUES");

        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new DashboardFragment(), "DASHBOARD");
            transaction.setTransition(transaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        boolean isNotifyScreen = intent.getBooleanExtra("isNotifyChat", false);
        if (isNotifyScreen) {
            findViewById(R.id.nav_chat).performClick();
        }
        initiListener();
        requestMultiplePermissions();

    }

    private void initiListener() {
        containerSchoolBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSchoolBoard();
            }
        });

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
                            Toast.makeText(Dashboard.this, "Please accept this permission to use this application", Toast.LENGTH_SHORT).show();
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

    public LinearLayout getContainerSchoolBoard() {
        return containerSchoolBoard;
    }

    private void checkTheLoginType(NavigationView navigation) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String loginType = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        String name = sharedPreferences.getString(Constants.NAME, "");
        View headerView = navigation.getHeaderView(0);
        CircularImageView ivprofileImage = (CircularImageView) headerView.findViewById(R.id.profileImagehamburg);
        TextView tvProfilename = (TextView) headerView.findViewById(R.id.profileNamehamburg);
        TextView tvProfileDesignation = (TextView) headerView.findViewById(R.id.profileDesigantionhamburg);
        if (loginType != null) {
            if (loginType.equalsIgnoreCase("teacher")) {
                Menu menuNav = navigation.getMenu();
                menuNav.findItem(R.id.hamIssues).setVisible(false);
                menuNav.findItem(R.id.hamApproovedStories).setVisible(false);
                menuNav.findItem(R.id.hamMyEvents).setVisible(false);
                menuNav.findItem(R.id.hamMyAnnoucements).setVisible(false);
                menuNav.findItem(R.id.hamPastAttendance).setVisible(false);
                tvProfilename.setText(name);
                tvProfileDesignation.setText("Teacher");
            } else if (loginType.equalsIgnoreCase("principal") || loginType.equalsIgnoreCase("vice_principal")) {
                Menu menuNav = navigation.getMenu();
                menuNav.findItem(R.id.hamPastIssues).setVisible(false);
                menuNav.findItem(R.id.hamUnApprovedStories).setVisible(false);
                menuNav.findItem(R.id.hamMonthlyAttendacneReport).setVisible(false);
                if (loginType.equalsIgnoreCase("principal")) {
                    tvProfileDesignation.setText("Principal");
                } else {
                    tvProfileDesignation.setText("Vice Principal");
                }
                tvProfilename.setText(name);
            }
        }

    }


    public void openSchoolBoardEvents() {
        Menu menuNav = toolbar.getMenu();
        containerSchoolBoard.setVisibility(View.GONE);
        menuNav = toolbar.getMenu();
        menuNav.findItem(R.id.notification).setVisible(true);
        menuNav.findItem(R.id.create_issue).setVisible(false);
        toolBarLogo.setVisibility(View.GONE);
        toolbar.setNavigationIcon(R.drawable.ncp_back);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("Events");
        Bundle bundle = new Bundle();
        bundle.putString("type", "school_events");
        Fragment fragmentEvent = new EventsFragment();
        fragmentEvent.setArguments(bundle);
        loadFragment(fragmentEvent, "EVENTS");
    }

    public void openSchoolBoardAnnoucements() {
        Menu menuNav = toolbar.getMenu();
        toolbar.setNavigationIcon(R.drawable.ncp_back);
        containerSchoolBoard.setVisibility(View.GONE);
        menuNav = toolbar.getMenu();
        menuNav.findItem(R.id.notification).setVisible(true);
        menuNav.findItem(R.id.create_issue).setVisible(false);
        toolBarLogo.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("Announcements");
        Bundle bundle = new Bundle();
        bundle.putString("type", "school_annoucements");
        Fragment fragmentAnn = new AnnoucnementsFragment();
        fragmentAnn.setArguments(bundle);
        loadFragment(fragmentAnn, "ANNOUNCMENTS");
    }

    public void openSchoolBoard() {
        Menu menuNav = toolbar.getMenu();
        toolbar.setNavigationIcon(R.drawable.ncp_back);
        containerSchoolBoard.setVisibility(View.GONE);
        menuNav = toolbar.getMenu();
        menuNav.findItem(R.id.notification).setVisible(true);
        menuNav.findItem(R.id.create_issue).setVisible(false);
        toolBarLogo.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("School Board");
        Bundle bundle = new Bundle();
        bundle.putString("type", "school_stories");
        Fragment fragment = new StoriesFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, "STORIES");
    }

    public void openMyEvents() {
        Menu menuNav = toolbar.getMenu();
        containerSchoolBoard.setVisibility(View.GONE);
        menuNav = toolbar.getMenu();
        menuNav.findItem(R.id.notification).setVisible(true);
        menuNav.findItem(R.id.create_issue).setVisible(false);
        toolBarLogo.setVisibility(View.GONE);
        toolbar.setNavigationIcon(R.drawable.ncp_back);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("My Events");
        Bundle bundle = new Bundle();
        bundle.putString("type", "my_events");
        Fragment fragmentEvent = new EventsFragment();
        fragmentEvent.setArguments(bundle);
        loadFragment(fragmentEvent, "EVENTS");
    }

    public void openMyAnnoucements() {
        Menu menuNav = toolbar.getMenu();
        toolbar.setNavigationIcon(R.drawable.ncp_back);
        containerSchoolBoard.setVisibility(View.GONE);
        menuNav = toolbar.getMenu();
        menuNav.findItem(R.id.notification).setVisible(true);
        menuNav.findItem(R.id.create_issue).setVisible(false);
        toolBarLogo.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("My Announcements");
        Bundle bundle = new Bundle();
        bundle.putString("type", "my_announcements");
        Fragment fragmentAnn = new AnnoucnementsFragment();
        fragmentAnn.setArguments(bundle);
        loadFragment(fragmentAnn, "ANNOUNCMENTS");
    }

    public void openMyStories() {
        Menu menuNav = toolbar.getMenu();
        toolbar.setNavigationIcon(R.drawable.ncp_back);
        containerSchoolBoard.setVisibility(View.GONE);
        menuNav = toolbar.getMenu();
        menuNav.findItem(R.id.notification).setVisible(true);
        menuNav.findItem(R.id.create_issue).setVisible(false);
        toolBarLogo.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("My Stories");
        Bundle bundle = new Bundle();
        bundle.putString("type", "my_story");
        Fragment fragment = new StoriesFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, "STORIES");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Menu menuNav = toolbar.getMenu();
        Bundle bundle = new Bundle();
        Fragment fragment = null;
        switch (id) {
            case R.id.hamUnApprovedStories:
                toolbar.setNavigationIcon(R.drawable.ncp_back);
                containerSchoolBoard.setVisibility(View.GONE);
                menuNav.findItem(R.id.notification).setVisible(false);
                menuNav.findItem(R.id.create_issue).setVisible(false);
                toolBarLogo.setVisibility(View.GONE);
                toolbarTitle.setVisibility(View.VISIBLE);
                toolbarTitle.setText("Un Approved Stories");
                loadFragment(new UnApprovedStoriesFragment(), "UNAPPROVEDSTORIES");
                break;
            case R.id.hamPastIssues:
                openIssues();
                break;
            case R.id.hamPastAttendance:
                containerSchoolBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.hamNotification:
                containerSchoolBoard.setVisibility(View.GONE);
                startActivity(new Intent(Dashboard.this, NotificationActivity.class));
                break;
            case R.id.hamMyProfile:
                containerSchoolBoard.setVisibility(View.GONE);
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.hamMyStories:
                openMyStories();
                break;
            case R.id.hamMyEvents:
                openMyEvents();
                break;
            case R.id.hamMyAnnoucements:
                openMyAnnoucements();
                break;
            case R.id.hamMonthlyAttendacneReport:
                containerSchoolBoard.setVisibility(View.VISIBLE);
                toolbar.setNavigationIcon(R.drawable.ncp_back);
                containerSchoolBoard.setVisibility(View.GONE);
                menuNav.findItem(R.id.notification).setVisible(true);
                menuNav.findItem(R.id.create_issue).setVisible(false);
                toolBarLogo.setVisibility(View.GONE);
                toolbarTitle.setVisibility(View.VISIBLE);
                toolbarTitle.setText("Monthly Attendance Report");
                loadFragment(new MonthlyAttendanceReportFragment(), "ATTENDANCE_REPORT");
                break;
            case R.id.hamIssues:
                toolbar.setNavigationIcon(R.drawable.ncp_back);
                containerSchoolBoard.setVisibility(View.GONE);
                menuNav.findItem(R.id.notification).setVisible(true);
                menuNav.findItem(R.id.create_issue).setVisible(false);
                toolBarLogo.setVisibility(View.GONE);
                toolbarTitle.setVisibility(View.VISIBLE);
                toolbarTitle.setText("Issues");
                loadFragment(new IssuesFragment(), "ISSUES");
                break;
            case R.id.hamApproovedStories:
                toolbar.setNavigationIcon(R.drawable.ncp_back);
                containerSchoolBoard.setVisibility(View.GONE);
                menuNav.findItem(R.id.notification).setVisible(true);
                menuNav.findItem(R.id.create_issue).setVisible(false);
                toolBarLogo.setVisibility(View.GONE);
                toolbarTitle.setVisibility(View.VISIBLE);
                toolbarTitle.setText("Approve Stories");
                loadFragment(new ApprovalStoriesFragment(), "APPROVAl");
                break;
            case R.id.action_logout_id:
                logout();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openIssues() {
        Menu menuNav = toolbar.getMenu();
        toolbar.setNavigationIcon(R.drawable.ncp_back);
        containerSchoolBoard.setVisibility(View.GONE);
        menuNav.findItem(R.id.notification).setVisible(true);
        menuNav.findItem(R.id.create_issue).setVisible(false);
        toolBarLogo.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("Issues");
        loadFragment(new IssuesFragment(), "ISSUES");
    }

    public BottomNavigationView getNavigationView() {
        return navigationView;
    }

    public void loadFragment(Fragment fragment, String fragmentId) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment, fragmentId);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (IllegalStateException e) {
//            Log.e(TAG, "exception : " + e);
        }

    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ImageView getToolBarLogo() {
        return toolBarLogo;
    }

    public TextView getToolbarTitle() {
        return toolbarTitle;
    }

    @Override
    public void onBackPressed() {
        DashboardFragment fragment = (DashboardFragment) getSupportFragmentManager().findFragmentByTag("DASHBOARD");
        if (fragment != null && fragment.isVisible()) {
            finish();
            ActivityCompat.finishAffinity(this);
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            toolbar.setVisibility(View.VISIBLE);
            navigationView.setSelectedItemId(R.id.nav_home);
            Menu menuNav = toolbar.getMenu();
            menuNav.findItem(R.id.notification).setVisible(true);
            menuNav.findItem(R.id.create_issue).setVisible(false);
            toolBarLogo.setVisibility(View.VISIBLE);
            toolbarTitle.setVisibility(View.GONE);
            containerSchoolBoard.setVisibility(View.VISIBLE);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void logout() {

        String sessionIdValue = "";
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        final LoginParam loginParam = new LoginParam();
        final Logout logout = new Logout();
        logout.setJsonrpc(Constants.JSON_RPC);
        logout.setMethod(Constants.METHOD_VAL);
        logout.setLoginParam(loginParam);
//        Log.e(TAG, "Model json data : " + new Gson().toJson(logout));
//        Log.e(TAG, "Model json data session id  : " + sessionIdValue);
        final ProgressDialog dialog = Utils.showDialog(this, "Logout user please wait...");
        dialog.show();
        (Utils.httpService(Dashboard.this).logout(logout, sessionIdValue)).enqueue(new Callback<Logout>() {
            @Override
            public void onResponse(Call<Logout> call, Response<Logout> response) {
                dialog.dismiss();
//                Log.e(TAG, "Logout response : " + new Gson().toJson(response.body()));
                Logout logout1 = response.body();
                try {
                    Result result = logout1.getResult();
                    if (result.getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        //method to stop the services
                        stopChatServices();
                        startActivity(new Intent(Dashboard.this, NcpLoginScreen.class));
                        finish();
                    } else {
                        Utils.showToastCustom(Dashboard.this, "Can't logout Please try again later");
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "null exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<Logout> call, Throwable t) {
                dialog.dismiss();
//                Log.e(TAG, "Failure response " + t.getMessage());
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {

                    Utils.showToastCustom(Dashboard.this, Constants.NO_CONNECTION);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        DashboardFragment fragment = (DashboardFragment) getSupportFragmentManager().findFragmentByTag("DASHBOARD");
        if (fragment != null && fragment.isVisible()) {
            containerSchoolBoard.setVisibility(View.VISIBLE);
        } else {
            containerSchoolBoard.setVisibility(View.GONE);
        }
        ImageView ivprofileImage = (ImageView) headerView.findViewById(R.id.profileImagehamburg);
        SharedPreferences preferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String profileImage = preferences.getString(Constants.PROFILE_IMAGE, "");
        if (profileImage != null && profileImage != "") {
            Glide.with(this).load(profileImage).apply(new RequestOptions().placeholder(R.drawable.ncp_avator)).into(ivprofileImage);
        }
    }

    //method to load the data
    void loadData(int pageNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);

        NotificationRequest.Params params = new NotificationRequest.Params(pageNumber);
        NotificationRequest notificationRequest = new NotificationRequest(Constants.JSON_RPC, params);

        String sessionIdValue = "";
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        (Utils.httpService(Dashboard.this).getNotification(notificationRequest, sessionIdValue)).enqueue(new Callback<NotificationRoot>() {
            @Override
            public void onResponse(Call<NotificationRoot> call, Response<NotificationRoot> response) {
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {

                        notifyItemCount = response.body().getResult().getUnread();
                        setupBadge();
                    } else {

                    }
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onFailure(Call<NotificationRoot> call, Throwable t) {

            }
        });
    }

    //method to set count for notification
    public static void setupBadge() {

        if (notifyCountTextview != null) {
            if (notifyItemCount == 0 || notifyItemCount < 0) {
                if (notifyCountTextview.getVisibility() != View.GONE) {
                    notifyCountTextview.setVisibility(View.GONE);
                }
            } else {

                String countText = String.valueOf(notifyItemCount);
                if (notifyItemCount > 99) {
                    countText = "99+";
                }

                notifyCountTextview.setText(countText);
                if (notifyCountTextview.getVisibility() != View.VISIBLE) {
                    notifyCountTextview.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter(Constants.notification));
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

  /*  public void startDownload(String url) {
        Log.e(TAG, "fil " + url);
        Toast.makeText(Dashboard.this, "Downloading...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Dashboard.this, DownloadNotificationService.class);
        intent.putExtra("file_url", url);
        startService(intent);
    }

    private void registerReceiver() {

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.PROGRESS_UPDATE);
        bManager.registerReceiver(mBroadcastReceiver, intentFilter);

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.PROGRESS_UPDATE)) {
                boolean downloadComplete = intent.getBooleanExtra("downloadComplete", false);
                if (downloadComplete) {
                    Toast.makeText(getApplicationContext(), "File download completed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };*/

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyItemCount += 1;
            setupBadge();
        }
    };
}
