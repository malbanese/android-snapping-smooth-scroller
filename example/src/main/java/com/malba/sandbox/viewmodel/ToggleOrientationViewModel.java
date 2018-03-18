package com.malba.sandbox.viewmodel;


import android.content.Context;
import android.databinding.Bindable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.malba.sandbox.R;
import com.malba.widget.SnappingLinearLayoutManager;
import com.malba.widget.SnappingSmoothScroller;

/**
 * Input section for the toggle margins parameter.
 */
public class ToggleOrientationViewModel extends InputToggleViewModel {
    private boolean mToggle = false;

    public ToggleOrientationViewModel(Context context) {
        super(context.getResources().getString(R.string.orientation_toggle));
    }

    @Override
    public void setValue(boolean value) {
        super.setValue(value);
        mToggle = value;
    }

    @Override
    public boolean getValue() {
        return mToggle;
    }
}
