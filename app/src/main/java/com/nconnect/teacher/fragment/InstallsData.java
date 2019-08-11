package com.nconnect.teacher.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.Data;
import com.nconnect.teacher.model.Parent;
import com.nconnect.teacher.model.Teacher;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class InstallsData extends Fragment {

    private static final String TAG = InstallsData.class.getSimpleName();
    private ImageView ivDownloadNonInstalledParent, ivDownlaodNonInstalledTeacher;
    private TextView tvActiveparentInstalledCount, tvActiveParentINstalledPercentage;
    private TextView tvActiveTeacherInstalledCount, tvActiveTeacherInstalledPercetage;
    private boolean writtenToDisk = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_installs_data, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        ivDownlaodNonInstalledTeacher = (ImageView) view.findViewById(R.id.downloadNonInstalledTeachersXlsx);
        ivDownloadNonInstalledParent = (ImageView) view.findViewById(R.id.downloadNonInstalledPArentXlsx);
        tvActiveparentInstalledCount = (TextView) view.findViewById(R.id.activeparentInstalledCount);
        tvActiveParentINstalledPercentage = (TextView) view.findViewById(R.id.activeparentsInstalledPercentage);
        tvActiveTeacherInstalledCount = (TextView) view.findViewById(R.id.activeTeacherInstalledCount);
        tvActiveTeacherInstalledPercetage = (TextView) view.findViewById(R.id.activeTeachersInstalledPercentage);
        getInstallsData();
    }

    private void getInstallsData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        JSONObject jsonObject = new JSONObject();
        JSONObject param = new JSONObject();
        try {
            jsonObject.put("param", param);
            jsonObject.put(Constants.JSON_RPC_, Constants.JSON_RPC);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + "web/nConnect/install-data", jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
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
                        Parent parent = data.getParent();
                        Teacher teacher = data.getTeacher();
                        tvActiveparentInstalledCount.setText("" + parent.getNumbers());
                        tvActiveParentINstalledPercentage.setText("(" + parent.getPercentage() + ")");
                        tvActiveTeacherInstalledCount.setText("" + teacher.getNumbers());
                        tvActiveTeacherInstalledPercetage.setText("(" + teacher.getPercentage() + ")");
                        initDownloadXlsx(parent.getXlNonInstall(), teacher.getXlNonInstall());
                    } else {
                        Toast.makeText(getContext(), "Cannot get Usage data ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
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


    private void initDownloadXlsx(String xlNonInstallParent, String xlNonInstallTeacher) {
        ivDownloadNonInstalledParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getXls(xlNonInstallParent, "Non_installed_parent");
            }
        });
        ivDownlaodNonInstalledTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getXls(xlNonInstallTeacher,"Non_installed_teacher");
            }
        });
    }

    public void getXls(String url, String fileName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String session = sharedPreferences.getString(Constants.SESSION_ID, "");
        session = Constants.SESSION_ID + "=" + session;

        ProgressDialog dialog = new ProgressDialog(getContext(), R.style.dialog_background);
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
                            writtenToDisk = writeResponseBodyToDisk(response.body(), fileName);
//                            Log.e(TAG, "written ro disk : " + writtenToDisk);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "File downloaded", Toast.LENGTH_SHORT).show();
                        }
                    }.execute();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Can't download please try again later ", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "error : " + t);
                dialog.dismiss();
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            File downloaded_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName + ".xls");
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
                Toast.makeText(getContext(), "Can't download please try again later ", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Can't download please try again later ", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "Exception : " + e);
            return false;
        }
    }
}

