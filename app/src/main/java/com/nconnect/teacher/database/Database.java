package com.nconnect.teacher.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nconnect.teacher.model.chat.ChatMessage;
import com.nconnect.teacher.services.SmackService;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class Database {

    public static Context context;
    private static final String TAG = Database.class.getSimpleName();
    public static final String DBNAME = "pappaya.db";
    private static SQLiteDatabase mydb;
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_ID = "message_id";
    public static final String ISMINE = "ismine";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String ID = "id";
    public static final String MESSAGESENT = "message_sent";
    public static final String FILEURL = "file_url";
    public static final String FILENAME = "file_name";
    public static final String ISONLINE = "isOnline";
    public static final String FILESIZE = "file_size";

    public static String NOTIFICATIONTABLENAME = "Notification";
    public static String HISTORYTABLENAME = "History";
    public static final String RECEIVERNAME = "receiver_name";
    public static final String SENDERNAME = "sender_name";

    public Database(Context context) {
        this.context = context;
        mydb = context.openOrCreateDatabase(Database.DBNAME, Context.MODE_PRIVATE, null);
    }

    public static String CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + HISTORYTABLENAME +
            " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MESSAGE_ID + " TEXT, " +
            SENDER + " TEXT, " + RECEIVER + " TEXT, " + MESSAGE + " TEXT, " +
            ISMINE + " TEXT, " + DATE + " TEXT, " + TIME + " TEXT, " + MESSAGESENT + " BOOLEAN, " + FILEURL + " TEXT, " +
            FILENAME + " TEXT, " + RECEIVERNAME + " TEXT, " + SENDERNAME + " TEXT, " + ISONLINE + " BOOLEAN);";

    public static String CREATE_NOTIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTIFICATIONTABLENAME +
            " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DBModel.Notification.message + " TEXT, " +
            DBModel.Notification.read_status + " TEXT, " + DBModel.Notification.notification_id + " INTEGER, " + DBModel.Notification.model + " TEXT, " + DBModel.Notification.date + " TEXT);";

    public static void createTable(String tablename) {
        String TABLE_NAME = "'" + tablename + "'";
        try {
            mydb = context.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
            String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MESSAGE_ID + " TEXT, " +
                    SENDER + " TEXT, " + RECEIVER + " TEXT, " + MESSAGE + " TEXT, " +
                    ISMINE + " TEXT, " + DATE + " TEXT, " + TIME + " TEXT, " + MESSAGESENT + " BOOLEAN, " + FILEURL + " TEXT, " + FILENAME + " TEXT, " + ISONLINE + " BOOLEAN);";
            mydb.execSQL(CREATE_TABLE);
            mydb.close();
        } catch (Exception e) {
            Log.e(TAG, "error creating table : " + e);
        }
    }
    public static void createHistoryTable() {
        try {
            mydb = context.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
            mydb.execSQL(CREATE_HISTORY_TABLE);
            //mydb.close();
        } catch (Exception e) {
            Log.e(TAG, "error creating table : " + e);
        }
    }

    public static SQLiteDatabase getDataBase(){
        return  mydb;

    }

    public static long insertMessageToOffline(String tablename, String msgid, String sender, String receiver,
                                              String message, boolean ismine, String date, String time,
                                              String file_url, String file_name) {
        String tblname = "'" + tablename + "'";
        long id = 0;
        try {
            mydb = context.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
            /*String INSERT_TABLE_VALUES = "INSERT INTO " + tblname + " VALUES ( null, ?, ?, ?, ?, ?, ?, ?)";
            mydb.execSQL(INSERT_TABLE_VALUES, new String[]{msgid, sender, receiver, message, String.valueOf(ismine), date, time});
*/
            ContentValues values = new ContentValues();
            values.put(SENDER, sender);
            values.put(RECEIVER, receiver);
            values.put(MESSAGE, message);
            values.put(MESSAGE_ID, msgid);
            values.put(ISMINE, ismine);
            values.put(DATE, date);
            values.put(TIME, time);
            values.put(MESSAGESENT, false);
            values.put(FILEURL, file_url);
            values.put(FILENAME, file_name);
            id = mydb.insert(tablename, null, values);

            mydb.close();
//            Log.e("DB", "INSERTED");
        } catch (Exception e) {
//            Log.e("DBERROR", e.toString());
        }

        return id;
    }

    public static long insertToDB(ChatMessage chatMessage, Context context, String title_name) {
        long id = 0;
        try {

            if (!isTableExists(HISTORYTABLENAME, context)) {
                createHistoryTable();
            }

            //mydb = context.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);

            ContentValues values = new ContentValues();
            values.put(SENDER, chatMessage.getSender());
            values.put(RECEIVER, chatMessage.getReceiver());
            values.put(MESSAGE, chatMessage.getBody());
            values.put(MESSAGE_ID, chatMessage.getMsgid());
            values.put(ISMINE, chatMessage.isMine());
            values.put(DATE, chatMessage.getDate());
            values.put(TIME, chatMessage.getTime());
            values.put(MESSAGESENT, false);
            values.put(FILEURL, chatMessage.getFile_url());
            values.put(FILENAME, chatMessage.getFile_name());
            values.put(ISONLINE, true);
            values.put(RECEIVERNAME, title_name);
            values.put(SENDERNAME, chatMessage.getSender_name());

            handleLocalDataInsertUpdate(HISTORYTABLENAME, chatMessage.getSender(), chatMessage.getReceiver(), values);

//            Log.e("DB", "INSERTED");
        } catch (Exception e) {
//            Log.e("DBERROR", e.toString());
        }

        return id;
    }

    public static long insertUpcomingMsgToDB(ChatMessage chatMessage, Context context, String title_name) {
        long id = 0;
        try {

            if (!isTableExists(HISTORYTABLENAME, context)) {
                createHistoryTable();
            }

            //mydb = context.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);

            ContentValues values = new ContentValues();
            values.put(SENDER, chatMessage.getReceiver());
            values.put(RECEIVER, chatMessage.getSender());
            values.put(MESSAGE, chatMessage.getBody());
            values.put(MESSAGE_ID, chatMessage.getMsgid());
            values.put(ISMINE, chatMessage.isMine());
            values.put(DATE, chatMessage.getDate());
            values.put(TIME, chatMessage.getTime());
            values.put(MESSAGESENT, false);
            values.put(FILEURL, chatMessage.getFile_url());
            values.put(FILENAME, chatMessage.getFile_name());
            values.put(RECEIVERNAME, title_name);
            values.put(SENDERNAME, chatMessage.getReceiver_name());

            handleLocalDataInsertUpdate(HISTORYTABLENAME, chatMessage.getReceiver(), chatMessage.getSender(), values);
//            Log.e("DB", "INSERTED");
        } catch (Exception e) {
//            Log.e("DBERROR", e.toString());
        }

        return id;
    }

    /*public static void getMessagesFromOffline(String tablename, Context context, ChatAdapter mChatAdapter, LinearLayout loadingLayout) {
        mydb = context.openOrCreateDatabase(Database.DBNAME, Context.MODE_PRIVATE, null);
        Cursor allrows = mydb.rawQuery("SELECT DISTINCT " + ID + "," + SENDER + "," + RECEIVER + "," + MESSAGE_ID + ","
                + MESSAGE + "," + ISMINE + "," + DATE + "," + TIME + "," + MESSAGESENT + " FROM " + tablename + ";", null);
        if (allrows.moveToFirst()) {
            do {

                String message_sent = allrows.getString(allrows.getColumnIndex(Database.MESSAGESENT));
                String sender = allrows.getString(allrows.getColumnIndex(Database.SENDER));
                String receiver = allrows.getString(allrows.getColumnIndex(Database.RECEIVER));
                String msg = allrows.getString(allrows.getColumnIndex(Database.MESSAGE));
                String msgId = allrows.getString(allrows.getColumnIndex(Database.MESSAGE_ID));
                String ismine = allrows.getString(allrows.getColumnIndex(Database.ISMINE));
                String date = allrows.getString(allrows.getColumnIndex(Database.DATE));
                String time = allrows.getString(allrows.getColumnIndex(Database.TIME));
                String file_url = allrows.getString(allrows.getColumnIndex(Database.TIME));
                ChatMessage chatMessage = new ChatMessage(msgId, sender, receiver, msg, ismine, date, time, Utils.getBooleanValue(message_sent));
                mChatAdapter.add(chatMessage);

                loadingLayout.setVisibility(View.GONE);
            }
            while (allrows.moveToNext());
            Log.e(TAG, "Db data : " + new Gson().toJson(ChatAdapter.mChatList));
        }
    }*/

    //process offline messages
    public static void getMessagesFromOffline(String tablename, Context context) {
        try{
            mydb = context.openOrCreateDatabase(Database.DBNAME, Context.MODE_PRIVATE, null);
            Cursor allrows = mydb.rawQuery("SELECT * FROM "+tablename+" WHERE "+Database.ISONLINE+" = 0", null);
            //Cursor allrows = mydb.rawQuery("SELECT DISTINCT * FROM" + tablename + "WHERE"+ Database.ISONLINE=0";", null);
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


                    ChatMessage chatMessage = new ChatMessage(msgId, sender, receiver, msg, ismine, date, time,
                            Utils.getBooleanValue(message_sent), file_url, file_name, timeMillis);

                    SmackService.xmppConnection.sendMessage(chatMessage, tablename, id, context);

                    /*try{
                        ContentValues values = new ContentValues();
                        values.put(ISONLINE, 1);
                        updateTable(tablename, values, Long.valueOf(id));
                    }catch (Exception e){}*/

                }

                while (allrows.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isTableExists(String tableName, Context context) {
        mydb = context.openOrCreateDatabase(Database.DBNAME, Context.MODE_PRIVATE, null);
        Cursor cursor = mydb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public static void handleLocalDataInsertUpdate(String tableName, String sender, String receiver, ContentValues values) {

        try{
            if (mydb == null){
                return;
            }
            Cursor allrows = mydb.rawQuery("select ID from "+tableName+" where "+SENDER+" = '" + sender + "' AND "+RECEIVER+"='"+receiver+"'", null);
            int size = allrows.getCount();

            if (size == 0){
                mydb.insert(HISTORYTABLENAME, null, values);
            }else {
                if (allrows != null) {
                    if (allrows.moveToLast()) {

                        long id = allrows.getLong(allrows.getColumnIndex(Database.ID));

                        updateTable(HISTORYTABLENAME, values, id);
                    }
                }
            }

            allrows.close();
        }catch (Exception e){}
    }

    //method to update value
    public static void updateTable(String tableName, ContentValues cv, long id){
        try{
            mydb.update(tableName, cv, ID +"="+id, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // method to delete a Record
    public static int deleteRecord(String table_name, String ID)
    {
        String where="ID=?";
        int numberOFEntriesDeleted= mydb.delete(table_name, where, new String[]{ID}) ;
        return numberOFEntriesDeleted;
    }

}