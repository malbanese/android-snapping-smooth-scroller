package com.malba.sandbox.viewmodel;

import android.content.Context;

import com.malba.sandbox.R;
import com.malba.widget.SnappingSmoothScroller;

/**
 * Input section for the parent second snap parameter.
 */
public class SnapSecondParentViewModel extends InputSectionSnapperViewModel {
    private SnappingSmoothScroller.SnappingOptions mOptions;

    public SnapSecondParentViewModel(Context context, SnappingSmoothScroller.SnappingOptions options) {
        super(context.getResources().getString(R.string.parent_second_snap_title));
        mOptions = options;
    }

    @Override
    public void setValue(float value) {
        super.setValue(value);
        mOptions.setParentSnappingLocations(mOptions.getParentFirstSnapLocation(), value);
    }

    @Override
    public float getValue() {
        return mOptions.getParentSecondSnapLocation();
    }
}
