package com.sulkud.touristguide.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sulkud.touristguide.R;
import com.sulkud.touristguide.models.PlaceModel;

import java.util.List;

public class PlacesListAdapter extends BaseAdapter {

    private Context context;
    private List<PlaceModel> places;

    public PlacesListAdapter(Context context, List<PlaceModel> places) {
        this.context = context;
        this.places = places;
    }

    @Override
    public int getCount() {
        Log.i(getClass().getSimpleName(), places.size() + " list count");
        return places.size();
    }

    @Override
    public PlaceModel getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.i(getClass().getSimpleName(), "getView");
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_place_list, null);
            viewHolder.placeNameTv = (TextView) view.findViewById(R.id.placeNameTextView);
            viewHolder.placeLatLngTv = (TextView) view.findViewById(R.id.placeLatLngNameTextView);
            viewHolder.placeDescTv = (TextView) view.findViewById(R.id.placeDesc);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.placeNameTv.setText(places.get(position).placeName);
        viewHolder.placeLatLngTv.setText(places.get(position).latitude + ", " + places.get(position).longitude);
        viewHolder.placeDescTv.setText(places.get(position).placeDescription);

        return view;
    }

    class ViewHolder{

        TextView placeNameTv;
        TextView placeLatLngTv;
        TextView placeDescTv;

    }
}
