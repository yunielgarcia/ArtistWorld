package com.mycompany.artistworld.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ygarcia on 9/28/2017.
 */

public class Project implements Parcelable{

    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("contents")
    private List<Content> mContents;
    @SerializedName("idea_creator")
    private String mIdeaCreator;
    @SerializedName("vote_percent")
    private int mVotePercent;
    @SerializedName("slug")
    private String mSlug;

    public Project(String mTitle, String mDescription, List<Content> contents, String creator, int votePercent, String slug) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mContents = contents;
        this.mIdeaCreator = creator;
        this.mVotePercent = votePercent;
        this.mSlug = slug;
    }

    protected Project(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
        if (in.readByte() == 0x01) {
            mContents = new ArrayList<Content>();
            in.readList(mContents, Content.class.getClassLoader());
        } else {
            mContents = null;
        }

        mIdeaCreator = in.readString();
        mVotePercent = in.readInt();
        mSlug = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getmTitle() {
        return mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public List<Content> getmContents() {
        return mContents;
    }

    public String getmIdeaCreator() {
        return mIdeaCreator;
    }

    public int getmVotePercent() {
        return mVotePercent;
    }

    public String getmSlug() {
        return mSlug;
    }

    //Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);

        if (mContents == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mContents);
        }

        dest.writeString(mIdeaCreator);
        dest.writeInt(mVotePercent);
        dest.writeString(mSlug);
    }
}
