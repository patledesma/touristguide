package com.sulkud.touristguide.helper;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.sulkud.touristguide.R;
import com.sulkud.touristguide.models.RouteModel;

import java.io.IOException;


public abstract class AsyncRouteRequest extends AsyncTask<Void, Void, RouteModel> {

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
        onQueryingGoogleMatrixAPI();
    }

    @Override
    protected RouteModel doInBackground(Void... params) {
        try {
            DownloadUrl downloadUrl = new DownloadUrl();
            String routeRequestResult = downloadUrl.readUrl(GOOGLE_DISTANCE_MATRIX_API);
            return (new Gson()).fromJson(routeRequestResult, RouteModel.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(RouteModel routeModel) {
        super.onPostExecute(routeModel);
        onRouteRequestResult(routeModel);
    }

    public abstract void onRouteRequestResult(RouteModel model);

    public abstract void onQueryingGoogleMatrixAPI();
}
