package com.nconnect.teacher.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.activity.PostStoryActivity;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.S3Upload;
import com.nconnect.teacher.model.Vals;
import com.nconnect.teacher.model.events.Events;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.stories.Class;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PostEvents extends Fragment implements DialogVpClasses.VpClassesListener {
    private String fromDate = "", toDate = "", fromTime = "", toTime = "";
    private static final String TAG = "Post Event";
    private EditText edTitle, edDesc, edAddress, edLandmark;
    private Button btnPostEvent;
    private LinearLayout containerFromDate, containerToDate;
    private TextView tvFromDate, tvToDate, tvUploadImage, tvImageName, tvImageSize;
    private TextView toolbarTitle;
    private ImageView toolBarLogo;
    private BottomNavigationView navigationView;
    private Toolbar toolbar;
    private TextView tvSelectStandards;
    private String base64String;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICKFILE_RESULT_CODE = 200;
    private Uri selectedImage;
    private Bitmap bitmap;
    String imageName;
    private ImageView ivSelectStandard;
    private String sessionIdValue;
    RelativeLayout containerVpClasses;
    private List<Class> classList = new ArrayList<>();
    private TextView tvTextPublish;
    private LinearLayout containerSchoolBoard;
    private String postTo = "";
    private long fromTimeMillis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_events, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();
        tvTextPublish = (TextView) view.findViewById(R.id.textPublish);
        containerVpClasses = (RelativeLayout) view.findViewById(R.id.containerPusblishClasses);
        ivSelectStandard = (ImageView) view.findViewById(R.id.arrowSelectstandards);
        edDesc = (EditText) view.findViewById(R.id.post_events_description);
        edTitle = (EditText) view.findViewById(R.id.post_events_title);
        btnPostEvent = (Button) view.findViewById(R.id.post_events_submit);
        containerFromDate = (LinearLayout) view.findViewById(R.id.postEventFromDateContainer);
        containerToDate = (LinearLayout) view.findViewById(R.id.postEventToDateContainer);
        edAddress = (EditText) view.findViewById(R.id.post_events_address);
        tvFromDate = (TextView) view.findViewById(R.id.fromDateText);
        tvToDate = (TextView) view.findViewById(R.id.toDateText);
        tvUploadImage = (TextView) view.findViewById(R.id.post_events_uploadimage);
        tvSelectStandards = (TextView) view.findViewById(R.id.select_standards);
        tvImageName = (TextView) view.findViewById(R.id.post_events_image_name);
        tvImageSize = (TextView) view.findViewById(R.id.post_events_image_size);
        edLandmark = (EditText) view.findViewById(R.id.post_events_landmork);
        navigationView = ((Dashboard) getActivity()).getNavigationView();
        toolBarLogo = ((Dashboard) getActivity()).getToolBarLogo();
        toolbarTitle = ((Dashboard) getActivity()).getToolbarTitle();
        toolbar = ((Dashboard) getActivity()).getToolbar();
        /*if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }*/
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String loginType = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        containerVpClasses.setVisibility(View.GONE);
        btnPostEvent.setVisibility(View.GONE);
        if (containerSchoolBoard.getVisibility() == View.VISIBLE) {
            containerSchoolBoard.setVisibility(View.GONE);
        }
        requestMultiplePermissions();
        initializeListeners();
        checkFieldsOrEmpty();
    }


    private void requestMultiplePermissions() {
        Dexter.withActivity(getActivity())
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
                            Toast.makeText(getActivity(), "Please accept this permission to use this application", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public void checkFieldsOrEmpty() {
        if (!edTitle.getText().toString().isEmpty()
                && !edDesc.getText().toString().isEmpty()
                && !edAddress.getText().toString().isEmpty()
                && !edLandmark.getText().toString().isEmpty()
                && tvFromDate.getText().toString().length() > 0
                && tvToDate.getText().toString().length() > 0) {
            btnPostEvent.setEnabled(true);
            btnPostEvent.setBackground(getResources().getDrawable(R.drawable.ncp_button_back));
        } else {
            btnPostEvent.setEnabled(false);
            btnPostEvent.setBackground(getResources().getDrawable(R.drawable.ncp_button_back_default));
        }
    }

    private void initializeListeners() {
        edDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkFieldsOrEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkFieldsOrEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edLandmark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkFieldsOrEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvImageName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkFieldsOrEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnPostEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postEventWithClasses();
            }
        });
        tvFromDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkFieldsOrEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvToDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkFieldsOrEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        containerFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
