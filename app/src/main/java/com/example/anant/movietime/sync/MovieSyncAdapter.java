package com.example.anant.movietime.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.anant.movietime.BuildConfig;
import com.example.anant.movietime.R;
import com.example.anant.movietime.data.MovieContract;
import com.example.anant.movietime.data.MovieProvider;
import com.example.anant.movietime.types.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by anant on 12/9/16.
 *

 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int SYNC_INTERVAL = 60;
    private static final int SYNC_FLEXTIME = 60*60;
    private static final long DAY_IN_MILLIS = 1000L*SYNC_INTERVAL;
    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    public static final String[] MOVIE_COLUMNS ={
            MovieContract.MovieEntry._ID, //0
            MovieContract.MovieEntry.COLUMN_TITLE, //1
            MovieContract.MovieEntry.COLUMN_SUMMARY, //2
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE, //3
            MovieContract.MovieEntry.COLUMN_POPULARITY, //4
            MovieContract.MovieEntry.COLUMN_AVERAGE_VOTES, //5
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT, //6
            MovieContract.MovieEntry.COLUMN_POSTER_URL, //7
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL, //8

    };
    public static final int ID = 0;
    public static final int TITLE = 1;
    public static final int SUMMARY = 2;
    public static final int RELEASE_DATE = 3;
    public static final int POPULARITY = 4;
    public static final int AVERAGE_VOTES = 5;
    public static final int VOTE_COUNT = 6;
    public static final int POSTER_URL = 7;
    public static final int BACKDROP_URL = 8;




    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String moviesJsonString = getJsonFromUrl();
        Log.d(LOG_TAG, "recieved JSON from server");
        Log.v(LOG_TAG, moviesJsonString);
        Movie[] movies = convertJsonArrayToMovies(stringToJson(moviesJsonString));
        Log.d(LOG_TAG, "Converted JSON to movies");
        Vector<ContentValues> contentValuesVector = new Vector<>(movies.length);
        MovieProvider movieProvider = new MovieProvider();
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
        for(Movie movie:movies){
            ContentValues values = new ContentValues();
            values.put(MOVIE_COLUMNS[TITLE], movie.getTitle());
            values.put(MOVIE_COLUMNS[SUMMARY], movie.getSummary());
            values.put(MOVIE_COLUMNS[RELEASE_DATE], Movie.dateToString(movie.getReleaseDate()));
            values.put(MOVIE_COLUMNS[POPULARITY], movie.getPopularity());
            values.put(MOVIE_COLUMNS[AVERAGE_VOTES], movie.getAverageVotes());
            values.put(MOVIE_COLUMNS[VOTE_COUNT], movie.getVoteCount());
            values.put(MOVIE_COLUMNS[POSTER_URL], movie.getPosterUrl().toString());
            values.put(MOVIE_COLUMNS[BACKDROP_URL], movie.getBackdropUrl().toString());

            contentValuesVector.add(values);
        }
        if(contentValuesVector.size()>0){
            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArray);
            getContext().getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null
            );

            getContext().getContentResolver().bulkInsert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    contentValuesArray
            );
        }
        Log.d(LOG_TAG, "Sync done");

    }
    private String getJsonFromUrl(){
        BufferedReader reader;
        HttpURLConnection urlConnection;
        StringBuilder buffer;
        buffer = new StringBuilder();
        String jsonString = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/popular/";
            final String API_KEY = BuildConfig.MY_THE_MOVIE_DB_API_KEY;
            final String API_KEY_PARAM = "api_key";

            Uri queryUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();
            URL url = new URL(queryUri.toString());
            Log.v("KAMEHAMEHA", "Query URI :"+url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream == null)
                return  null;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String newLine;

            while((newLine = reader.readLine()) != null)
                buffer.append(newLine).append("\n");


            jsonString = buffer.toString();
            Log.v("KAMEHAMEHA",jsonString);


        }catch (IOException e){
            Log.v("KAMEHAMEHA",e.getMessage(),e);
        }
        return jsonString;
    }
    private JSONArray stringToJson(String string){
        final String OWM_RESULT = "results";
        JSONArray jsonArray=null;
        try{
            jsonArray = new JSONObject(string).getJSONArray(OWM_RESULT);
            Log.v("KAMEHAMEHA",jsonArray.toString());
        }catch (JSONException e){
            Log.e("KAMEHAMEHA", e.getMessage(), e);
        }
        return jsonArray;
    }
    private Movie[] convertJsonArrayToMovies(JSONArray jsonArray){

        if(jsonArray==null){
            Log.e("KAMEHAMEHA","JSONArray is Null");
            System.exit(666);
        }
        Movie[] movies = null;
        try{
            movies = new Movie[jsonArray.length()];
            for(int i=0; i< jsonArray.length(); i++){
                movies[i] = Movie.jsonToMovie(jsonArray.getJSONObject(i));
            }
        }catch(JSONException je){
            Log.e("KAMEHAMEHA", je.getMessage(), je);
        }

        return movies;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime){
        Account account = getSyncAccount(context);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        int multiplier = 24;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        multiplier = Integer.parseInt(sharedPreferences.getString("sync_frequency", "1440"));

        MovieSyncAdapter.configurePeriodicSync(context, multiplier*SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, MovieContract.CONTENT_AUTHORITY, true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
//        syncImmediately(context);
    }
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
}
