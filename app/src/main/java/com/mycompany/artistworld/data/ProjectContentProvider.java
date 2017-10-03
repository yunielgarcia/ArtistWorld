package com.mycompany.artistworld.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by ygarcia on 10/2/2017.
 */

public class ProjectContentProvider extends ContentProvider{

    // Member variable for a ProjectDbHelper that's initialized in the onCreate() method
    private ProjectDbHelper mProjectDbHelper;

    public static final int PROJECTS = 100;
    public static final int PROJECTS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        //construct an empty matcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //add wich uri structure to recognize
        //directory
        uriMatcher.addURI(ArtistWorldContract.AUTHORITY, ArtistWorldContract.PATH_PROJECTS, PROJECTS);

        //single item
        uriMatcher.addURI(ArtistWorldContract.AUTHORITY, ArtistWorldContract.PATH_PROJECTS + "/#", PROJECTS_WITH_ID);

        return  uriMatcher;
    }

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mProjectDbHelper = new ProjectDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mProjectDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match){
            case PROJECTS:
                //make query here
                returnCursor = db.query(ArtistWorldContract.ProjectEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues cv) {
        final SQLiteDatabase db = mProjectDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri ;

        switch (match){
            case PROJECTS:
                //insert projects
                long id = db.insert(ArtistWorldContract.ProjectEntry.TABLE_NAME, null, cv);
                //if success we have id if not id == -1
                if (id > 0){//then we construct the uri to be returned
                    returnUri = ContentUris.withAppendedId(ArtistWorldContract.ProjectEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row int " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
