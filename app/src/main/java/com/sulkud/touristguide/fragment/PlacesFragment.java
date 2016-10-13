package com.sulkud.touristguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sulkud.touristguide.R;
import com.sulkud.touristguide.adapter.CustomInfoWindowAdapter;
import com.sulkud.touristguide.interfaces.PlaceSelectedListener;
import com.sulkud.touristguide.models.PlaceModel;

public class PlacesFragment extends Fragment
        implements OnMapReadyCallback, PlaceSelectedListener {

    private ViewGroup view;
    private GoogleMap mMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_places, container, false);

        return view;
    }

    @Override
    public void onResume() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.placesMap);
        mapFragment.getMapAsync(this);
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(getClass().getSimpleName(), "onMapReady");
        mMap = googleMap;
        LatLng tacurong = new LatLng(6.687757, 124.678383);
        mMap.addMarker(new MarkerOptions().position(tacurong).title("Marker in Tacurong"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tacurong, 10.0f));
    }

    @Override
    public void onPlaceLocationSelectedListener(PlaceModel placeModel) {
        if (placeModel != null) {
            Log.i(getClass().getSimpleName(), "clicked " +
                    placeModel.placeName + " " +
                    Double.valueOf(placeModel.latitude) + " " +
                    Double.valueOf(placeModel.longitude));
            MarkerOptions options = new MarkerOptions()
                    .position(
                            new LatLng(
                                    Double.valueOf(placeModel.latitude),
                                    Double.valueOf(placeModel.longitude))
                    )
                    .title(placeModel.placeName)
                    .snippet(placeModel.latitude + ", " + placeModel.longitude);
            mMap.clear();

            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));

            Marker marker = mMap.addMarker(options);
            marker.setPosition(new LatLng(
                    Double.valueOf(placeModel.latitude),
                    Double.valueOf(placeModel.longitude)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(placeModel.latitude),
                    Double.valueOf(placeModel.longitude)), 15));

        }
    }
}
