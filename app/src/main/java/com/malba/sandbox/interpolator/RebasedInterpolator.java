package com.malba.sandbox.interpolator;

import android.view.animation.Interpolator;

public class RebasedInterpolator implements Interpolator{
    private Interpolator mBaseInterpolator;
    private float mStartValue;

    public RebasedInterpolator(Interpolator baseInterpolator, float startingPercent) {
        mBaseInterpolator = baseInterpolator;
        mStartValue = mBaseInterpolator.getInterpolation(startingPercent);
    }

    @Override
    public float getInterpolation(float percent) {
        return mBaseInterpolator.getInterpolation(percent) - mStartValue + (mStartValue * percent);
    }
}
