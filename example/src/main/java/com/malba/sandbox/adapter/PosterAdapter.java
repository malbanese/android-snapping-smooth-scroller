package com.malba.sandbox.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malba.sandbox.R;
import com.malba.sandbox.databinding.PosterBinding;
import com.malba.sandbox.model.SimplePoster;

import java.util.ArrayList;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder> {
    private ArrayList<SimplePoster> a_SimplePoster = new ArrayList<>();

    public PosterAdapter(ArrayList<SimplePoster> a_SimplePoster) {
        this.a_SimplePoster = a_SimplePoster;
    }

    private RecyclerView mRecyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        PosterBinding binding;

        ViewHolder(final PosterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setMovie(SimplePoster simplePoster) {
            binding.setSimplePoster(simplePoster);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PosterBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.poster, parent,
                false);

        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setMovie( a_SimplePoster.get(position) );
    }

    @Override
    public int getItemCount() {
        return a_SimplePoster.size();
    }
}
