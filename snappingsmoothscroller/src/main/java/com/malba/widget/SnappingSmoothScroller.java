package com.malba.widget;

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
@SuppressWarnings("WeakerAccess")
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
     * Identifier to identify when the start time of the snapping interpolator has not yet happened.
     */
    public static final long NO_START_SNAP_TIME = -1;

    /**
     * The options used to calculate snapping parameters.
     */
    private SnappingOptions mOptions;

    /**
     * Keep track of when we started snapping the child to the parent.
     */
    private long mSnapStartTime = NO_START_SNAP_TIME;

    /**
     * The calculated milliseconds per pixels that this will scroll when searching.
     */
    private float mCalculatedMillisPerPx;

    /**
     * @param options The options used to calculate snapping points.
     * @param context The context in which this smooth scroller belongs to.
     */
    public SnappingSmoothScroller(SnappingOptions options, Context context) {
        super(context);
        setSnappingOptions(options, context);
    }

    /**
     * Sets the snapping options associated with this smooth scroller.
     * @param options The options used to calculate snapping points.
     * @param context The context in which this smooth scroller belongs to.
     */
    public void setSnappingOptions(SnappingOptions options, Context context) {
        mOptions = options;
        mCalculatedMillisPerPx = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }

    /**
     * Calculates a snapping location.
     * @param start The starting point.
     * @param end The ending point.
     * @param snapLocation The location which should be snapped to.
     * @return The absolute snapping location.
     */
    private int calculateSnapLocation(int start, int end, float snapLocation) {
        if(Math.abs(snapLocation) <= 1.0f) {
            return Math.round(start + (end - start) * snapLocation);
        }

        return Math.round(start + snapLocation);
    }

    /**
     * Calculates the snapping location for the provided child.
     * @param child The child to calculate the snapping locations on.
     * @param layoutManager The layout manager to do calculations with.
     * @param snapResult The array to save the result to.
     * @param direction The direction the calculations should be made in. DIRECTION_X, DIRECTION_Y.
     * @return snapResult.
     */
    public float[] calculateChildSnapLocations(View child, LayoutManager layoutManager, float[] snapResult, int direction) {
        snapResult[0] = snapResult[1] = 0;

        if(layoutManager != null) {
            // Use the layout params to get additional measurement information
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int start = 0, end = 0;

            if(direction == DIRECTION_X && layoutManager.canScrollHorizontally()) {
                if(mOptions.mIncludeChildDecorations) {
                    start = layoutManager.getDecoratedLeft(child);
                    end = layoutManager.getDecoratedRight(child);
                } else {
                    start = child.getLeft();
                    end = child.getRight();
                }

                if(mOptions.mIncludeChildMargins) {
                    start -= params.leftMargin;
                    end += params.rightMargin;
                }
            } else if(direction == DIRECTION_Y && layoutManager.canScrollVertically()) {
                if(mOptions.mIncludeChildDecorations) {
                    start = layoutManager.getDecoratedTop(child);
                    end = layoutManager.getDecoratedBottom(child);
                } else {
                    start = child.getTop();
                    end = child.getBottom();
                }

                if(mOptions.mIncludeChildMargins) {
                    start -= params.topMargin;
                    end -= params.bottomMargin;
                }
            }

            snapResult[0] = calculateSnapLocation(start, end, mOptions.mChildSnap[0]);
            snapResult[1] = calculateSnapLocation(start, end, mOptions.mChildSnap[1]);
        }

        return snapResult;
    }

    /**
     * Calculates the start and end snapping positions of the parent RecyclerView.
     * @param layoutManager The layout manager to do calculations with.
     * @param snapResult The array to save the result to.
     * @param direction The direction the calculations should be made in. DIRECTION_X, DIRECTION_Y.
     * @return snapResult.
     */
    public float[] calculateParentSnapLocations(LayoutManager layoutManager, float[] snapResult, int direction) {
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
     * Calculates the snapping distance that should be scrolled, inorder to snap the child into place.
     * @param layoutManager The layout manager to do calculations with.
     * @param child The child we are trying to snap into place.
     * @param direction The scrolling direction.
     * @return The distance that should be translated for a snap to occur.
     */
    public int calculateSnappingDistance(View child, LayoutManager layoutManager, int direction) {
        if(layoutManager != null) {
            if ((direction == DIRECTION_X && layoutManager.canScrollHorizontally()) ||
                    (direction == DIRECTION_Y && layoutManager.canScrollVertically())) {
                // Calculate the true snapping positions.
                float[] childSnap = calculateChildSnapLocations(child, layoutManager, new float[2], direction);
                float[] parentSnap = calculateParentSnapLocations(layoutManager, new float[2], direction);

                // The calculated snap distances.
                int[] snapDistance = {
                        (int) (parentSnap[0] - childSnap[0]),
                        (int) (parentSnap[1] - childSnap[1])
                };

                // Our item is to the left of both positions, snap to the left.
                if (snapDistance[0] > 0 && snapDistance[1] > 0) {
                    return snapDistance[0];
                }

                // Our item is to the right of both positions, snap to the right.
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

    /**
     * Overridden to calculate the speed per pixel using our options object.
     */
    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        if(mOptions == null) {
            return super.calculateSpeedPerPixel(displayMetrics);
        }

        return mOptions.mMSPerInch / displayMetrics.densityDpi;
    }

    /**
     * Overridden to use our options specific scroll speed.
     */
    @Override
    protected int calculateTimeForScrolling(int dx) {
        return (int) Math.ceil(Math.abs(dx) * mCalculatedMillisPerPx);
    }

    /**
     * Overridden to snap into place using our options object.
     */
    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        final int dx = calculateSnappingDistance(targetView, getLayoutManager(), DIRECTION_X);
        final int dy = calculateSnappingDistance(targetView, getLayoutManager(), DIRECTION_Y);

        if(dx != 0 || dy != 0) {
            mSnapStartTime = System.currentTimeMillis();
            action.update(-dx, -dy, 500, mOptions.mTargetFoundInterpolator);
        }
    }

    /**
     * Attempts to calculate the current animation percentage.
     * @return The current animation percentage [0 - 1.0].
     */
    public float getAnimationPercentage() {
        if(NO_START_SNAP_TIME != mSnapStartTime) {
            final long elapsedTime = System.currentTimeMillis() - mSnapStartTime;

            if(elapsedTime < mOptions.mSnapDuration) {
                return elapsedTime / mOptions.mSnapDuration;
            }
            return 1;
        }
        return 0;
    }

    /**
     * Class used to configure snapping options for a SnappingSmoothScroller instance.
     */
    public static class SnappingOptions {
        // The number of MS it will take to scroll one inch during seeking.
        private float mMSPerInch = 25.0f;

        // The location on the child which will snap to the parent/
        private final float[] mChildSnap = { 0.50f, 0.50f };

        // The location on the parent which will snap to the child.
        private final float[] mParentSnap = { 0.25f, 0.75f };

        // The interpolator that will be used during the target found phase
        private Interpolator mTargetFoundInterpolator = new DecelerateInterpolator();

        // The amount of time snapping a child in place will take.
        private int mSnapDuration = 500;

        // If true, margins will be included in the snapping calculation.
        private boolean mIncludeChildMargins = false;

        // If true, decorations will be included in the snapping calculation.
        private boolean mIncludeChildDecorations = false;

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
            mTargetFoundInterpolator = targetFoundInterpolator;
        }

        /**
         * Sets the amount of time it will take for a child to snap in place, once it has been
         * brought into view of the parent.
         * @param snapDuration The time the child will take to snap in milliseconds.
         */
        public void setSnapDuration(int snapDuration) {
            mSnapDuration = snapDuration;
        }

        /**
         * Sets whether or not this will use margins to calculate snap points with.
         * @param includeChildMargins True if this should take margins into account.
         */
        public void setIncludeChildMargins(boolean includeChildMargins) {
            mIncludeChildMargins = includeChildMargins;
        }

        /**
         * Sets whether or not this will use decorations to calculate snap points with.
         * @param includeChildDecorations True if this should take decorations into account.
         */
        public void setIncludeChildDecorations(boolean includeChildDecorations) {
            mIncludeChildDecorations = includeChildDecorations;
        }

        /**
         * @return The parent's first snapping location.
         */
        public float getParentFirstSnapLocation() {
            return mParentSnap[0];
        }

        /**
         * @return The parent's second snapping location.
         */
        public float getParentSecondSnapLocation() {
            return mParentSnap[1];
        }

        /**
         * @return The child's first snapping location.
         */
        public float getChildFirstSnapLocation() {
            return mChildSnap[0];
        }

        /**
         * @return The child's second snapping location.
         */
        public float getChildSecondSnapLocation() {
            return mChildSnap[1];
        }

        /**
         * @return True if this calculates the snap point while including margins.
         */
        public boolean getIncludeChildMargins() {
            return mIncludeChildMargins;
        }

        /**
         * @return True if this calculates the snap point while including decorations.
         */
        public boolean getIncludeChildDecorations() {
            return mIncludeChildDecorations;
        }
    }
}
