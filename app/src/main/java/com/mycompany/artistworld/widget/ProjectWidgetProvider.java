package com.mycompany.artistworld.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.activities.MainActivity;
import com.mycompany.artistworld.activities.ProjectDetailActivity;
import com.mycompany.artistworld.model.Project;
import com.squareup.picasso.Picasso;

import static com.mycompany.artistworld.fragments.HomeFragment.IDEA_SELECTED;

/**
 * Implementation of App Widget functionality.
 */
public class ProjectWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String slug, String imgPath, int voteWeight, Project projectInWidget) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.project_widget_provider);

        //create an intent to lauch the mainActivity when click in logo
        Intent launAppIntent = new Intent(context, MainActivity.class);
        PendingIntent launchAppPendingIntent = PendingIntent.getActivity(context, 0, launAppIntent, 0);

        //create an intent to launch the idea detail view when click in the idea
        Intent launchDetailIntent = new Intent(context, ProjectDetailActivity.class);
        Bundle extra = new Bundle();
        extra.putParcelable(IDEA_SELECTED, projectInWidget);
        launchDetailIntent.putExtras(extra);
        PendingIntent launchDetailPendingIntent = PendingIntent.getActivity(context, 1, launchDetailIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (projectInWidget != null) {
            views.setTextViewText(R.id.widget_percent, String.valueOf(projectInWidget.getmVotePercent()) + "%" );
            if (projectInWidget != null) {
                Picasso.with(context)
                        .load(projectInWidget.getmContents().get(0).getmDisplayImg())
                        .into(views, R.id.widget_idea_img, new int[] {appWidgetId});
            }
        } else {
            String defaultText = context.getResources().getString(R.string.appwidget_text);
            views.setTextViewText(R.id.widget_percent, defaultText);
        }

        //Onclicks
        views.setOnClickPendingIntent(R.id.widget_logo, launchAppPendingIntent);
        //hook the click event if the idea exits
        if (projectInWidget != null) {
            views.setOnClickPendingIntent(R.id.widget_idea_ll_cont, launchDetailPendingIntent);
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        ProjectService.startActionUpdateWidget(context);
    }

    public static void updateProjectWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String slug, String imgPath, int voteWeight, Project projectInWidget) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, slug, imgPath, voteWeight, projectInWidget);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

