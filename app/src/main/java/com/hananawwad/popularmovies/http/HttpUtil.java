package com.hananawwad.popularmovies.http;

import com.hananawwad.popularmovies.util.Constants;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by user on 1/3/2016.
 */
public class HttpUtil {

    private static MovieDBService service;
    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MovieDBService.class);
    }

    public static MovieDBService getService() {
        return service;
    }
}