//                c.add(Calendar.DAY_OF_YEAR, 1);
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                // fromDate picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String month = "";
                        String day = "";
//                                tvFromDate.setText(Constants.MONTHS[monthOfYear] + " " + dayOfMonth + "," + year);
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
                        fromDate = year + "-" + month + "-" + day;
//                        Log.e(TAG, fromDate);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @SuppressLint("DefaultLocale")
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                       /* Calendar datetime = Calendar.getInstance();
                                        Calendar c = Calendar.getInstance();
                                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        datetime.set(Calendar.MINUTE, minute);
                                        if (datetime.getTimeInMillis() > c.getTimeInMillis()) {
                                            Log.e(TAG, "selected time greater : " + getTime(datetime.getTimeInMillis()));
                                        } else {
                                            Log.e(TAG, "selected time lessthan: " + getTime(datetime.getTimeInMillis()));
                                        }
                                        String _12HourTime = new SimpleDateFormat("hh:mm a").format(new Date());
                                        Log.e(TAG, "time : " + _12HourTime);
                                        Log.e(TAG, "one " + "hour of day : " + hourOfDay + "\n" + "hour of minute : " + minute);
                                        Log.e(TAG, "two " + "hour of day : " + mHour + "\n" + "hour of minute : " + mMinute);*/
                                        if (dayOfMonth == mDay) {
                                            /*Log.e(TAG, "one " + "hour of day : " + hourOfDay + "\n" + "hour of minute : " + minute);
                                            Log.e(TAG, "two " + "hour of day : " + mHour + "\n" + "hour of minute : " + mMinute);
                                            if (hourOfDay >= mHour && minute >= mMinute) {
                                                int hour = hourOfDay % 12;
                                                fromTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hourOfDay < 12 ? "am" : "pm");
                                                fromDate = fromDate + " " + fromTime;
                                                tvFromDate.setText(fromDate);
                                                if (!dateValidation()) {
                                                    return;
                                                }
                                            } else {
                                                tvFromDate.setText("");
                                                Toast.makeText(getContext(), "Invalid time", Toast.LENGTH_SHORT).show();
                                            }*/
                                            int hour = hourOfDay % 12;
                                            fromTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hourOfDay < 12 ? "am" : "pm");
                                            fromDate = fromDate + " " + fromTime;
                                            String _12HourTime = new SimpleDateFormat("yy-MM-dd hh:mm a").format(new Date());
                                            boolean isValidTime = validateDate(_12HourTime, fromDate);
                                            if (isValidTime) {
                                                tvFromDate.setText(fromDate);
                                            } else {
                                                Toast.makeText(getContext(), "Please enter valid Time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            int hour = hourOfDay % 12;
                                            fromTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hourOfDay < 12 ? "am" : "pm");
                                            fromDate = fromDate + " " + fromTime;
                                            tvFromDate.setText(fromDate);
                                            if (!dateValidation()) {
                                                return;
                                            }
                                        }

                                    }
                                }, mHour, mMinute, false);
                                timePickerDialog.show();
                                timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                                timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                            }
                        }, 50);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            }
        });
        containerToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
//                c.add(Calendar.DAY_OF_YEAR, 1);
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                // fromDate picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = monthOfYear + 1;
                                String month = "";
                                String day = "";
//                                tvToDate.setText(Constants.MONTHS[monthOfYear] + " " + dayOfMonth + "," + year);
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
                                toDate = year + "-" + month + "-" + day;
