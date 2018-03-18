package com.malba.sandbox.activity;

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

import com.malba.sandbox.R;
import com.malba.sandbox.adapter.VerticalMovieAdapter;
import com.malba.sandbox.databinding.ActivityMainBinding;
import com.malba.sandbox.model.SimplePoster;
import com.malba.sandbox.viewmodel.AnimationDurationViewModel;
import com.malba.sandbox.viewmodel.ToggleDecorationsViewModel;
import com.malba.sandbox.viewmodel.ToggleMarginViewModel;
import com.malba.sandbox.viewmodel.ToggleOrientationViewModel;
import com.malba.util.SnappingPreviewItemDecorator;
import com.malba.sandbox.viewmodel.SnapFirstChildViewModel;
import com.malba.sandbox.viewmodel.SnapSecondChildViewModel;
import com.malba.sandbox.viewmodel.SnapFirstParentViewModel;
import com.malba.sandbox.viewmodel.SnapSecondParentViewModel;
import com.malba.widget.SnappingLinearLayoutManager;
import com.malba.widget.SnappingSmoothScroller;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Keep track if our side menu is open.
    private boolean mMenuOpen = false;

    // Keep track of the activity's binding.
    private ActivityMainBinding mBinding;

    private SnappingSmoothScroller.SnappingOptions mSnappingOptions;

    private SnappingPreviewItemDecorator mSnappingDecorator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final VerticalMovieAdapter adapter = new VerticalMovieAdapter( getMockData(999) );
        RecyclerView recyclerView = mBinding.mainRecyclerView;
        recyclerView.setAdapter(adapter);

        mSnappingOptions = new SnappingSmoothScroller.SnappingOptions();
        SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setSnappingOptions(mSnappingOptions);
        recyclerView.setLayoutManager( layoutManager );

        mSnappingDecorator = new SnappingPreviewItemDecorator(mSnappingOptions, this);
        recyclerView.addItemDecoration(mSnappingDecorator);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int position = parent.getChildAdapterPosition(view);
                outRect.right = (position == adapter.getItemCount()) ? 0 : 150;
            }
        });

        setupDataBinding();
    }

    private void setupDataBinding() {
        mBinding.setChildFirstSnap( new SnapFirstChildViewModel(this, mSnappingOptions) );
        mBinding.setChildSecondSnap( new SnapSecondChildViewModel(this, mSnappingOptions) );
        mBinding.setParentFirstSnap( new SnapFirstParentViewModel(this, mSnappingOptions) );
        mBinding.setParentSecondSnap( new SnapSecondParentViewModel(this, mSnappingOptions) );
        mBinding.setAnimationDuration( new AnimationDurationViewModel(this, mSnappingOptions) );
        mBinding.setUseDecorations( new ToggleDecorationsViewModel(this, mSnappingOptions) );
        mBinding.setUseMargins( new ToggleMarginViewModel(this, mSnappingOptions) );
        mBinding.setToggleOrientation( new ToggleOrientationViewModel(this) );

        final Observable.OnPropertyChangedCallback mPropertyListener = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int property) {
                mBinding.mainRecyclerView.invalidateItemDecorations();
            }
        };

        final Observable.OnPropertyChangedCallback mOrientationListener = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                int orientation = mBinding.getToggleOrientation().getValue() ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL;
                SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(MainActivity.this, orientation, false);
                layoutManager.setSnappingOptions(mSnappingOptions);
                mBinding.mainRecyclerView.setLayoutManager( layoutManager );
                mSnappingDecorator.resetOrientation();
            }
        };

        mBinding.getChildFirstSnap().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getChildSecondSnap().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getParentFirstSnap().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getParentSecondSnap().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getUseDecorations().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getUseMargins().addOnPropertyChangedCallback(mPropertyListener);
        mBinding.getToggleOrientation().addOnPropertyChangedCallback(mOrientationListener);
    }

    public ArrayList<SimplePoster> getMockData(int count) {
        ArrayList<SimplePoster> a_SimplePoster = new ArrayList<>(count);

        for(int i=0; i < count; i++) {
            a_SimplePoster.add( new SimplePoster(String.valueOf(i)) );
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