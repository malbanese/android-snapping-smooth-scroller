package com.malba.sandbox;

import android.content.Context;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * This class aims to provide a more configurable version of the original LinearSmoothScroller.
 * The primary feature that differentiates it, is the ability to specify snapping points on both
 * the child, and the layout manager.
 */
public class SnappingSmoothScroller extends LinearSmoothScroller {
    /**
     * Enumeration value denoting a calculation in the horizontal direction.
     */
    public static final int DIRECTION_X = 0;

    /**
     * Enumeration value denoting a calculation in the vertical direction.
     */
    public static final int DIRECTION_Y = 1;

    /**
     * The options used to calculate snapping parameters.
     */
    private SnappingOptions mOptions;

    public SnappingSmoothScroller(Context context) {
        super(context);
    }

    /**
     * Sets the snapping options which are used to calculate snapping points.
     * @param options The options used to calculate snapping points.
     */
    public void setSnappingOptions(SnappingOptions options) {
        mOptions = options;
    }

    /**
     * Calculates a snapping location, given the correct parameters.
     * @param start The starting point.
     * @param end The ending point.
     * @param snapLocation The location which should be snapped to.
     * @return The absolute snapping location.
     */
    public int calculateSnapLocation(int start, int end, float snapLocation) {
        if(Math.abs(snapLocation) <= 1.0f) {
            return Math.round(start + (end - start) * snapLocation);
        }

        return Math.round(start + snapLocation);
    }

    /**
     * Calculates the snapping location for the provided child.
     * @param child The child to calculate the snapping locations on.
     * @param snapResult The array to save the result to.
     * @param direction The direction the calculations should be made in. DIRECTION_X, DIRECTION_Y.
     * @return snapResult.
     */
    private float[] calculateChildSnapLocations(View child, float[] snapResult, int direction) {
        final RecyclerView.LayoutManager layoutManager = getLayoutManager();
        snapResult[0] = snapResult[1] = 0;

        if(layoutManager != null) {
            // Use the layout params to get additional measurement information
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            if(direction == DIRECTION_X && layoutManager.canScrollHorizontally()) {
                final int start = layoutManager.getDecoratedLeft(child) - params.leftMargin;
                final int end = layoutManager.getDecoratedRight(child) + params.rightMargin;
                snapResult[0] = calculateSnapLocation(start, end, mOptions.mChildSnap[0]);
                snapResult[1] = calculateSnapLocation(start, end, mOptions.mChildSnap[1]);
            }

            if(direction == DIRECTION_Y && layoutManager.canScrollVertically()) {
                final int start = layoutManager.getDecoratedTop(child) - params.topMargin;
                final int end = layoutManager.getDecoratedBottom(child) + params.bottomMargin;
                snapResult[0] = calculateSnapLocation(start, end, mOptions.mChildSnap[0]);
                snapResult[1] = calculateSnapLocation(start, end, mOptions.mChildSnap[1]);
            }
        }

        return snapResult;
    }

    /**
     * Calculates the snapping location for the layout manager.
     * @param snapResult The array to save the result to.
     * @param direction The direction the calculations should be made in. DIRECTION_X, DIRECTION_Y.
     * @return snapResult.
     */
    private float[] calculateParentSnapLocations(float[] snapResult, int direction) {
        final RecyclerView.LayoutManager layoutManager = getLayoutManager();
        snapResult[0] = snapResult[1] = 0;

        if(layoutManager != null) {
            // Starting and ending locations to base our calculations off of.
            final int start, end;

            // Calculate the parent start and end positions for the correct direction.
            if(direction == DIRECTION_X) {
                start = layoutManager.getPaddingLeft();
                end = layoutManager.getWidth() - layoutManager.getPaddingRight();
            } else {
                start = layoutManager.getPaddingTop();
                end = layoutManager.getHeight() - layoutManager.getPaddingBottom();
            }

            snapResult[0] = calculateSnapLocation(start, end, mOptions.mParentSnap[0]);
            snapResult[1] = calculateSnapLocation(start, end, mOptions.mParentSnap[1]);
        }

        return snapResult;
    }

