package com.malba.sandbox.viewmodel;

import android.content.Context;

import com.malba.sandbox.R;
import com.malba.widget.SnappingSmoothScroller;

/**
 * Input section for the animation duration section.
 */
public class AnimationDurationViewModel extends InputSectionSnapperViewModel {
    private SnappingSmoothScroller.SnappingOptions mOptions;

    public AnimationDurationViewModel(Context context, SnappingSmoothScroller.SnappingOptions options) {
        super(context.getResources().getString(R.string.animation_duration_title));
        setMaxSliderValue(1500);
        mOptions = options;
    }

    @Override
    public void setValue(float value) {
        super.setValue(value);
        mOptions.setSnapDuration((int)(value));
    }

    @Override
    public void setValuePercent(int percent) {
        setValue(percent);
    }

    @Override
    public int getValuePercent() {
        return (int)getValue();
    }

    @Override
    public float getValue() {
        return mOptions.getSnapDuration();
    }
}
