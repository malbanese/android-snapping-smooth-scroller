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
import com.malba.sandbox.adapter.PosterAdapter;
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

public class SnappingPlaygroundActivity extends AppCompatActivity {
    // Keep track if our side menu is open.
    private boolean mMenuOpen = false;

    // Keep track of the activity's binding.
    private ActivityMainBinding mBinding;

    // Keep track of the smooth scroller options for when we toggle the layout orientation
    private SnappingSmoothScroller.SnappingOptions mSnappingOptions;

    // Keep track of the snapping decorator for when we toggle the layout direction.
    private SnappingPreviewItemDecorator mSnappingDecorator;

    // Property listener so we can update the item decorations.
    final Observable.OnPropertyChangedCallback mPropDecorationListener = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int property) {
            mBinding.mainRecyclerView.invalidateItemDecorations();
        }
    };

    // Property listener so we can swap orientations on the layout manager.
    final Observable.OnPropertyChangedCallback mPropOrientationListener = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            int orientation = mBinding.getToggleOrientation().getValue() ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL;
            SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(SnappingPlaygroundActivity.this, orientation, false);
            layoutManager.setSnappingOptions(mSnappingOptions);
            mBinding.mainRecyclerView.setLayoutManager(layoutManager);
            mSnappingDecorator.resetOrientation();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mSnappingOptions = new SnappingSmoothScroller.SnappingOptions();
        mSnappingDecorator = new SnappingPreviewItemDecorator(mSnappingOptions, this);

        // Set up the adapter
        final PosterAdapter adapter = new PosterAdapter( getMockData(999) );

        // Set up the layout manager
        SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setSnappingOptions(mSnappingOptions);

        // Set up the RecyclerView
        RecyclerView recyclerView = mBinding.mainRecyclerView;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( layoutManager );
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

    /**
     * Sets up the databinding with the views in our options menu, so that we can interact
     * with the various options.
     */
    private void setupDataBinding() {
        mBinding.setChildFirstSnap( new SnapFirstChildViewModel(this, mSnappingOptions) );
        mBinding.setChildSecondSnap( new SnapSecondChildViewModel(this, mSnappingOptions) );
        mBinding.setParentFirstSnap( new SnapFirstParentViewModel(this, mSnappingOptions) );
        mBinding.setParentSecondSnap( new SnapSecondParentViewModel(this, mSnappingOptions) );
        mBinding.setAnimationDuration( new AnimationDurationViewModel(this, mSnappingOptions) );
        mBinding.setUseDecorations( new ToggleDecorationsViewModel(this, mSnappingOptions) );
        mBinding.setUseMargins( new ToggleMarginViewModel(this, mSnappingOptions) );
        mBinding.setToggleOrientation( new ToggleOrientationViewModel(this) );

        mBinding.getChildFirstSnap().addOnPropertyChangedCallback(mPropDecorationListener);
        mBinding.getChildSecondSnap().addOnPropertyChangedCallback(mPropDecorationListener);
        mBinding.getParentFirstSnap().addOnPropertyChangedCallback(mPropDecorationListener);
        mBinding.getParentSecondSnap().addOnPropertyChangedCallback(mPropDecorationListener);
        mBinding.getUseDecorations().addOnPropertyChangedCallback(mPropDecorationListener);
        mBinding.getUseMargins().addOnPropertyChangedCallback(mPropDecorationListener);
        mBinding.getToggleOrientation().addOnPropertyChangedCallback(mPropOrientationListener);
    }

    /**
     * Generates a mock data array.
     * @param count The number of data items to create.
     * @return A populated mock data array.
     */
    private ArrayList<SimplePoster> getMockData(int count) {
        ArrayList<SimplePoster> a_SimplePoster = new ArrayList<>(count);

        for(int i=0; i < count; i++) {
            a_SimplePoster.add( new SimplePoster(String.valueOf(i)) );
        }

        return a_SimplePoster;
    }

    /**
     * Overridden to open the options menu on the 'O' key.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_O == keyCode && event.getRepeatCount() == 0) {
            toggleMenu();
        }

        return false;
    }

    /**
     * Toggles the menu being opened / closed.
     */
    private void toggleMenu() {
        mMenuOpen = !mMenuOpen;
        int targetLayout = (mMenuOpen) ? R.layout.activity_main_browsing_menu : R.layout.activity_main;

        TransitionManager.beginDelayedTransition(mBinding.constraintRoot);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, targetLayout);
        constraintSet.applyTo(mBinding.constraintRoot);
    }
}
