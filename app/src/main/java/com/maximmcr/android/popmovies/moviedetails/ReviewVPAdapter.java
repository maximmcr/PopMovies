package com.maximmcr.android.popmovies.moviedetails;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.model.Review;

import java.util.ArrayList;

import static com.maximmcr.android.popmovies.utils.Utility.getShortReview;

/**
 * Created by Frei on 08.07.2017.
 */

public class ReviewVPAdapter extends PagerAdapter {

    ArrayList<Review> mReview;
    private Context mContext;
    private OnReviewClickListener mListener;

    public ReviewVPAdapter(Context context, ArrayList<Review> review, OnReviewClickListener listener) {
        mContext = context;
        mReview = review;
        mListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.list_item_comment, container, false);

        TextView authorTV = (TextView) layout.findViewById(R.id.comment_author);
        authorTV.setText(mReview.get(position).getAuthor());

        TextView contentTV = (TextView) layout.findViewById(R.id.comment_content);
        contentTV.setText(getShortReview(mReview.get(position).getContent()));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onReviewClick(mReview.get(position));
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

    public void replace(ArrayList<Review> reviews) {
        mReview = reviews;
        notifyDataSetChanged();
    }

    public interface OnReviewClickListener {
        void onReviewClick(Review review);
    }
}
