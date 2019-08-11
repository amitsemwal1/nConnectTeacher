package com.nconnect.teacher.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.NcpLoginScreen;
import com.nconnect.teacher.helper.VolleySingleton;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Vals;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class EnterDetailsFragment extends DialogFragment implements TextWatcher {

    private static final String TAG = EnterDetailsFragment.class.getSimpleName();
    private EditText edAbout, edPhone, edMail, edAddress, edDob;
    private Button btnNext;

    public static EnterDetailsFragment newInstance() {
        EnterDetailsFragment enterDetailsFragment = new EnterDetailsFragment();
        return enterDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_details, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        btnNext = (Button) view.findViewById(R.id.next);
        edAbout = (EditText) view.findViewById(R.id.about);
        edPhone = (EditText) view.findViewById(R.id.number);
        edMail = (EditText) view.findViewById(R.id.mail);
        edAddress = (EditText) view.findViewById(R.id.address);
        edDob = (EditText) view.findViewById(R.id.dob);
        edAbout.addTextChangedListener(this);
        edPhone.addTextChangedListener(this);
        edAddress.addTextChangedListener(this);
        edDob.addTextChangedListener(this);
        edMail.addTextChangedListener(this);
        if (!edMail.getText().toString().isEmpty()
                && !edDob.getText().toString().isEmpty()
                && !edAddress.getText().toString().isEmpty()
                && !edDob.getText().toString().isEmpty()
                && !edPhone.getText().toString().isEmpty()
                && !edAbout.getText().toString().isEmpty()) {
            btnNext.setEnabled(true);
            btnNext.setBackground(getResources().getDrawable(R.drawable.ncp_button_back));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
        }
        edDob.setOnClickListener(new View.OnClickListener() {
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
                                    edDob.setText("" + date);
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
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        Vals vals = new Vals();
        vals.setAbout(edAbout.getText().toString());
        vals.setMobilePhone(edPhone.getText().toString());
        vals.setWorkEmail(edMail.getText().toString());
        vals.setAddress(edAddress.getText().toString());
        vals.setBirthday(edDob.getText().toString());
        Params params = new Params();
        params.setVals(vals);
        params.setStatus("2");
        Stories stories = new Stories();
        stories.setStoriesParams(params);
        JSONObject json = null;
        try {
            json = new JSONObject(new Gson().toJson(stories));
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        }

//        Log.e(TAG, "model data ; " + json);
        ProgressDialog dialog = new ProgressDialog(getContext(), R.style.dialog_background);
        dialog.setTitle("Updating profile...");
        dialog.setCancelable(false);
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.BASE_URL + "web/nConnect/teacher/update/profile",
                json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject resonseJSon = null;
//                Log.e(TAG, "response : " + response.toString());
                try {
                    resonseJSon = new JSONObject(response.toString());
                    JSONObject resultJson = resonseJSon.getJSONObject(Constants.RESULT);
                    String responseStr = resultJson.getString(Constants.RESPONSE);
                    if (responseStr.equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        Toast.makeText(getContext(), "Your profile updated succesfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        dismiss();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), NcpLoginScreen.class));
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Cannot update profile Details", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
//                    Log.e(TAG, "response : " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, " error " + error);
                dialog.dismiss();
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!edMail.getText().toString().isEmpty()
                && !edDob.getText().toString().isEmpty()
                && !edAddress.getText().toString().isEmpty()
                && !edDob.getText().toString().isEmpty()
                && !edPhone.getText().toString().isEmpty()
                && !edAbout.getText().toString().isEmpty()) {
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
}
