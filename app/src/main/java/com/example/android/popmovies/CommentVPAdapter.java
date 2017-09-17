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

import com.example.android.popmovies.model.CommentModel;

import java.util.ArrayList;

import static com.example.android.popmovies.Utility.getShortComment;
import static com.example.android.popmovies.Utility.isOnline;

/**
 * Created by Frei on 08.07.2017.
 */

public class CommentVPAdapter extends PagerAdapter {

    ArrayList<CommentModel> mCommentModel;
    Context mContext;

    public CommentVPAdapter(Context context, ArrayList<CommentModel> commentModel) {
        mContext = context;
        mCommentModel = commentModel;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.list_item_comment, container, false);

        TextView authorTV = (TextView) layout.findViewById(R.id.comment_author);
        authorTV.setText(mCommentModel.get(position).getAuthor());

        TextView contentTV = (TextView) layout.findViewById(R.id.comment_content);
        contentTV.setText(getShortComment(mCommentModel.get(position).getContent()));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(mContext)) {
                    Uri uri = Uri.parse(mCommentModel.get(position).getUrl());
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
        return mCommentModel.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
