package com.nconnect.teacher.model;

import android.net.Uri;

public class MultipleImageVideo {

    String path;
    String type;
    Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public MultipleImageVideo() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MultipleImageVideo(String path, String type) {
        this.path = path;
        this.type = type;
    }
}
