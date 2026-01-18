package com.THLight.BLE.USBeacon.Writer.Simple.ui.recycler;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class CustomRecyclerView extends RecyclerView {
    private static final int SCROLL_TO_BOTTOM_LOAD_NEXT_RANGE = 10;
    private CustomRecyclerViewScrollListener customRecyclerViewScrollListener;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHasFixedSize(true);
    }

    public void setCustomRecyclerViewScrollListener(CustomRecyclerViewScrollListener customRecyclerViewScrollListener) {
        this.customRecyclerViewScrollListener = customRecyclerViewScrollListener;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        scrollRecyclerViewItem();
    }

    private void scrollRecyclerViewItem() {
        if (customRecyclerViewScrollListener != null) {
            customRecyclerViewScrollListener.onRecyclerViewItemScroll(this, isScrollToBottom());
        }
    }

    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        switch (newState) {
            case SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                if (customRecyclerViewScrollListener != null) {
                    customRecyclerViewScrollListener.onScrollStateIdle();
                }
                break;
            case SCROLL_STATE_DRAGGING: // The RecyclerView is currently being dragged by outside input such as user touch input.
                if (customRecyclerViewScrollListener != null) {
                    customRecyclerViewScrollListener.onScrollStateDragging();
                }
                break;
        }
    }

    public boolean isScrollToBottom() {
        int firstVisibleItemPosition = 0;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            firstVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            firstVisibleItemPosition = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) getLayoutManager()).findFirstVisibleItemPositions(null);
            firstVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        }
        int visibleItemCount = getChildCount();
        int totalItemCount = getLayoutManager().getItemCount();
        return visibleItemCount + firstVisibleItemPosition + SCROLL_TO_BOTTOM_LOAD_NEXT_RANGE >= totalItemCount;
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int position = 0; position < lastVisibleItemPositions.length; position++) {
            if (position == 0) {
                maxSize = lastVisibleItemPositions[position];
            } else if (lastVisibleItemPositions[position] > maxSize) {
                maxSize = lastVisibleItemPositions[position];
            }
        }
        return maxSize;
    }

    public interface CustomRecyclerViewScrollListener {

        void onRecyclerViewItemScroll(CustomRecyclerView recyclerView, boolean isScrollBottom);

        void onScrollStateIdle();

        void onScrollStateDragging();
    }
}
