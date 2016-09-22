package com.sulkud.touristguide.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sulkud.touristguide.R;

import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends Fragment
        implements OnMapReadyCallback {

    private ViewGroup view;
    private ViewPager placesViewPager;
    private PlacesViewPagerAdapter placesViewPagerAdapter;

    private GoogleMap mMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_places, null);

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        List<Fragment> fragmentList = new ArrayList<>();

        fragmentList.add(new VisitedPlacesFragment());
        fragmentList.add(new BookmarkedPlacesFragment());

        placesViewPager = (ViewPager) view.findViewById(R.id.placesPager);
        placesViewPagerAdapter = new PlacesViewPagerAdapter(getFragmentManager(), getActivity(), fragmentList);
        placesViewPager.setAdapter(placesViewPagerAdapter);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng tacurong = new LatLng(6.687757, 124.678383);
        mMap.addMarker(new MarkerOptions().position(tacurong).title("Marker in Tacurong"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tacurong, 10.0f));
    }

    public class PlacesViewPagerAdapter extends FragmentPagerAdapter{

        private Context context;
        private List<Fragment> fragmentList;

        public PlacesViewPagerAdapter(FragmentManager fragmentManager, Context context, List<Fragment> fragmentList) {
            super(fragmentManager);
            this.context = context;
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
