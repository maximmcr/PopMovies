package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.android.popmovies.Utility.getShortComment;
import static com.example.android.popmovies.Utility.isOnline;

/**
 * Created by Frei on 08.07.2017.
 */

public class CommentVPAdapter extends PagerAdapter {

    ArrayList<MovieInfo.Comment> mComments;
    Context mContext;

    public CommentVPAdapter(Context context, ArrayList<MovieInfo.Comment> comments) {
        mContext = context;
        mComments = comments;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.list_item_comment, container, false);

        TextView authorTV = (TextView) layout.findViewById(R.id.comment_author);
        authorTV.setText(mComments.get(position).mAuthor);

        TextView contentTV = (TextView) layout.findViewById(R.id.comment_content);
        contentTV.setText(getShortComment(mComments.get(position).mContent));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(mContext)) {
                    Intent openComment = new Intent(Intent.ACTION_VIEW);
                    openComment.setData(Uri.parse(mComments.get(position).mUrl));
                    mContext.startActivity(openComment);
                } else {
                    Snackbar.make(v, "There is no internet connection!", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
