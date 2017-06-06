package com.internship.nkuskov.socialmap.RecyclerView;

/**
 * interface for choosing write model class in RecycleViewAdapter
 */
public interface RecyclerListItem {
    int destinationAddButton = 1;
    int destinationItem = 2;

    int getRecyclerListItemType();

}
