package com.mdelacalle.komootsandbox.model;

import android.util.Log;

import io.realm.RealmObject;

public class Image  extends RealmObject {
    String src;

    public String getSrc() {
        return src;
    }

    public String getSrcFixed() {
        String fixedImage = src.substring(0,src.indexOf('?'));
        Log.i("***", fixedImage);
        return fixedImage;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
