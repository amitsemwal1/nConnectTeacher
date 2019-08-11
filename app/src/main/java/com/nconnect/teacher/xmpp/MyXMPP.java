package com.nconnect.teacher.xmpp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.nconnect.teacher.OttoEvents.AndroidBusProvider;
import com.nconnect.teacher.OttoEvents.OfflineToOnlineEvent;
import com.nconnect.teacher.OttoEvents.RefreshListEvent;
import com.nconnect.teacher.activity.ChatActivity;
import com.nconnect.teacher.adapter.ChatAdapter;
import com.nconnect.teacher.database.Database;
import com.nconnect.teacher.helper.MySingleton;
import com.nconnect.teacher.listener.UpcomingChatListener;
import com.nconnect.teacher.model.chat.ChatMessage;
import com.nconnect.teacher.services.NetworkService;
import com.nconnect.teacher.services.SmackService;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import static android.content.Context.MODE_PRIVATE;

public class MyXMPP {
    private static final String TAG = MyXMPP.class.getSimpleName();
    public static boolean connected = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    public static XMPPTCPConnection connection;
    public static String loginUser;
    public static String passwordUser;
    public static MyXMPP instance = null;
    public static String msg_id = "";
    public static boolean instanceCreated = false;
    private boolean receiver_available = false;
    private static byte[] dataReceived;

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
//            Log.e(TAG, "Class not found Exception : " + ex);
        }
    }

    public boolean loggedin = false;
    public org.jivesoftware.smack.chat.Chat Mychat;
    Gson gson;
//    ChatService context;
    public static ChatManagerListenerImpl mChatManagerListener;
    UpcomingChatListener mMessageListener;
    DeliveryReceiptManager mDeliveryReceiptManager;
    long id;
    String tableName;
    private boolean chat_created = false;
    private String serverAddress;
    private XmppDeliveryReceiptManager mXmppDeliveryReceiptManager;
    private SmackService smackContext;
/*

    private MyXMPP(ChatService context, String serverAdress, String logiUser, String passwordser) {
        this.serverAddress = serverAdress;
        this.loginUser = logiUser;
        this.passwordUser = passwordser;
        this.context = context;
        init();

    }
*/


    public MyXMPP(SmackService smackContext, String serverAddress, String loginUser, String passwordUser) {
        this.serverAddress = serverAddress;
        this.loginUser = loginUser;
        this.passwordUser = passwordUser;
        this.smackContext = smackContext;
        init();
    }
