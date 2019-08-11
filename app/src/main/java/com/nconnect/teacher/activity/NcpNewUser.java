package com.nconnect.teacher.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import org.w3c.dom.Text;

import com.nconnect.teacher.R;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.login.LoginParam;
import com.nconnect.teacher.model.login.LoginUser;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class NcpNewUser extends AppCompatActivity {

    private static final String TAG = NcpNewUser.class.getSimpleName();
    private Button btnGetLoginDetails;
    private EditText edTeacherId;
    private TextView tvScreenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_new_user);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Utils.setHideKeyboardOnTouch(this, findViewById(R.id.parentContainer));
        initializeViews();

//        Log.e(TAG, "user list : " + new Gson().toJson(NcpLoginScreen.usersList));
    }


    private void initializeViews() {
        tvScreenName = (TextView) findViewById(R.id.screenName);
        edTeacherId = (EditText) findViewById(R.id.teacherId);
        btnGetLoginDetails = (Button) findViewById(R.id.getLoginDetails);
        Intent intent = getIntent();
        int type = intent.getIntExtra("screen", 0);
        if (type != 0) {
            if (type == 1) {
                tvScreenName.setText("NEW USER");
            } else if (type == 2) {
                tvScreenName.setText("FORGOT PASSWORD");
            }
        }
        edTeacherId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edTeacherId.getText().toString().isEmpty()) {
                    btnGetLoginDetails.setEnabled(true);
                    btnGetLoginDetails.setBackground(getResources().getDrawable(R.drawable.ncp_button_back));
                } else {
                    btnGetLoginDetails.setEnabled(false);
                    btnGetLoginDetails.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnGetLoginDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                if (type == 1) {
                    url = "web/nConnect/teacher/onboard";
                    newUserOnBoard(url);
                } else if (type == 2) {
                    url = "web/nConnect/forgot_password";
                    forgotPassword(url);
                }
            }
        });

    }

    private void forgotPassword(String url) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_get_login_detail, null);
        Button btnCool = alertLayout.findViewById(R.id.cool);
        AlertDialog.Builder alert = new AlertDialog.Builder(NcpNewUser.this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        AlertDialog dialog = alert.create();
        btnCool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        LoginParam param = new LoginParam();
        param.setEmployeeId(edTeacherId.getText().toString());
        LoginUser user = new LoginUser();
        user.setLoginParam(param);
        JSONObject baseJSon = null;
        try {
            baseJSon = new JSONObject(new Gson().toJson(user));
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e(TAG, "json exception : " + e);
        }
        ProgressDialog progressDialog = new ProgressDialog(NcpNewUser.this, R.style.dialog_background);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + url,
                baseJSon, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.e(TAG, "response : " + response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject resJson = new JSONObject(response.toString());
                    JSONObject resultJson = resJson.getJSONObject(Constants.RESULT);
                    String resStr = resultJson.getString(Constants.RESPONSE);
                    if (resStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        progressDialog.dismiss();
                        dialog.show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(NcpNewUser.this, "Please check your Teacher Id", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Log.e(TAG, "exception: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "error : " + error);
                progressDialog.dismiss();
            }
        });
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(NcpNewUser.this).addToRequestQueue(objectRequest);
    }

    private void newUserOnBoard(String url) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_get_login_detail, null);
        Button btnCool = alertLayout.findViewById(R.id.cool);
        TextView tvMobileNoTag = alertLayout.findViewById(R.id.textMobileNumber);
        AlertDialog.Builder alert = new AlertDialog.Builder(NcpNewUser.this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        AlertDialog dialog = alert.create();
        btnCool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        LoginParam param = new LoginParam();
        param.setEmployeeId(edTeacherId.getText().toString());
        LoginUser user = new LoginUser();
        user.setLoginParam(param);
        JSONObject baseJSon = null;
        try {
            baseJSon = new JSONObject(new Gson().toJson(user));
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e(TAG, "json exception : " + e);
        }

        ProgressDialog progressDialog = new ProgressDialog(NcpNewUser.this, R.style.dialog_background);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + url,
                baseJSon, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.e(TAG, "response : " + response.toString());
                progressDialog.dismiss();
                try {
                    JSONObject resJson = new JSONObject(response.toString());
                    JSONObject resultJson = resJson.getJSONObject(Constants.RESULT);
                    String resStr = resultJson.getString(Constants.RESPONSE);
                    if (resStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        progressDialog.dismiss();
                        tvMobileNoTag.setText("" + resJson.getString("message"));
                        dialog.show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(NcpNewUser.this, "Please check your Teacher Id", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
//                    Log.e(TAG, "exception: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "error : " + error);
                progressDialog.dismiss();
            }
        });
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(NcpNewUser.this).addToRequestQueue(objectRequest);
    }
}
