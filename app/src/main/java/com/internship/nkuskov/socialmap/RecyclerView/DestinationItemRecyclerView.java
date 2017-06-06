/*
Overriding class for creating each CardView with image,name and clickable
 */

package com.internship.nkuskov.socialmap.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.internship.nkuskov.socialmap.R;

public class DestinationItemRecyclerView extends ViewHolder implements View.OnClickListener{

    public TextView destName;
    public ImageView destIcon;


    public DestinationItemRecyclerView(Context mContext, View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        destName = (TextView) itemView.findViewById(R.id.dest_name);
        destIcon = (ImageView) itemView.findViewById(R.id.dest_icon);

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(),"DestinationItem!!" + destName.getText(),Toast.LENGTH_SHORT).show();
    }

    public void setCardView(RecyclerListItem item){
        destName.setText(((DestinationItem)item).getDestName());
        destIcon.setImageResource(((DestinationItem)item).getDestIconId());
    }
}
