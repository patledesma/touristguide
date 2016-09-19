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

public class EventsFragment extends Fragment {

    private ViewGroup view;
    private ListView lvEventList;
    private EventsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_events, null);

        lvEventList = (ListView) view.findViewById(R.id.lvEventsList);
        adapter = new EventsAdapter(getActivity().getApplicationContext(),
                getActivity().getResources().getStringArray(R.array.events),
                getActivity().getResources().getStringArray(R.array.dates));
        lvEventList.setAdapter(adapter);

        return view;
    }
}
