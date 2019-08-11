package com.nconnect.teacher.model.chat;

import com.google.gson.Gson;

import java.util.Random;

public class ChatMessage {

    private String body, sender, receiver;
    private String Date, Time;
    private String msgid;
    private boolean isMine;
    private boolean message_sent;
    private String file_url;
    private String file_name;
    private long timeMillis;
    private String receiver_name;
    private String sender_name;
    private String file_size;


    public ChatMessage() {

    }

    public ChatMessage(String msgId, String sender, String receiver, String msg,
                       String ismine, String date, String time, boolean message_sent,
                       String file_url, String file_name, long timeMillis) {
        this.msgid = msgId;
        this.sender = sender;
        this.receiver = receiver;
        this.body = msg;
        this.isMine = Boolean.parseBoolean(ismine);
        this.Date = date;
        this.Time = time;
        this.message_sent = message_sent;
        this.file_url = file_url;
        this.file_name = file_name;
        this.timeMillis = timeMillis;

    }


    public ChatMessage(String Sender, String Receiver, String messageString, String ID, boolean isMINE,
                       String file_url, String file_name) {
        body = messageString;
        isMine = isMINE;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
        this.file_url = file_url;
        this.file_name = file_name;
    }


    public void setBody(String body) {
        this.body = body;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public void setMsgID() {
        msgid += "-" + String.format("%02d", new Random().nextInt(100));
    }

    public String getBody() {
        return body;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isMessage_sent() {
        return message_sent;
    }

    public void setMessage_sent(boolean message_sent) {
        this.message_sent = message_sent;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public static ChatMessage instanceOf(String messageString) {
        if (messageString == null) {
            return new ChatMessage();
        } else {
            return new Gson().fromJson(messageString, ChatMessage.class);
        }
    }
}