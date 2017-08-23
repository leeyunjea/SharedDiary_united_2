package com.example.shareddiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 이예지 on 2017-08-14.
 */

public class Adapter_Home extends BaseAdapter {
    Context context;
    int resource;
    ArrayList<FriendsItem> friendsItems = new ArrayList<FriendsItem>();

    public Adapter_Home(Context context, int resource, ArrayList<FriendsItem> friendsItems) {
        this.context = context;
        this.resource = resource;
        this.friendsItems = friendsItems;
    }

    @Override
    public int getCount() {
        return friendsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsItems.get(position);
    }

    @Override
    public long getItemId(int position_a) {
        return position_a;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resource, parent, false);
        }

        ImageView icon = (ImageView)convertView.findViewById(R.id.imageView);
        icon.setImageResource(friendsItems.get(position).mIcon);

        TextView textView = (TextView)convertView.findViewById(R.id.name);
        textView.setText(friendsItems.get(position).name);

        return convertView;
    }

}
