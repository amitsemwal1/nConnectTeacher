package com.nconnect.teacher.model.Notifications;

public class NotificationDetail {

    String read_status;
    long local_dbId;
    int notification_id;
    String message;
    String model;
    String date;

    public NotificationDetail(String read_status, long local_dbId, int notification_id, String message, String model, String date) {
        this.read_status = read_status;
        this.local_dbId = local_dbId;
        this.notification_id = notification_id;
        this.message = message;
        this.model = model;
        this.date = date;
    }

    public String getRead_status() {
        return read_status;
    }

    public void setRead_status(String read_status) {
        this.read_status = read_status;
    }

    public long getLocal_dbId() {
        return local_dbId;
    }

    public void setLocal_dbId(long local_dbId) {
        this.local_dbId = local_dbId;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}