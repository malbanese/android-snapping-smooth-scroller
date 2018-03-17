package com.malba.sandbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.malba.sandbox.adapter.VerticalMovieAdapter;
import com.malba.sandbox.model.SimplePoster;
import com.malba.sandbox.util.SnappingPreviewItemDecorator;
import com.malba.widget.SnappingLinearLayoutManager;
import com.malba.widget.SnappingSmoothScroller;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VerticalMovieAdapter adapter = new VerticalMovieAdapter( getMockData(25) );
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        recyclerView.setAdapter(adapter);

        SnappingSmoothScroller.SnappingOptions snappingOptions = new SnappingSmoothScroller.SnappingOptions();
        SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        layoutManager.setSnappingOptions(snappingOptions);
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.addItemDecoration( new SnappingPreviewItemDecorator(snappingOptions, this));
    }

    public ArrayList<SimplePoster> getMockData(int count) {
        ArrayList<SimplePoster> a_SimplePoster = new ArrayList<>(count);

        for(int i=0; i < count; i++) {
            a_SimplePoster.add( new SimplePoster("Poster " + i) );
        }

        return a_SimplePoster;
    }
}
