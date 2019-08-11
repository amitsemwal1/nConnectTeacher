package com.nconnect.teacher.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.ChatActivity;
import com.nconnect.teacher.activity.Dashboard;
import com.nconnect.teacher.adapter.ChatHistoryAdapter;
import com.nconnect.teacher.adapter.ParentsListAdapter;
import com.nconnect.teacher.database.Database;
import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.helper.MySingleton;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.model.chat.ChatMessage;
import com.nconnect.teacher.model.chat.Father;
import com.nconnect.teacher.model.chat.Mother;
import com.nconnect.teacher.model.chat.ParentRequest;
import com.nconnect.teacher.model.chat.Parents;
import com.nconnect.teacher.model.chat.ParentsRoot;
import com.nconnect.teacher.model.gradeandsection.Grade;
import com.nconnect.teacher.model.gradeandsection.Section;
import com.nconnect.teacher.model.gradeandsection.Student;
import com.nconnect.teacher.model.gradeandsection.StudentGradesAndSection;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment {

    public static String student_name;
    public static TextView infoTextview;
    ImageView newChat;
    ImageView backButton;
    int userToken = 0;
    SharedPreferences sharedPreferences;
    //    ListView studentListView;
    RecyclerView studentListView;
    ListView historyListView;
    TextView sendInviteButton;
    private TextView successSentInfoView;
    private Toolbar toolbar;
    private LinearLayout containerSchoolBoard;
    private LinearLayout emptyLayout;
    private LinearLayout selectionLayout;
    private Spinner spnClass, spnSection;
    private List<Grade> grades;
    private List<Section> sectionList;
    private int sectionId = 0, gradeId = 0;
    private List<Student> students;
    private LinearLayout inviteLayout;
    List<ChatMessage> chatHistory;
    String sender = "";
    private TextView titleView;
    public static View loadingDialog;
    boolean isSectionCalled = false;
    private List<Parents> parents_list;
    private int totalCount = 10;
    private int pageNumber = 1;
    ParentsListAdapter parentsListAdapter;
    LinearLayoutManager layoutManager;
    View rootView;

    public ChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        init(rootView);

        toolbar = ((Dashboard) getActivity()).getToolbar();
        toolbar.setVisibility(View.GONE);

        return rootView;
    }

    private void init(View rootView) {
        chatHistory = new ArrayList<>();
        containerSchoolBoard = ((Dashboard) getActivity()).getContainerSchoolBoard();

        titleView = rootView.findViewById(R.id.titleView);

        loadingDialog = rootView.findViewById(R.id.progress_layout);
        TextView progressText = rootView.findViewById(R.id.progressText);
        progressText.setText("Please wait . . .");
        loadingDialog.setClickable(false);

        backButton = rootView.findViewById(R.id.backButton);
        emptyLayout = rootView.findViewById(R.id.emptyLayout);
        spnClass = rootView.findViewById(R.id.classSpinner);
        spnSection = rootView.findViewById(R.id.sectionSpinner);

        newChat = rootView.findViewById(R.id.newChat);
        emptyLayout = rootView.findViewById(R.id.emptyLayout);
        selectionLayout = rootView.findViewById(R.id.selectionLayout);
        selectionLayout.setVisibility(View.GONE);
        studentListView = rootView.findViewById(R.id.studentListView);
        historyListView = rootView.findViewById(R.id.historyListView);
        inviteLayout = rootView.findViewById(R.id.inviteLayout);
        successSentInfoView = rootView.findViewById(R.id.successSentInfoView);
        sendInviteButton = rootView.findViewById(R.id.sendInviteButton);
        infoTextview = rootView.findViewById(R.id.infoTextview);
        TextView selectTextView = rootView.findViewById(R.id.selectTextView);
        selectTextView.setText("Please tap on the top right “\u2295” button to start new chat with parent");

//        history_list = new ArrayList<>();

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        userToken = sharedPreferences.getInt("user_token", 0);
        sender = sharedPreferences.getString(Constants.SENDER, "");

        initParentsList();
        initListener();
//        getHistoryData();
    }

    private void initParentsList() {
        parents_list = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        studentListView.setLayoutManager(layoutManager);
        studentListView.setItemAnimator(new DefaultItemAnimator());
        parentsListAdapter = new ParentsListAdapter(getActivity(), parents_list, selectionLayout, inviteLayout);
        studentListView.setAdapter(parentsListAdapter);
    }

    private void initListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.setVisibility(View.GONE);
                if (inviteLayout.getVisibility() == View.VISIBLE) {
                    selectionLayout.setVisibility(View.VISIBLE);
                    inviteLayout.setVisibility(View.GONE);
                    titleView.setText("Create With Parent");

                } else if (selectionLayout.getVisibility() == View.VISIBLE) {


                    selectionLayout.setVisibility(View.GONE);
                    titleView.setText("Create With Parent");
                    //method to get history data
//                    Log.e("HISTORY", "Back button history method called");
                    getHistoryData();

                    //historyListView.setVisibility(View.GONE);

                } else {
                    toolbar.setVisibility(View.VISIBLE);
                    getActivity().onBackPressed();
                }
            }
        });

        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newChat.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
                historyListView.setVisibility(View.GONE);
                selectionLayout.setVisibility(View.VISIBLE);
                titleView.setText("Create New Chat");
                loadingDialog.setVisibility(View.VISIBLE);
                parents_list.clear();
                parentsListAdapter.notifyDataSetChanged();
                pageNumber = 1;
                totalCount = 10;
                initParentsList();
                getParentData(pageNumber);
