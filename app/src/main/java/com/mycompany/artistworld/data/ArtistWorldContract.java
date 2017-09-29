package com.mycompany.artistworld.data;

import android.provider.BaseColumns;

/**
 * Created by ygarcia on 9/29/2017.
 */

public class ArtistWorldContract {

    public static final class ProjectEntry implements BaseColumns{
        public static final String TABLE_NAME = "project";
        public static final String COLUMN_PROJECT_SLUG = "slug";
        public static final String COLUMN_PROJECT_TITLE = "title";
        public static final String COLUMN_VOTE_WEIGHT = "voteWeight";
        public static final String COLUMN_MAIN_CONTENT_PATH = "mainContentPath";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }
}
