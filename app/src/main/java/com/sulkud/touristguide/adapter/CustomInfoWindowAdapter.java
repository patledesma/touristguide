package com.sulkud.touristguide.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.sulkud.touristguide.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;

    public CustomInfoWindowAdapter(Context context) {
        myContentsView = LayoutInflater.from(context).inflate(R.layout.view_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        ImageView iImage = ((ImageView) myContentsView.findViewById(R.id.iImage));
        String bitmap = (String)marker.getTag();
        Log.i(getClass().getSimpleName(), "Bitmap: " + (String)marker.getTag());
        TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle());
        Log.i(getClass().getSimpleName(), "Title: " + marker.getTitle());
        TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.tDescription));
        tvSnippet.setText(marker.getSnippet());
        Log.i(getClass().getSimpleName(), "Snippet: " + marker.getSnippet());

        return myContentsView;
    }
}
