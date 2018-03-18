package com.malba.sandbox.viewmodel;

import android.content.Context;

import com.malba.sandbox.R;
import com.malba.widget.SnappingSmoothScroller;

/**
 * Input section for the child first snap parameter.
 */
public class ChildFirstSnapViewModel extends InputSectionSnapperViewModel {
    private SnappingSmoothScroller.SnappingOptions mOptions;

    public ChildFirstSnapViewModel(Context context, SnappingSmoothScroller.SnappingOptions options) {
        super(context.getResources().getString(R.string.child_first_snap_title));
        mOptions = options;
    }

    @Override
    public void setValue(float value) {
        super.setValue(value);
        mOptions.setChildSnappingLocations(value, mOptions.getChildSecondSnapLocation());
    }

    @Override
    public float getValue() {
        return mOptions.getChildFirstSnapLocation();
    }
}
