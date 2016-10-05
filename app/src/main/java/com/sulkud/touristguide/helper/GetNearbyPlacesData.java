package com.sulkud.touristguide.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sulkud.touristguide.R;
import com.sulkud.touristguide.adapter.CustomInfoWindowAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    private String taskTag;
    private Context context;

    ResultListener resultListener;

    public GetNearbyPlacesData(Context context, String taskTag) {
        this.taskTag = taskTag;
        this.context = context;
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];

            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        Log.i("GET_PLACE_DATA", nearbyPlacesList.toString());
        if (resultListener != null) {
            resultListener.onFinishRequest(nearbyPlacesList);
        } else {
            if (this.taskTag.equals("poi.attraction")) {
                nearbyPlacesList = getTouristAttractionHashList();
            }
            ShowNearbyPlaces(nearbyPlacesList);
        }
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute","Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            String bitmap = googlePlace.get("bitmap");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            markerOptions.snippet(lat + ", " + lng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(bitmap);
            //move map camera
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this.context));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }

    public interface ResultListener{
        void onFinishRequest(List<HashMap<String, String>> place);
    }

    public List<HashMap<String, String>> getTouristAttractionHashList(){
        List<HashMap<String, String>> attractions = new ArrayList<>();

        HashMap<String, String> entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Pangadilan Water Falls of Columbio");
        entry.put("lat", "6.635625");
        entry.put("lng", "125.083734");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Palm Oil of Isulan");
        entry.put("lat", "6.6709587");
        entry.put("lng", "124.6319103");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Banana Plantation of Lambayong");
        entry.put("lat", "6.800257");
        entry.put("lng", "124.633704");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Rajahbuayan Convention Center of Lutayan");
        entry.put("lat", "6.6083016");
        entry.put("lng", "124.8412112");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Muscovado of President Quirino");
        entry.put("lat", "6.701257");
        entry.put("lng", "124.743017");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Birds Sanctuary of Tacurong City");
        entry.put("lat", "6.628550");
        entry.put("lng", "124.638005");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Lagbasan Cave of Senator Ninoy Aquino");
        entry.put("lat", "6.384193");
        entry.put("lng", "124.354145");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Bamban Falls of Bagumbayan");
        entry.put("lat", "6.4568242");
        entry.put("lng", "124.5454731");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Hot and Cold Spring of Esperanza");
        entry.put("lat", "6.693618");
        entry.put("lng", "124.550148");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Tuna Bay of Palimbang");
        entry.put("lat", "6.211846");
        entry.put("lng", "124.189563");
        entry.put("bitmap", "");
        attractions.add(entry);

        entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Mangroves of Lebak");
        entry.put("lat", "6.629472");
        entry.put("lng", "124.054569");
        entry.put("bitmap", "");
        attractions.add(entry);

        /*entry = new HashMap<>();
        entry.put("vicinity", "Sultan Kudarat");
        entry.put("place_name", "Balut Island of Kalamansig");
        entry.put("lat", "5.3982258");
        entry.put("lng", "125.3760378");
        attractions.add(entry);*/

        return attractions;
    }
}
