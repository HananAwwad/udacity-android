package com.hananawwad.popularmovies.http;

import com.hananawwad.popularmovies.model.MovieResults;
import com.hananawwad.popularmovies.model.ReviewResults;
import com.hananawwad.popularmovies.model.TrailerResults;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * Created by user on 1/3/2016.
 */
public interface MovieDBService {

    @GET("/3/discover/movie")
    Call<MovieResults> getMovieResults(@Query("api_key") String apiKey, @Query("sort_by") String sortBy);

    @GET("/3/movie/{id}/videos")
    Call<TrailerResults> getTrailersResults(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    Call<ReviewResults> getReviewsResults(@Path("id") long movieId, @Query("api_key") String apiKey);
}
