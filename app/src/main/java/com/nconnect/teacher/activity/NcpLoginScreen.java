package com.nconnect.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.fragment.EnterDetailsFragment;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.Users;
import com.nconnect.teacher.model.login.LoginParam;
import com.nconnect.teacher.model.login.LoginUser;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class NcpLoginScreen extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = NcpLoginScreen.class.getSimpleName();
    private EditText edUsername, edPassword;
    private Button btnLogin;
    private TextView tvNewUser, tvForgotPassword;
    public static List<Users> usersList = new ArrayList<>();
    private EditText edBaseURl;
    private Button btnConnect;
    private JSONObject jsonObj = null;
    public static String name = "";
    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_login_screen);
        initializeViews();
    }

    private void initializeViews() {
        initUserPref();
        edBaseURl = (EditText) findViewById(R.id.editBaseUrl);
        btnConnect = (Button) findViewById(R.id.connect);
        ivLogo = (ImageView) findViewById(R.id.Logo);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edBaseURl.getText().toString().isEmpty()) {
                    Toast.makeText(NcpLoginScreen.this, "Empty string ", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Constants.BASE_URL = edBaseURl.getText().toString();
//                Log.e(TAG, "utrl " + Constants.BASE_URL);
                Toast.makeText(NcpLoginScreen.this, "switch to " + Constants.BASE_URL, Toast.LENGTH_SHORT).show();
            }
        });
        edUsername = (EditText) findViewById(R.id.username);
        edPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        tvNewUser = (TextView) findViewById(R.id.newUser);
        tvForgotPassword = (TextView) findViewById(R.id.forgotPassword);
        btnLogin.setOnClickListener(this);
        tvNewUser.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        //hide keyboard

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setHideKeyboardOnTouch(this, findViewById(R.id.parentActivity));
    }

    private void initUserPref() {
        SharedPreferences pref = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String USERS = pref.getString("USERS", "");
        if (USERS != null) {
            if (USERS.isEmpty()) {
                SharedPreferences.Editor editor = pref.edit();
                List<Users> users = new ArrayList<>();
                String userStr = new Gson().toJson(users);
                editor.putString("USERS", userStr);
                editor.commit();
            } else {
//                Log.e(TAG, "USERS" + USERS);
            }
        }
    }

    public static void setHideKeyboardOnTouch(final Context context, View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        try {
            //Set up touch listener for non-text box views to hide keyboard.
            if (!(view instanceof EditText || view instanceof ScrollView)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        return false;
                    }
                });
            }
            //If a layout container, iterate over children and seed recursion.
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setHideKeyboardOnTouch(context, innerView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG, "Exception on hiding keyboard : " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                login();
                break;
            case R.id.newUser:
                startActivity(new Intent(this, NcpNewUser.class).putExtra("screen", 1));
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, NcpNewUser.class).putExtra("screen", 2));
                break;
        }
    }


    private void login() {
        if (!validate()) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.FCM_PREF, MODE_PRIVATE);
        String token = sharedPreferences.getString("fcm_id_teacher", "");
//        Log.e(TAG, "token : " + token);
        String username = edUsername.getText().toString();
        String password = edPassword.getText().toString();
        LoginParam loginParam = new LoginParam(username, password, token, Constants.LOGIN_TYPE_VALUE);
        LoginUser loginUser = new LoginUser(Constants.JSON_RPC, loginParam);
