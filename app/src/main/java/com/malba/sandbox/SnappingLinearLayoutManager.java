package com.malba.sandbox;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by malba on 7/14/2017.
 */

public class SnappingLinearLayoutManager extends LinearLayoutManager {
    public SnappingLinearLayoutManager(Context context, View previousFocus) {
        super(context);
        this.previousFocus = previousFocus;
    }

    public SnappingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SnappingLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, View previousFocus) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.previousFocus = previousFocus;
    }

    private View previousFocus;

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        SnappingSmoothScroller smoothScroller = new SnappingSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    @Override
    public boolean onRequestChildFocus(final RecyclerView parent, final RecyclerView.State state, final View child, final View focused) {
        if(previousFocus != child) {
            previousFocus = child;
            int focusPosition = getPosition(child);
            smoothScrollToPosition(parent, state, focusPosition);
        }

        return true;
    }
}
