package com.mycompany.artistworld.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ygarcia on 9/29/2017.
 */

public class DataTestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake guests
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG, "kisa-kuku");
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_TITLE, "KISA KUKU");
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_VOTE_WEIGHT, 3);
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_MAIN_CONTENT_PATH, "http://cdn.jazwings.com/media/contents/idea_content/2016/09/14/a8eff61c-20c3-4431-b47e-b85d98398aa4_display_img.jpg");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG, "luno-and-friends");
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_TITLE, "LUNO AND FRIENDS");
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_VOTE_WEIGHT, 5);
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_MAIN_CONTENT_PATH, "https://cdn.jazwings.com/media/contents/idea_content/2016/09/14/1443287384926_display_img.jpg");
        list.add(cv);


        //insert all in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (ArtistWorldContract.ProjectEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(ArtistWorldContract.ProjectEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}
