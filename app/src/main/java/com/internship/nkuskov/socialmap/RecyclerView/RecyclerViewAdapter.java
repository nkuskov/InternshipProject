/*
Class for filling in RecyclerView
 */

package com.internship.nkuskov.socialmap.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.internship.nkuskov.socialmap.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<RecyclerListItem> destinationItems;
    private Context mContext;

    public RecyclerViewAdapter(Context mContext ,List<RecyclerListItem> destinationItems) {
        this.mContext = mContext;
        this.destinationItems = destinationItems;

    }

    //getting write item model
    @Override
    public int getItemViewType(int position) {
        return destinationItems.get(position).getRecyclerListItemType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, null);
        switch (viewType) {
            case RecyclerListItem.destinationAddButton:
                return new DestinationAddRecyclerView(mContext , v);
            case RecyclerListItem.destinationItem:
                return new DestinationItemRecyclerView(mContext , v);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecyclerListItem item = destinationItems.get(position);
        switch (holder.getItemViewType()){
            case RecyclerListItem.destinationAddButton:
                ((DestinationAddRecyclerView)holder).setCardView(item);
                break;
            case RecyclerListItem.destinationItem:
                ((DestinationItemRecyclerView)holder).setCardView(item);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return destinationItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
