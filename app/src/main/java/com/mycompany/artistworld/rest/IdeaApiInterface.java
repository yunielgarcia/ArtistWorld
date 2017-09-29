package com.mycompany.artistworld.rest;

import com.mycompany.artistworld.model.ProjectResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ygarcia on 9/28/2017.
 */

public interface IdeaApiInterface {

    @GET("v1/ideas")
    Call<ProjectResponse> discoverIdeas(
            @Query("is_active") String isActive,
            @Query("ordering") String ordering,
            @Query("page") int page,
            @Query("phase_name") String phaseName);

    @GET("v1/ideas/trending")
    Call<ProjectResponse> getTrendingIdeas();

    @GET("v1/search/ideas")
    Call<ProjectResponse> findIdeas(
            @Query("query") String query,
            @Query("page") int page);

}
