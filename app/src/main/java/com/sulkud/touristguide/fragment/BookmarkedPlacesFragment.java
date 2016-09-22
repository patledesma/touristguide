package com.sulkud.touristguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sulkud.touristguide.R;
import com.sulkud.touristguide.adapter.EventsAdapter;

public class BookmarkedPlacesFragment extends Fragment {

    ViewGroup view;
    private ListView lvVisitedPlaces;
    private EventsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState){
        view = (ViewGroup) inflater.inflate(R.layout.fragment_bookmarked_places, null);

        lvVisitedPlaces = (ListView) view.findViewById(R.id.lvBookmarkedPlaces);
        adapter = new EventsAdapter(getActivity().getApplicationContext(),
                getActivity().getResources().getStringArray(R.array.places),
                getActivity().getResources().getStringArray(R.array.latlong));
        lvVisitedPlaces.setAdapter(adapter);

        return view;
    }
}
