package com.maximmcr.android.popmovies.moviedetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.model.Video;

import java.util.ArrayList;

/**
 * Created by Frei on 10.07.2017.
 */

public class VideoAdapter extends ArrayAdapter<Video> {

    private Context mContext;
    private OnVideoClickListener mListener;

    private static class ViewHolder {
        TextView type;
        TextView name;
    }

    public VideoAdapter(Context context, ArrayList<Video> videos, OnVideoClickListener listener) {
        super(context, R.layout.list_item_video, videos);
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Video video = getItem(position);

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

        viewHolder.type.setText(video.getType());
        viewHolder.name.setText(video.getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onVideoClick(video);
            }
        });

        return convertView;
    }

    public void replace(ArrayList<Video> videos) {
        clear();
        addAll(videos);
        notifyDataSetChanged();
    }

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }
}
