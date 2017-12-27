package com.malba.sandbox.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malba.sandbox.R;
import com.malba.sandbox.databinding.MovieRowBinding;
import com.malba.sandbox.model.Movie;

import java.util.ArrayList;

public class VerticalMovieAdapter extends RecyclerView.Adapter<VerticalMovieAdapter.ViewHolder> {
    private ArrayList<Movie> a_Movie = new ArrayList<>();

    public VerticalMovieAdapter(ArrayList<Movie> a_Movie) {
        this.a_Movie = a_Movie;
    }

    private RecyclerView mRecyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MovieRowBinding binding;

        public ViewHolder(final MovieRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    mRecyclerView.smoothScrollToPosition( mRecyclerView.getChildAdapterPosition(binding.getRoot()) );
                    mRecyclerView.smoothScrollToPosition(20);
                }
            });
        }

        public void setMovie(Movie movie) {
            binding.setMovie(movie);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MovieRowBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.movie_row, parent,
                false);

        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setMovie( a_Movie.get(position) );
    }

    @Override
    public int getItemCount() {
        return a_Movie.size();
    }
}
