package com.malba.sandbox.interpolator;

import android.view.animation.Interpolator;

/**
 * Interpolator that ingests an existing interpolator, and rebases it around a starting percent.
 * The resultant interpolator will represent I(startPercent -> 1.0) and will interpolate between
 * I(0,1.0).
 */
public class RebasedInterpolator implements Interpolator {
    private Interpolator mBaseInterpolator;
    private float mStartValue;
    private float mStartPercent;

    /**
     * @param baseInterpolator The interpolator to rebase.
     * @param startingPercent The starting percentage to start rebasing at.
     */
    public RebasedInterpolator(Interpolator baseInterpolator, float startingPercent) {
        mBaseInterpolator = baseInterpolator;
        mStartValue = mBaseInterpolator.getInterpolation(startingPercent);
        mStartPercent = startingPercent;
    }

    @Override
    public float getInterpolation(float percent) {
        // Get the rebased percent, taking into consideration the starting percent.
        final float rebasedPercent = mStartPercent + (1.0f - mStartPercent) * percent;

        // Return the rebased interpolator value, taking into consideration the starting value.
        return mBaseInterpolator.getInterpolation(rebasedPercent) - mStartValue * (1.0f - percent);
    }
}
