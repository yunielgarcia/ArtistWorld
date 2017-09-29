package com.mycompany.artistworld.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ygarcia on 9/28/2017.
 */

public class Content implements Parcelable{

    @SerializedName("url")
    private String mUrl;
    @SerializedName("id")
    private String mId;
    @SerializedName("content")
    private String mContent;
    @SerializedName("idea")
    private String mIdea;
    @SerializedName("external_content_url")
    private String mExternal_content_url;
    @SerializedName("display_img")
    private String mDisplayImg;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("order")
    private String mOrder;

    public Content(String mUrl, String mId, String mContent, String mIdea, String mExternal_content_url, String mDisplayImg, String mDescription, String mOrder) {
        this.mUrl = mUrl;
        this.mId = mId;
        this.mContent = mContent;
        this.mIdea = mIdea;
        this.mExternal_content_url = mExternal_content_url;
        this.mDisplayImg = mDisplayImg;
        this.mDescription = mDescription;
        this.mOrder = mOrder;
    }

    protected Content(Parcel in) {
        mUrl = in.readString();
        mId = in.readString();
        mContent = in.readString();
        mIdea = in.readString();
        mExternal_content_url = in.readString();
        mDisplayImg = in.readString();
        mDescription = in.readString();
        mOrder = in.readString();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    public String getmUrl() {
        return mUrl;
    }

    public String getmId() {
        return mId;
    }

    public String getmContent() {
        return mContent;
    }

    public String getmIdea() {
        return mIdea;
    }

    public String getmExternal_content_url() {
        return mExternal_content_url;
    }

    public String getmDisplayImg() {
        return mDisplayImg;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmOrder() {
        return mOrder;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUrl);
        dest.writeString(mId);
        dest.writeString(mContent);
        dest.writeString(mIdea);
        dest.writeString(mExternal_content_url);
        dest.writeString(mDisplayImg);
        dest.writeString(mDescription);
        dest.writeString(mOrder);
    }
}
