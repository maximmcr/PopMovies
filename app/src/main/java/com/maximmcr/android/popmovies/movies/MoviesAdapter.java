package com.maximmcr.android.popmovies.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frei on 24.10.2017.
 */

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private ArrayList<Movie> mMovies;
    private Context mContext;
    private OnItemClickListener mListener;
    private View mImageView;

    MoviesAdapter(Context context, ArrayList<Movie> movies, OnItemClickListener listener) {
        mContext = context;
        mMovies = movies;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(
                mContext).inflate(R.layout.list_item_movie, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder viewHolder, final int position) {

        final Movie movie = mMovies.get(position);

        Picasso.with(mContext)
                .load(String.valueOf(movie.getPosterPath()))
                .error(R.drawable.image_not_loaded)
                .placeholder(R.drawable.progress_animation)
                .into(viewHolder.poster);

        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView = (View) viewHolder.poster;
                if (mListener != null) {
                    mListener.onItemClick(movie);
                }
            }
        });
    }

//    void removeMovie(int id) {
//        int position = 0;
//        for (int i = 0; i < mMovies.size(); i++) {
//            if (mMovies.get(i).getId() == id) {
//                mMovies.remove(i);
//                position = i;
//                break;
//            }
//        }
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, mMovies.size() - position);
//    }

    void replaceMovies(ArrayList<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    View getImageView() {
        return mImageView;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_poster)
        ImageView poster;

        MovieViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface OnItemClickListener {
        void onItemClick(Movie movie);
    }
}
