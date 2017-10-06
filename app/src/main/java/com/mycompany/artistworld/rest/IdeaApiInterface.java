package com.mycompany.artistworld.rest;

import com.mycompany.artistworld.model.Project;
import com.mycompany.artistworld.model.ProjectResponse;
import com.mycompany.artistworld.objects.AuthUser;
import com.mycompany.artistworld.objects.IdeaVotePost;
import com.mycompany.artistworld.objects.UserCredentials;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    @GET("v1/ideas/{idea_slug}")
    Call<Project> getIdea(@Path("idea_slug") String slug);

    //maybe later create another interface for user actions
    @POST("auth/login/")
    Call<UserCredentials> login(@Body AuthUser authUser);

    @POST("v1/votes")
    Call<ResponseBody> vote(@Body IdeaVotePost ideaVotePost);


}
