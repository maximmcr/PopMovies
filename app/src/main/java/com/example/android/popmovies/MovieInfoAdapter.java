package com.example.android.popmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieInfoAdapter extends ArrayAdapter<MovieInfo> {

    public MovieInfoAdapter(Activity context, List<MovieInfo> movieInfo) {
        super(context, 0, movieInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieInfo mInfo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_film, parent, false);
        }

        ImageView poster = (ImageView) convertView.findViewById(R.id.list_item_poster);
        poster.setImageResource(mInfo.mPosterId);

        TextView movieName = (TextView) convertView.findViewById(R.id.list_item_name);
        movieName.setText(mInfo.mName);

        return convertView;
    }
}
