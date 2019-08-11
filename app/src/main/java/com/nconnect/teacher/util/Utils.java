package com.nconnect.teacher.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jivesoftware.smack.packet.Presence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.nconnect.teacher.R;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.helper.MySpannable;
import com.nconnect.teacher.model.DashBoardModel;
import com.nconnect.teacher.model.Users;
import com.nconnect.teacher.services.SmackService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    private static final AtomicLong LAST_TIME_MS = new AtomicLong();
    private static final String TAG = Utils.class.getSimpleName();
//    private static Retrofit retrofit = null;

    public static RetrofitClient httpService(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(new ConnectivityInterceptor(context))
                .build();
//
//        if (retrofit == null) {
            Retrofit  retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
//        }

        RetrofitClient api = retrofit.create(RetrofitClient.class);
        return api;
    }


    public static RetrofitClient getClient() {
//        if (retrofit == null) {
        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
//        }
        RetrofitClient api = retrofit.create(RetrofitClient.class);
        return api;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast_container, null);
        ImageView icon = (ImageView) view.findViewById(R.id.Toasticon);
        TextView text = (TextView) view.findViewById(R.id.Toasttext);
        toast.getView().setPadding(20, 20, 20, 20);
        toast.getView().setPadding(20, 20, 20, 20);
        view.setBackgroundResource(R.drawable.info_toast_background);
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.info_vector_ico));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.info_toast_background));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.info_toast));
        }
        text.setText(message);
        toast.setView(view);
        toast.show();
    }

    public static ProgressDialog showDialog(Context context, String message) {
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
//        progressDialog.setProgressDrawable(context.getResources().getDrawable(R.drawable.ncp_spinner_circle));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }

    public static void showToastCustom(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast_container, null);
        ImageView icon = (ImageView) view.findViewById(R.id.Toasticon);
        TextView text = (TextView) view.findViewById(R.id.Toasttext);
        toast.getView().setPadding(20, 20, 20, 20);
        toast.getView().setPadding(20, 20, 20, 20);
        view.setBackgroundResource(R.drawable.info_toast_background);
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.info_vector_ico));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.info_toast_background));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.info_toast));
        }
        text.setText(message);
        toast.setView(view);
        toast.show();
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationView menuView = (BottomNavigationView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShifting(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
//            Log.e("TAG", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
//            Log.e("TAG", "Unable to change value of shift mode", e);
        }
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        makeTextViewResizable(tv, 5, "View More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        return extension;
    }

    public static String getImagePath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public static AlertDialog showProgress(Activity activity, String text) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.progress_layout, null);
        TextView textView = alertLayout.findViewById(R.id.progressText);
        textView.setText(text);
        AlertDialog.Builder alert = new AlertDialog.Builder(activity, android.R.style.Theme_NoTitleBar);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        AlertDialog dialog = alert.create();
        return dialog;
    }
    public static ProgressDialog showProgress(Context context, String progressText) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.full_screen_dialog) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.progress_layout);
                TextView tvProgressText = (TextView) findViewById(R.id.progressText);
                ProgressBar bar = (ProgressBar) findViewById(R.id.progressbar);
                tvProgressText.setText(progressText);
                getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            }
        };
        dialog.setCancelable(false);

        return dialog;
    }

    public static final View initProgress(Activity context, String progressMessage) {
        View progressDialog = context.findViewById(R.id.progress_layout);
        TextView progressText = progressDialog.findViewById(R.id.progressText);
        progressText.setText(progressMessage);
        return progressDialog;
    }

    public static void enableSound(int sound, MediaPlayer mp, Context context) {
        Float f = Float.valueOf(sound);
//        Log.e("checkingsounds", "&&&&&   " + f);
        mp.setVolume(f, f);
        mp.setLooping(true);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //Max Volume 15
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  //this will return current volume.
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sound, AudioManager.FLAG_PLAY_SOUND);   //here you can set custom volume.
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static Bitmap createThumbnailFromPath(String filePath, int type) {
        return ThumbnailUtils.createVideoThumbnail(filePath, type);
    }

