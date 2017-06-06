package com.internship.nkuskov.socialmap.RecyclerView;

import com.internship.nkuskov.socialmap.R;

/**
 * Class for creating Add Button in RecyclerView
 **/

public class DestinationAddButton implements RecyclerListItem {
    private String buttonText = "ADD";
    private int iconId = R.drawable.add_dest_btn;

    @Override
    public int getRecyclerListItemType() {
        return RecyclerListItem.destinationAddButton;
    }

    public String getButtonText() {
        return buttonText;
    }

    public int getIconId() {
        return iconId;
    }
}