//                                Log.e(TAG, toDate);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                            @SuppressLint("DefaultLocale")
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                if (dayOfMonth == mDay) {
                                                   /* if (hourOfDay >= mHour && minute >= mMinute) {
                                                        int hour = hourOfDay % 12;
                                                        toTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hourOfDay < 12 ? "am" : "pm");
                                                        toDate = toDate + " " + toTime;
                                                        tvToDate.setText(toDate);
                                                        if (!dateValidation()) {
                                                            return;
                                                        }
                                                    } else {
                                                        tvToDate.setText("");
                                                        Toast.makeText(getContext(), "Please enter valid time", Toast.LENGTH_SHORT).show();
                                                    }*/
                                                    int hour = hourOfDay % 12;
                                                    toTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hourOfDay < 12 ? "am" : "pm");
                                                    toDate = toDate + " " + toTime;
                                                    boolean isValidTime = validateDate(fromDate, toDate);
                                                    if (isValidTime) {
                                                        tvToDate.setText(toDate);
                                                    } else {
                                                        Toast.makeText(getContext(), "Please enter valid Date and Time", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    int hour = hourOfDay % 12;
                                                    toTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hourOfDay < 12 ? "am" : "pm");
                                                    toDate = toDate + " " + toTime;
                                                    tvToDate.setText(toDate);
                                                    if (!dateValidation()) {
                                                        return;
                                                    }
                                                }

                                            }
                                        }, mHour, mMinute, false);
                                        timePickerDialog.show();
                                        timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                                        timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                                    }
                                }, 50);
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            }
        });
        tvUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerSchoolBoard.getVisibility() == View.VISIBLE) {
                    containerSchoolBoard.setVisibility(View.GONE);
                }
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(galleryIntent, PICKFILE_RESULT_CODE);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        tvSelectStandards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectStandards.setVisibility(View.GONE);
                containerVpClasses.setVisibility(View.VISIBLE);
                selectStandard();
            }
        });
        ivSelectStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStandard();
            }
        });
    }

    public String getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
