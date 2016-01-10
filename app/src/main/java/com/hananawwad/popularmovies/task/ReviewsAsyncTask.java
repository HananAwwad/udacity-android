package com.hananawwad.popularmovies.task;

import android.util.Log;
import android.widget.ProgressBar;

import com.hananawwad.popularmovies.BuildConfig;
import com.hananawwad.popularmovies.http.HttpUtil;
import com.hananawwad.popularmovies.model.Review;
import com.hananawwad.popularmovies.model.ReviewResults;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by Mladen Babic <email>info@mladenbabic.com</email> on 11/15/2015.
 */
public class ReviewsAsyncTask extends CommonAsyncTask<Review> {

    private static final String TAG = "ReviewsAsyncTask";
    private final long mMovieId;

    public ReviewsAsyncTask(long movieId, ProgressBar mProgressBar, FetchDataListener mListener) {
        super(mProgressBar, mListener);
        this.mMovieId = movieId;
    }

    @Override
    protected ArrayList<Review> doInBackground(Void... params) {

        Log.d(TAG, "Connecting to movie api to get all reviews for movie with id: "+mMovieId);

        Call<ReviewResults> createdCall = HttpUtil.getService().getReviewsResults(mMovieId, BuildConfig.MOVIES_API_KEY);
        try {
            Response<ReviewResults> result = createdCall.execute();

            Log.d(TAG, "The reviews for movie with id: "+result.body().results.size());
            return result.body().results;
        } catch (IOException e) {
            Log.e(TAG, "IOException occurred in doInBackground()");
        }
        return null;
    }
}