    /**
     * Calculates the snapping distance that should be scrolled to snap the child into place.
     * @param child The child we are trying to snap into place.
     * @param direction The scrolling direction.
     * @return The distance that should be translated for a snap to occur.
     */
    public int calculateSnappingDistance(View child, int direction) {
        LayoutManager layoutManager = getLayoutManager();

        if(layoutManager != null) {
            if ((direction == DIRECTION_X && layoutManager.canScrollHorizontally()) ||
                    (direction == DIRECTION_Y && layoutManager.canScrollVertically())) {
                // Calculate the true snapping positions.
                float[] childSnap = calculateChildSnapLocations(child, new float[2], direction);
                float[] parentSnap = calculateParentSnapLocations(new float[2], direction);

                // The calculated snap distances.
                int[] snapDistance = {
                        (int) (parentSnap[0] - childSnap[0]),
                        (int) (parentSnap[1] - childSnap[1])
                };

                // We can snap at 0, but not 1.
                if (snapDistance[0] > 0 && snapDistance[1] > 0) {
                    return snapDistance[0];
                }

                // We can snap at 1, but not 0.
                if (snapDistance[0] < 0 && snapDistance[1] < 0) {
                    return snapDistance[1];
                }

                // We can snap at either, choose the closest.
                if (snapDistance[0] > 0 && snapDistance[1] < 0) {
                    return (snapDistance[0] < -snapDistance[1]) ? snapDistance[0] : snapDistance[1];
                }
            }
        }

        // Both snap points on the child were between the parent snap points, nothing neede :)
        return 0;
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return mOptions.mMSPerInch / displayMetrics.densityDpi;
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        final int dx = calculateSnappingDistance(targetView, DIRECTION_X);
        final int dy = calculateSnappingDistance(targetView, DIRECTION_Y);

        if(dx != 0 || dy != 0) {
            action.update(-dx, -dy, 500, mOptions.mTargetFoundInterpolator);
        }
    }

    /**
     * Class used to configure snapping options for a SnappingSmoothScroller instance.
     */
    public static class SnappingOptions {
        // The number of MS it will take to scroll one inch during seeking.
        private float mMSPerInch = 25.0f;

        // The location on the child which will snap to the parent/
        private float[] mChildSnap = { 0.50f, 0.50f };

        // The location on the parent which will snap to the child.
        private float[] mParentSnap = { 0.25f, 0.75f };

        // The interpolator that will be used during the target found phase
        private Interpolator mTargetFoundInterpolator = new DecelerateInterpolator();

        /**
         * @param msPerInch The number of MS it will take to animate over one inch of screen space,
         *                  while seeking towards the view belonging to our scrolling position. Only
         *                  utilized when the view is not yet bound to the RecyclerView.
         */
        public void setMSPerInch(float msPerInch) {
            this.mMSPerInch = msPerInch;
        }

        /**
         * Sets up the positions on the child which will attempt to snap to the corresponding position
         * on the parent. If (value <= 1.0f), a percentage will be used. If (value > 1.0f) a pixel
         * value will be used.
         * @param firstLocation The first child snapping location.
         * @param secondLocation The second child snapping location.
         */
        public void setChildSnappingLocations(float firstLocation, float secondLocation) {
            mChildSnap[0] = firstLocation;
            mChildSnap[1] = secondLocation;
        }

        /**
         * Sets up the positions on the child which will attempt to snap to the corresponding position
         * on the parent. If (value <= 1.0f), a percentage will be used. If (value > 1.0f) a pixel
         * value will be used.
         * @param location The child snapping location.
         */
        public void setChildSnappingLocation(float location) {
            setChildSnappingLocations(location, location);
        }

        /**
         * Sets up the positions on the parent which will be used as an anchor to snap to the
         * corresponding position on the child. If (value <= 1.0f), a percentage will be used.
         * If (value > 1.0f) a pixel value will be used.
         * @param firstLocation The first parent snapping location.
         * @param secondLocation The second parent snapping location.
         */
        public void setParentSnappingLocations(float firstLocation, float secondLocation) {
            mParentSnap[0] = firstLocation;
            mParentSnap[1] = secondLocation;
        }

        /**
         * Sets up the positions on the parent which will be used as an anchor to snap to the
         * corresponding position on the child. If (value <= 1.0f), a percentage will be used.
         * If (value > 1.0f) a pixel value will be used.
         * @param location The child snapping location.
         */
        public void setParentSnappingLocation(float location) {
            setParentSnappingLocations(location, location);
        }

        /**
         * @param targetFoundInterpolator Sets the interpolator which will be used when the scroll
         *                                position's view is attached to the RecyclerView.
         */
        public void setTargetFoundInterpolator(Interpolator targetFoundInterpolator) {
            this.mTargetFoundInterpolator = targetFoundInterpolator;
        }
    }
}
