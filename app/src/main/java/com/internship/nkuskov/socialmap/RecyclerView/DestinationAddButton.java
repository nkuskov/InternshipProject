package com.internship.nkuskov.socialmap.RecyclerView;

import com.internship.nkuskov.socialmap.R;

/**
 * Created by nkuskov on 6/5/2017.
 */

public class DestinationAddButton implements RecyclerListItem {
    private String buttonText = "Add new Destination";
    private int iconId = R.drawable.dest_icon_img;

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
