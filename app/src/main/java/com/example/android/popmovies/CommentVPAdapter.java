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

    ArrayList<CommentModel> mCommentModels;
    Context mContext;

    public CommentVPAdapter(Context context, ArrayList<CommentModel> commentModels) {
        mContext = context;
        mCommentModels = commentModels;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.list_item_comment, container, false);

        TextView authorTV = (TextView) layout.findViewById(R.id.comment_author);
        authorTV.setText(mCommentModels.get(position).mAuthor);

        TextView contentTV = (TextView) layout.findViewById(R.id.comment_content);
        contentTV.setText(getShortComment(mCommentModels.get(position).mContent));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(mContext)) {
                    Uri uri = Uri.parse(mCommentModels.get(position).mUrl);
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
        return mCommentModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
