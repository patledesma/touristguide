package com.sulkud.touristguide.helper;

import android.content.Context;
import android.os.AsyncTask;

import com.sulkud.touristguide.R;
import com.sulkud.touristguide.models.RouteModel;


public abstract class AsyncRouteRequest extends AsyncTask<Void, Void, Object> {

    public static final String UNIT_METRIC = "metric";
    public static final String UNIT_IMPERIAL = "imperial";

    private final String TAG_ORIGINS = "<origins>";
    private final String TAG_DESTINATIONS = "<destinations>";
    private final String TAG_UNIT = "<units>";
    private final String TAG_GOOGLE_API_KEY = "<api_key>";

    private String GOOGLE_DISTANCE_MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json?units=" + TAG_UNIT +
            "&origins=" + TAG_ORIGINS + "&destinations=" + TAG_DESTINATIONS + "&key=" + TAG_GOOGLE_API_KEY;

    public AsyncRouteRequest(Context context, String unitMeasurement, String origin, String destination) {
        GOOGLE_DISTANCE_MATRIX_API = GOOGLE_DISTANCE_MATRIX_API
                .replace(TAG_UNIT, unitMeasurement)
                .replace(TAG_ORIGINS, origin)
                .replace(TAG_DESTINATIONS, destination)
                .replace(TAG_GOOGLE_API_KEY, context.getString(R.string.google_maps_key));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Void... params) {
        return null;
    }

    public abstract void onRouteRequestResult(RouteModel model);
}
