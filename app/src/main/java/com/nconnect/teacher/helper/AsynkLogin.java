package com.nconnect.teacher.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.login.Child;
import com.nconnect.teacher.model.login.LoginParam;
import com.nconnect.teacher.model.login.LoginUser;
import com.nconnect.teacher.util.Constants;

import static android.content.Context.MODE_PRIVATE;

public class AsynkLogin extends AsyncTask<Void, Void, Void> {
    private static final String TAG = AsynkLogin.class.getSimpleName();
    Context context = null;
    String password = "";

    @Override
    protected Void doInBackground(Void... voids) {
        login();
        return null;
    }

    public AsynkLogin(Context context, String password) {
        this.context = context;
        this.password = password;
    }

    private void login() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.FCM_PREF, MODE_PRIVATE);
        String token = sharedPreferences.getString("fcm_id_teacher", "");
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String login = loginPreference.getString(Constants.LOGIN, "");
        LoginParam loginParam = new LoginParam(login, password, token, Constants.LOGIN_TYPE_VALUE);
        LoginUser loginUser = new LoginUser(Constants.JSON_RPC, loginParam);
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(new Gson().toJson(loginUser));
//            Log.e(TAG, "model data  : " + jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e(TAG, "Excepttion : " + e);
        }

        JsonObjectRequest json = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + Constants.END_POINT_LOGIN, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.e(TAG, /"response : " + response.toString());
                        try {
                            JSONObject resonseJSon = new JSONObject(response.toString());
                            JSONObject resultJson = resonseJSon.getJSONObject(Constants.RESULT);
                            Result result = new Gson().fromJson(resultJson.toString(), Result.class);
                            if (result.getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                                SharedPreferences.Editor editor = loginPreference.edit();
                                editor.putInt(Constants.USER_TOKEN, result.getUserToken());
                                editor.putString(Constants.SESSION_ID, result.getSessionId());
                                editor.putString(Constants.NAME, result.getName());
                                editor.putString(Constants.LOGIN_TYPE, result.getLogin_type());
                                List<Child> children = result.getChildren();
                                String jsonStudentTokens = new Gson().toJson(children);
                                editor.putString(Constants.STUDENT_TOKENS_JSON, jsonStudentTokens);
                                editor.commit();
                            } else {
                                new AsynkLogin(context, password).execute();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            new AsynkLogin(context, password).execute();
//                            Log.e(TAG, "exceptio : " + e);
                        } catch (NullPointerException e) {
                            new AsynkLogin(context, password).execute();
//                            Log.e(TAG, " exception : " + e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "error : " + error);
                new AsynkLogin(context, password).execute();
            }
        });
        json.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(json);
    }
}
