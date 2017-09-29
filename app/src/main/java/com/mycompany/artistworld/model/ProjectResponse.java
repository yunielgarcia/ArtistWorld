package com.mycompany.artistworld.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ygarcia on 9/28/2017.
 */

public class ProjectResponse {
    @SerializedName("count")
    private int mCount;
    @SerializedName("next")
    private String mNext;
    @SerializedName("previous")
    private String mPrevious;
    @SerializedName("results")
    private List<Project> mResults;

    public int getmCount() {
        return mCount;
    }

    public String getmNext() {
        return mNext;
    }

    public String getmPrevious() {
        return mPrevious;
    }

    public List<Project> getmResults() {
        return mResults;
    }
}
