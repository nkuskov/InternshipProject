package com.internship.nkuskov.socialmap.RecyclerView;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.internship.nkuskov.socialmap.R;


public class DestinationAddRecyclerView extends ViewHolder implements View.OnClickListener {

    TextView addText;
    ImageView addIcon;

    public DestinationAddRecyclerView(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        addText = (TextView) itemView.findViewById(R.id.dest_name);
        addIcon = (ImageView) itemView.findViewById(R.id.dest_icon);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(),"DestinationADDButton!!",Toast.LENGTH_SHORT).show();
    }

    public void setCardView(RecyclerListItem item){
        addText.setText(((DestinationAddButton)item).getButtonText());
        addIcon.setImageResource(((DestinationAddButton)item).getIconId());
    }
}
