package com.malba.sandbox.viewmodel;


import android.content.Context;

import com.malba.sandbox.R;
import com.malba.widget.SnappingSmoothScroller;

/**
 * Input section for the toggle margins parameter.
 */
public class ToggleMarginViewModel extends InputToggleViewModel {
    private SnappingSmoothScroller.SnappingOptions mOptions;

    public ToggleMarginViewModel(Context context, SnappingSmoothScroller.SnappingOptions options) {
        super(context.getResources().getString(R.string.margin_toggle));
        mOptions = options;
    }

    @Override
    public void setValue(boolean value) {
        super.setValue(value);
        mOptions.setIncludeChildMargins(value);
    }

    @Override
    public boolean getValue() {
        return mOptions.getIncludeChildMargins();
    }
}
