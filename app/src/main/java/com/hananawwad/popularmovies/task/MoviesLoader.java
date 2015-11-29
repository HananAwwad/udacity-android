package com.hananawwad.popularmovies.task;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.hananawwad.popularmovies.BuildConfig;
import com.hananawwad.popularmovies.model.MovieModel;
import com.hananawwad.popularmovies.util.Constants;
import com.hananawwad.popularmovies.util.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 11/22/2015.
 */
public class MoviesLoader extends AsyncTaskLoader<List<MovieModel>> {

    public static final String TAG = "MoviesLoader";
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    List<MovieModel> movieModelList;

    public MoviesLoader(Context context) {
        super(context);
    }

    @Override
    public List<MovieModel> loadInBackground() {
        try {
            Log.d(TAG, "Connecting to movie api to get all movies");
            Uri builtUri = Uri.parse(Constants.MOVIE_URL).buildUpon()
                    .appendQueryParameter(Constants.SORT_BY_KY_PARAM, PreferenceUtil.getPrefs(getContext(), Constants.MODE_VIEW, Constants.SORT_BY_POPULARITY_DESC))
                    .appendQueryParameter(Constants.API_KEY_PARAM, BuildConfig.MOVIES_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            String moviesJsonStr = buffer.toString();
            JSONObject forecastJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = forecastJson.getJSONArray("results");
            movieModelList = new ArrayList<MovieModel>();
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJSON = moviesArray.getJSONObject(i);
                String title = movieJSON.getString("original_title");
                String posterPath = movieJSON.getString("poster_path");
                String overview = movieJSON.getString("overview");
                double voteAverage = movieJSON.getDouble("vote_average");
                String releaseDate = movieJSON.getString("release_date");
                MovieModel movieModel = new MovieModel();
                movieModel.title = title;
                movieModel.overview = overview;
                movieModel.posterPath = posterPath;
                movieModel.voteAverage = voteAverage;
                movieModel.releaseDate = releaseDate;
                movieModelList.add(movieModel);
            }
            Log.d(TAG, "The movies fetched successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return movieModelList;
    }

    @Override
    public void deliverResult(List<MovieModel> movies) {
        movieModelList = movies;
        if (isStarted()) {
            super.deliverResult(movieModelList);
        }
    }

    @Override
    protected void onStartLoading() {
        if (movieModelList != null) {
            deliverResult(movieModelList);
        }

        if (takeContentChanged() || movieModelList == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }
}