/*

    public static MyXMPP getInstance(ChatService context, String server, String user, String pass) {
        if (instance == null) {
            instance = new MyXMPP(context, server, user, pass);
            instanceCreated = true;
        }
        return instance;
    }
*/

    public static MyXMPP getInstance() {
        return instance;
    }

    public static MyXMPP getSmackInstance(SmackService smackContext, String serverAddress, String loginUser, String passwordUser) {
        if (instance == null) {
            instance = new MyXMPP(smackContext, serverAddress, loginUser, passwordUser);
            instanceCreated = true;
        }
        return instance;
    }

    public static XMPPTCPConnection getConnection() {
        return connection;
    }

    public static void getArchievedMessage(String receiver_name, final ChatAdapter mChatAdapter) {
        MamManager mamManager = MamManager.getInstanceFor(connection);
        try {
            receiver_name = receiver_name.replace(" ", "_");
            Jid jid = JidCreate.from(receiver_name + "@openfire.pappaya.education");
            MamManager.MamQueryResult result = mamManager.mostRecentPage(jid, 200);
            ArrayList<ChatMessage> dataList = new ArrayList<>();
            if (result.forwardedMessages.size() >= 1) {
                for (int i = 0; i < result.forwardedMessages.size(); i++) {
                    Message message = (Message) result.forwardedMessages.get(i).getForwardedStanza();
                    try {
                        JSONObject jsonObject = new JSONObject(message.getBody());

                        String msgid = jsonObject.getString("msgid");
                        String body = jsonObject.getString("body");
                        String sender = jsonObject.getString("sender");
                        String receiver = jsonObject.getString("receiver");
                        String Date = jsonObject.getString("Date");
                        String Time = jsonObject.getString("Time");

                        String file_url = "";
                        if (jsonObject.has(Database.FILEURL)){
                            file_url = jsonObject.getString(Database.FILEURL);
                        }
                        String file_name = "";
                        if (jsonObject.has(Database.FILENAME)){
                            file_name = jsonObject.getString(Database.FILENAME);
                        }
                        String file_size = "";
                        if (jsonObject.has(Database.FILESIZE)){
                            file_size = jsonObject.getString(Database.FILESIZE);
                        }
                        String isMine = "false";
                        if (receiver_name.equalsIgnoreCase(receiver)) {
                            isMine = "true";
                        }

                        //Database.insertMessageToOffline(sender, msgid, sender, receiver, body, Util.getBooleanValue(isMine), Date, Time);

                        //clear existing items
                        if (i==0){
                            mChatAdapter.clear();
                        }
                        //23 Apr 2019 10:20am
                        //long timeMillis = Utils.getMillisWithDate(Date+" "+Time, Constants.date);
                        long timeMillis = Utils.getMillisWithDate(Date, Constants.DATE_HOURS_MINI_FORMAT);

                        if (timeMillis > 0){

                            ChatMessage chatMessage = new ChatMessage(msgid, sender, receiver, body, isMine, Date, Time, true, file_url, file_name, timeMillis);
                            chatMessage.setFile_size(file_size);
                            dataList.add(chatMessage);
                        }
                        //mChatAdapter.add(chatMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (dataList.size() > 0){

                        //Log.e("mam", "message received" + message.getBody());
                        Collections.sort(dataList, new Comparator<ChatMessage>(){
                            public int compare(ChatMessage obj1, ChatMessage obj2) {
                                // ## Ascending order
                                return Long.compare(obj1.getTimeMillis(), obj2.getTimeMillis());
                                // ## Descending order
                                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                            }
                        });

                        mChatAdapter.mChatList = dataList;
                    }

                }


            }

            //event to refresh the list adapter
            AndroidBusProvider.getInstance().post(new RefreshListEvent(true));

        } catch (XmppStringprepException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        }

    }

    public void init() {
        gson = new Gson();
        //Toast.makeText(smackContext, "This is MyXMPP context"+ smackContext.toString(), Toast.LENGTH_LONG).show();
        mMessageListener = new UpcomingChatListener(smackContext);
        mChatManagerListener = new ChatManagerListenerImpl();
        initialiseConnection();
    }

    public void initialiseConnection() {
        if (!Utils.replaceNull(this.loginUser).equalsIgnoreCase("")){
            new LongOperation().execute(this.loginUser, this.loginUser);
        }
    }

    public static void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (connection != null)
                    connection.disconnect();
            }
        }).start();
    }

    public void connect(final String caller) {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                try{

                    if (connection.isConnected())
                        return false;
                    isconnecting = true;
                    if (isToasted)
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (NetworkService.isConnection) {
                                    //Toast.makeText(smackContext, caller + "=>connecting....", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
//                    Log.e(TAG, "Connect() Function " + caller + "=>connecting....");
                    try {
                        try {
                            connection.connect();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        connected = true;
                    } catch (IOException e) {
                        if (isToasted)
                            new Handler(Looper.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (NetworkService.isConnection) {
                                                //Toast.makeText(smackContext, "(" + caller + ")" + "IOException: ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

//                        Log.e(TAG, "(" + caller + ")" + "IOException: " + e.getMessage());
                    } catch (SmackException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (NetworkService.isConnection) {
                                    //Toast.makeText(smackContext, "(" + caller + ")" + "SMACKException: ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
//                        Log.e(TAG, "(" + caller + ")" + "SMACKException: " + e.getMessage());
                    } catch (XMPPException e) {
                        if (isToasted)
                            new Handler(Looper.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (NetworkService.isConnection) {
                                                //Toast.makeTeToast.makeText(smackContext, "(" + caller + ")" + "XMPPException: ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
//                        Log.e(TAG, "connect(" + caller + ")" + "XMPPException: " + e.getMessage());
                    }
                }catch (Exception e){}
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }

    public void login() {
        try {
            String name = Constants.Chat1;
            String receiverName = Constants.Chat2;

            connect(name);
            connection.login();
//            connection.login(loginUser, passwordUser);
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
//            Log.e(TAG, "Yey! We're connected to the Xmpp server!");

            Presence subscribe = new Presence(Presence.Type.subscribe);
            subscribe.setTo(receiverName + "@openfire.pappaya.education");
            connection.sendPacket(subscribe);

            Database.getMessagesFromOffline(name, smackContext);

            //SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PAPPAYA_PREF, MODE_PRIVATE);
            //Boolean firstTimeAppOpen = sharedPreferences.getBoolean(Constants.firstTimeAppOpen, false);

            //method to get chat history and inserted it to local db

            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    MyXMPP.getArchievedMessage(receiverName, ChatActivity.mChatAdapter);
                }
            }, 100);*/
            //Do something after 100ms
            MyXMPP.getArchievedMessage(receiverName, ChatActivity.mChatAdapter);

            //MyXMPP.getArchievedMessage(receiverName, ChatActivity.mChatAdapter);
            /*SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.firstTimeAppOpen, true);*/


            /*if (!firstTimeAppOpen) {

                String receiver_name = sharedPreferences.getString(Constants.RECEIVER_NAME, "");

                //method to get chat history and inserted it to local db
                MyXMPP.getArchievedMessage(receiver_name, ChatActivity.mChatAdapter);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.firstTimeAppOpen, true);

                Database.getMessagesFromOffline(receiver_name, context, ChatActivity.mChatAdapter);
            }*/


        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
//            Log.e(TAG, "XAMPP Exception : " + e);
        } catch (Exception e) {
//            Log.e(TAG, "EXception : " + e.getLocalizedMessage());
        }
    }

    public void sendMessage(ChatMessage chatMessage, String tablename, String id, Context context) {
        /*this.id = id;
        this.tableName = tableName;*/
        String body = gson.toJson(chatMessage);
        try {
            EntityJid jid = JidCreate.entityBareFrom(chatMessage.getReceiver() + "@" + serverAddress);
            Mychat = ChatManager.getInstanceFor(connection).createChat(jid, mMessageListener);
            chat_created = true;
        } catch (XmppStringprepException e) {
            e.printStackTrace();
//            Log.e(TAG, "exception : " + e);
        }
        final Message message = new Message();
        message.setBody(body);
        message.setStanzaId(chatMessage.getMsgid());
        message.setType(Message.Type.chat);
        receiver_available = false;
        try {
            if (connection.isAuthenticated()) {

                if (!chatMessage.getMsgid().equalsIgnoreCase(msg_id)) {
                    msg_id = chatMessage.getMsgid();
                    Mychat.sendMessage(message);
                    //DeliveryReceiptManager.addDeliveryReceiptRequest(message);
                    //connection.sendStanza(message);

                   if (!tablename.equals("") && !id.equals("")){

                       // method to delete a Record
                       Database.deleteRecord(tablename, id);
                   }
                }
                Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
                BareJid jid = JidCreate.bareFrom(chatMessage.getReceiver() + "@" + serverAddress);
                Roster roster = Roster.getInstanceFor(connection);

                /*roster.addRosterListener(new RosterListener() {
                    @Override
                    public void entriesAdded(Collection<Jid> addresses) {

                        Toast.makeText(context, "entriesAdded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void entriesUpdated(Collection<Jid> addresses) {
                        Toast.makeText(context, "entriesUpdated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void entriesDeleted(Collection<Jid> addresses) {
                        Toast.makeText(context, "entriesDeleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void presenceChanged(Presence presence) {
                        boolean user = presence.isAvailable();
                        //Toast.makeText(context, "presenceChanged", Toast.LENGTH_SHORT).show();
                    }
                });*/

                /*try{
                    Presence availability = roster.getPresence(jid);
                    //Presence.Mode userMode = availability.getMode();

                    //get the status of receiver
                    int status = Utils.retrieveState_mode(availability.getMode(),availability.isAvailable());

                    Log.d("Available Status", String.valueOf(status));
                    // searchUsers(chatMessage.getReceiver());
                    if (!availability.isAvailable()){
                        JSONObject jsonObject = new JSONObject();
                        JSONObject params = new JSONObject();
                        String _msg = chatMessage.getBody();
                        if (!Utils.replaceNull(chatMessage.getFile_url()).equals("")) {
                            _msg = Utils.getFileNameFromUrl(chatMessage.getFile_url());
                        }
                        try {
                            JSONObject modelObject = new JSONObject();
                            modelObject.put("model", Constants.CHAT);
                            modelObject.put("receiver_name", chatMessage.getReceiver_name());
                            modelObject.put("sender_name", chatMessage.getSender_name());
                            modelObject.put("sender", chatMessage.getSender());
                            modelObject.put("receiver", chatMessage.getReceiver());
                            params.put("username", chatMessage.getReceiver().toUpperCase());
                            params.put("title", "New message from "+ chatMessage.getSender_name());
                            params.put("body", _msg);
                            params.put("data", modelObject);
                            jsonObject.put(Constants.PARAMS, params);
                        } catch (JSONException e) {
                        }
                        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, Constants.CHATNOTIFICATION, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String dfg = "";
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String dfg = "";
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<String, String>();
                                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                                String session = sharedPreferences.getString(Constants.SESSION_ID, "");
                                headers.put("Cookie", Constants.SESSION_ID + "=" + session);
                                return headers;
                            }
                        };
                        MySingleton.getInstance(context).addToRequestQueue(request);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }*/


                try {
                    //Presence availability = roster.getPresence(jid);
                    //Presence.Mode userMode = availability.getMode();


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (!receiver_available) {
                                JSONObject jsonObject = new JSONObject();
                                JSONObject params = new JSONObject();
                                String _msg = chatMessage.getBody();
                                if (!Utils.replaceNull(chatMessage.getFile_url()).equals("")) {
                                    _msg = Utils.getFileNameFromUrl(chatMessage.getFile_url());
                                }
                                try {
                                    JSONObject modelObject = new JSONObject();
                                    modelObject.put("model", Constants.CHAT);
                                    modelObject.put("receiver_name", chatMessage.getReceiver_name());
                                    modelObject.put("sender_name", chatMessage.getSender_name());
                                    modelObject.put("sender", chatMessage.getSender());
                                    modelObject.put("receiver", chatMessage.getReceiver());
                                    params.put("username", chatMessage.getReceiver().toUpperCase());
                                    params.put("title", "New message from " + chatMessage.getSender_name());
                                    params.put("body", _msg);
                                    params.put("data", modelObject);
                                    jsonObject.put(Constants.PARAMS, params);
                                } catch (JSONException e) {
                                }
                                JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, Constants.CHATNOTIFICATION, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String demo ="";
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        String e="";
                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> headers = new HashMap<String, String>();
                                        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                                        String session = sharedPreferences.getString(Constants.SESSION_ID, "");
                                        headers.put("Cookie", Constants.SESSION_ID + "=" + session);
                                        return headers;
                                    }
                                };
                                MySingleton.getInstance(context).addToRequestQueue(request);

                            }
                        }
                    }, 3000);

                    //get the status of receiver
                        /*int status = Utils.retrieveState_mode(availability.getMode(),availability.isAvailable());

                        Log.d("Available Status", String.valueOf(status));
                        // searchUsers(chatMessage.getReceiver());
                        if (!available){

                        }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {



                /*Database database = new Database(context);
                if (!database.isTableExists(chatMessage.getReceiver(), context)) {
                    database.createTable(chatMessage.getSender());
                }

                database.insertMessageToOffline(chatMessage.getSender(), chatMessage.getMsgid(), chatMessage.getSender(), chatMessage.getReceiver(), chatMessage.getBody(), true, chatMessage.getDate(), chatMessage.getTime());
*/
//                Log.e(TAG, "Connection is not authenticated try to login ");
                login();

                    //ChatActivity.handleOfflineOnlineMessage(chatMessage);

            }
        } catch (SmackException.NotConnectedException e) {

//            Log.e(TAG, "xmpp.SendMessage() " + "msg Not sent!-Not Connected!" + e);

        } catch (Exception e) {

//            Log.e(TAG, "xmpp.SendMessage()-Exception" + "msg Not sent!" + e.getMessage());
        }
    }

    private class ChatManagerListenerImpl implements ChatManagerListener, ChatStateListener {
        @Override
        public void chatCreated(final org.jivesoftware.smack.chat.Chat chat, final boolean createdLocally) {
            if (!createdLocally)
                chat.addMessageListener(mMessageListener);
        }

        @Override
        public void processMessage(Chat chat, Message message) {

        }

        @Override
        public void stateChanged(Chat chat, ChatState state, Message message) {
            if (ChatState.composing.equals(state)) {
                Log.d("Chat State", chat.getParticipant() + " is typing..");
                //Toast.makeText(context, chat.getParticipant() + " is typing..", Toast.LENGTH_SHORT).show();
            } else if (ChatState.gone.equals(state)) {
                Log.d("Chat State", chat.getParticipant() + " has left the conversation.");
                //Toast.makeText(context, chat.getParticipant() + " has left the conversation.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Chat State", chat.getParticipant() + ": " + state.name());
                //Toast.makeText(context, chat.getParticipant() + ": " + state.name(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class XmppDeliveryReceiptManager implements ReceiptReceivedListener {


        final String TAG = XmppDeliveryReceiptManager.class.getSimpleName();

        Context mContext;


        XmppDeliveryReceiptManager(Context mContext) {
            this.mContext = mContext;
//            Log.e(TAG, "XmppDeliveryReceiptManager : initialized ");
        }

        @Override
        public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
//            Log.e(TAG, "onReceiptReceived : receiptId = " + receiptId);

            receiver_available = true;
            //method to update message sent report

            /*try{
                if (id !=0){
                    ContentValues cv = new ContentValues();
                    cv.put(Database.MESSAGESENT, true);
                    Database.updateTable(tableName, cv, id);
                }
            }catch (Exception e){
                e.printStackTrace();
            }*/

            // do need full with "receiptId"
//            Log.e(TAG, "stanza" + receipt);
        }
    }

    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {
//            Log.e(TAG, "xmpp" + "Connected!");
            connected = true;

            if (!connection.isAuthenticated()) {
                login();
            } else {
                MyXMPP.getArchievedMessage(ChatActivity.RECEIVERNAME, ChatActivity.mChatAdapter);
            }

            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ChatActivity.loadingLayout.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }

                }
            }).start();*/
        }

        @Override
        public void connectionClosed() {

            if (NetworkService.isConnection && !connection.isAuthenticated()) {
                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkService.isConnection) {
                                // TODO Auto-generated method stub
                                //Toast.makeText(smackContext, "ConnectionCLosed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//                Log.e(TAG, "xmpp " + "ConnectionCLosed!");
                connected = false;
                chat_created = false;
                loggedin = false;
                disconnect();
                initialiseConnection();
//                Log.e(TAG, "connected : " + connected + "\n" + "chat_created : " + chat_created + "\n" + "logged in : " + loggedin);
            }
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {

            if (NetworkService.isConnection && !connection.isAuthenticated()) {

                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkService.isConnection) {
                                //Toast.makeText(smackContext, "ConnectionClosedOn Error!!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
//                Log.e(TAG, "xmpp" + "ConnectionClosedOn Error!");
                connected = false;
                chat_created = false;
                loggedin = false;
//                Log.e(TAG, "connected : " + connected + "\n" + "chat_created : " + chat_created + "\n" + "logged in : " + loggedin);
                disconnect();
                initialiseConnection();
            }
        }

        @Override
        public void reconnectingIn(int arg0) {
            if (NetworkService.isConnection && !connection.isAuthenticated()) {

//                Log.e(TAG, "xmpp" + "Reconnectingin " + arg0);
                loggedin = false;
//                Log.e(TAG, "connected : " + connected + "\n" + "chat_created : " + chat_created + "\n" + "logged in : " + loggedin);
                disconnect();
                initialiseConnection();
            }
        }

        @Override
        public void reconnectionFailed(Exception arg0) {


            if (NetworkService.isConnection && !connection.isAuthenticated()) {

                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkService.isConnection) {
                                //Toast.makeText(smackContext, "ReconnectionFailed!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
//                Log.e(TAG, "xmpp" + "ReconnectionFailed!");
                connected = false;
                chat_created = false;
                loggedin = false;
                disconnect();
                initialiseConnection();
//                Log.e(TAG, "connected : " + connected + "\n" + "chat_created : " + chat_created + "\n" + "logged in : " + loggedin);
            }
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (NetworkService.isConnection) {
                            //Toast.makeText(smackContext, "REConnected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//            Log.e(TAG, "xmpp" + "ReconnectionSuccessful");
            connected = true;
            chat_created = false;
            loggedin = false;
//            Log.e(TAG, "connected : " + connected + "\n" + "chat_created : " + chat_created + "\n" + "logged in : " + loggedin);
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
//            Log.e(TAG, "xmpp" + "Authenticated!");
            loggedin = true;
            ChatManager.getInstanceFor(connection).addChatListener(mChatManagerListener);
            chat_created = false;
//            Log.e(TAG, "connected : " + connected + "\n" + "chat_created : " + chat_created + "\n" + "logged in : " + loggedin);

            MyXMPP.getArchievedMessage(ChatActivity.RECEIVERNAME, ChatActivity.mChatAdapter);

            AndroidBusProvider.getInstance().post(new OfflineToOnlineEvent(true));

            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ChatActivity.loadingLayout.setVisibility(View.GONE);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e(TAG, "INterrupted Exception : " + e);
                    }

                }
            }).start();*/
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (NetworkService.isConnection) {
                            //Toast.makeText(smackContext, "Connected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                if (NetworkService.isConnection && (connection == null || !connection.isAuthenticated())) {

                    XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();

                    config.setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled);
                    try {
                        DomainBareJid domainBareJid = JidCreate.domainBareFrom(serverAddress);
                        config.setServiceName(domainBareJid);
                        config.setHostAddress(InetAddress.getByName(serverAddress));
                        config.setXmppDomain("openfire.pappaya.education");
                        config.setUsernameAndPassword(params[0], params[1]);
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
//                        Log.e(TAG, "xception : " + e);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
//                        Log.e(TAG, "exception : " + e);
                    }

                    config.setHost(serverAddress);
                    config.setPort(Constants.PORT);
                    config.setDebuggerEnabled(true);
                    config.setConnectTimeout(1000);
                    config.setKeystorePath(null);

                    XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
                    XMPPTCPConnection.setUseStreamManagementDefault(true);
                    connection = new XMPPTCPConnection(config.build());

                    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
                    connection.addConnectionListener(connectionListener);
                    connection.setUseStreamManagement(true);
                    connection.setPacketReplyTimeout(20000);
//        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
//        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
//        SASLAuthentication.blacklistSASLMechanism("PLAIN");

                    ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT,
                            DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
                    ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT,
                            new DeliveryReceiptRequest().getNamespace(), new DeliveryReceiptRequest.Provider());

                    mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(connection);
                    mXmppDeliveryReceiptManager = new XmppDeliveryReceiptManager(smackContext);
                    try{
                        mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(connection);
                    }catch (Exception e){}
                    mDeliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
                    mDeliveryReceiptManager.addReceiptReceivedListener(mXmppDeliveryReceiptManager);

                    ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();
                    ServerPingWithAlarmManager.onCreate(smackContext);
                    ServerPingWithAlarmManager.getInstanceFor(connection).setEnabled(true);
                    ReconnectionManager.setEnabledPerDefault(true);

                    PingManager.getInstanceFor(connection).registerPingFailedListener(new PingFailedListener() {
                        @Override
                        public void pingFailed() {
                            disconnect();
                            initialiseConnection();
                        }
                    });

                    login();
                }

            } catch (Exception e) {
//                Log.e("LongOperation", "Interrupted", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}