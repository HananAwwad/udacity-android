package com.hananawwad.popularmovies.provider;

import android.content.Context;
import android.net.Uri;

import com.hananawwad.popularmovies.BuildConfig;
import com.hananawwad.popularmovies.model.MovieModel;

import java.util.List;

import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;
import nl.littlerobots.cupboard.tools.provider.UriHelper;
import nl.qbusict.cupboard.CupboardFactory;

/**
 * Created by user on 1/5/2016.
 */
public class FavoriteMovieContentProvider extends CupboardContentProvider {


    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final String DB_NAME = "movies.db";

    static {
        CupboardFactory.cupboard().register(MovieModel.class);
    }

    public FavoriteMovieContentProvider() {
        super(AUTHORITY, DB_NAME, 1);
    }

    public static MovieModel getMovieData(Context context, long id) {
        UriHelper uriHelper = UriHelper.with(FavoriteMovieContentProvider.AUTHORITY);
        Uri moviesUri = uriHelper.getUri(MovieModel.class);
        return CupboardFactory.cupboard().withContext(context).query(moviesUri, MovieModel.class).withSelection("id = ?", "" + id).get();
    }

    public static void deleteMovieData(Context context, long id) {
        UriHelper uriHelper = UriHelper.with(FavoriteMovieContentProvider.AUTHORITY);
        Uri moviesUri = uriHelper.getUri(MovieModel.class);
        CupboardFactory.cupboard().withContext(context).delete(moviesUri, "id = ?", id + "");
    }

    public static void putMovieData(Context context, MovieModel mMovieData) {
        UriHelper uriHelper = UriHelper.with(FavoriteMovieContentProvider.AUTHORITY);
        Uri movieUri = uriHelper.getUri(MovieModel.class);
        CupboardFactory.cupboard().withContext(context).put(movieUri, mMovieData);
    }

    public static List<MovieModel> getFavorites(Context context){
        UriHelper uriHelper = UriHelper.with(FavoriteMovieContentProvider.AUTHORITY);
        Uri movieUri = uriHelper.getUri(MovieModel.class);
        return CupboardFactory.cupboard().withContext(context).query(movieUri, MovieModel.class).list();
    }
}
