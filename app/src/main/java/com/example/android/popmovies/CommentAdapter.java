package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.android.popmovies.Utility.getShortComment;
import static com.example.android.popmovies.Utility.isOnline;

/**
 * Created by Frei on 06.07.2017.
 */

public class CommentAdapter extends BaseAdapter {

    ArrayList<MovieInfo.Comment> mComments;
    Context context;

    public CommentAdapter(Context context, ArrayList<MovieInfo.Comment> comments) {
        super();
        mComments = comments;
        this.context = context;
    }
    @Override
    public int getCount() { return mComments.size(); }

    @Override
    public Object getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_comment, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.author = (TextView) convertView.findViewById(R.id.comment_author);
            viewHolder.content = (TextView) convertView.findViewById(R.id.comment_content);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.author.setText(mComments.get(position).mAuthor);
        viewHolder.content.setText(getShortComment(mComments.get(position).mContent));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(context)) {
                    Intent openComment = new Intent(Intent.ACTION_VIEW);
                    openComment.setData(Uri.parse(mComments.get(position).mUrl));
                    context.startActivity(openComment);
                } else {
                    Snackbar.make(v, "There is no internet connection!", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        TextView author;
        TextView content;
    }
}
