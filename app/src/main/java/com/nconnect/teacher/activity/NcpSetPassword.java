package com.nconnect.teacher.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.fragment.EnterDetailsFragment;
import com.nconnect.teacher.helper.AsynkLogin;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.login.LoginParam;
import com.nconnect.teacher.model.login.LoginUser;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class NcpSetPassword extends AppCompatActivity {

    private static final String TAG = NcpSetPassword.class.getSimpleName();
    private TextInputLayout textHintWrongPassword;
    private EditText edConfirmPassword, edNewPassword;
    private TableRow rowWrongpassword;
    private TextView tvMessageWrongPassword;
    private Button btnNext;
    private int userPosition = 0;

    private String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_set_password);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Utils.setHideKeyboardOnTouch(this, findViewById(R.id.parentContainer));
        initializeViews();
    }

    private void initializeViews() {
        edNewPassword = (EditText) findViewById(R.id.newPassword);
        textHintWrongPassword = (TextInputLayout) findViewById(R.id.hintWrongPassword);
        edConfirmPassword = (EditText) findViewById(R.id.confirmNewPassword);
        rowWrongpassword = (TableRow) findViewById(R.id.lineWrongpassword);
        tvMessageWrongPassword = (TextView) findViewById(R.id.messageWrongPassword);
        btnNext = (Button) findViewById(R.id.btnSetPasswordNext);
        if (!edNewPassword.getText().toString().isEmpty() && !edConfirmPassword.getText().toString().isEmpty()) {
            btnNext.setEnabled(true);
            btnNext.setBackground(getResources().getDrawable(R.drawable.ncp_button_back));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
        }
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if (type != null) {
            if (type.equalsIgnoreCase("set")) {
                btnNext.setText("Submit");
            } else if (type.equalsIgnoreCase("forget")) {
                btnNext.setText("Reset Password");
            }
        }
        initializeListeners();
    }

    private void initializeListeners() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPassword();
            }
        });
        edNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edNewPassword.getText().toString().isEmpty() && !edConfirmPassword.getText().toString().isEmpty()) {
                    btnNext.setEnabled(true);
                    btnNext.setBackground(getResources().getDrawable(R.drawable.ncp_button_back));
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edNewPassword.getText().toString().isEmpty() && !edConfirmPassword.getText().toString().isEmpty()) {
                    btnNext.setEnabled(true);
                    btnNext.setBackground(getResources().getDrawable(R.drawable.ncp_button_back));
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void confirmPassword() {

        if (!validate()) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String login = sharedPreferences.getString(Constants.LOGIN, "");
        LoginParam param = new LoginParam();
        param.setLogin(login);
        if (type.equalsIgnoreCase("set")) {
            param.setStatus("1");
        } else {
            param.setStatus("2");
        }
        param.setPassword(edConfirmPassword.getText().toString());
        LoginUser user = new LoginUser();
        user.setLoginParam(param);
        user.setJsonRpc(Constants.JSON_RPC);
        JSONObject baseJson = null;
        try {
            baseJson = new JSONObject(new Gson().toJson(user));
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e(TAG, "excetion : " + e);
        }
//        Log.e(TAG, "model data : " + baseJson.toString());
        ProgressDialog dialog = new ProgressDialog(this, R.style.dialog_background);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait...");
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + "web/nConnect/onboard/set_password",
                baseJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.e(TAG, "response : " + response.toString());
                try {
                    JSONObject resonseJSon = new JSONObject(response.toString());
                    JSONObject resultJson = resonseJSon.getJSONObject(Constants.RESULT);
                    String responseStr = resultJson.getString(Constants.RESPONSE);
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        if (type.equalsIgnoreCase("set")) {
                            dialog.dismiss();
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            EnterDetailsFragment newFragment = EnterDetailsFragment.newInstance();
                            newFragment.show(transaction, "ENTER_DETAILS");
                            new AsynkLogin(NcpSetPassword.this, edConfirmPassword.getText().toString()).execute();
                        } else {
                            dialog.dismiss();
                            startActivity(new Intent(NcpSetPassword.this, NcpLoginScreen.class));
                            finish();
                        }
                    } else {
                        dialog.dismiss();
                        Toast.makeText(NcpSetPassword.this, "Cannot set password please try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
//                    Log.e(TAG, "exception : " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "error : " + error);
                dialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.COOKIE, Constants.SESSION_ID + "=" + sharedPreferences.getString(Constants.SESSION_ID, ""));
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private boolean validate() {

        if (!edNewPassword.getText().toString().equalsIgnoreCase(edConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
