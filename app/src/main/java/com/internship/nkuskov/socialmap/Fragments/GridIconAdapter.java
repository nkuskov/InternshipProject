package com.internship.nkuskov.socialmap.Fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.internship.nkuskov.socialmap.R;

public class GridIconAdapter extends BaseAdapter {
    private Context mContext;

    public Integer[] mIconIds = {R.drawable.airport_icon, R.drawable.bank_icon, R.drawable.bar_icon, R.drawable.child_icon,
            R.drawable.fitness_center_icon, R.drawable.home_icon, R.drawable.hospital_icon,
            R.drawable.hotel_icon, R.drawable.railway_icon, R.drawable.relationship_icon,
            R.drawable.restaurant_icon, R.drawable.theatre_icon, R.drawable.work_icon};

    public GridIconAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mIconIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mIconIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85,85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mIconIds[position]);
        imageView.setTag(mIconIds[position]);
        return imageView;
    }
}