//    private static final AtomicLong LAST_TIME_MS = new AtomicLong();

    public static long uniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();
        while (true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime + 1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }

    public static String getUniqueId() {
        UUID id = UUID.randomUUID();
        return id.toString();
    }

    public static String removeCommas(String s) {
        s = s.replaceAll("( )*$", "");
        System.out.println(s.replaceAll("(,)*$", ""));
        return s;
    }

    public static List<DashBoardModel> getPrincipalDashBoard() {
        List<DashBoardModel> models = new ArrayList<>();
        models.add(new DashBoardModel(Constants.DASH_ANALYTICAL, "Analytics Dashboard", R.drawable.ncp_analytical_dashboard));
        models.add(new DashBoardModel(Constants.DASH_STORIES, "Post Stories", R.drawable.ncp_latest_stories));
        models.add(new DashBoardModel(Constants.DASH_ATTENDANCE, "Student Attendance", R.drawable.ncp_events));
        models.add(new DashBoardModel(Constants.DASH_CHAT, "Virtual PTM", R.drawable.ncp_chat_with_teacher));
        models.add(new DashBoardModel(Constants.DASH_ISSUES, "Resolve Issues", R.drawable.ncp_resolve_issue));
        models.add(new DashBoardModel(Constants.DASH_EVENTS, "Post Events", R.drawable.ncp_post_event));
        models.add(new DashBoardModel(Constants.DASH_ANNOUNCEMENTS, "Post Announcement", R.drawable.ncp_announcements));
        return models;
    }

    public static List<DashBoardModel> getTeacherDashBoard() {
        List<DashBoardModel> models = new ArrayList<>();
        models.add(new DashBoardModel(Constants.DASH_STORIES, "Post Stories", R.drawable.ncp_latest_stories));
        models.add(new DashBoardModel(Constants.DASH_ATTENDANCE, "Student Attendance", R.drawable.ncp_events));
        models.add(new DashBoardModel(Constants.DASH_CHAT, "Virtual PTM", R.drawable.ncp_chat_with_teacher));
        models.add(new DashBoardModel(Constants.DASH_ISSUES, "Resolve Issues", R.drawable.ncp_resolve_issue));
        return models;
    }

   /* public static List<DashBoardModel> getVicePrincipalDashBoard() {
        List<DashBoardModel> models = new ArrayList<>();
        models.add(new DashBoardModel(Constants.DASH_STORIES, "Post Stories", R.drawable.ncp_latest_stories));
        models.add(new DashBoardModel(Constants.DASH_ATTENDANCE, "Student Attendance", R.drawable.ncp_events));
        models.add(new DashBoardModel(Constants.DASH_CHAT, "Chat With Parents", R.drawable.ncp_chat_with_teacher));
        models.add(new DashBoardModel(Constants.DASH_ISSUES, "Resolve Issues", R.drawable.ncp_resolve_issue));
        models.add(new DashBoardModel(Constants.DASH_EVENTS, "Post Events", R.drawable.ncp_post_event));
        models.add(new DashBoardModel(Constants.DASH_ANNOUNCEMENTS, "Post Announcement", R.drawable.ncp_announcements));
        return models;
    }
*/

    public static String getTimeStamp() {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return timestamp;
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
//        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        //"E MMMM dd,yyyy"
        String formattedDate = DateFormat.format("dd MMMM ,yyyy", cal).toString();
        return formattedDate;
    }


    public static String getCurrentTime() {
        Date today = Calendar.getInstance().getTime();
        return Constants.timeFormat.format(today);
    }

    public static String getCurrentDate() {
        Date today = Calendar.getInstance().getTime();
        return Constants.dateFormat.format(today);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

    public static Boolean getBooleanValue(String mValue) {

        Boolean mResult = false;

        if (mValue != null
                && (mValue.equalsIgnoreCase("true") || mValue
                .equalsIgnoreCase("1"))) {

            mResult = true;

        }
        return mResult;
    }

    //method to check online/offline
    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get the file name from the url
    public static String getFileNameFromUrl(String url) {

        String filename = "";
        //get the file name based on last occurence of "/"
        try {
            filename = url.substring(url.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
        } catch (Exception e) {
        }

        return filename;
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


    public static long getMillisWithDate(String date, String dateFormat) {
        long timeInMilliseconds = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMilliseconds;
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }


    public static void saveUsers(ArrayList<Users> list, String key, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<Users> getUsers(String key, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Users>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void clearUsers(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    /*
     * It replace null values to empty string
     */
    public static String replaceNull(String input) {

        if (input == null || input.equals("null") || input.equals("NULL") || input.equals("Null")) {
            return "";
        }
        return input == null ? "" : input;
    }

    public static String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }


    /*Method to convert UTC to local date/time*/
    public static String convertUtcToLocalTime(String myDate) {

        /*get system time*/

        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_HOURS_MINI_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_HOURS_MINI_FORMAT);
        dateFormatter.setTimeZone(TimeZone.getDefault());
        date = dateFormatter.format(value);

        return date;
    }

    /*Method to convert local to UTC date/time*/
    public static String convertLocalToUtcTime() {

        String lv_dateFormateInUTC = "";
        Calendar calendar = GregorianCalendar.getInstance();

        SimpleDateFormat lv_formatter = new SimpleDateFormat(Constants.DATE_HOURS_MINI_FORMAT);
        lv_formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        lv_dateFormateInUTC = lv_formatter.format(calendar.getTime());
        return lv_dateFormateInUTC;
    }

    public static String getFormatDate(String myDate, String fromFormat, String toFormat) {
        String value = "";
        try {
            /*
             * Converted date format
             * */
            SimpleDateFormat dateformat = new SimpleDateFormat(fromFormat);
            Date date = dateformat.parse(myDate);
            SimpleDateFormat sdf = new SimpleDateFormat(toFormat);
            value = sdf.format(date.getTime());

        } catch (Exception e) {
            Log.d("Date", "Error set Date", e);
        }
        return value;
    }

    //this method provides the url of drive
    public static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
//            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    //chat service action
    public static void chatServiceActions(Context context){
        try{

            context.stopService(new Intent(context, SmackService.class));

            Intent s_intent = new Intent(context, SmackService.class);
            context.startService(s_intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static int retrieveState_mode(Presence.Mode userMode, boolean isOnline) {
        int userState = 0;
        /** 0 for offline, 1 for online, 2 for away,3 for busy*/
        if (userMode == Presence.Mode.available) {
            userState = 1;
        }else if(userMode == Presence.Mode.dnd) {
            userState = 4;
        } else if (userMode == Presence.Mode.xa) {
            userState = 3;
        } else if (userMode == Presence.Mode.away) {
            userState = 2;
        }else if (isOnline) {
            userState = 1;
        }
        return userState;
    }

    //method to handle chat time for 4pm to 7pm
    public static boolean chatAccessTime(){

        Boolean chatAvailable = false;
        try{

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 16);// for 6 hour
            calendar.set(Calendar.MINUTE, 0);// for 0 min
            calendar.set(Calendar.SECOND, 0);// for 0 sec

            long startTimeMillis = calendar.getTimeInMillis();

            calendar.set(Calendar.HOUR_OF_DAY, 19);
            long endTimeMillis = calendar.getTimeInMillis();

            long currentTimeMillis = Calendar.getInstance().getTimeInMillis();

            if(startTimeMillis <= currentTimeMillis && currentTimeMillis <= endTimeMillis){
                chatAvailable = true;
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return chatAvailable;

    }

    public static String getDocDir() {
        return Environment.getExternalStorageDirectory() + File.separator + Constants.GALLERY_DIRECTORY_NAME + "/" + "documents" + "/";
    }

    public static String getImgDir() {
        return Environment.getExternalStorageDirectory() + File.separator + Constants.GALLERY_DIRECTORY_NAME + "/" + "images" + "/";
    }

    public static String getVidDir() {
        return Environment.getExternalStorageDirectory() + File.separator + Constants.GALLERY_DIRECTORY_NAME + "/" + "videos" + "/";
    }
}
