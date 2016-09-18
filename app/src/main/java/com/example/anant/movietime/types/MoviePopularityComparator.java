package com.example.anant.movietime.types;

import java.util.Comparator;

/**
 * Created by anant on 14/8/16.
 */
public class MoviePopularityComparator implements Comparator<Movie> {
    @Override
    public int compare(Movie m1, Movie m2) {
        if(m1.getPopularity()>m2.getPopularity())
            return 1;
        else if(m1.getPopularity()<m2.getPopularity())
            return -1;
        return 0;
    }
}
