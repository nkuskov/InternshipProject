/*
Class for creating each DestinationItem in RecyclerView
 */

package com.internship.nkuskov.socialmap.RecyclerView;

public class DestinationItem implements RecyclerListItem {
    private String destName;
    private int destIconId;
    private double latitude;
    private double longitude;

    public DestinationItem(String destName, int destIconId, double latitude, double longitude) {
        this.destName = destName;
        this.destIconId = destIconId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public DestinationItem(String destName,int destIconId){
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
