package com.malba.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Extension of {@link android.support.v7.widget.LinearLayoutManager LinearLayoutManager} which
 * utilizes a {@link com.malba.widget.SnappingSmoothScroller SnappingSmoothScroller} in order
 * to snap the view in place during scroll and focus operations.
 */
public class SnappingLinearLayoutManager extends LinearLayoutManager {
    /**
     * The snapping options which are fed into the smooth scroller.
     */
    private SnappingSmoothScroller.SnappingOptions mSnappingOptions = new SnappingSmoothScroller.SnappingOptions();

    /**
     * The previously in-focus view.
     */
    private View mPreviousFocus;

    /**
     * @see android.support.v7.widget.LinearLayoutManager(Context, int, boolean)
     */
    public SnappingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    /**
     * Sets the snapping options that will be passed into the SnappingSmoothScroller instance.
     * @param snappingOptions The options to be passed into the SnappingSmoothScroller instance.
     */
    public void setSnappingOptions(SnappingSmoothScroller.SnappingOptions snappingOptions) {
        mSnappingOptions = snappingOptions;
    }

    /**
     * Overridden to use a custom smooth scroller.
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        // Set up the new smooth scroller
        SnappingSmoothScroller smoothScroller = new SnappingSmoothScroller(mSnappingOptions, recyclerView.getContext());
        smoothScroller.setRecyclerView(recyclerView);
        smoothScroller.setTargetPosition(position);

        // Stop scrolling if we are not idle
        if(RecyclerView.SCROLL_STATE_IDLE != recyclerView.getScrollState()) {
            recyclerView.stopScroll();
        }

        startSmoothScroll(smoothScroller);
    }

    /**
     * Overridden to snap the focused view into position using a smooth scroller.
     */
    @Override
    public boolean onRequestChildFocus(final RecyclerView parent, final RecyclerView.State state, final View child, final View focused) {
        // Trigger scrolling events when we have a new child in focus.
        if(mPreviousFocus != child) {
            mPreviousFocus = child;
            int focusPosition = getPosition(child);
            smoothScrollToPosition(parent, state, focusPosition);
        }

        return true;
    }
}
