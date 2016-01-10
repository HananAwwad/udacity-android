package com.hananawwad.popularmovies.task;

import android.util.Log;
import android.widget.ProgressBar;

import com.hananawwad.popularmovies.BuildConfig;
import com.hananawwad.popularmovies.http.HttpUtil;
import com.hananawwad.popularmovies.model.Trailer;
import com.hananawwad.popularmovies.model.TrailerResults;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by Mladen Babic <email>info@mladenbabic.com</email> on 11/15/2015.
 */
public class TrailersAsyncTask extends CommonAsyncTask<Trailer> {

    private static final String TAG = "TrailersAsyncTask";

    private long mMovieId;


    public TrailersAsyncTask(long mMovieId, ProgressBar mProgressBar, FetchDataListener mListener) {
        super(mProgressBar, mListener);
        this.mMovieId = mMovieId;
    }


    @Override
    protected ArrayList<Trailer> doInBackground(Void... params) {

        Log.d(TAG, "Connecting to movie api to get all trailers for movie with id: "+mMovieId);

        Call<TrailerResults> createdCall = HttpUtil.getService().getTrailersResults(mMovieId, BuildConfig.MOVIES_API_KEY);

        try {
            Response<TrailerResults> result = createdCall.execute();

            Log.d(TAG, "The trailers for movie with id: "+result.body().results.size());

            return result.body().results;

        } catch (IOException e) {
            Log.e(TAG, "IOException occurred in doInBackground()");
        }
        return null;
    }

}
