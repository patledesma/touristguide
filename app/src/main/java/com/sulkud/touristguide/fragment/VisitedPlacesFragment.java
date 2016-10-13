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
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.sulkud.touristguide.R;
import com.sulkud.touristguide.adapter.PlacesListAdapter;
import com.sulkud.touristguide.helper.database.DatabaseHandler;
import com.sulkud.touristguide.interfaces.PlaceSelectedListener;

public class VisitedPlacesFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    ViewGroup view;
    private PlacesListAdapter adapter;
    private PlaceSelectedListener placeSelectedListener;
    private ListView lvVisitedPlaces;
    private DatabaseHandler databaseHandler;

    private TextView tEmpty;

    GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView");
        view = (ViewGroup) inflater.inflate(R.layout.fragment_places, container, false);
        databaseHandler = new DatabaseHandler(getActivity());

        tEmpty = (TextView) view.findViewById(R.id.tEmpty);

        adapter = new PlacesListAdapter(getActivity().getApplicationContext(), databaseHandler.getAllPlaces(DatabaseHandler.TABLE_TAG_TYPE_VISITED));
        lvVisitedPlaces = (ListView) view.findViewById(R.id.lvList);
        lvVisitedPlaces.setOnItemClickListener(this);
        lvVisitedPlaces.setAdapter(adapter);
        Log.i(getClass().getSimpleName(), adapter.getCount() + " item count");

        if (adapter.getCount() == 0) {
            tEmpty.setVisibility(View.VISIBLE);
            tEmpty.setText("No Visited Places");
        } else {
            tEmpty.setVisibility(View.GONE);
        }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
