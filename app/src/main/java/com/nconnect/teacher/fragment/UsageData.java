package com.nconnect.teacher.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.Data;
import com.nconnect.teacher.model.Issue;
import com.nconnect.teacher.model.Parent;
import com.nconnect.teacher.model.Teacher;
import com.nconnect.teacher.model.stories.Story;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class UsageData extends Fragment {

    private static final String TAG = UsageData.class.getSimpleName();
    private TextView tvActiveParentsCount, tvActiveTeacherCount, tvActiveparentPercentage, tvActiveTEacherpercentage;
    private ImageView ivDownloadInactiveTeacherXlsx, ivDownloadInactiveParentXlsx;
    private TextView tvTotalStories, tvStoriesPerTeacher, tvLikePerStory;
    private TextView tvTotalIssues, tvEscalatedIssues, tvResolvedIssues;
    private TextView tvViewResolved, tvViewEscalated;
    boolean writtenToDisk = false;
    private String xlInactiveParent = "", xlInactiveTeacher = "";


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usage_data, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        tvActiveParentsCount = (TextView) view.findViewById(R.id.activeparentCount);
        tvActiveparentPercentage = (TextView) view.findViewById(R.id.activeparentsPercentage);
        tvActiveTeacherCount = (TextView) view.findViewById(R.id.activeTeacherCount);
        tvActiveTEacherpercentage = (TextView) view.findViewById(R.id.activeTeacherPercentage);
        ivDownloadInactiveTeacherXlsx = (ImageView) view.findViewById(R.id.downloadInactiveTeacherXlsx);
        tvTotalStories = (TextView) view.findViewById(R.id.totalStories);
        tvStoriesPerTeacher = (TextView) view.findViewById(R.id.storiesperTeacher);
        tvLikePerStory = (TextView) view.findViewById(R.id.likePerStory);
        tvTotalIssues = (TextView) view.findViewById(R.id.totalIssueRaised);
        tvEscalatedIssues = (TextView) view.findViewById(R.id.totalIssueEscalated);
        tvResolvedIssues = (TextView) view.findViewById(R.id.totalIssueResolved);
        ivDownloadInactiveParentXlsx = (ImageView) view.findViewById(R.id.downloadInactiveParentXlsx);
        tvViewEscalated = (TextView) view.findViewById(R.id.viewEscalated);
        tvViewResolved = (TextView) view.findViewById(R.id.viewResolve);
        getUsagedata();
        initListener();

    }

    private void initListener() {
        ivDownloadInactiveParentXlsx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getXls(xlInactiveTeacher, "Inactive_parent");
            }
        });
        ivDownloadInactiveTeacherXlsx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getXls(xlInactiveTeacher, "Inactive_teacher");
            }
        });
        tvViewEscalated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Dashboard.class);
                intent.putExtra("value", "escalated");
                startActivity(intent);
            }
        });
        tvViewResolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Dashboard.class);
                intent.putExtra("value", "resolved");
                startActivity(intent);
            }
        });
    }


    private void getUsagedata() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        JSONObject jsonObject = new JSONObject();
        JSONObject param = new JSONObject();
        try {
            jsonObject.put("param", param);
            jsonObject.put(Constants.JSON_RPC_, Constants.JSON_RPC);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + "web/nConnect/usage-data", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.e(TAG, "resposense m: " + response.toString());
                JSONObject resjson = null;
                try {
                    resjson = new JSONObject(response.toString());
                    JSONObject result = resjson.getJSONObject("result");
//                    Log.e(TAG, "result : " + result.toString());
                    String responseStr = result.getString("response");
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        Data data = new Gson().fromJson(result.getJSONObject("data").toString(), Data.class);
//                        Log.e(TAG, "Data : " + new Gson().toJson(data));
                        Story story = data.getStory();
                        Issue issue = data.getIssue();
                        Parent parent = data.getParent();
                        Teacher teacher = data.getTeacher();
//                        Log.e(TAG, "response : " + new Gson().toJson(data));
                        tvActiveParentsCount.setText("" + parent.getNumbers());
                        tvActiveparentPercentage.setText("(" + parent.getPercentage() + ")");
                        tvActiveTeacherCount.setText("" + teacher.getNumbers());
                        tvActiveparentPercentage.setText("(" + teacher.getPercentage() + ")");
                        tvTotalStories.setText("" + story.getTotal());
                        tvStoriesPerTeacher.setText("" + story.getPerTeacher());
                        tvLikePerStory.setText("" + story.getLikePerStory());
                        tvTotalIssues.setText("" + issue.getTotal());
                        tvEscalatedIssues.setText("" + issue.getEscalated());
                        tvResolvedIssues.setText("" + issue.getResolved());
                        xlInactiveParent = parent.getXlInactive();
                        xlInactiveTeacher = teacher.getXlInactive();
//                        Log.e(TAG, "" + xlInactiveTeacher + xlInactiveParent);
                    } else {
                        Toast.makeText(getContext(), "Cannot get Usage data ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "errorr : " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Cookie", "session_id=" + sessionId);
                return map;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    public void getXls(String url, String fileName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String session = sharedPreferences.getString(Constants.SESSION_ID, "");
        session = Constants.SESSION_ID + "=" + session;

        ProgressDialog dialog = new ProgressDialog(getContext(), R.style.dialog_background);
//
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();


        (Utils.httpService(getContext()).downloadFile(url, session)).enqueue(new Callback<ResponseBody>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                Log.e(TAG, "body : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            writtenToDisk = writeResponseBodyToDisk(response.body(), fileName, getContext());
//                            Log.e(TAG, "written ro disk : " + writtenToDisk);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (writtenToDisk) {
                                Toast.makeText(getActivity(), "File downloaded", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Can't download please try again later ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(getContext(), "Can't download please try again later ", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "error : " + t);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static boolean writeResponseBodyToDisk(ResponseBody body, String fileName, Context context) {
        try {
            File downloaded_file = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName + ".xls");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
//                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(downloaded_file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
//                    Log.e(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Toast.makeText(context, "Can't download please try again later ", Toast.LENGTH_SHORT).show();

                    }
                };
//                Log.e(TAG, "Exception : e" + e);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Toast.makeText(context, "Can't download please try again later ", Toast.LENGTH_SHORT).show();

                }
            };
//            Toast.makeText(getContext(), "Can't download please try again later ", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "Exception : " + e);
            return false;
        }
    }
}
