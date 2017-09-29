package com.mycompany.artistworld.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ygarcia on 9/29/2017.
 */

public class ProjectDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "artistProject.db";

    private static final int DATABASE_VERSION = 1;

    public ProjectDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PROJECT_TABLE = "CREATE TABLE " +
                ArtistWorldContract.ProjectEntry.TABLE_NAME + " ( " +
                ArtistWorldContract.ProjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG + " TEXT NOT NULL," +
                ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_TITLE + " TEXT NOT NULL," +
                ArtistWorldContract.ProjectEntry.COLUMN_MAIN_CONTENT_PATH + " TEXT NOT NULL," +
                ArtistWorldContract.ProjectEntry.COLUMN_VOTE_WEIGHT + " INTEGER NOT NULL," +
                ArtistWorldContract.ProjectEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_PROJECT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArtistWorldContract.ProjectEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
