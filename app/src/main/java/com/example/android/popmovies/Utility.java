package com.example.android.popmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
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
    public static boolean isSeeSaved(Context context) {
        String option = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_list_key), context.getString(R.string.pref_list_default));
        String saved = context.getResources().getStringArray(R.array.sysVal)[2];
        if (option.equals(saved)) return true;
        else return false;
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

    public static String byteArrayToString(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public static Bitmap stringToBitmap(String s) {
        try {
            byte [] encodeByte=Base64.decode(s,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
    public static byte[] bitmapToByteArray(Bitmap b) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    public static byte[] drawableToByteArray(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmapToByteArray(bitmap);
    }
}
