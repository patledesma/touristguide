package com.sulkud.touristguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sulkud.touristguide.R;
import com.sulkud.touristguide.activity.MainActivity;
import com.sulkud.touristguide.adapter.PlacesListAdapter;
import com.sulkud.touristguide.helper.database.DatabaseHandler;
import com.sulkud.touristguide.interfaces.PlaceSelectedListener;

public class BookmarkedPlacesFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    ViewGroup view;
    private ListView lvVisitedPlaces;
    private PlacesListAdapter adapter;
    private PlaceSelectedListener placeSelectedListener;
    private DatabaseHandler databaseHandler;

    private TextView tEmpty;
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_places, container, false);
        databaseHandler = new DatabaseHandler(getActivity());

        tEmpty = (TextView) view.findViewById(R.id.tEmpty);

        lvVisitedPlaces = (ListView) view.findViewById(R.id.lvList);
        adapter = new PlacesListAdapter(getActivity().getApplicationContext(), databaseHandler.getAllPlaces(DatabaseHandler.TABLE_TAG_TYPE_BOOKMARKED));
        lvVisitedPlaces.setAdapter(adapter);
        lvVisitedPlaces.setOnItemClickListener(this);

        if (adapter.getCount() == 0) {
            tEmpty.setVisibility(View.VISIBLE);
            tEmpty.setText("No Bookmarked Places");
        } else {
            tEmpty.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onResume() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.placesMap);
        mapFragment.getMapAsync(this);
        super.onResume();
    }

    public void setOnPlaceSelectedListener(PlaceSelectedListener placeSelectedListener) {
        this.placeSelectedListener = placeSelectedListener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i(getClass().getSimpleName(), "itemClicked!");
//        this.placeSelectedListener.onPlaceLocationSelectedListener(adapter.getItem(i));
        /*FragmentActivity activity = (FragmentActivity)getApplicationContext();
        activity.testFunction(adapter.getItem(i));*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(getClass().getSimpleName(), "onMapReady");
        mMap = googleMap;
        LatLng tacurong = new LatLng(6.687757, 124.678383);
        mMap.addMarker(new MarkerOptions().position(tacurong).title("Marker in Tacurong"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tacurong, 10.0f));
    }
}
