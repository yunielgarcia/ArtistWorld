package com.mycompany.artistworld.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ygarcia on 10/4/2017.
 */

public class UserCredentials {
    @SerializedName("token")
    private String mToken;
    @SerializedName("id")
    private int mId;

    public UserCredentials(String token, int id) {
        this.mToken = token;
        this.mId = id;
    }

    public String getmToken() {
        return mToken;
    }

    public int getId() {
        return mId;
    }
}
