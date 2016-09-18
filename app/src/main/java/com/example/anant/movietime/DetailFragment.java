package com.example.anant.movietime;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anant.movietime.data.MovieContract;
import com.example.anant.movietime.types.Movie;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static String DETAIL_URI = "URI";
    private Uri mUri;
    private static final int DETAIL_LOADER = 13;
    public ViewHolder mViewHolder;

    public static final String[] MOVIE_COLUMNS ={
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID, //0
            MovieContract.MovieEntry.COLUMN_TITLE, //1
            MovieContract.MovieEntry.COLUMN_SUMMARY, //2
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE, //3
            MovieContract.MovieEntry.COLUMN_POPULARITY, //4
            MovieContract.MovieEntry.COLUMN_AVERAGE_VOTES, //5
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT, //6
            MovieContract.MovieEntry.COLUMN_POSTER_URL, //7
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL, //8
            //9
    };
    public static final int NAME = 0;
    public static final int TITLE = 1;
    public static final int SUMMARY = 2;
    public static final int RELEASE_DATE = 3;
    public static final int POPULARITY = 4;
    public static final int AVERAGE_VOTES = 5;
    public static final int VOTE_COUNT = 6;
    public static final int POSTER_URL = 7;
    public static final int BACKDROP_URL = 8;


    Movie current = null;
    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        mViewHolder = new ViewHolder(rootView);
        return rootView;
    }

    String formatDate(String rawDate){
        int month = 0;
        month = Integer.parseInt(rawDate.substring(0,2));
        String yearString = rawDate.substring(rawDate.indexOf('/')+1);
        String monthString;
        switch (month){
            case 1:
                monthString = "January";
                break;
            case 2:
                monthString = "February";
                break;
            case 3:
                monthString = "March";
                break;
            case 4:
                monthString = "April";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "June";
                break;
            case 7:
                monthString = "July";
                break;
            case 8:
                monthString = "August";
                break;
            case 9:
                monthString = "September";
                break;
            case 10:
                monthString = "October";
                break;
            case 11:
                monthString = "November";

                break;
            case 12:
                monthString = "December";
                break;
            default:
                monthString = "Undecimber";
        }
        return monthString+", "+yearString;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null != mUri){
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    MovieContract.MovieEntry._ID+" = ?",
                    new String[]{String.valueOf(ContentUris.parseId(mUri))},
                    null
            );
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null){


            boolean b = data.moveToFirst();
            mViewHolder.titleView.setText(data.getString(TITLE));
            String ratingString = data.getInt(AVERAGE_VOTES)+"/10";
            mViewHolder.ratingView.setText(ratingString);
            mViewHolder.summaryView.setText(data.getString(SUMMARY));
            String votesCount = "of "+data.getInt(VOTE_COUNT)+" votes";
            mViewHolder.votesView.setText(votesCount);
            Date currentReleaseDate = Movie.stringToDate(data.getString(RELEASE_DATE));
            String dateText = new SimpleDateFormat("MM/yyyy", Locale.getDefault())
                    .format(currentReleaseDate);
            mViewHolder.dateView.setText(formatDate(dateText));
            Log.d(LOG_TAG, "Poster Url is"+data.getString(POSTER_URL));

            Picasso.with(getContext()).load(data.getString(POSTER_URL)).into(mViewHolder.posterView);


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    public static class ViewHolder{
        public final TextView titleView;
        public final ImageView posterView;
        public final TextView summaryView;
        public final TextView ratingView;
        public final TextView votesView;
        public final TextView dateView;

        public ViewHolder(View view) {
            this.dateView = (TextView) view.findViewById(R.id.detail_movie_info_date);
            this.titleView = (TextView) view.findViewById(R.id.detail_title);
            this.posterView = (ImageView) view.findViewById(R.id.detail_poster);
            this.summaryView = (TextView) view.findViewById(R.id.detail_movie_info_summary);
            this.ratingView = (TextView) view.findViewById(R.id.detail_movie_info_rating);
            this.votesView = (TextView) view.findViewById(R.id.detail_movie_info_votes);
        }
    }

}
