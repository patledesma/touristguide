package com.sulkud.touristguide.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends Fragment
        implements OnMapReadyCallback, PlaceSelectedListener {

    private ViewGroup view;
    private ViewPager placesViewPager;
    private PlacesViewPagerAdapter placesViewPagerAdapter;
    private List<String> fragmentTitleList;
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

        Log.i(getClass().getSimpleName(), "onMapReady()");
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentTitleList = new ArrayList<>();

        VisitedPlacesFragment visitedPlacesFragment = new VisitedPlacesFragment();
        visitedPlacesFragment.setOnPlaceSelectedListener(this);
        fragmentList.add(visitedPlacesFragment);
        fragmentTitleList.add("Visited Places");

        BookmarkedPlacesFragment bookmarkedPlacesFragment = new BookmarkedPlacesFragment();
        bookmarkedPlacesFragment.setOnPlaceSelectedListener(this);
        fragmentList.add(bookmarkedPlacesFragment);
        fragmentTitleList.add("Bookmarked Places");

        placesViewPager = (ViewPager) view.findViewById(R.id.placesPager);
        placesViewPagerAdapter = new PlacesViewPagerAdapter(getChildFragmentManager(), getActivity().getApplicationContext(), fragmentList);
        placesViewPager.setAdapter(placesViewPagerAdapter);
        placesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().setTitle(fragmentTitleList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getActivity().setTitle(fragmentTitleList.get(placesViewPager.getCurrentItem()));

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

    public class PlacesViewPagerAdapter extends FragmentPagerAdapter{

        private Context context;
        private List<Fragment> fragmentList;

        public PlacesViewPagerAdapter(FragmentManager fragmentManager, Context context, List<Fragment> fragmentList) {
            super(fragmentManager);
            Log.i(getClass().getSimpleName(), "PlacesViewPagerAdapter");
            this.context = context;
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(getClass().getSimpleName(), "getItem = " + this.fragmentList.get(position));
            return this.fragmentList.get(position);
        }

        @Override
        public int getCount() {
            Log.i(getClass().getSimpleName(), "getCount " + fragmentList.size());
            return fragmentList.size();
        }
    }
}
