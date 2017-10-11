package com.mycompany.artistworld.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.data.ArtistWorldContract;
import com.mycompany.artistworld.model.Project;
import com.mycompany.artistworld.rest.IdeaApiInterface;
import com.mycompany.artistworld.rest.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mycompany.artistworld.data.ArtistWorldContract.BASE_CONTENT_URI;

/**
 * Created by ygarcia on 10/9/2017.
 */

public class ProjectService extends IntentService {

    public static final String ACTION_UPDATE_WIDGET_IDEA =
            "com.mycompany.artistworld.action.water_plants";

    String slug = null;
    String imgPath = null;
    int voteWeight = 0;
    Project projectInWidget = null;

    public ProjectService() {
        super("ProjectService");
    }

    public static void startActionUpdateWidget(Context context) {
        Intent intent = new Intent(context, ProjectService.class);
        intent.setAction(ACTION_UPDATE_WIDGET_IDEA);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGET_IDEA.equals(action)) {
                handleActionUpdateWidget();
            }
        }
    }

    private void handleActionUpdateWidget(){
        //this will grab the latest project from fav (if exist) or return null and update the widget
        //after loading the complete idea from the server.
        Uri PROJECT_URI = BASE_CONTENT_URI.buildUpon().appendPath(ArtistWorldContract.PATH_PROJECTS).build();
        Cursor cursor = getContentResolver().query(
                PROJECT_URI,
                null,
                null,
                null,
                ArtistWorldContract.ProjectEntry.COLUMN_TIMESTAMP + " DESC"
        );



        if (cursor != null && cursor.getCount() > 0){
            // Indices for the _id, description, and priority columns
            int slugIndex = cursor.getColumnIndex(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG);
            int imgPathIndex = cursor.getColumnIndex(ArtistWorldContract.ProjectEntry.COLUMN_MAIN_CONTENT_PATH);
            int voteWeightIndex = cursor.getColumnIndex(ArtistWorldContract.ProjectEntry.COLUMN_VOTE_WEIGHT);

            cursor.moveToFirst();

            slug = cursor.getString(slugIndex);
            imgPath = cursor.getString(imgPathIndex);
            voteWeight = cursor.getInt(voteWeightIndex);
        }

        IdeaApiInterface ideaService = ServiceGenerator.createService(IdeaApiInterface.class);

        if (slug != null){
            //load the idea here and then if success go to detail
            Call<Project> call = ideaService.getIdea(slug);
            call.enqueue(new Callback<Project>() {
                @Override
                public void onResponse(Call<Project> call, Response<Project> response) {
                    if (response.isSuccessful()){
                        projectInWidget = response.body();
                        //this needs to be called inside success after loading the complete idea
                        requestUpdateWidgetWithLoadedData(slug, imgPath, voteWeight, projectInWidget);
                    } else {
                        if (response.code() == 401){
                            Toast.makeText(getBaseContext(), getString(R.string.unauthenticated), Toast.LENGTH_SHORT).show();
                            requestUpdateWidgetWithLoadedData(slug, imgPath, voteWeight, projectInWidget);
                        } else if (response.code() >= 400){
                            Toast.makeText(getBaseContext(), getString(R.string.client_error) + response.code() + " " + response.message() , Toast.LENGTH_LONG).show();
                            requestUpdateWidgetWithLoadedData(slug, imgPath, voteWeight, projectInWidget);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Project> call, Throwable t) {
                    //call it anyway, we'll update the ui if null
                    requestUpdateWidgetWithLoadedData(slug, imgPath, voteWeight, projectInWidget);
                }
            });
        }else {
            requestUpdateWidgetWithLoadedData(null, null, 0, null);
        }

    }

    private void requestUpdateWidgetWithLoadedData(String slug, String imgPath, int voteWeight, Project projectInWidget){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ProjectWidgetProvider.class));
        //Now update all widgets
        ProjectWidgetProvider.updateProjectWidget(this, appWidgetManager, appWidgetIds, slug, imgPath, voteWeight, projectInWidget);
    }
}



























