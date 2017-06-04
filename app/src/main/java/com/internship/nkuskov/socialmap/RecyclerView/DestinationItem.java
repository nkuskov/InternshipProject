/*
Class for creating each DestinationItem in RecyclerView
 */

package com.internship.nkuskov.socialmap.RecyclerView;

public class DestinationItem implements RecyclerListItem{
    private String destName;
    private int destIconId;

    public DestinationItem(String destName, int destIconId) {
        this.destName = destName;
        this.destIconId = destIconId;
    }

    @Override
    public int getRecyclerListItemType() {
        return RecyclerListItem.destinationItem;
    }

    public String getDestName() {
        return destName;
    }

    public int getDestIconId() {
        return destIconId;
    }
}
