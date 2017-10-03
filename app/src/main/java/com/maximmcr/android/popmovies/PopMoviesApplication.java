package com.maximmcr.android.popmovies;

import android.content.Context;

import com.maximmcr.android.popmovies.api.TmdbApi;
import com.facebook.stetho.Stetho;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Frei on 16.07.2017.
 */

public class PopMoviesApplication extends android.app.Application {

    private static final String TMDB_REQUEST_BASE = "http://api.themoviedb.org/3/movie/";

    private static TmdbApi tmdbApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build()
        );

        retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_REQUEST_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApi = retrofit.create(TmdbApi.class);
    }

    public static TmdbApi getTmdbApi() {
        return tmdbApi;
    }

}
