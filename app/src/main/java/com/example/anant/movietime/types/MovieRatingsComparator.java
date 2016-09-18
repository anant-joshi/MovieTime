package com.example.anant.movietime.types;

import java.util.Comparator;

/**
 * Created by anant on 14/8/16.
 */
public class MovieRatingsComparator implements Comparator<Movie> {
    @Override
    public int compare(Movie m1, Movie m2) {
        if(m1.getAverageVotes()>m2.getAverageVotes())
            return 1;
        else if(m1.getAverageVotes()<m2.getAverageVotes())
            return -1;
        else if(m1.getVoteCount()!=m2.getVoteCount()){
            return (m1.getVoteCount()>m2.getVoteCount())?1:-1;
        }
        return 0;
    }
}
