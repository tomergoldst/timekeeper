package com.tomergoldst.timekeeperdemo;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {

    private int mVerSpace;
    private int mHrzSpace;

    public SimpleItemDecoration(int verticalSpaceInPixels) {
        mVerSpace = verticalSpaceInPixels;
        mHrzSpace = 0;
    }

    public SimpleItemDecoration(int verticalSpaceInPixels, int horizontalSpaceInPixels) {
        mVerSpace = verticalSpaceInPixels;
        mHrzSpace = horizontalSpaceInPixels;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        outRect.left = mHrzSpace;
        outRect.right = mHrzSpace;
        outRect.bottom = mVerSpace;
        outRect.top = mVerSpace;
    }

}
