package com.nconnect.teacher.model;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.nconnect.teacher.model.stories.ClassSectionList;
import com.nconnect.teacher.util.Constants;




public class TeacherClassSectionCombinedApi extends AsyncTask<String, Void, String> {
    String TAG = "BannerServiceCall";
    int responseCode;
    HttpURLConnection conn;
    public Activity activity;
    private TeacherCombinedClassSectionCallback teacherCombinedClassSectionCallback;
    private String sessionId;
    private String responseMessage;
    private List<ClassSectionList> classSectionLists;


    public TeacherClassSectionCombinedApi(Activity getActivity, TeacherCombinedClassSectionCallback teacherCombinedClassSectionCallback,String sessionId) {
        this.activity = getActivity;
        this.teacherCombinedClassSectionCallback = teacherCombinedClassSectionCallback;
        this.sessionId = sessionId;
        classSectionLists = new ArrayList<>();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.i(TAG, "run in background");

        try {

            URL url = new URL(Constants.BASE_URL + "web/nConnect/teacherClasses/combined");
            URLConnection urlcon = url.openConnection();
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("jsonrpc", "2.0");
            postDataParams.put("params", "");
            conn = (HttpURLConnection) urlcon;
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Cookie",this.sessionId);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            conn.setConnectTimeout(30);
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postDataParams.toString());

            writer.flush();
            writer.close();
            os.close();

            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            Log.i(TAG, "Response Msge:" + responseMessage + " Response Code:" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                return new String("false : " + responseCode);
            }
        } catch (IOException e) {
            return new String("Exception: " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            return new String("Exception: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    @Override
    protected void onPostExecute(String result) {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try {
                Log.i(TAG, "Response Code: " + Integer.toString(responseCode));
                Log.i(TAG, "Response Message: " + result);
                JSONObject jsonObject = new JSONObject(result);
                Log.i(TAG, "Response Message: " + result);

                String result1 = jsonObject.getString("result");
                JSONObject resObj = new JSONObject(result1);
                String status = resObj.getString("status");
                if(status.equalsIgnoreCase("success")) {
                    String grades = resObj.getString("grades");

                    JSONArray gradesArray = new JSONArray(grades);

                    for (int i = 0; i < gradesArray.length(); i++) {
                        if (gradesArray.getJSONObject(i).length() != 0) {
                            JSONObject Jobject = gradesArray.getJSONObject(i);
                            ClassSectionList classSectionList = new ClassSectionList();
                            classSectionList.grade = Jobject.getString("grade");
                            classSectionList.section = Jobject.getString("section");
                            classSectionList.gradeID = Jobject.getString("grade_id");
                            classSectionList.sectionId = Jobject.getString("section_id");

                            classSectionLists.add(classSectionList);
                        }
                    }
                    teacherCombinedClassSectionCallback.teacherClassCombinedCallback(classSectionLists);
                }else{
                    teacherCombinedClassSectionCallback.teacherClassCombinedErrorCallback(responseCode,responseMessage );

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Log.i(TAG, "Response Code: " + Integer.toString(responseCode));
            Log.i(TAG, "Response Message:" + result);
        }
        conn.disconnect();
    }
}

