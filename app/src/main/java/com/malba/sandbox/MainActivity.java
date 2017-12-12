package com.malba.sandbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;

import com.malba.sandbox.adapter.VerticalMovieAdapter;
import com.malba.sandbox.model.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VerticalMovieAdapter adapter = new VerticalMovieAdapter( getMockData(25) );
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager( layoutManager );
    }

    public ArrayList<Movie> getMockData(int count) {
        ArrayList<Movie> a_Movie = new ArrayList<>(count);

        for(int i=0; i < count; i++) {
            a_Movie.add( new Movie("Generic Movie " + i) );
        }

        return a_Movie;
    }
}
