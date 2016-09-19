package com.sulkud.touristguide.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sulkud.touristguide.R;

public class EventsAdapter extends BaseAdapter{

    Context context;
    String[] eventsList;
    String[] dateList;

    public EventsAdapter(Context context, String[] eventsList, String[] dateList) {
        this.context = context;
        this.eventsList = eventsList;
        this.dateList = dateList;
    }

    @Override
    public int getCount() {
        return eventsList.length;
    }

    @Override
    public Object getItem(int i) {
        return eventsList[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.item_event_list, null);

            viewHolder.tEventName = (TextView) view.findViewById(R.id.tEventName);
            viewHolder.tEventDate = (TextView) view.findViewById(R.id.tEventDate);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tEventName.setText(eventsList[i]);
        viewHolder.tEventDate.setText(dateList[i]);

        return view;
    }

    private class ViewHolder {
        TextView tEventName;
        TextView tEventDate;
    }
}
