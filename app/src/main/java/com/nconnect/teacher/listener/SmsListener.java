package com.nconnect.teacher.listener;

public interface SmsListener {
    public void messageReceived(String messageText, String sender);
}
