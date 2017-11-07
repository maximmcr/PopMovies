package com.maximmcr.android.popmovies;

/**
 * Created by Frei on 03.10.2017.
 */

public interface BasePresenter<V> {

    void attachView(V view);

    void detachView();

}
