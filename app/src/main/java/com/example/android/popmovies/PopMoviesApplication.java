package com.example.android.popmovies;

import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created by Frei on 16.07.2017.
 */

public class PopMoviesApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build()
        );
    }

}
