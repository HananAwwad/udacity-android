package com.hananawwad.popularmovies.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by user on 1/3/2016.
 */
public abstract class CommonLoader<T>  extends AsyncTaskLoader<List<T>> {

    private static final String TAG = "CommonLoader";

    private List<T> results;

    public CommonLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(List<T> commonResults) {
        Log.d(TAG,"Calling deliver result");
        results = commonResults;
        if (isStarted()) {
            super.deliverResult(results);
        }
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG,"Calling on start loading.");
        if (results != null) {
            deliverResult(results);
        }

        if (takeContentChanged() || results == null) {
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


