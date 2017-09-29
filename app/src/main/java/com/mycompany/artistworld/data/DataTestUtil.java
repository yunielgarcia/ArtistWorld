package com.mycompany.artistworld.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ygarcia on 9/29/2017.
 */

public class DataTestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
//        //create a list of fake guests
//        List<ContentValues> list = new ArrayList<ContentValues>();
//
//        ContentValues cv = new ContentValues();
//        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG, "kisa-kuku");
//        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, 12);
//        list.add(cv);
//
//
//        //insert all guests in one transaction
//        try
//        {
//            db.beginTransaction();
//            //clear the table first
//            db.delete (WaitlistContract.WaitlistEntry.TABLE_NAME,null,null);
//            //go through the list and add one by one
//            for(ContentValues c:list){
//                db.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, c);
//            }
//            db.setTransactionSuccessful();
//        }
//        catch (SQLException e) {
//            //too bad :(
//        }
//        finally
//        {
//            db.endTransaction();
//        }

    }
}
