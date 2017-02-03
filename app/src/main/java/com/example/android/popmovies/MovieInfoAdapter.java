package com.example.android.popmovies;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.MovieViewHolder> {

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView poster;
        TextView title;
        TextView rating;
        TextView releaseDate;
        TextView overview;
        TextView popularity;

        public MovieViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.list_item_card);
            poster = (ImageView) itemView.findViewById(R.id.list_item_poster);
            title = (TextView) itemView.findViewById(R.id.list_item_title);
            rating = (TextView) itemView.findViewById(R.id.list_item_rating);
            releaseDate = (TextView) itemView.findViewById(R.id.list_item_release);
            overview = (TextView) itemView.findViewById(R.id.list_item_overview);
            popularity = (TextView) itemView.findViewById(R.id.list_item_popularity);
        }
    }

    Context context;
    ArrayList<MovieInfo> moviesInfo;
    public MovieInfoAdapter(ArrayList<MovieInfo> moviesInfo) {
        this.moviesInfo = moviesInfo;
    }

    @Override
    public int getItemCount() {
        return moviesInfo.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_film, viewGroup, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, int i) {
        MovieInfo movie = moviesInfo.get(i);
        viewHolder.title.setText(movie.title);
        viewHolder.releaseDate.setText(movie.releaseDate);
        viewHolder.overview.setText(movie.overview);
        viewHolder.rating.setText("Rating: " + String.valueOf(movie.rating));
        viewHolder.popularity.setText("Users' popularity: " + String.valueOf(movie.popularity));
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342" + String.valueOf(movie.posterId)).into(viewHolder.poster);
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
}
