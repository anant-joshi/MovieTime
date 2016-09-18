package com.example.anant.movietime.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.anant.movietime.data.MovieContract.MovieEntry;

/**
 * Created by anant on 11/9/16.
 */
public class MovieProvider extends ContentProvider {
    private MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
    private static final int ALL_MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();


    public static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, ALL_MOVIES);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES+"/#", MOVIE_WITH_ID);
        return matcher;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int delete = 0;
        if(selection == null){
            SQLiteDatabase db = movieDbHelper.getWritableDatabase();
            delete = db.delete(MovieEntry.TABLE_NAME, null, null);
        }else{
            SQLiteDatabase database = movieDbHelper.getWritableDatabase();
            delete =  database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
        }
        if(delete>0)
            getContext().getContentResolver().notifyChange(uri, null);
        return delete;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        Log.d(LOG_TAG, "Uri is: "+uri.toString());

        switch (sUriMatcher.match(uri)){
            case ALL_MOVIES:{
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }break;
            case MOVIE_WITH_ID:{
                long id = ContentUris.parseId(uri);
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(LOG_TAG, DatabaseUtils.dumpCursorToString(cursor));
            }break;
            default:{
                throw new UnsupportedOperationException("Unknown uri "+uri);
            }
        }
        Log.v(LOG_TAG, cursor.toString());
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)){
            case ALL_MOVIES:{
                return MovieEntry.CONTENT_TYPE;

            }
            case MOVIE_WITH_ID:{
                return MovieEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri "+uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        long _id = 0;
        Uri returnUri = null;
        switch (sUriMatcher.match(uri)){
            case MOVIE_WITH_ID:{
                _id = db.insert(MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = MovieEntry.buildContentUri(_id);
                }else{
                    throw new SQLException("Unable to insert into "+uri);
                }
            }break;
            default:{
                throw new UnsupportedOperationException("Unknown uri "+uri);
            }

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int numUpdated = 0;
        movieDbHelper = new MovieDbHelper(getContext());
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        if(contentValues == null){
            throw new IllegalArgumentException("Cannot have null arguments");
        }

        switch (sUriMatcher.match(uri)){
            case MOVIE_WITH_ID:{
                numUpdated = db.update(
                        MovieEntry.TABLE_NAME,
                        contentValues,
                        MovieEntry._ID+" = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))}
                );
            }break;
            case ALL_MOVIES:{
                numUpdated = db.update(
                        MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
            }break;
            default:{
                throw new UnsupportedOperationException("Unknown uri");
            }
        }
        if(numUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return numUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        ContentValues[] contentValues = values;
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){

            case ALL_MOVIES:{
                db.beginTransaction();
                try {
                    for(ContentValues value: values){
                        if(value == null)
                            throw new IllegalArgumentException("Cannot have null content values");

                        long _id = -1;
                        try{
                            _id = db.insertOrThrow(MovieEntry.TABLE_NAME, null, value);

                        }catch (SQLiteConstraintException sce){
                            Log.w("KAMEHAMEHA",
                                    new StringBuilder("Attempting to insert ")
                                            .append(value.getAsString(MovieEntry.COLUMN_TITLE))
                                            .append("but value is already in db")
                                            .toString(),
                                    sce
                            );
                        }

                        if(_id != -1){
                            numInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
            }break;
            default:{
                throw new UnsupportedOperationException("Unknown uri "+uri);
            }
        }
        if(numInserted>0)
            getContext().getContentResolver().notifyChange(uri, null);
        return numInserted;
    }
}
