//        Log.e(TAG, "Login Model data :" + new Gson().toJson(loginUser));
//        Log.e(TAG, "url : " + Constants.BASE_URL + Constants.END_POINT_LOGIN);
        try {
            jsonObj = new JSONObject(new Gson().toJson(loginUser));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.e(TAG, "user list : " + new Gson().toJson(NcpLoginScreen.usersList));
        View dialog = Utils.initProgress(this, "Please Wait . . .");
        dialog.setVisibility(View.VISIBLE);
        dialog.setClickable(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        Constants.BASE_URL + Constants.END_POINT_LOGIN,
                        jsonObj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

//                        Log.e(TAG, "response ; " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONObject resultJson = jsonObject.getJSONObject(Constants.RESULT);
                            Result result = new Gson().fromJson(resultJson.toString(), Result.class);
                            if (result.getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                                if (!result.getLogin_type().equalsIgnoreCase("parent")) {
                                    dialog.setVisibility(View.GONE);
                                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(Constants.LOGIN, edUsername.getText().toString());
                                    editor.putString(Constants.PASSWORD, password);
                                    editor.putString(Constants.NAME, result.getName());
                                    editor.putInt(Constants.USER_TOKEN, result.getUserToken());
                                    editor.putString(Constants.PROFILE_IMAGE, result.getDp());
                                    editor.putString(Constants.SESSION_ID, result.getSessionId());
                                    editor.putString(Constants.LOGIN_TYPE, result.getLogin_type());
                                    editor.putString(Constants.SENDER, username);
                                    String status = resultJson.getString(Constants.STATUS_CODE);
                                    editor.putString(Constants.STATUS_CODE, status);
                                    if (status.equalsIgnoreCase(Constants.GO_TO_SETPASSWORD)) {
                                        editor.putBoolean("isLogin", false);
                                        editor.commit();
                                        startActivity(new Intent(NcpLoginScreen.this, NcpSetPassword.class)
                                                .putExtra("type", "set"));
                                        finish();
                                    } else if (status.equalsIgnoreCase(Constants.GO_TO_ENTER_DETAIL)) {
                                        editor.putBoolean("isLogin", false);
                                        editor.commit();
                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        EnterDetailsFragment fragment = EnterDetailsFragment.newInstance();
                                        fragment.show(transaction, "ENTER_DETAILS");
                                    } else if (status.equalsIgnoreCase(Constants.GO_TO_DASHBOARD)) {
                                        try {
                                            //chat service action
                                            Utils.chatServiceActions(NcpLoginScreen.this);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        editor.putBoolean("isLogin", true);
                                        editor.commit();
                                        startActivity(new Intent(NcpLoginScreen.this, Dashboard.class));
                                        finish();
                                    } else if (status.equalsIgnoreCase(Constants.GO_TO_FORGOT_PASSWORD)) {
                                        editor.putBoolean("isLogin", false);
                                        editor.commit();
                                        startActivity(new Intent(NcpLoginScreen.this, NcpSetPassword.class).
                                                putExtra("type", "forget"));
                                        finish();
                                    }
                                } else {
                                    dialog.setVisibility(View.GONE);
                                    Toast.makeText(NcpLoginScreen.this, "Please check your username and password", Toast.LENGTH_SHORT).show();
                                }
                            } else if (result.getResponse().equalsIgnoreCase("fail")) {
                                dialog.setVisibility(View.GONE);
                                Toast.makeText(NcpLoginScreen.this, "Please check your username and password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.setVisibility(View.GONE);
//                            Log.e(TAG, "Excetion : " + e);
                            Toast.makeText(NcpLoginScreen.this, "Please check your username and password", Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e) {
                            dialog.setVisibility(View.GONE);
//                            Log.e(TAG, "Excetion : " + e);
                            Toast.makeText(NcpLoginScreen.this, "Please check your username and password", Toast.LENGTH_SHORT).show();
                        }

//                        Log.e(TAG, "user list : " + new Gson().toJson(NcpLoginScreen.usersList));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.setVisibility(View.GONE);
//                        Log.e(TAG, "Error : " + error);
//                        Log.e(TAG, "user list : " + new Gson().toJson(NcpLoginScreen.usersList));
                        Toast.makeText(NcpLoginScreen.this, "Cannot login please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(NcpLoginScreen.this).addToRequestQueue(request);
            }
        }, 3000);

    }

    private boolean validate() {
        String username = edUsername.getText().toString();
        String password = edPassword.getText().toString();
        Drawable drawable = getResources().getDrawable(R.drawable.ncp_error);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
            edUsername.setError("Username is required", drawable);
            edPassword.setError("Password is required", drawable);
            return false;
        } else {

            edUsername.setError(null);
            edPassword.setError(null);
        }
        if (TextUtils.isEmpty(username)) {
            edUsername.setError("Username is required", drawable);
            return false;
        } else {
            edUsername.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            edPassword.setError("Password is required", drawable);
            return false;
        } else {
            edPassword.setError(null);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