//                loadData();
            }
        });

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    if (chatHistory.size() > 0) {
                        ChatMessage chatMessage = chatHistory.get(i);
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.RECEIVER_NAME, chatMessage.getReceiver().toLowerCase());
                        editor.commit();
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("screen", "ChatFragment");
                        intent.putExtra("name", chatMessage.getReceiver_name());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                }
            }
        });

        spnClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (grades == null || grades.size() == 0) {
                    loadingDialog.setVisibility(View.GONE);
                    return;
                }

                for (int i = 0; i < grades.size(); i++) {
                    sectionList = grades.get(position).getSection();
                }
                parents_list.clear();
                parentsListAdapter.notifyDataSetChanged();
                pageNumber = 1;
                totalCount = 10;
                ArrayAdapter<Section> sectionArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_filter_item, sectionList);
                sectionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sectionArrayAdapter.notifyDataSetChanged();
                spnSection.setAdapter(sectionArrayAdapter);
                gradeId = grades.get(position).getId();
                isSectionCalled = false;
                loadingDialog.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (sectionList == null || sectionList.size() == 0) {
                    loadingDialog.setVisibility(View.GONE);
                    return;
                }
                sectionId = sectionList.get(position).getSectionId();
                if (!isSectionCalled) {
                    isSectionCalled = true;
                    //method to load parent list
//                    Log.e("TAG", "method call inside spinner");
                    getParentData(pageNumber);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        studentListView.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = layoutManager.getChildCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                /*Log.e("TAG", "scrolled");
                Log.e("TAG", "lsat in screen : " + lastInScreen);
                Log.e("TAG", "firstVisibleItemPosition : " + firstVisibleItemPosition);
                Log.e("TAG", "page number : " + pageNumber);
                Log.e("TAG", "total countt : " + totalCount);
                */
                if (lastInScreen == totalCount) {
                    //here the section to increase the page number to get next 20 items
                    totalCount += 10;
                    pageNumber += 1;
                    getParentData(pageNumber);
                }
            }
        });

        sendInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> hashMap = (HashMap<String, Object>) inviteLayout.getTag();
                String student_id = (String) hashMap.get("student_id");
                Object object = hashMap.get("object");

                String info = "Invite has been sent to the " + student_name + "’s";
                String who = "";
                if (object instanceof Father) {
                    Father father = (Father) object;
                    info += " father!";
                    who = "father";
                } else if (object instanceof Mother) {
                    Mother mother = (Mother) object;
                    info += " mother!";
                    who = "mother";
                }

                //method to send invite
                sendInvite(who, student_id, info);

                /*successSentInfoView.setVisibility(View.VISIBLE);
                successSentInfoView.setText(name);*/
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //method to load data
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();

        toolbar = ((Dashboard) getActivity()).getToolbar();
        toolbar.setVisibility(View.GONE);


        if (containerSchoolBoard.getVisibility() == View.VISIBLE) {
            containerSchoolBoard.setVisibility(View.GONE);
        }

        //method to get history data
