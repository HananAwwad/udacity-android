package com.hananawwad.popularmovies.loaders;

import android.content.Context;
import android.util.Log;

import com.hananawwad.popularmovies.BuildConfig;
import com.hananawwad.popularmovies.http.HttpUtil;
import com.hananawwad.popularmovies.model.MovieModel;
import com.hananawwad.popularmovies.model.MovieResults;
import com.hananawwad.popularmovies.util.Constants;
import com.hananawwad.popularmovies.util.PreferenceUtil;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by user on 11/22/2015.
 */
public class MoviesLoader extends CommonLoader {

    public static final String TAG = "MoviesLoader";

    public MoviesLoader(Context context) {
        super(context);
    }

    @Override
    public List<MovieModel> loadInBackground() {

        Log.d(TAG, "Connecting to movie api to get all movies");
        Call<MovieResults> createdCall = HttpUtil.getService().getMovieResults(BuildConfig.MOVIES_API_KEY,
                PreferenceUtil.getPrefs(getContext(), Constants.MODE_VIEW, Constants.SORT_BY_POPULARITY_DESC));
        try {
            Response<MovieResults> result = createdCall.execute();
            return result.body().results;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException occurred in loadInBackground()" + e.getMessage());
        }
        return null;

    }


}
