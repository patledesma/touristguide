package com.sulkud.touristguide.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog = builder.create();

                View item = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_events, null);
                dialog.show();
                dialog.setContentView(item);

                TextView tTitle = (TextView) item.findViewById(R.id.tTitle);
                TextView tContent = (TextView) item.findViewById(R.id.tContent);
                ImageView iPreview = (ImageView) item.findViewById(R.id.imagePreview);
                Button bNavigate = (Button) item.findViewById(R.id.bStartNavigate);
                bNavigate.setVisibility(View.GONE);

                String[] events = getActivity().getResources().getStringArray(R.array.events);
                String[] description = getActivity().getResources().getStringArray(R.array.events_description);
                TypedArray imgs = getResources().obtainTypedArray(R.array.events_preview);
                imgs.getResourceId(position, -1);

                iPreview.setImageResource(imgs.getResourceId(position, -1));
                imgs.recycle();

                tTitle.setText(events[position] + "");
                tContent.setText(description[position] + "");

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
