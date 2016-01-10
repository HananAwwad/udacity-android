package com.hananawwad.popularmovies.loaders;

import android.content.Context;
import android.util.Log;

import com.hananawwad.popularmovies.provider.FavoriteMovieContentProvider;

/**
 * Created by user on 1/5/2016.
 */
public class FavoritesLoader extends CommonLoader {

    private static final String TAG = "FavoritesLoader";
    public FavoritesLoader(Context context) {
        super(context);
    }

    @Override
    public Object loadInBackground() {
        Log.d(TAG,"Loading favorites movies from DB");
        return FavoriteMovieContentProvider.getFavorites(getContext());
    }
}
