package com.sulkud.touristguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sulkud.touristguide.R;
import com.sulkud.touristguide.adapter.EventsAdapter;
import com.sulkud.touristguide.adapter.PlacesListAdapter;
import com.sulkud.touristguide.interfaces.PlaceSelectedListener;
import com.sulkud.touristguide.models.PlaceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VisitedPlacesFragment extends Fragment implements AdapterView.OnItemClickListener {

    ViewGroup view;
    private PlacesListAdapter adapter;
    private PlaceSelectedListener placeSelectedListener;
    private ListView lvVisitedPlaces;

    //this is just a sample coordinates, this will be gone when real geolocations are given
    String[] randPlaces = {
            "6.520817, 124.839346",
            "6.506417, 124.859352",
            "6.499520, 124.834039",
            "6.488162, 124.842817",
            "6.463213, 124.872621",
            "6.523454, 124.826077",
            "6.522845, 124.862210",
            "6.493435, 124.825669",
            "6.494986, 124.844801",
            "6.497736, 124.850122",
            "6.498070, 124.837682",
            "6.505261, 124.852617"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView");
        view = (ViewGroup) inflater.inflate(R.layout.fragment_visited_places, container, false);

        adapter = new PlacesListAdapter(getActivity().getApplicationContext(), createPlaceList());
        lvVisitedPlaces = (ListView) view.findViewById(R.id.lvVistedPlaces);
        lvVisitedPlaces.setOnItemClickListener(this);
        lvVisitedPlaces.post(new Runnable() {
            @Override
            public void run() {
                lvVisitedPlaces.setAdapter(adapter);
            }
        });
        Log.i(getClass().getSimpleName(), adapter.getCount() + " item count");
        return view;
    }

    public void setOnPlaceSelectedListener(PlaceSelectedListener placeSelectedListener) {
        this.placeSelectedListener = placeSelectedListener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i(getClass().getSimpleName(), "itemClicked!");
        this.placeSelectedListener.onPlaceLocationSelectedListener(adapter.getItem(i));
    }

    private List<PlaceModel> createPlaceList() {
        List<PlaceModel> places = new ArrayList<>();
        for (int i = 0; i < randPlaces.length; i++) {
            PlaceModel item = new PlaceModel();
            item.placeName = "Place " + (i + 1);
            String[] splitLoc = randPlaces[i].split(",");
            item.latitude = splitLoc[0];
            item.longitude = splitLoc[1];
            places.add(item);
        }
        return places;
    }

}
