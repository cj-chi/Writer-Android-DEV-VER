package com.THLight.BLE.USBeacon.Writer.Simple.ui.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Allen on 2020/3/4.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ViewTypeInterface> itemTypeList;

    public RecyclerViewAdapter(Context context, List<ViewTypeInterface> itemTypeList) {
        this.context = context;
        this.itemTypeList = itemTypeList;
    }

    @Override
    public int getItemViewType(int position) {
        ViewTypeInterface itemType = getItemTypeByPosition(position);
        return itemType.getLayoutId();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(viewType, parent, false);
        ViewTypeInterface itemType = getItemTypeByViewType(viewType);
        return itemType.getViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewTypeInterface itemType = getItemTypeByPosition(position);
        itemType.bindViewHolder(holder, getItemTypeIndexByPosition(position));
    }

    @Override
    public int getItemCount() { // 取得 item 總數
        int count = 0;
        for (ViewTypeInterface itemType : itemTypeList) {
            count += itemType.getItemCount();
        }
        return count;
    }

    private ViewTypeInterface getItemTypeByPosition(int position) { // 透過 position 找出該位置是什麼 ViewType
        int count = 0;
        for (ViewTypeInterface itemType : itemTypeList) {
            count += itemType.getItemCount();
            if (position < count) {
                return itemType;
            }
        }
        return null;
    }

    private ViewTypeInterface getItemTypeByViewType(int viewType) {  // 找出列表中對應的ViewType
        for (ViewTypeInterface itemType : itemTypeList) {
            if (itemType.getLayoutId() == viewType) {
                return itemType;
            }
        }
        return null;
    }

    private int getItemTypeIndexByPosition(int position) {
        int count = 0;
        int response = 0;
        for (ViewTypeInterface itemType : itemTypeList) {
            count += itemType.getItemCount();
            if (position < count) {
                int preItemCount = (count - itemType.getItemCount());
                response = position - preItemCount;
                System.out.println("getItemTypeIndexByPosition -----> foreach " + response +
                        " , position : " + position +
                        " , preItemCount : " + preItemCount +
                        " , count : " + count +
                        " , itemType : " + itemType.getItemCount());
                return response;
            }
        }
        response = -1;
        System.out.println("getItemTypeIndexByPosition -----> not foreach " + response);
        return response;
    }

    public interface ViewTypeInterface {
        int getItemCount();

        int getLayoutId();

        RecyclerView.ViewHolder getViewHolder(View itemView);

        void bindViewHolder(RecyclerView.ViewHolder viewHolder, int index);
    }
}