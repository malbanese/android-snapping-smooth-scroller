package com.malba.sandbox.interpolator;

import android.view.animation.Interpolator;

/**
 * Interpolation class that allows for the seamless joining of multiple interpolators. It is assumed
 * that all provided interpolators are in the range of 0-1.
 */
public class CombinedInterpolator implements Interpolator {
    // Keep track of the interpolators.
    private InterpolatorPosition[] mInterpolators;

    // Used to avoid finding the interpolator every update cycle.
    private int mLastInterpolatorIndex = 0;

    @Override
    public float getInterpolation(float percent) {
        // Check to see if we need to find a new interpolator for this percentage.
        if(!isValidInterpolatorIndexAtPercent(mLastInterpolatorIndex, percent)) {
            mLastInterpolatorIndex = getInterpolatorIndexAtPercent(percent);
        }

        // As long as we have a valid interpolator index, continue.
        if(mLastInterpolatorIndex != -1) {
            return getCombinedInterpolation(mLastInterpolatorIndex, percent);
        }

        return 0;
    }

    /**
     * Adds a new interpolator which will run from the starting percent, until the next interpolator
     * with a larger starting percent is present.
     * @param interpolator The interpolator to work with.
     * @param startPercent The interpolation percentage it will start at.
     */
    public void addInterpolator(Interpolator interpolator, float startPercent) {
        InterpolatorPosition newPosition = new InterpolatorPosition(interpolator, startPercent);

        if(mInterpolators == null) {
            mInterpolators = new InterpolatorPosition[1];
            mInterpolators[0] = newPosition;
        } else {
            InterpolatorPosition[] interpolators = new InterpolatorPosition[ mInterpolators.length + 1];

            // Calculate the insertion position, and add it.
            final int insertionPosition = findInsertionPosition(startPercent);
            interpolators[insertionPosition] = newPosition;

            // Copy the first half of our array.
            if(insertionPosition > 0) {
                System.arraycopy(mInterpolators, 0, interpolators, 0, insertionPosition);
            }

            // Copy the second half of our array
            if(insertionPosition < mInterpolators.length) {
                System.arraycopy(mInterpolators, insertionPosition, interpolators, insertionPosition + 1, mInterpolators.length - insertionPosition);
            }

            // Reset the interpolators array.
            mInterpolators = interpolators;
        }
    }

    /**
     * Can be used to retrieve an interpolator running at a particular percent.
     * @param percent The percent to search an interpolator for.
     * @return The index for the running interpolator.
     */
    public int getInterpolatorIndexAtPercent(float percent) {
        if(mInterpolators != null) {
            for (int i = 0; i < mInterpolators.length; i++) {
               if (mInterpolators[i].mStartPercent > percent) {
                    // If we have i as 0 at this point, no interpolator was bound early enough
                    // to trigger the provided interpolator.
                    return (i == 0) ? -1 : (i-1);
               } else if (i == mInterpolators.length - 1) {
                   // We are the last interpolator, and still less than the percent
                   // We can return the provided interpolator.
                   return i;
               }
            }
        }

        return -1;
    }

    /**
     * Finds the insertion position, given a starting percentage.
     * @param startPercent The starting position to insert.
     * @return The position that should be inserted at, or -1 if the interpolation array has
     * not been created yet. May return the current size of the interpolator array.
     */
    private int findInsertionPosition(float startPercent) {
        if(mInterpolators != null) {
            for (int i = 0; i < mInterpolators.length; i++) {
                if (startPercent < mInterpolators[i].mStartPercent) {
                    return i;
                }
            }

            return mInterpolators.length;
        }

        return -1;
    }

    /**
     * Used to check that an interpolator at the provided index is valid for the provided percent.
     * @param index The interpolator array index to check at the percent.
     * @param percent The percent to test the validity of the index at.
     * @return True if the combination uses the correct interpolator.
     */
    public boolean isValidInterpolatorIndexAtPercent(int index, float percent) {
        // Check the bounds, and that the start percent is greater than or equal to the index percent.
        // Check to make sure the start percent is greater or equal to the index position
        // Check to make sure the index is at the last interpolator OR the next one has a greater percent.
        return index < mInterpolators.length
            && index >= 0
            && mInterpolators[index].mStartPercent <= percent
            && (index == mInterpolators.length - 1 || mInterpolators[index+1].mStartPercent > percent );
    }

    /**
     * Gets interpolation by interpolating linearly across the interpolation at the specified index.
     * @param index The index of the interpolator to use.
     * @param percent The perccent to interpolate with.
     * @return The interpolation value.
     */
    private float getCombinedInterpolation(int index, float percent) {
        // Get the starting and the ending percentages.
        final InterpolatorPosition position = mInterpolators[index];
        final float startPercent = position.mStartPercent;
        final float endPercent = (mInterpolators.length - 1 == index) ?
                1.0f : mInterpolators[index + 1].mStartPercent;

        // Gets the slice percent for this interpolator.
        final float slicePercent = percent / endPercent - startPercent;

        // Return the scaled percentage for this interpolation slice.
        return startPercent + (endPercent - startPercent) * position.mInterpolator.getInterpolation(slicePercent);
    }

    /**
     * Internal class to keep track of the interpolators, and the positions they should trigger at.
     */
    private class InterpolatorPosition {
        final Interpolator mInterpolator;
        final float mStartPercent;

        /**
         * Creates a new interpolator at the provided starting percent.
         * @param interpolator The interpolator to trigger.
         * @param startPercent The starting percent to trigger at.
         */
        InterpolatorPosition(Interpolator interpolator, float startPercent) {
            mInterpolator = interpolator;
            mStartPercent = startPercent;
        }
    }
}
