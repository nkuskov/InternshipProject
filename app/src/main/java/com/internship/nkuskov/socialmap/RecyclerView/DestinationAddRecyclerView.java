package com.internship.nkuskov.socialmap.RecyclerView;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.internship.nkuskov.socialmap.Fragments.AddNewDestinationFragment;
import com.internship.nkuskov.socialmap.MapsActivity;
import com.internship.nkuskov.socialmap.R;

/**
 * This class is model for addButton for NewDestinations cards in RecyclerView
 */

public class DestinationAddRecyclerView extends ViewHolder implements View.OnClickListener {

    private TextView addText;
    private ImageView addIcon;
    private Context mContext;   //getting context of MainActivity
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;
    private AddNewDestinationFragment mAddNewDestinationFragment;


    public DestinationAddRecyclerView(Context mContext, View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.addText = (TextView) itemView.findViewById(R.id.dest_name);
        this.addIcon = (ImageView) itemView.findViewById(R.id.dest_icon);
        this.mContext = mContext;
        this.mFragmentTransaction = ((MapsActivity) mContext).getmFragmentTransaction();
        this.mFragmentManager = ((MapsActivity)mContext).getmFragmentManager();
        this.mAddNewDestinationFragment = ((MapsActivity) mContext).getmAddNewDestinationFragment();
    }

    //When clicked on AddButtonCard fragment for Adding new DestinationCard will show
    @Override
    public void onClick(View v) {
        if(mFragmentManager.findFragmentByTag(mAddNewDestinationFragment.TAG)==null) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.add(R.id.fragment_layout, mAddNewDestinationFragment, mAddNewDestinationFragment.TAG);
            mFragmentTransaction.commit();
            ((MapsActivity) mContext).findViewById(R.id.fragment_layout).bringToFront();
        }
    }

    public void setCardView(RecyclerListItem item) {
        addText.setText(((DestinationAddButton) item).getButtonText());
        addIcon.setImageResource(((DestinationAddButton) item).getIconId());
    }
}
