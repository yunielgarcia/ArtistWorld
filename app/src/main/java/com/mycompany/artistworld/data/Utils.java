package com.mycompany.artistworld.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.mycompany.artistworld.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ygarcia on 10/5/2017.
 */

public final class Utils {

    public static boolean isLoggedIn(Context context){
        //Retrieving and validating if user is logged in
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_name), MODE_PRIVATE);
        String restoredToken = prefs.getString(context.getString(R.string.token_key), null);
//        prefs.registerOnSharedPreferenceChangeListener(this);
        return restoredToken != null;
    }

    public static String getToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_name), MODE_PRIVATE);
        String restoredToken = prefs.getString(context.getString(R.string.token_key), null);
        return restoredToken;
    }

}
