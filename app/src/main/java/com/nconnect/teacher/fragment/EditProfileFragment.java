package com.nconnect.teacher.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.ProfileActivity;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Vals;
import com.nconnect.teacher.model.attendance.Data;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class EditProfileFragment extends DialogFragment {

    private static final String TAG = EditProfileFragment.class.getSimpleName();
    private Toolbar toolbar;
    private TextView tvToolBarTitle;
    private TextView tvCancelEditing;
    private Button btnSubmitEditing;
    private EditText edAbout, edEmail, edPhone, edAddress, eddob;
    private View progressDialog;
    private com.nconnect.teacher.model.attendance.Data data;
    private String profileImage = "";

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        Bundle bundle = getArguments();
        String editableData = bundle.getString("editable_profile");
        data = new Gson().fromJson(editableData, Data.class);
        eddob = (EditText) view.findViewById(R.id.edit_dob);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tvToolBarTitle = (TextView) view.findViewById(R.id.toolbarTitle);
        tvCancelEditing = (TextView) view.findViewById(R.id.cancelEditprofile);
        btnSubmitEditing = (Button) view.findViewById(R.id.submitProfileEdit);
        edAbout = (EditText) view.findViewById(R.id.edit_about);
        edAddress = (EditText) view.findViewById(R.id.edit_address);
        edEmail = (EditText) view.findViewById(R.id.edit_email);
        edPhone = (EditText) view.findViewById(R.id.edit_phone);

        progressDialog = view.findViewById(R.id.progress_layout);
        initData();
        initializeListeners();
    }

    private void initData() {
        edAbout.setText(data.getAbout());
        edAddress.setText(data.getAddress());
        eddob.setText(data.getDob());
        edEmail.setText(data.getWorkEmail());
        edPhone.setText(data.getMobilePhone());
        profileImage = data.getDp();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreen_dialog_theme);
    }

    private void initializeListeners() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnSubmitEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        tvCancelEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext(), R.style.PopupMenu);
                builder.setMessage("Are you sure you want to update your changes");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        updateProfile();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        eddob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                int mHour = c.get(Calendar.HOUR_OF_DAY);//current hour
                int mMinute = c.get(Calendar.MINUTE);//current minute
                // fromDate picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                int age = Integer.parseInt(Utils.getAge(year, monthOfYear, dayOfMonth));
                                if (age >= 18) {
                                    monthOfYear = monthOfYear + 1;
                                    String month = "";
                                    String day = "";
                                    if (monthOfYear < 10) {
                                        month = String.valueOf(monthOfYear);
                                        month = "0" + month;
                                    } else {
                                        month = String.valueOf(monthOfYear);
                                    }
                                    if (dayOfMonth < 10) {
                                        day = String.valueOf(dayOfMonth);
                                        day = "0" + day;
                                    } else {
                                        day = String.valueOf(dayOfMonth);
                                    }
                                    String date = year + "-" + month + "-" + day;
//                                    Log.e(TAG, date);
                                    eddob.setText("" + date);
                                } else {
                                    Toast.makeText(getContext(), "The age has to be above 18", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            }
        });
    }

    private void updateProfile() {
        Vals vals = new Vals();
        vals.setAbout(edAbout.getText().toString());
        vals.setMobilePhone(edPhone.getText().toString());
        vals.setWorkEmail(edEmail.getText().toString());
        vals.setAddress(edAddress.getText().toString());
        vals.setBirthday(eddob.getText().toString());
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
        TextView progressText = progressDialog.findViewById(R.id.progressText);
        progressText.setText("Validating User please wait...");
        progressDialog.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + "web/nConnect/teacher/update/profile",
                json, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject resonseJSon = null;
                progressDialog.setVisibility(View.GONE);
//                Log.e(TAG, "response : " + response.toString());
                try {
                    resonseJSon = new JSONObject(response.toString());
                    JSONObject resultJson = resonseJSon.getJSONObject(Constants.RESULT);
                    String responseStr = resultJson.getString(Constants.RESPONSE);
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        Toast.makeText(getContext(), "Your profile updated succesfully", Toast.LENGTH_SHORT).show();
                        ((ProfileActivity) getActivity()).initdata();
                        getActivity().getSupportFragmentManager().beginTransaction().detach(EditProfileFragment.this).commit();
                    } else {
                        Toast.makeText(getContext(), "Cannot update profile Details", Toast.LENGTH_SHORT).show();
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
                progressDialog.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
                headers.put(Constants.COOKIE, Constants.SESSION_ID + "=" + sharedPreferences.getString(Constants.SESSION_ID, ""));
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

}
