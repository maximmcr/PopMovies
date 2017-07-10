package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Frei on 10.07.2017.
 */

public class VideoAdapter extends ArrayAdapter<MovieInfo.Video> {

    private static final String YOUTUBE_REQUEST_BASE = "https://www.youtube.com/watch";

    private Context mContext;

    private static class ViewHolder {
        TextView type;
        TextView name;
    }

    public VideoAdapter(Context context, ArrayList<MovieInfo.Video> videos) {
        super(context, R.layout.list_item_video, videos);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final MovieInfo.Video video = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_video, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.video_name);
            viewHolder.type = (TextView) convertView.findViewById(R.id.video_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.type.setText(video.mType);
        viewHolder.name.setText(video.mName);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isOnline(mContext)) {
                    Uri uri = Uri.parse(YOUTUBE_REQUEST_BASE)
                            .buildUpon()
                            .appendQueryParameter("v", video.mPath)
                            .build();
                    Intent playVideo = new Intent(Intent.ACTION_VIEW, uri);
                    if (playVideo.resolveActivity(mContext.getPackageManager()) != null) {
                        Intent chooser = Intent.createChooser(playVideo, "Play via");
                        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(chooser);
                    }
                } else {
                    Snackbar.make(v, "There is no internet connection!", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        return convertView;
    }
}
