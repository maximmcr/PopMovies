package com.maximmcr.android.popmovies.settings;

import android.content.Context;
import android.preference.PreferenceManager;

import com.maximmcr.android.popmovies.R;

/**
 * Created by Frei on 23.10.2017.
 */

public class SharedPrefsRepoImpl implements SharedPreferenceRepository {

    private static SharedPrefsRepoImpl INSTANCE = null;

    private Context mContext;

    private SharedPrefsRepoImpl(Context context) {
        mContext = context;
    }

    public static SharedPrefsRepoImpl getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SharedPrefsRepoImpl(context);
        }
        return INSTANCE;
    }

    @Override
    public boolean isOptionSaved() {
        String option = PreferenceManager
                .getDefaultSharedPreferences(mContext)
                .getString(
                        mContext.getString(R.string.pref_list_key),
                        mContext.getString(R.string.pref_list_default)
                );
        String saved = mContext.getResources().getStringArray(R.array.sysVal)[2];
        return option.equals(saved);
    }

    @Override
    public String getCurrentFiltering() {
        return PreferenceManager
                .getDefaultSharedPreferences(mContext)
                .getString(
                        mContext.getString(R.string.pref_list_key),
                        mContext.getString(R.string.pref_list_default)
                );
    }
}
