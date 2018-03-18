package com.malba.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by malba on 7/14/2017.
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

    public SnappingLinearLayoutManager(Context context, View previousFocus) {
        super(context);
        this.mPreviousFocus = previousFocus;
    }

    public SnappingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SnappingLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, View previousFocus) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mPreviousFocus = previousFocus;
    }

    /**
     * Sets the snapping options that will be passed into the SnappingSmoothScroller instance.
     * @param snappingOptions The options to be passed into the SnappingSmoothScroller instance.
     */
    public void setSnappingOptions(SnappingSmoothScroller.SnappingOptions snappingOptions) {
        mSnappingOptions = snappingOptions;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        SnappingSmoothScroller smoothScroller = new SnappingSmoothScroller(mSnappingOptions, recyclerView.getContext());
        smoothScroller.setRecyclerView(recyclerView);
        smoothScroller.setTargetPosition(position);

        if(RecyclerView.SCROLL_STATE_IDLE != recyclerView.getScrollState()) {
            recyclerView.stopScroll();
        }

        startSmoothScroll(smoothScroller);
    }

    @Override
    public boolean onRequestChildFocus(final RecyclerView parent, final RecyclerView.State state, final View child, final View focused) {
        if(mPreviousFocus != child) {
            mPreviousFocus = child;
            int focusPosition = getPosition(child);
            smoothScrollToPosition(parent, state, focusPosition);
        }

        return true;
    }
}
