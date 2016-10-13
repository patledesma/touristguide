package com.sulkud.touristguide.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.sa90.materialarcmenu.ArcMenu;
import com.sulkud.touristguide.R;
import com.sulkud.touristguide.adapter.CustomInfoWindowAdapter;
import com.sulkud.touristguide.fragment.BookmarkedPlacesFragment;
import com.sulkud.touristguide.fragment.EventsFragment;
import com.sulkud.touristguide.fragment.NavigateFragment;
import com.sulkud.touristguide.fragment.VisitedPlacesFragment;
import com.sulkud.touristguide.helper.DirectionsJSONParser;
import com.sulkud.touristguide.helper.GetNearbyPlacesData;
import com.sulkud.touristguide.helper.database.DatabaseHandler;
import com.sulkud.touristguide.interfaces.PlaceSelectedListener;
import com.sulkud.touristguide.models.PlaceModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private GoogleMap mMap;
    private Fragment eventsFragment, visitedPlacesFragment, bookmarkedPlacesFragment, navigationFragment;

    private ArcMenu arcMenu;
    private FloatingActionButton fabHospital, fabHotel, fabBank, fabRestaurant, fabTourist;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Button visitedPlaceBtn, bookmarkPlaceBtn;

    private LinearLayout llSearch, llButtons;
    private double latitude;
    private double longitude;
    private int PROXIMITY_RADIUS = 10000;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private DatabaseHandler dbHandler;
    private ArrayList<LatLng> markerPoints = new ArrayList<>();

    //FACEBOOK FIELDS
    private CallbackManager facebookCallbackManager;
    private ShareDialog facebookShareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        initialize();
        initializeFacebookAPI();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void initialize() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        arcMenu = (ArcMenu) findViewById(R.id.arcMenu);
        arcMenu.setRadius(200f); //change value which ever fits your screen
        fabHospital = (FloatingActionButton) findViewById(R.id.fabHospital);
        fabHotel = (FloatingActionButton) findViewById(R.id.fabHotel);
        fabBank = (FloatingActionButton) findViewById(R.id.fabBank);
        fabRestaurant = (FloatingActionButton) findViewById(R.id.fabRestaurant);
        fabTourist = (FloatingActionButton) findViewById(R.id.fabTourist);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        llButtons = (LinearLayout) findViewById(R.id.llSearch);
        bookmarkPlaceBtn = (Button) findViewById(R.id.bookmarkPlaceBtn);
        visitedPlaceBtn = (Button) findViewById(R.id.visitedPlaceBtn);

        fabHospital.setOnClickListener(this);
        fabHotel.setOnClickListener(this);
        fabBank.setOnClickListener(this);
        fabRestaurant.setOnClickListener(this);
        fabTourist.setOnClickListener(this);
        bookmarkPlaceBtn.setOnClickListener(this);
        visitedPlaceBtn.setOnClickListener(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        eventsFragment = new EventsFragment();
        visitedPlacesFragment = new VisitedPlacesFragment();
        bookmarkedPlacesFragment = new BookmarkedPlacesFragment();
        navigationFragment = new NavigateFragment();

        dbHandler = new DatabaseHandler(this);
    }

    private void initializeFacebookAPI(){
        facebookCallbackManager = CallbackManager.Factory.create();
        facebookShareDialog = new ShareDialog(this);
        facebookShareDialog.registerCallback(facebookCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(MainActivity.this, "facebook onSuccess()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "facebook onCancel()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "facebook onError()", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.shareOnFacebook) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Hi! Currently @ " + mCurrLocationMarker.getTitle())
                        .setContentDescription(mCurrLocationMarker.getPosition().latitude + " " + mCurrLocationMarker.getPosition().longitude)
                        .setContentUrl(Uri.parse("https://www.google.com/maps/preview/@" +
                                mCurrLocationMarker.getPosition().latitude + "," +
                                mCurrLocationMarker.getPosition().longitude + ",20z"))
                        .build();
                facebookShareDialog.show(linkContent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.w("StackTrace", getSupportFragmentManager().getBackStackEntryCount() + " stack count");

        if (id == R.id.nav_map) {
            arcMenu.setVisibility(View.VISIBLE);
            removeFragment(eventsFragment);
            removeFragment(visitedPlacesFragment);
            removeFragment(bookmarkedPlacesFragment);
            removeFragment(navigationFragment);
            llSearch.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.VISIBLE);
            this.setTitle("Maps");
        } else if (id == R.id.nav_visited_places) {
            arcMenu.setVisibility(View.GONE);
            removeFragment(eventsFragment);
            removeFragment(navigationFragment);
            removeFragment(bookmarkedPlacesFragment);
            switchFragment(visitedPlacesFragment);
            llSearch.setVisibility(View.GONE);
            llButtons.setVisibility(View.GONE);
            this.setTitle("Visited Places");
        } else if (id == R.id.nav_bookmark) {
            arcMenu.setVisibility(View.GONE);
            removeFragment(visitedPlacesFragment);
            removeFragment(navigationFragment);
            removeFragment(visitedPlacesFragment);
            switchFragment(bookmarkedPlacesFragment);
            llSearch.setVisibility(View.GONE);
            llButtons.setVisibility(View.GONE);
            this.setTitle("Bookmarked Places");
        } else if (id == R.id.nav_events) {
            arcMenu.setVisibility(View.GONE);
            removeFragment(visitedPlacesFragment);
            removeFragment(bookmarkedPlacesFragment);
            removeFragment(navigationFragment);
            switchFragment(eventsFragment);
            llSearch.setVisibility(View.GONE);
            llButtons.setVisibility(View.GONE);
            this.setTitle("Events");
        } else if (id == R.id.nav_navigate) {
            arcMenu.setVisibility(View.GONE);
            removeFragment(eventsFragment);
            removeFragment(visitedPlacesFragment);
            removeFragment(bookmarkedPlacesFragment);
            switchFragment(navigationFragment);
            llSearch.setVisibility(View.GONE);
            llButtons.setVisibility(View.GONE);
            this.setTitle("Navigate");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            String url = "";
            Object[] dataTransfer;
            GetNearbyPlacesData getNearbyPlacesData;
            try {
                addressList = geocoder.getFromLocationName(location, 1);

                Address address = addressList.get(0);
                final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mCurrLocationMarker.setPosition(latLng);
                url = getCurrentPlaceUrl(latLng.latitude, latLng.longitude);
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData = new GetNearbyPlacesData(this, "map_search");
                getNearbyPlacesData.setResultListener(new GetNearbyPlacesData.ResultListener() {
                    @Override
                    public void onFinishRequest(List<HashMap<String, String>> place) {
                        Log.w("onFinishRequest", place.get(0).toString());
                        mCurrLocationMarker.setTitle(place.get(0).get("place_name"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }

                    @Override
                    public void onStartNavigate(boolean goNavigate, LatLng latLng1) {
                        //ignore this here since we dont need to navigate, UNLESS we need to...
                    }
                });
                getNearbyPlacesData.execute(dataTransfer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }

    private void removeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        String url = "";
        Object[] dataTransfer;
        GetNearbyPlacesData getNearbyPlacesData;
        switch (v.getId()) {
            case R.id.fabHospital:
                arcMenu.toggleMenu();
                mMap.clear();
                Log.d("onClick", "Button is Clicked");
                url = getUrl(latitude, longitude, "hospital");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                Log.d("onClick", url);
                getNearbyPlacesData = new GetNearbyPlacesData(this, "hospital");
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainActivity.this, "Nearby Hospitals", Toast.LENGTH_LONG).show();
                break;
            case R.id.fabHotel:
                arcMenu.toggleMenu();
                mMap.clear();
                Log.d("onClick", "Button is Clicked");
                url = getUrl(latitude, longitude, "hotel");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                Log.d("onClick", url);
                getNearbyPlacesData = new GetNearbyPlacesData(this, "hotel");
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainActivity.this, "Nearby Hotels", Toast.LENGTH_LONG).show();
                break;
            case R.id.fabBank:
                arcMenu.toggleMenu();
                mMap.clear();
                Log.d("onClick", "Button is Clicked");
                url = getUrl(latitude, longitude, "bank");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                Log.d("onClick", url);
                getNearbyPlacesData = new GetNearbyPlacesData(this, "bank");
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainActivity.this, "Nearby Banks", Toast.LENGTH_LONG).show();
                break;
            case R.id.fabRestaurant:
                arcMenu.toggleMenu();
                mMap.clear();
                Log.d("onClick", "Button is Clicked");
                url = getUrl(latitude, longitude, "restaurant");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                Log.d("onClick", url);
                getNearbyPlacesData = new GetNearbyPlacesData(this, "restaurant");
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainActivity.this, "Nearby Restaurants", Toast.LENGTH_LONG).show();
                break;
            case R.id.fabTourist:
                arcMenu.toggleMenu();
                mMap.clear();
                Log.d("onClick", "Button is Clicked");
                url = getUrl(latitude, longitude, "poi.attraction");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                Log.d("onClick", url);
                getNearbyPlacesData = new GetNearbyPlacesData(this, "poi.attraction");
                getNearbyPlacesData.setResultListener(new GetNearbyPlacesData.ResultListener() {
                    @Override
                    public void onFinishRequest(List<HashMap<String, String>> place) {
                        //dont care about this
                    }

                    @Override
                    public void onStartNavigate(boolean goNavigate, LatLng latLng) {
                        //i need to trigger this to infalte NavigationFragment and set tourist destination
                        if (navigationFragment != null) {
                            NavigateFragment.toTouristDestination = latLng;
                            NavigateFragment.startToTouristDestination = goNavigate;
                            arcMenu.setVisibility(View.GONE);
                            removeFragment(eventsFragment);
                            removeFragment(visitedPlacesFragment);
                            switchFragment(navigationFragment);
                            llSearch.setVisibility(View.GONE);
                            llButtons.setVisibility(View.GONE);
                            MainActivity.this.setTitle("Navigate");
                        }
                    }
                });
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainActivity.this, "Nearby Tourist Attractions", Toast.LENGTH_LONG).show();
                break;
            case R.id.bookmarkPlaceBtn:
                double bookmarkedLat = mCurrLocationMarker.getPosition().latitude;
                double bookmarkedLng = mCurrLocationMarker.getPosition().longitude;

                if (dbHandler.queryIfExistPlace(String.valueOf(bookmarkedLat), String.valueOf(bookmarkedLng), DatabaseHandler.TABLE_TAG_TYPE_BOOKMARKED)) {
                    Toast.makeText(this, "Place is already bookmarked!", Toast.LENGTH_LONG).show();
                } else {
                    PlaceModel model = new PlaceModel(0, mCurrLocationMarker.getTitle(), String.valueOf(bookmarkedLat), String.valueOf(bookmarkedLng), "", DatabaseHandler.TABLE_TAG_TYPE_BOOKMARKED);
                    dbHandler.addPlace(model, DatabaseHandler.TABLE_TAG_TYPE_BOOKMARKED);
                    Toast.makeText(this, "Bookmarked Place...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.visitedPlaceBtn:
                double visitedLat = mCurrLocationMarker.getPosition().latitude;
                double visitedLng = mCurrLocationMarker.getPosition().longitude;

                if (dbHandler.queryIfExistPlace(String.valueOf(visitedLat), String.valueOf(visitedLng), DatabaseHandler.TABLE_TAG_TYPE_VISITED)) {
                    Toast.makeText(this, "Place is already bookmarked!", Toast.LENGTH_LONG).show();
                } else {
                    PlaceModel model = new PlaceModel(0, mCurrLocationMarker.getTitle(), String.valueOf(visitedLat), String.valueOf(visitedLng), "", DatabaseHandler.TABLE_TAG_TYPE_VISITED);
                    dbHandler.addPlace(model, DatabaseHandler.TABLE_TAG_TYPE_VISITED);
                    Toast.makeText(this, "Visited Place...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.drawRouteBtn:
                break;
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    private String getCurrentPlaceUrl(double latitude, double longitude) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

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
        markerOptions.title("Tacurong City");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(MainActivity.this, "Your Current Location", Toast.LENGTH_LONG).show();

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public void showHistory(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_history, null);
        dialog.show();
        dialog.setContentView(view);

        TextView tTitle = (TextView) view.findViewById(R.id.tTitle);
        TextView tContent = (TextView) view.findViewById(R.id.tContent);

        tTitle.setText("HISTORY");
        tContent.setText(R.string.history);

        Button dialogButton = (Button) dialog.findViewById(R.id.bDismiss);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //temporary

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

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
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
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    public void testFunction(PlaceModel placeModel) {
        Log.e("MainActivity", "testFunction !!!!!!!!!!!!!");
        navigationFragment = new NavigateFragment();
        if (navigationFragment != null) {
            Log.e("MainActivity", "navigationFragment is not null");
            NavigateFragment.toTouristDestination = new LatLng(Double.valueOf(placeModel.latitude),
                    Double.valueOf(placeModel.longitude));
            NavigateFragment.startToTouristDestination = true;
            arcMenu.setVisibility(View.GONE);
            removeFragment(eventsFragment);
            removeFragment(visitedPlacesFragment);
            switchFragment(navigationFragment);
            llSearch.setVisibility(View.GONE);
            llButtons.setVisibility(View.GONE);
            MainActivity.this.setTitle("Navigate");
        } else {
            Log.e("MainActivity", "navigation is null fvcking fragment!!!!!!");
        }
    }
}
