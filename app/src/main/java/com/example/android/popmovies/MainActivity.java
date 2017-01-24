package com.example.android.popmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    MovieInfoAdapter movieInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView view = (GridView) findViewById(R.id.activity_main);
        MovieInfo[] movies = {
                new MovieInfo(R.mipmap.ic_launcher, "Example1"),
                new MovieInfo(R.mipmap.ic_launcher, "Example2"),
                new MovieInfo(R.mipmap.ic_launcher, "Example3"),
                new MovieInfo(R.mipmap.ic_launcher, "Example4"),
                new MovieInfo(R.mipmap.ic_launcher, "Example5"),
                new MovieInfo(R.mipmap.ic_launcher, "Example6"),
                new MovieInfo(R.mipmap.ic_launcher, "Example7"),
                new MovieInfo(R.mipmap.ic_launcher, "Example8"),
                new MovieInfo(R.mipmap.ic_launcher, "Example9"),
                new MovieInfo(R.mipmap.ic_launcher, "Example10")
        };
        movieInfoAdapter = new MovieInfoAdapter(this, Arrays.asList(movies));
        view.setAdapter(movieInfoAdapter);
    }
}
