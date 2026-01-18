package com.THLight.BLE.USBeacon.Writer.Simple.ui.recycler;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.THLight.BLE.USBeacon.Writer.Simple.R;

public class VerticalLoadingViewType implements RecyclerViewAdapter.ViewTypeInterface {
    private boolean isVisible;

    @Override
    public int getItemCount() {
        return isVisible() ? 1 : 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_vertical_loading;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View itemView) {
        return new VerticalLoadingViewHolder(itemView);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder viewHolder, int index) {
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public class VerticalLoadingViewHolder extends RecyclerView.ViewHolder {

        public VerticalLoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
