package com.example.anant.movietime.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anant on 14/8/16.
 */
public class Movie implements Parcelable {
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342/";
    private static final String LOG_TAG = Movie.class.getSimpleName();


    private String title;
    private String summary;
    private Date releaseDate;
    private double popularity;
    private double averageVotes;
    private int voteCount;
    private URL posterUrl;
    private URL backdropUrl;

    //Getters

    public String getTitle() {
        return title;
    }

    public double getAverageVotes() {
        return averageVotes;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getSummary() {
        return summary;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public URL getPosterUrl() {
        return posterUrl;
    }

    public URL getBackdropUrl() {
        return backdropUrl;
    }
    //Setters


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAverageVotes(double averageVotes) {
        this.averageVotes = averageVotes;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    public void setReleaseDate(String date){
        this.releaseDate = stringToDate(date);

    }
    public static Date stringToDate(String date){
        Date d = null;
        try{
            d = (Date) (new SimpleDateFormat("yyyy-MM-dd")).parse(date);
        }catch (ParseException p){
            Log.e(LOG_TAG, "Error parsing date", p);
        }
        return d;
    }

    public void setPosterUrl(String imagePath) {

        URL posterUrl =  null;
        try{
            posterUrl = new URL(IMAGE_BASE_URL+imagePath);
        }catch(MalformedURLException mfue){
            Log.e("KAMEHAMEHA", mfue.getMessage(), mfue);
        }
        this.posterUrl = posterUrl;

    }

    public void setBackdropUrl(String backdropPath) {

        URL backdropUrl = null;
        try{
            backdropUrl = new URL(IMAGE_BASE_URL+backdropPath);
        }catch(MalformedURLException mfue){
            Log.e("KAMEHAMEHA", mfue.getMessage(),mfue);
        }

        this.backdropUrl = backdropUrl;
    }

    public Movie(){}

    public Movie
            (   String title,
                String summary,
                double averageVotes,
                double popularity,
                String imagePath,
                String backdropPath,
                String releaseDate,
                int voteCount  ){


        setAverageVotes(averageVotes);
        setPopularity(popularity);
        setPosterUrl(imagePath);
        setReleaseDate(releaseDate);
        setTitle(title);
        setSummary(summary);
        setVoteCount(voteCount);
        setBackdropUrl(backdropPath);
    }
    public static Movie jsonToMovie(JSONObject jsonObject){
        final String TMDB_BACKDROP = "backdrop_path";
        final String TMDB_NAME = "original_title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_SUMMARY = "overview";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_AVERAGE = "vote_average";
        final String TMDB_VOTE_COUNT = "vote_count";
        Movie movie = null;
        try{
            movie = new Movie(jsonObject.getString(TMDB_NAME),
                    jsonObject.getString(TMDB_SUMMARY),
                    jsonObject.getDouble(TMDB_AVERAGE),
                    jsonObject.getDouble(TMDB_POPULARITY),
                    jsonObject.getString(TMDB_POSTER),
                    jsonObject.getString(TMDB_BACKDROP),
                    jsonObject.getString(TMDB_RELEASE_DATE),
                    jsonObject.getInt(TMDB_VOTE_COUNT)
            );

        }catch(JSONException je){
            Log.e("KAMEHAMEHA", je.getMessage(), je);
        }

        return movie;
    }
    public static JSONObject movieToJson(Movie movie){
        final String TMDB_BACKDROP = "backdrop_path";
        final String TMDB_NAME = "original_title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_SUMMARY = "overview";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_AVERAGE = "vote_average";
        final String TMDB_VOTE_COUNT = "vote_count";
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject();
            jsonObject.put(TMDB_NAME, movie.getTitle());
            jsonObject.put(TMDB_AVERAGE, movie.getAverageVotes());
            jsonObject.put(TMDB_BACKDROP, movie.getBackdropUrl());
            jsonObject.put(TMDB_POPULARITY, movie.getPopularity());
            jsonObject.put(TMDB_POSTER, movie.getPosterUrl());
            jsonObject.put(TMDB_RELEASE_DATE, movie.getReleaseDate());
            jsonObject.put(TMDB_SUMMARY, movie.getSummary());
            jsonObject.put(TMDB_VOTE_COUNT, movie.getSummary());
        }catch(JSONException je){
            Log.e("KAMEHAMEHA", je.getMessage(), je);
        }
        return jsonObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getTitle());
        parcel.writeString(this.getSummary());
        parcel.writeString(this.getReleaseDate().toString());
        parcel.writeString(this.getPosterUrl().toString());
        parcel.writeString(this.getBackdropUrl().toString());
        parcel.writeInt(this.getVoteCount());
        parcel.writeDouble(this.getAverageVotes());
        parcel.writeDouble(this.getPopularity());
    }
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel) {
            Movie movie = new Movie();
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
    private Movie(Parcel in){
        this.setTitle(in.readString());
        this.setSummary(in.readString());
        this.releaseDate = new Date(in.readString());
        try{
            this.posterUrl = new URL(in.readString());
            this.backdropUrl = new URL(in.readString());
        }catch(MalformedURLException e){
            Log.e("KAMEHAMEHA",e.getMessage(), e);
        }
        this.setVoteCount(in.readInt());
        this.setAverageVotes(in.readDouble());
        this.setPopularity(in.readDouble());
    }
    public static String dateToString(Date date){
        String dateString = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateString = dateFormat.format(date);
        return dateString;
    }
}
