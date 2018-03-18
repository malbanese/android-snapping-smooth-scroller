package com.malba.sandbox;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
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
import com.malba.util.SnappingPreviewItemDecorator;
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

    private SnappingSmoothScroller.SnappingOptions mSnappingOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final VerticalMovieAdapter adapter = new VerticalMovieAdapter( getMockData(25) );
        RecyclerView recyclerView = mBinding.mainRecyclerView;
        recyclerView.setAdapter(adapter);

        mSnappingOptions = new SnappingSmoothScroller.SnappingOptions();
        SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        layoutManager.setSnappingOptions(mSnappingOptions);
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.addItemDecoration( new SnappingPreviewItemDecorator(mSnappingOptions, this));
//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                final int position = parent.getChildAdapterPosition(view);
//                outRect.right = (position == adapter.getItemCount()) ? 0 : 150;
//            }
//        });

        setupDataBinding();
    }

    private void setupDataBinding() {
        mBinding.setChildFirstSnap( new ChildFirstSnapViewModel(this, mSnappingOptions) );
        mBinding.setChildSecondSnap( new ChildSecondSnapViewModel(this, mSnappingOptions) );
        mBinding.setParentFirstSnap( new ParentFirstSnapViewModel(this, mSnappingOptions) );
        mBinding.setParentSecondSnap( new ParentSecondSnapViewModel(this, mSnappingOptions) );

        Observable.OnPropertyChangedCallback mPropertyListener = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int property) {
                mBinding.mainRecyclerView.invalidateItemDecorations();
            }
        };

        mBinding.getChildFirstSnap().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getChildSecondSnap().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getParentFirstSnap().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getParentSecondSnap().addOnPropertyChangedCallback(mPropertyListener);

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
