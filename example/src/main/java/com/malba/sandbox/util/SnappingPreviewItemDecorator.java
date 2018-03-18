package com.malba.sandbox.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.malba.widget.SnappingSmoothScroller;

/**
 * Item decorator to visualize the snapping points for the RecyclerView.
 */
public class SnappingPreviewItemDecorator extends RecyclerView.ItemDecoration {
    private static final int UNKNOWN_ORIENTATION = Integer.MIN_VALUE;

    // The smooth scroller to pull our visualization calculations from.
    private SnappingSmoothScroller mSnappingSmoothScroller;

    // The orientation of the RecyclerView.
    private int mOrientation = UNKNOWN_ORIENTATION;

    // The parent snapping location array.
    private float[] mParentSnapLocation = new float[2];

    // The child snapping location array.
    private float[] mChildSnapLocation = new float[2];

    // The paint to draw the parent anchors with.
    private Paint mParentPaint = new Paint();

    // The paint to draw the parent anchors with.
    private Paint mChildPaint = new Paint();

    // The paint to draw the parent anchors with.
    private Paint mChildSecondaryPaint = new Paint();

    /**
     * @param snappingOptions The snapping options to pull from, in order to draw the snapping lines.
     * @param context The context to setup the snapping smooth scroller with.
     */
    public SnappingPreviewItemDecorator(SnappingSmoothScroller.SnappingOptions snappingOptions, Context context) {
        mSnappingSmoothScroller = new SnappingSmoothScroller(snappingOptions, context);
        mParentPaint.setColor(0xFF0000FF);
        mParentPaint.setStrokeWidth(3);

        mChildPaint.setColor(0xFF00FF00);
        mChildPaint.setAlpha((int)(255 * 0.15f));
        mChildPaint.setStrokeWidth(3);

        mChildSecondaryPaint.setColor(0xFF0000FF);
        mChildSecondaryPaint.setAlpha((int)(255 * 0.15f));
        mChildSecondaryPaint.setStrokeWidth(3);
    }

    // Draws the snap anchors of a child.
    private void drawChildAnchor(Canvas canvas, View child, RecyclerView parent) {
        if(LinearLayoutManager.HORIZONTAL == mOrientation) {
            mSnappingSmoothScroller.calculateChildSnapLocations(child, parent.getLayoutManager(), mChildSnapLocation, SnappingSmoothScroller.DIRECTION_X);
            canvas.drawLine(mChildSnapLocation[0], parent.getTop(), mChildSnapLocation[0], parent.getBottom(), mChildPaint);
            canvas.drawLine(mChildSnapLocation[1], parent.getTop(), mChildSnapLocation[1], parent.getBottom(), mChildSecondaryPaint);
        } else {
            mSnappingSmoothScroller.calculateChildSnapLocations(child, parent.getLayoutManager(), mChildSnapLocation, SnappingSmoothScroller.DIRECTION_Y);
            canvas.drawLine(parent.getLeft(), mChildSnapLocation[0], parent.getRight(), mChildSnapLocation[0], mChildPaint);
            canvas.drawLine(parent.getLeft(), mChildSnapLocation[1], parent.getRight(), mChildSnapLocation[1], mChildSecondaryPaint);
        }
    }

    // Draws the parent snap anchors.
    private void drawParentAnchor(Canvas canvas, RecyclerView parent) {
        if(LinearLayoutManager.HORIZONTAL == mOrientation) {
            mSnappingSmoothScroller.calculateParentSnapLocations(parent.getLayoutManager(), mParentSnapLocation, SnappingSmoothScroller.DIRECTION_X);
            canvas.drawLine(mParentSnapLocation[0], parent.getTop(), mParentSnapLocation[0], parent.getBottom(), mParentPaint);
            canvas.drawLine(mParentSnapLocation[1], parent.getTop(), mParentSnapLocation[1], parent.getBottom(), mParentPaint);
        } else {
            mSnappingSmoothScroller.calculateParentSnapLocations(parent.getLayoutManager(), mParentSnapLocation, SnappingSmoothScroller.DIRECTION_Y);
            canvas.drawLine(parent.getLeft(), mParentSnapLocation[0], parent.getRight(), mParentSnapLocation[0], mParentPaint);
            canvas.drawLine(parent.getLeft(), mParentSnapLocation[1], parent.getRight(), mParentSnapLocation[1], mParentPaint);
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);

        // Calculate the orientation if unknown.
        if(UNKNOWN_ORIENTATION == mOrientation && parent.getLayoutManager() instanceof LinearLayoutManager) {
            mOrientation = ((LinearLayoutManager)parent.getLayoutManager()).getOrientation();
        }

        // Draw the child snapping points.
        final int childCount = parent.getChildCount();
        for(int i=0; i < childCount; i++) {
            drawChildAnchor(canvas, parent.getChildAt(i), parent);
        }

        // Draw the parent snapping points.
        drawParentAnchor(canvas, parent);
    }
}
