package com.nconnect.teacher.model.stories;

import android.net.Uri;

import java.io.File;

public class Attachment {
    private String imagePath;
    private String contentype;
    private File uri;
    private Uri imageUri;

    public File getUri() {
        return uri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public void setUri(File uri) {
        this.uri = uri;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getContentype() {
        return contentype;
    }

    public void setContentype(String contentype) {
        this.contentype = contentype;
    }
}
