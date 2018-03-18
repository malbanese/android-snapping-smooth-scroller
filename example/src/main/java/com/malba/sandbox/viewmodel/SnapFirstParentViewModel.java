package com.malba.sandbox.viewmodel;

import android.content.Context;

import com.malba.sandbox.R;
import com.malba.widget.SnappingSmoothScroller;

/**
 * Input section for the parent first snap parameter.
 */
public class SnapFirstParentViewModel extends InputSectionSnapperViewModel {
    private SnappingSmoothScroller.SnappingOptions mOptions;

    public SnapFirstParentViewModel(Context context, SnappingSmoothScroller.SnappingOptions options) {
        super(context.getResources().getString(R.string.parent_first_snap_title));
        mOptions = options;
    }

    @Override
    public void setValue(float value) {
        super.setValue(value);
        mOptions.setParentSnappingLocations(value, mOptions.getParentSecondSnapLocation());
    }

    @Override
    public float getValue() {
        return mOptions.getParentFirstSnapLocation();
    }
}
