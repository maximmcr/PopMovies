package com.maximmcr.android.popmovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.model.Review;

import java.util.ArrayList;

import static com.maximmcr.android.popmovies.utils.Utility.getShortComment;
import static com.maximmcr.android.popmovies.utils.Utility.isOnline;

/**
 * Created by Frei on 08.07.2017.
 */

public class CommentVPAdapter extends PagerAdapter {

    ArrayList<Review> mReview;
    private Context mContext;

    public CommentVPAdapter(Context context, ArrayList<Review> review) {
        mContext = context;
        mReview = review;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.list_item_comment, container, false);

        TextView authorTV = (TextView) layout.findViewById(R.id.comment_author);
        authorTV.setText(mReview.get(position).getAuthor());

        TextView contentTV = (TextView) layout.findViewById(R.id.comment_content);
        contentTV.setText(getShortComment(mReview.get(position).getContent()));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(mContext)) {
                    Uri uri = Uri.parse(mReview.get(position).getUrl());
                    Intent openComment = new Intent(Intent.ACTION_VIEW, uri);
                    openComment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        return mReview.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
