package com.malba.sandbox.interpolator;

import android.view.animation.Interpolator;

public class TransformingInterpolator implements Interpolator {
    /**
     * Generates a new interpolator that transitions from the starting velocity, to the terminal
     * velocity of the provided interpolator.
     * @param startingVelocity The velocity in px/ms the interpolation should start at.
     * @param distance The total distance the transformed interpolator will cover.
     * @param duration The total amount of time the interpolator will be used for.
     * @param transitionPercent The percentage of the interpolation that should be used for transitioning.
     */
    public Interpolator transitionInterpolator(float startingVelocity, float distance, int duration, float transitionPercent) {
        // Calculate the transition interpolation parameters
        final int transitionDuration = (int) (duration * transitionPercent);
        final float transitionDistance = distance * transitionPercent;

        // Calculate the easing interpolation parameters
        final int easingDuration = (int) (duration * (1 - transitionPercent));
        final float easingDistance = distance * (1 - transitionPercent);

        // Calculate the base max velocity of the interpolator.
        final float maxVelocityPercent = findMaxVelocityPercent();
        final float maxVelocity = getVelocityAtTime(easingDistance, easingDuration, (int) (maxVelocityPercent * easingDuration));


        final Interpolator rebasedInterpolator = new RebasedInterpolator(mBaseInterpolator, maxVelocityPercent);

        return null;
    }

    private Interpolator mBaseInterpolator;

    public TransformingInterpolator(Interpolator baseInterpolator) {
        mBaseInterpolator = baseInterpolator;
    }

    /**
     * Returns the velocity at a specified time.
     * @param distance The total distance being traveled by the interpolator.
     * @param duration The total time being interpolated.
     * @param currentTime The time to grab the velocity at. Must be [0 - duration].
     * @return The velocity of the interpolator given a time.
     */
    public float getVelocityAtTime(float distance, int duration, int currentTime) {
        if(currentTime > 0) {
            // Grab the interpolation at the current time, and the previous millisecond.
            final float prevInterpolation = getInterpolation( (currentTime - 1) / duration );
            final float curInterpolation = getInterpolation( currentTime / duration );

            // Return the difference, multiplied by the distance.
            return (curInterpolation - prevInterpolation) * distance;
        }

        return 0;
    }

    /**
     * Finds the point of maximum velocity for this interpolator. Searches using 100 slices.
     * @return The point of maximum velocity.
     */
    public float findMaxVelocityPercent() {
        return findMaxVelocityPercent(100);
    }

    /**
     * Finds the point of maximum velocity for this interpolator.
     * @param sliceCount The number of slices that will be looked at to find the maximum.
     * @return The point of maximum velocity.
     */
    public float findMaxVelocityPercent(int sliceCount) {
        final float slice = 1.0f / sliceCount;

        float maxVelocity = 0;
        float maxVelocityPercent = 0;
        float prevInterpolation = 0;

        // Search for the maximum velocity.
        for(int i=0; i < sliceCount; i++) {
            final float interpolation = getInterpolation(slice * i);
            final float velocity = interpolation - prevInterpolation;
            prevInterpolation = interpolation;

            if(velocity > maxVelocity) {
                maxVelocity = velocity;
                maxVelocityPercent = slice * i;
            }
        }

        return maxVelocityPercent;
    }

    @Override
    public float getInterpolation(float v) {
        return mBaseInterpolator.getInterpolation(v);
    }
}
