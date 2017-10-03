package com.mycompany.artistworld.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ygarcia on 9/29/2017.
 */

public class ArtistWorldContract {

    public static final String AUTHORITY = "com.mycompany.artistworld";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +  AUTHORITY);

    public static final String PATH_PROJECTS = "projects";

    public static final class ProjectEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROJECTS).build();

        public static final String TABLE_NAME = "project";
        public static final String COLUMN_PROJECT_SLUG = "slug";
        public static final String COLUMN_PROJECT_TITLE = "title";
        public static final String COLUMN_VOTE_WEIGHT = "voteWeight";
        public static final String COLUMN_MAIN_CONTENT_PATH = "mainContentPath";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }
}
