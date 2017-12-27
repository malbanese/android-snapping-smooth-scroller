package com.malba.sandbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.malba.sandbox.adapter.VerticalMovieAdapter;
import com.malba.sandbox.model.Movie;
import com.malba.widget.SnappingLinearLayoutManager;

import java.util.ArrayList;

public class FocusFixActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_fix);

        VerticalMovieAdapter adapter = new VerticalMovieAdapter( getMockData(250) );
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        LinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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