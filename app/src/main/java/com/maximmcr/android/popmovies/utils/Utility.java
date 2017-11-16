package com.maximmcr.android.popmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Frei on 07.07.2017.
 */

public class Utility {
    public static String getShortReview(String review) {
        if (review.length() > 400) {
            review = review.substring(0, 200);
            if (review.endsWith(" ")) {
                review = review.substring(0, review.length() - 2);
            }
            review = review + "...";
        }

        return review;
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

//    public static byte[] bitmapToByteArray(Bitmap b) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        return baos.toByteArray();
//    }
//    public static byte[] drawableToByteArray(Drawable drawable) {
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//        Bitmap bitmap = bitmapDrawable.getBitmap();
//        return bitmapToByteArray(bitmap);
//    }
//
//    public static String byteArrayToString(byte[] b) {
//        return Base64.encodeToString(b, Base64.DEFAULT);
//    }
//    public static Bitmap stringToBitmap(String s) {
//        try {
//            byte [] encodeByte=Base64.decode(s,Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        } catch(Exception e) {
//            e.getMessage();
//            return null;
//        }
//    }
//
//    public static int pxToDp(Context context, int px) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        return (int) (px / displayMetrics.density);
//    }
//    public static int dpToPx(Context context, int dp) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        return (int) (dp * displayMetrics.density);
//    }
}
