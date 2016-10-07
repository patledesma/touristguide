package com.sulkud.touristguide.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sulkud.touristguide.R;
import com.sulkud.touristguide.helper.AsyncRouteRequest;
import com.sulkud.touristguide.helper.DirectionsJSONParser;
import com.sulkud.touristguide.helper.GetNearbyPlacesData;
import com.sulkud.touristguide.models.RouteModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class NavigateFragment extends Fragment implements
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private ViewGroup view;
    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private ImageButton bSearch;
    private double latitude;
    private double longitude;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<LatLng> markerPoints = new ArrayList<>();
    private Button drawRouteBtn;
    private LocationRequest mLocationRequest;

    public static LatLng toTouristDestination;

    private double baseFare = 9;
    private TextView tBusAirconFare, tBusFare, tMulticabFare, tTricycleFare, tDistance, tDuration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_navigate, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.navigateMap);
        mapFragment.getMapAsync(this);

        initialize();

        return view;
    }

    private void initialize() {
        bSearch = (ImageButton) view.findViewById(R.id.bSearch);
        bSearch.setOnClickListener(this);

        drawRouteBtn = (Button) view.findViewById(R.id.drawRouteBtn);
        drawRouteBtn.setOnClickListener(this);

        tBusAirconFare = (TextView) view.findViewById(R.id.tBusAirconFare);
        tBusFare = (TextView) view.findViewById(R.id.tBusFare);
        tMulticabFare = (TextView) view.findViewById(R.id.tMulticabFare);
        tTricycleFare = (TextView) view.findViewById(R.id.tTriCycleFare);
        tDistance = (TextView) view.findViewById(R.id.tDistance);
        tDuration = (TextView) view.findViewById(R.id.tDuration);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(getClass().getSimpleName(), "onMapReady");
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Log.e("NAVIGATION", "onmapclick : " + markerPoints.size());
                if (markerPoints.size() == 0 || markerPoints.size() == 2) {
                    mMap.clear();
                    markerPoints.clear();
                    drawRouteBtn.setVisibility(View.GONE);
                }

                if (markerPoints.size() > 2) {
                    return;
                }

                // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
                // Upto 8 waypoints are allowed in a query for non-business users


                // Adding new item to the ArrayList
                markerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);
                drawRouteBtn.setVisibility(View.VISIBLE);
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                // Removes all the points from Google Map
                mMap.clear();

                // Removes all the points in the ArrayList
                markerPoints.clear();
                drawRouteBtn.setVisibility(View.GONE);
                tBusFare.setText("");
                tBusAirconFare.setText("");
                tMulticabFare.setText("");
                tTricycleFare.setText("");
                tDistance.setText("");
                tDuration.setText("");

            }
        });
    }

    private void showRoute() {
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            showFare(origin, dest);

            AsyncRouteRequest asyncRouteRequest = new AsyncRouteRequest(getActivity(),
                    AsyncRouteRequest.UNIT_METRIC,
                    origin.latitude + "," + origin.longitude,
                    dest.latitude + "," + dest.longitude) {
                @Override
                public void onRouteRequestResult(RouteModel model) {
                    tDistance.setText(model.rows.get(0).elements.get(0).distance.text);
                    tDuration.setText(model.rows.get(0).elements.get(0).duration.text);
                }

                @Override
                public void onQueryingGoogleMatrixAPI() {
                    Log.i(getClass().getSimpleName(), "getting estimates from google API");
                }
            };
            asyncRouteRequest.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onMapSearch() {
        Log.e("NavigationFragment", "!!! onMapSearch");
        EditText locationSearch = (EditText) view.findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            String url = "";
            Object[] dataTransfer;
            GetNearbyPlacesData getNearbyPlacesData;
            try {
                addressList = geocoder.getFromLocationName(location, 1);

                Address address = addressList.get(0);
                final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                /*MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mCurrLocationMarker = mMap.addMarker(markerOptions);*/
                url = getCurrentPlaceUrl(latLng.latitude, latLng.longitude);
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData = new GetNearbyPlacesData(getActivity(), "map_search");
                getNearbyPlacesData.setResultListener(new GetNearbyPlacesData.ResultListener() {
                    @Override
                    public void onFinishRequest(List<HashMap<String, String>> place) {
                        Log.w("onFinishRequest", place.get(0).toString());
                        mCurrLocationMarker.setTitle(place.get(0).get("place_name"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }

                    @Override
                    public void onStartNavigate(boolean goNavigate, LatLng latLng1) {

                    }
                });
                getNearbyPlacesData.execute(dataTransfer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCurrentPlaceUrl(double latitude, double longitude) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSearch:
                onMapSearch();
                break;
            case R.id.drawRouteBtn:
                showRoute();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
//        latitude = 6.687227;
        longitude = location.getLongitude();
//        longitude = 124.676925;
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(getActivity(), "Your Current Location", Toast.LENGTH_LONG).show();

        //this is when we have a guest location to travel
        //ESP tourist spots
        if (toTouristDestination != null) {
            Log.e(getClass().getSimpleName(), "toTouristDestination is not null");
            if (markerPoints != null) {
                markerPoints.clear();
                markerPoints.add(toTouristDestination);
                MarkerOptions options = new MarkerOptions();
                options.position(toTouristDestination);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(options);
                markerPoints.add(latLng);
                options.position(latLng);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(options);
                showRoute();
                Log.d("MarkerPoints", Arrays.deepToString(markerPoints.toArray()));
            }
        } else {
            Log.e(getClass().getSimpleName(), "toTouristDestination is null");
        }

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }

        /**
         * A class to parse the Google Places in JSON format
         */
        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {

                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(4);
                    lineOptions.color(Color.RED);
                }

                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for (int i = 2; i < markerPoints.size(); i++) {
            LatLng point = markerPoints.get(i);
            if (i == 2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception downloading", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public float[] calculationByDistance(LatLng StartP, LatLng EndP) {
        float[] results = new float[1];
        Location.distanceBetween(StartP.latitude, StartP.longitude,
                EndP.latitude, EndP.longitude,
                results);

        return results;
    }

    private void showFare(LatLng start, LatLng end) {
        double busFare, busAirFare, multicabFare, tricylcleFare;
        double minKM = 5;
        float distance;

        distance = calculationByDistance(start, end)[0] / 1000;

        if (distance < 5) {
            Log.e("navigate", "!!! less than 5 :" + distance);
            busFare = busAirFare = multicabFare = tricylcleFare = 9;
        } else {
            Log.e("navigate", "!!! greater than 5 :" + distance);
            busFare = (baseFare * minKM) + ((distance - minKM) * 1.40);
            busAirFare = (baseFare * minKM) + ((distance - minKM) * 1.80);
            multicabFare = (baseFare * minKM) + ((distance - minKM) * 1.40);
            tricylcleFare = (baseFare * minKM) + ((distance - minKM) * 1.40);
        }

        tBusFare.setText(String.format("%.2f PHP", busFare));
        tBusAirconFare.setText(String.format("%.2f PHP", busAirFare));
        tMulticabFare.setText(String.format("%.2f PHP", multicabFare));
        tTricycleFare.setText(String.format("%.2f PHP", tricylcleFare));
    }
}
