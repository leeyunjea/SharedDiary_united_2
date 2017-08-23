package com.example.shareddiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 이예지 on 2017-08-03.
 */
public class Adapter_CalContents extends BaseAdapter {
    private Context context;
    private int resource;
    private ArrayList<CalContents> contents = new ArrayList<CalContents>();

    public Adapter_CalContents(Context context, int resource, ArrayList<CalContents> contents) {
        this.context = context;
        this.resource = resource;
        this.contents = contents;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int position) {
        return contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView ==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource,parent,false);
        }
        TextView date = (TextView)convertView.findViewById(R.id.dates_textView);
        date.setText("날짜 : "+contents.get(position).getDate());

        TextView time = (TextView)convertView.findViewById(R.id.time_textView);
        time.setText("시간 : "+contents.get(position).getTime());

        TextView content = (TextView)convertView.findViewById(R.id.content_textView);
        content.setText("내용 : "+contents.get(position).getContent());

        TextView location = (TextView)convertView.findViewById(R.id.location_textView);
        location.setText("장소 : "+contents.get(position).getLocation());

        TextView myName = (TextView)convertView.findViewById(R.id.myName_textView);
        myName.setText("작성자"+contents.get(position).getMyName());
        return convertView;
    }
}
