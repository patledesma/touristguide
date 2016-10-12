package com.sulkud.touristguide.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sulkud.touristguide.R;
import com.sulkud.touristguide.activity.MainActivity;
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

        lvEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View item = inflater.inflate(R.layout.dialog_history, null);
                TextView tTitle = (TextView) item.findViewById(R.id.tTitle);
                TextView tContent = (TextView) item.findViewById(R.id.tContent);

                String[] events = getActivity().getResources().getStringArray(R.array.events);
                String[] description = getActivity().getResources().getStringArray(R.array.events_description);

                tTitle.setText(events[position] + "");
                tContent.setText(description[position] + "");

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(item);

                Button dialogButton = (Button) dialog.findViewById(R.id.bDismiss);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return view;
    }
}
