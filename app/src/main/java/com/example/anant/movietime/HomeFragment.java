package com.example.anant.movietime;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.anant.movietime.data.MovieContract;
import com.example.anant.movietime.types.Movie;
import com.example.anant.movietime.types.MovieAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private MovieAdapter movieAdapter;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private String mSortOrder = "popularity";

    @Override
    public void onResume() {
        super.onResume();
        String sortOrder = getSortOrder();
        if(!sortOrder.equalsIgnoreCase(mSortOrder)){
            mSortOrder = sortOrder;
            getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);
        }
    }

    private static final int LOADER_ID = 42;
    private static final String SELECTED_KEY = "selected position";
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

    public interface Callback{
        public void onItemSelected(Uri itemUri);
    }

    private String getSortOrder(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order), getString(R.string.item_popularity));
        if(sortOrder.equalsIgnoreCase("popularity"))
            return MOVIE_COLUMNS[POPULARITY]+" DESC";
        else
            return MOVIE_COLUMNS[AVERAGE_VOTES]+" DESC";
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return true;

    }

    private List<Movie> movies = new ArrayList<>();

    public void setMovies(Movie[] movies) {
        Collections.addAll(this.movies, movies);
    }
    private String displayMovies(){
        StringBuilder sb = new StringBuilder();
        for(Movie movie: movies){
            sb.append(movie.getTitle()).append("\n");

        }
        return sb.toString();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("HAABLOOBLAA", "Homefragment created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieAdapter = new MovieAdapter(getActivity(), null, 0);
        Log.v("Log1","Inflated Layout");
        mGridView = (GridView) rootView.findViewById(R.id.main_poster_view);
        mGridView.setAdapter(movieAdapter);
        mSortOrder = getString(R.string.item_popularity);
        Log.d("Log1", "Adapter set");


        mGridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                        if(cursor!=null){
                            ((Callback) getActivity()).onItemSelected(
                                    MovieContract.MovieEntry.buildContentUri(cursor.getLong(NAME))
                            );
                        }
                        mPosition = position;
                    }
                }
        );
        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition= savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition!=GridView.INVALID_POSITION)
            outState.putInt(SELECTED_KEY, mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = getSortOrder();
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI
                .buildUpon()
                .build();
        Log.d(LOG_TAG, "Loader created");

        return new CursorLoader(
                getContext(),
                movieUri,
                null,
                null,
                null,
                sortOrder
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        movieAdapter.swapCursor(data);
        mGridView.setAdapter(movieAdapter);
        if(mPosition!=GridView.INVALID_POSITION){
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

}
