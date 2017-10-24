package com.maximmcr.android.popmovies.settings;

/**
 * Created by Frei on 23.10.2017.
 */

public interface SharedPreferenceRepository {

    boolean isOptionSaved();

    String getCurrentFiltering();
}