//        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        //"E MMMM dd,yyyy"
        String formattedDate = DateFormat.format("dd-MM-yyyy hh:mm a", cal).toString();
        return formattedDate;
    }

    private void postEventWithClasses() {
        if (!dateValidation()) {
            return;
        }

        if (classList.isEmpty() && postTo.isEmpty()) {
            Toast.makeText(getContext(), "Please select class and section", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String loginType = sharedPreferences.getString(Constants.LOGIN_TYPE, "");
        Vals vals = new Vals();
        vals.setClasses(classList);
        if (classList.isEmpty()) {
            vals.setClasses(classList);
            vals.setPostTo("all");
        } else {
            vals.setClasses(classList);
            vals.setPostTo("");
        }
        vals.setName(edTitle.getText().toString());
        vals.setDescription(edDesc.getText().toString());
        vals.setAddress(edAddress.getText().toString());
        vals.setDatefrom(fromDate);
        vals.setDateTo(toDate);
        vals.setDp(base64String);
        vals.setLocation(edLandmark.getText().toString());
        Params params = new Params();
        params.setVals(vals);
        Events events = new Events();
        events.setJsonrpc(Constants.JSON_RPC);
        events.setParams(params);
        String sessionId = sharedPreferences.getString("session_id", "");
        if (sessionId != "") {
            sessionIdValue = "session_id" + "=" + sessionId;
        }
//        Log.e(TAG, "response : model :" + new Gson().toJson(events));
        ProgressDialog dialog = Utils.showDialog(getContext(), "Please wait while creating event...");
        dialog.show();
        (Utils.httpService(getContext()).postEventByVp(events, sessionIdValue)).enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {

//                Log.e(TAG, "resposne  : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Event Created Successfully", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Cannot post Event", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    dialog.dismiss();
//                    Log.e(TAG, "exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {
//                Log.e(TAG, "Failure : " + t);
                dialog.dismiss();
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Utils.showToastCustom(getContext(), "Please check your network connection");
                }
            }
        });
    }

    private boolean validateDate(String date1, String date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd hh:mm a");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = simpleDateFormat.parse(date1);
            d2 = simpleDateFormat.parse(date2);
            if (d1.after(d2)) {
                return false;
            }
        } catch (ParseException parse) {
//            Log.e(TAG, " Date Parse Exception -" + parse);
            Toast.makeText(getContext(), "Please enter valid Date and Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void selectStandard() {
        DialogVpClasses classes = DialogVpClasses.newInstance();
        classes.setTargetFragment(PostEvents.this, 300);
        classes.show(getActivity().getSupportFragmentManager(), "show dialog");
    }

    private boolean dateValidation() {
        if (fromDate.equalsIgnoreCase(toDate)) {
            Toast.makeText(getContext(), "Please enter a valid date and time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both from date and to date", Toast.LENGTH_SHORT).show();
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd hh:mm a");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = simpleDateFormat.parse(fromDate);
            d2 = simpleDateFormat.parse(toDate);
            if (d1.after(d2)) {
                Toast.makeText(getContext(), "Please enter valid Date and Time", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException parse) {
//            Log.e(TAG, " Date Parse Exception -" + parse);
            Toast.makeText(getContext(), "Please enter valid Date and Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (containerSchoolBoard.getVisibility() == View.VISIBLE) {
            containerSchoolBoard.setVisibility(View.GONE);
        }
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                byte[] imageBytes = getBytes(inputStream);
                uploadProfileImage(imageBytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
//                Log.e(TAG, "exception : " + e);
            } catch (IOException e) {
                e.printStackTrace();
//                Log.e(TAG, "Exception :  " + e);
            }
        }
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();
        int buffSize = 1024;
        byte[] buff = new byte[buffSize];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }
        return byteBuff.toByteArray();
    }

    @Override
    public void getVpClasses(List<Grade> grades, String postTo) {

        String text = "";
        classList.clear();
        this.postTo = postTo;

        if (classList.isEmpty() && postTo.isEmpty()) {
            btnPostEvent.setVisibility(View.GONE);
        }

        if (postTo.equalsIgnoreCase("true")) {
            tvTextPublish.setText("Post to All Parents");
            btnPostEvent.setVisibility(View.VISIBLE);
        } else {
            btnPostEvent.setVisibility(View.VISIBLE);
            for (int i = 0; i < grades.size(); i++) {
                text = text + grades.get(i).getName() + ",";
                Class classes = new Class();
                classes.setGradeId(grades.get(i).getId());
                classList.add(classes);
            }
            text = "grades : " + text;
            tvTextPublish.setText(text);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (containerSchoolBoard.getVisibility() == View.VISIBLE) {
            containerSchoolBoard.setVisibility(View.GONE);
        }
    }


    public void uploadProfileImage(byte[] image) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
        String filename = String.valueOf(System.currentTimeMillis());
        MultipartBody.Part body = MultipartBody.Part.createFormData("file_", filename + ".jpg", requestFile);
        ProgressDialog dialog = Utils.showProgress(getContext(), "Please wait...");
        dialog.setTitle("Please wait ...");
        dialog.show();
        (Utils.httpService(getContext()).uploadS3File(body)).enqueue(new Callback<S3Upload>() {
            @Override
            public void onResponse(Call<S3Upload> call, Response<S3Upload> response) {
//                Log.e(TAG, "response : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getMessage().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
                        dialog.dismiss();
                        base64String = response.body().getUrl();
                        tvImageName.setText(base64String);
                        Toast.makeText(getContext(), "Image attached", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Cant upload image please try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    dialog.dismiss();
//                    Log.e(TAG, "Exception : " + e);
                }
            }

            @Override
            public void onFailure(Call<S3Upload> call, Throwable t) {
                dialog.dismiss();
//                Log.e(TAG, "Error : " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Toast.makeText(getContext(), "Please check your network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