//        Log.e("HISTORY","On resume history method called");
        getHistoryData();

        if (selectionLayout.getVisibility() == View.VISIBLE) {
            historyListView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    //method to load the data
    void loadData() {
        try {
            grades = new ArrayList<>();
            sectionList = new ArrayList<>();

            Params params = new Params();
            if (userToken != 0) {
                params.setUserToken(userToken);
            }
            StudentGradesAndSection gradesAndSection = new StudentGradesAndSection();
            gradesAndSection.setJsonrpc("2.0");
            gradesAndSection.setParams(params);
            String sessionIdValue = "";
            String sessionId = sharedPreferences.getString("session_id", "");
            if (sessionId != "") {
                sessionIdValue = "session_id" + "=" + sessionId;
            }
//            Log.e("TAG", "get classes method : " + new Gson().toJson(gradesAndSection));
            (Utils.httpService(getContext()).setGradeAndSection(gradesAndSection, sessionIdValue)).enqueue(new Callback<StudentGradesAndSection>() {
                @Override
                public void onResponse(Call<StudentGradesAndSection> call, Response<StudentGradesAndSection> response) {
                    try {
                        StudentGradesAndSection studentGradesAndSection = response.body();
//                        Log.e("TAG", "response : " + new Gson().toJson(studentGradesAndSection));
                        Result result = studentGradesAndSection.getResult();
                        if (result != null) {
                            if (result.getResponse().equalsIgnoreCase("success")) {
                                grades.clear();
                                grades = result.getGrade();
                                ArrayAdapter<Grade> adapter = new ArrayAdapter<Grade>(getContext(), R.layout.spinner_filter_item, grades);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnClass.setAdapter(adapter);

                            } else {
                                //Utils.showToast(getContext(), "Cannot get class and section please try again");
                            }
                        } else {
                            //Utils.showToast(getContext(), "Something went wrong please try again");
                        }
                    } catch (NullPointerException e) {
                    }
                }

                @Override
                public void onFailure(Call<StudentGradesAndSection> call, Throwable t) {
                    if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                        Utils.showToast(getContext(), "Please check your internet connection");
                    }
                }
            });

        } catch (Exception e) {
        }
    }

    //method to load parent list
    void getParentData(int page) {
//        Log.e("TAG", "" + page);
        try {
            if (page == 1) {
                loadingDialog.setVisibility(View.VISIBLE);
            } else {
                loadingDialog.setVisibility(View.GONE);
            }
            ParentRequest.Params params = new ParentRequest.Params(gradeId, sectionId, page);
            ParentRequest parentRequest = new ParentRequest("2.0", params);
            String sessionIdValue = "";
            String sessionId = sharedPreferences.getString("session_id", "");
            if (sessionId != "") {
                sessionIdValue = "session_id" + "=" + sessionId;
            }
//            Log.e("TAG", "json request payload : " + new Gson().toJson(parentRequest) + "\n" + "session : " + sessionIdValue);
            (Utils.httpService(getContext()).getParentList(parentRequest, sessionIdValue)).enqueue(new Callback<ParentsRoot>() {
                @Override
                public void onResponse(Call<ParentsRoot> call, Response<ParentsRoot> response) {
                    try {
                        ParentsRoot parentsRoot = response.body();
//                        Log.e("TAG", "response : " + new Gson().toJson(response.body()));
                        com.nconnect.teacher.model.chat.Result result = parentsRoot.getResult();
                        if (result.getResponse().equalsIgnoreCase("success")) {
                            loadingDialog.setVisibility(View.GONE);
                            List<Parents> parentsList = result.getParents();
//                            Log.e("Parents","parents list : "+new Gson().toJson(parentsList));
                            parents_list.addAll(parentsList);
                            if (!parents_list.isEmpty()) {
                                if (!parentsList.isEmpty()) {
                                    parentsListAdapter.notifyItemInserted(parents_list.size() - 1);
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        loadingDialog.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ParentsRoot> call, Throwable t) {
                    loadingDialog.setVisibility(View.GONE);
                    if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                        //Utils.showToast(getContext(), "Please check your intenet connection");
                    }
                }
            });

        } catch (Exception e) {
            loadingDialog.setVisibility(View.GONE);
        }
    }

    //method to get history data
    void getHistoryData() {
        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
            String receiverName = sharedPreferences.getString(Constants.RECEIVER_NAME, "");
//            Log.e("TAG", "receiver name : " + receiverName);
            String senderName = sharedPreferences.getString(Constants.SENDER, "");
            Database database = new Database(getActivity());
            SQLiteDatabase mydb = Database.getDataBase();
            //Cursor allrows = mydb.rawQuery("select * from "+database.HISTORYTABLENAME+" where "+Database.SENDER+" = '" + senderName + "'", null);
            Cursor allrows = mydb.rawQuery("select * from " + database.HISTORYTABLENAME + " where " + Database.SENDER + " = '" + senderName + "'", null);

            //Cursor allrows = mydb.rawQuery("SELECT * FROM " + database.HISTORYTABLENAME, null);
            //Cursor allrows = mydb.rawQuery("SELECT DISTINCT * FROM" + tablename + "WHERE"+ Database.ISONLINE=0";", null);

            List<ChatMessage> history_list = new ArrayList<>();
            ArrayList<String> tempList = new ArrayList<>();
            if (allrows.moveToFirst()) {
                do {

                    String id = allrows.getString(allrows.getColumnIndex(Database.ID));
                    String message_sent = allrows.getString(allrows.getColumnIndex(Database.MESSAGESENT));
                    String sender = allrows.getString(allrows.getColumnIndex(Database.SENDER));
                    String receiver = allrows.getString(allrows.getColumnIndex(Database.RECEIVER));
                    String msg = allrows.getString(allrows.getColumnIndex(Database.MESSAGE));
                    String msgId = allrows.getString(allrows.getColumnIndex(Database.MESSAGE_ID));
                    String ismine = allrows.getString(allrows.getColumnIndex(Database.ISMINE));
                    String date = allrows.getString(allrows.getColumnIndex(Database.DATE));
                    String time = allrows.getString(allrows.getColumnIndex(Database.TIME));
                    String file_url = allrows.getString(allrows.getColumnIndex(Database.FILEURL));
                    String file_name = allrows.getString(allrows.getColumnIndex(Database.FILENAME));

                    //23 Apr 2019 10:20am
                    long timeMillis = Utils.getMillisWithDate(date, Constants.DATE_HOURS_MINI_FORMAT);

                    String receiver_name = "";
                    try {
                        receiver_name = allrows.getString(allrows.getColumnIndex(Database.RECEIVERNAME));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String sender_name = "";
                    try {
                        sender_name = allrows.getString(allrows.getColumnIndex(Database.SENDERNAME));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ChatMessage chatMessage = new ChatMessage(msgId, sender, receiver, msg, ismine, date, time,
                            Utils.getBooleanValue(message_sent), file_url, file_name, timeMillis);

                    chatMessage.setReceiver_name(receiver_name);
                    chatMessage.setSender_name(sender_name);
                    if (!tempList.contains(receiver)) {
                        tempList.add(receiver);
                        history_list.add(chatMessage);
                    }
                }

                while (allrows.moveToNext());
            }
            chatHistory.clear();
            for (int position = 0; position < history_list.size(); position++) {
                if (history_list.get(position).getReceiver().endsWith("F")
                        || history_list.get(position).getReceiver().endsWith("M")) {
                    ChatMessage chatMessage = history_list.get(position);
                    chatHistory.add(chatMessage);
                }
            }
            if (chatHistory.size() > 0) {
                emptyLayout.setVisibility(View.GONE);
                historyListView.setVisibility(View.VISIBLE);
                ChatHistoryAdapter chatHistoryAdapter = new ChatHistoryAdapter(getActivity(), chatHistory);
                historyListView.setAdapter(chatHistoryAdapter);
            } else {
                emptyLayout.setVisibility(View.VISIBLE);
                historyListView.setVisibility(View.GONE);
                newChat.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            emptyLayout.setVisibility(View.VISIBLE);
            historyListView.setVisibility(View.GONE);
            newChat.setVisibility(View.VISIBLE);
        }

    }

    //method to send invite
    void sendInvite(String who, String student_id, String info) {
        JSONObject jsonObject = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.STUDENT_ID, Integer.valueOf(student_id));
            params.put(Constants.INVITE, who);
            jsonObject.put(Constants.PARAMS, params);
        } catch (JSONException e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, Constants.SENDINVITE, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getJSONObject("result").get("message").equals("success")) {
                        successSentInfoView.setVisibility(View.VISIBLE);
                        successSentInfoView.setText(info);
                    }
                } catch (Exception e) {
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                String session = sharedPreferences.getString(Constants.SESSION_ID, "");
                headers.put("Cookie", Constants.SESSION_ID + "=" + session);
                return headers;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
}
