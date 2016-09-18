package com.example.anant.movietime.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.anant.movietime.data.MovieContract.MovieEntry;

/**
 * Created by anant on 10/9/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }








    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabae) {
        final String SQL_CREATE_TABLE = "CREATE TABLE "  +
                MovieEntry.TABLE_NAME+
                " ("+
                    MovieEntry._ID + " INTEGER PRIMARY KEY  AUTOINCREMENT, "+
                    MovieEntry.COLUMN_TITLE+" TEXT NOT NULL, "+
                    MovieEntry.COLUMN_SUMMARY+" TEXT NOT NULL, "+
                    MovieEntry.COLUMN_RELEASE_DATE+" TEXT NOT NULL, "+
                    MovieEntry.COLUMN_POPULARITY+" INTEGER NOT NULL, "+
                    MovieEntry.COLUMN_AVERAGE_VOTES+" REAL NOT NULL, "+
                    MovieEntry.COLUMN_VOTE_COUNT+" INTEGER NOT NULL, "+
                    MovieEntry.COLUMN_POSTER_URL+" TEXT NOT NULL, "+
                    MovieEntry.COLUMN_BACKDROP_URL+" TEXT NOT NULL "+
                ");";
        sqLiteDatabae.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
