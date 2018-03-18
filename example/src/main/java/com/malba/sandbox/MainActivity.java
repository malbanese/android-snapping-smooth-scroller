package com.malba.sandbox;

import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.View;

import com.malba.sandbox.adapter.VerticalMovieAdapter;
import com.malba.sandbox.databinding.ActivityMainBinding;
import com.malba.sandbox.model.SimplePoster;
import com.malba.sandbox.util.SnappingPreviewItemDecorator;
import com.malba.sandbox.viewmodel.ChildFirstSnapViewModel;
import com.malba.sandbox.viewmodel.ChildSecondSnapViewModel;
import com.malba.sandbox.viewmodel.ParentFirstSnapViewModel;
import com.malba.sandbox.viewmodel.ParentSecondSnapViewModel;
import com.malba.widget.SnappingLinearLayoutManager;
import com.malba.widget.SnappingSmoothScroller;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Keep track if our side menu is open.
    private boolean mMenuOpen = false;

    // Keep track of the activity's binding.
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final VerticalMovieAdapter adapter = new VerticalMovieAdapter( getMockData(25) );
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        recyclerView.setAdapter(adapter);

        SnappingSmoothScroller.SnappingOptions snappingOptions = new SnappingSmoothScroller.SnappingOptions();
        SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        layoutManager.setSnappingOptions(snappingOptions);
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.addItemDecoration( new SnappingPreviewItemDecorator(snappingOptions, this));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int position = parent.getChildAdapterPosition(view);
                outRect.right = (position == adapter.getItemCount()) ? 0 : 15;
            }
        });

        mBinding.setChildFirstSnap( new ChildFirstSnapViewModel(this, snappingOptions) );
        mBinding.setChildSecondSnap( new ChildSecondSnapViewModel(this, snappingOptions) );
        mBinding.setParentFirstSnap( new ParentFirstSnapViewModel(this, snappingOptions) );
        mBinding.setParentSecondSnap( new ParentSecondSnapViewModel(this, snappingOptions) );
    }

    public ArrayList<SimplePoster> getMockData(int count) {
        ArrayList<SimplePoster> a_SimplePoster = new ArrayList<>(count);

        for(int i=0; i < count; i++) {
            a_SimplePoster.add( new SimplePoster("Poster " + i) );
        }

        return a_SimplePoster;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_O == keyCode && event.getRepeatCount() == 0) {
            toggleMenu();
        }

        return false;
    }

    private void toggleMenu() {
        mMenuOpen = !mMenuOpen;
        ConstraintSet constraintSet = new ConstraintSet();

        if(mMenuOpen) {
            constraintSet.clone(this, R.layout.activity_main);
        } else {
            constraintSet.clone(this, R.layout.activity_main_browsing_menu);
        }

        TransitionManager.beginDelayedTransition(mBinding.constraintRoot);
        constraintSet.applyTo(mBinding.constraintRoot);
    }
}
