package com.example.android.popmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.MovieViewHolder> {

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;


        public MovieViewHolder(final View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.list_item_poster);
        }
    }

    Context context;
    ArrayList<MovieInfo> moviesInfo;
    Activity main;

    public MovieInfoAdapter(ArrayList<MovieInfo> moviesInfo, Activity activity) {
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
        final MovieInfo movie = moviesInfo.get(position);
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w342" + String.valueOf(movie.posterId))
                .error(R.drawable.image_not_loaded)
                .placeholder(R.drawable.progress_animation)
                .into(viewHolder.poster);

        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    Intent intent = new Intent(context, DetailedActivity.class);
                    intent.putExtra("id", String.valueOf(moviesInfo.get(position).id));
                    intent.putExtra("rating", movie.rating);
                    intent.putExtra("popularity", movie.popularity);

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

    public void clear() {
        if (moviesInfo.size() > 0) {
            this.moviesInfo.clear();
        }
        this.notifyItemRangeRemoved(0, moviesInfo.size());
    }

    public void addAll(ArrayList<MovieInfo> movies) {
        for (int i = 0; i < movies.size(); i++) {
            moviesInfo.add(movies.get(i));
        }
        this.notifyItemRangeInserted(0, moviesInfo.size());
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
