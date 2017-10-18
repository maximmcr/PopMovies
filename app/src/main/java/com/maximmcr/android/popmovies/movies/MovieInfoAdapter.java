package com.maximmcr.android.popmovies.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maximmcr.android.popmovies.moviedetails.DetailedActivity;
import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.Utility;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.MovieViewHolder>
        implements DetailedActivity.CallbackMovieRemoved {

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;

        public MovieViewHolder(final View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.list_item_poster);
        }
    }

    Context context;
    ArrayList<Movie> moviesInfo;
    Activity main;

    public MovieInfoAdapter(ArrayList<Movie> moviesInfo, Activity activity) {
        this.moviesInfo = moviesInfo;
        main = activity;
    }

    @Override
    public int getItemCount() {
        return moviesInfo.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_film, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder viewHolder, final int position) {
        final Movie movie = moviesInfo.get(position);
        if (Utility.isOptionSaved(context)) {
            viewHolder.poster.setImageBitmap(Utility.stringToBitmap(movie.getPosterPath()));
        } else {
            Picasso.with(context)
                    .load(context.getString(R.string.poster_base_path) + String.valueOf(movie.getPosterPath()))
                    .error(R.drawable.image_not_loaded)
                    .placeholder(R.drawable.progress_animation)
                    .into(viewHolder.poster);
        }

        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utility.isOptionSaved(context) || Utility.isOnline(context)) {
                    Intent intent = new Intent(context, DetailedActivity.class);
                    intent.putExtra("id", String.valueOf(moviesInfo.get(position).getId()));

                    Pair<View, String> pairImg = Pair.create(
                            (View) viewHolder.poster, context.getText(R.string.transition_poster).toString());

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            main, pairImg);

                    context.startActivity(intent, options.toBundle());
                } else {
                    Snackbar.make(v, "There is no internet connection!", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    public void clearAll() {
        if (moviesInfo.size() > 0) {
            this.moviesInfo.clear();
        }
        this.notifyItemRangeRemoved(0, moviesInfo.size());
    }

    @Override
    public void removeMovie(int id) {
        int position = 0;
        for (int i = 0; i < moviesInfo.size(); i++) {
            if (moviesInfo.get(i).getId() == id) {
                moviesInfo.remove(i);
                position = i;
                break;
            }
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, moviesInfo.size() - position);
    }

    public void addAll(ArrayList<Movie> movies) {
        for (int i = 0; i < movies.size(); i++) {
            moviesInfo.add(movies.get(i));
        }
        this.notifyItemRangeInserted(0, moviesInfo.size());
    }
}
