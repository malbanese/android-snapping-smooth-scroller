package com.malba.sandbox.viewmodel;

import android.content.Context;

import com.malba.sandbox.R;
import com.malba.widget.SnappingSmoothScroller;

/**
 * Input section for the child second snap parameter.
 */
public class ChildSecondSnapViewModel extends InputSectionSnapperViewModel {
    private SnappingSmoothScroller.SnappingOptions mOptions;

    public ChildSecondSnapViewModel(Context context, SnappingSmoothScroller.SnappingOptions options) {
        super(context.getResources().getString(R.string.child_second_snap_title));
        mOptions = options;
    }

    @Override
    public void setValue(float value) {
        super.setValue(value);
        mOptions.setChildSnappingLocations(mOptions.getChildFirstSnapLocation(), value);
    }

    @Override
    public float getValue() {
        return mOptions.getChildSecondSnapLocation();
    }
}
