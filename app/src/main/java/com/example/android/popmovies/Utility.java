package com.example.android.popmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Frei on 07.07.2017.
 */

public class Utility {
    public static String getShortComment(String comment) {
        if (comment.length() > 400) {
            comment = comment.substring(0, 200);
            if (comment.endsWith(" ")) {
                comment = comment.substring(0, comment.length() - 2);
            }
            comment = comment + "...";
        }

        return comment;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String formatDate(String filmDate) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        newFormat.applyPattern("MMMM yyyy");
        String result = filmDate;
        try {
            result = newFormat.format(oldFormat.parse(filmDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
