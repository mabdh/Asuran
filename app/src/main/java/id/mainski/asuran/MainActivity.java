package id.mainski.asuran;

import android.*;
import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.R.id.message;
import static android.graphics.Color.argb;
import static id.mainski.asuran.R.id.end;
import static id.mainski.asuran.R.id.map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationSource.OnLocationChangedListener,
        ResultCallback<Status>,GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, LocationListener, GoogleMap.OnMarkerClickListener,GoogleMap.OnCameraMoveListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PREFS_NAME = "AsuranPref";
    private GoogleMap mMap;
    MapView mapView;
    SupportMapFragment mapFragment;
    static HashMap<String, Marker> lifeMarkerHash;
    static HashMap<String, Marker> carMarkerHash;
    static HashMap<String, Marker> homeMarkerHash;
    static HashMap<String, Circle> lifeCircleHash;
    static HashMap<String, Circle> carCircleHash;
    static HashMap<String, Circle> homeCircleHash;
    static HashMap<String, Long> lifeTime;
    static HashMap<String, Long> carTime;
    static HashMap<String, Long> homeTime;
    static HashMap<String, JSONObject> dataDump;

    Marker lifeMarker;
    Marker carMarker;
    Marker homeMarker;

    // GEOFENCING
    protected HashMap<String,Geofence> mGeofenceList;
    protected GoogleApiClient googleApiClient;
    //private Button mAddGeofencesButton;

    private Location lastLocation;
    private LocationRequest locationRequest;

    //// geofence marker

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;

    private Marker selfMarker;
    private Marker autoLocationMarker;
    //DEBUG
    Marker startMarker;
    Marker finishMarker;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    Boolean isButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize array
        lifeMarkerHash = new HashMap<String, Marker>();
        carMarkerHash = new HashMap<String, Marker>();
        homeMarkerHash = new HashMap<String, Marker>();
        lifeCircleHash = new HashMap<String, Circle>();
        carCircleHash = new HashMap<String, Circle>();
        homeCircleHash = new HashMap<String, Circle>();

        lifeTime = new HashMap<String, Long>();
        carTime = new HashMap<String, Long>();
        homeTime = new HashMap<String, Long>();

        dataDump = new HashMap<String, JSONObject>();

        //initialize UI

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // show life
        final FloatingActionButton showLifeButton = (FloatingActionButton) findViewById(R.id.show_life);
        showLifeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(lifeMarker!=null){
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lifeMarker.getPosition().latitude, lifeMarker.getPosition().longitude))
                    .zoom(15)
                    .bearing(0)
                    .tilt(0)
                    .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        // show car
        final FloatingActionButton showCarButton = (FloatingActionButton) findViewById(R.id.show_car);
        showCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(carMarker!=null){

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(carMarker.getPosition().latitude, carMarker.getPosition().longitude))
                            .zoom(15)
                            .bearing(0)
                            .tilt(0)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        // show home
        final FloatingActionButton showHomeButton = (FloatingActionButton) findViewById(R.id.show_home);
        showHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(homeMarker!=null){
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(homeMarker.getPosition().latitude, homeMarker.getPosition().longitude))
                            .zoom(15)
                            .bearing(0)
                            .tilt(0)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        // Button
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isButtonClicked) {
                    final Animation halfRotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.half_rotate);
                    fab.startAnimation(halfRotate);


                    showLifeButton.animate().translationXBy(-180).translationYBy(-100).alpha(1);
                    //showLifeButton.setLayoutParams(layoutParams);

                    showCarButton.animate().translationYBy(-200).alpha(1);

                    showHomeButton.animate().translationXBy(180).translationYBy(-100).alpha(1);


                    isButtonClicked = true;


                }else{

                    final Animation invHalfRotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.inv_half_rotate);
                    fab.startAnimation(invHalfRotate);

                    showLifeButton.animate().translationXBy(180).translationYBy(100).alpha(0);
                    //showLifeButton.setLayoutParams(layoutParams);

                    showCarButton.animate().translationYBy(200).alpha(0);

                    showHomeButton.animate().translationXBy(-180).translationYBy(100).alpha(0);

                    isButtonClicked = false;
                }

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initiate map
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        // Initiate Geofence
        //mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);

        // Empty list for storing geofences.
        mGeofenceList = new HashMap<String,Geofence>();


        // Kick off the request to build GoogleApiClient.
        createGoogleApi();


        initMqtt();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Address> addressList = null;

                if (query != null || !query.equals("")) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(query, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });

        return true;
    }

    private void setMyCurrentLocationMarker() {
        try {

            if(!checkPermission()){
                askPermission();
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    LocationManager locationManager = (LocationManager)
                            getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();

                    if (!checkPermission()) {
                        askPermission();
                        return false;
                    }
                    Location location = locationManager.getLastKnownLocation(locationManager
                            .getBestProvider(criteria, false));

                    if (location != null) {
                        mapFocus(location.getLatitude(), location.getLongitude(), "Mark");
                    }


                    return false;
                }

            });


        } catch (Exception e) {
            Log.e("Exception", "Exception : " + e.getMessage());


        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        setMyCurrentLocationMarker();
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnCameraMoveListener(this);
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(50.9368923,6.9386854) , 12.0f) );

        // Restore all prefs
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(settings.getString("lifeLocLat",null)!= null && settings.getString("lifeLocLong",null) !=null) {
            lifeMarker.setPosition(new LatLng(Double.parseDouble(settings.getString("lifeLocLat", null)), Double.parseDouble(settings.getString("lifeLocLong", null))));
        }
        if(settings.getString("carLocLat",null)!= null && settings.getString("carLocLong",null) !=null) {
            carMarker.setPosition(new LatLng(Double.parseDouble(settings.getString("carLocLat", null)), Double.parseDouble(settings.getString("carLocLong", null))));
        }
        if(settings.getString("homeLocLat",null)!= null && settings.getString("homeLocLong",null) !=null) {
            homeMarker.setPosition(new LatLng(Double.parseDouble(settings.getString("homeLocLat", null)), Double.parseDouble(settings.getString("homeLocLong", null))));
        }
    }

    // Menu

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Additional Functions
    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void mapFocus(Double latitude, Double longitude, String markName) {
        if (lifeMarker != null) {
            lifeMarker.remove();
            lifeMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.life_icon)));
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    public void addNewCircle(GoogleMap map, InsuranceType iType, Double radius, LatLng pos, String id, String title, Long endTime) {

        Marker markerVar = map.addMarker(new MarkerOptions()
                .position(new LatLng(pos.latitude, pos.longitude))
                .title(title).visible(false).flat(true)
        );
        markerVar.setTag(id);
        Circle circleVar = null;
        switch (iType) {
            case LIFE:
                circleVar = map.addCircle(new CircleOptions()
                        .center(new LatLng(pos.latitude, pos.longitude))
                        .radius(radius)
                        .strokeWidth(1)
                        .fillColor(argb(70, 221, 254, 100))
                        .clickable(true).zIndex(0));

//                animateCircle(circleVar, id, radius);
                lifeMarkerHash.put(id, markerVar);
                lifeCircleHash.put(id, circleVar);
                lifeTime.put(id,endTime);
                //insuranceSpots.addNewInsurance(circleVar.getRadius(), InsuranceType.LIFE, markerVar.getPosition(), id);
                break;
            case CAR:
                circleVar = map.addCircle(new CircleOptions()
                        .center(new LatLng(pos.latitude, pos.longitude))
                        .radius(radius)
                        .strokeWidth(1)
                        .fillColor(argb(70, 247, 76, 100))
                        .clickable(true).zIndex(0));


//                animateCircle(circleVar, id, radius);
                carMarkerHash.put(id, markerVar);
                carCircleHash.put(id, circleVar);
                carTime.put(id,endTime);
                //insuranceSpots.addNewInsurance(circleVar.getRadius(), InsuranceType.CAR, markerVar.getPosition(), id);
                break;
            case HOME:

                markerVar.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon));
                circleVar = map.addCircle(new CircleOptions()
                        .center(new LatLng(pos.latitude, pos.longitude))
                        .radius(radius)
                        .strokeWidth(1)
                        .fillColor(argb(70, 91, 178, 100))
                        .clickable(true).zIndex(0));

//                animateCircle(circleVar, id, radius);
                homeMarkerHash.put(id, markerVar);
                homeCircleHash.put(id, circleVar);

                homeTime.put(id,endTime);
               // insuranceSpots.addNewInsurance(circleVar.getRadius(), InsuranceType.HOME, markerVar.getPosition(), id);
                break;
        }
        Geofence gf = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(
                        pos.latitude,
                        pos.longitude,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        mGeofenceList.put(id,gf);
        try {

            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            builder.addGeofence(gf);



            Intent intent = new Intent(this, GeofenceTransitionService.class);
            PendingIntent pi = PendingIntent.getService(this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    builder.build(),
                    pi
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }

    }


//    public void removeInsurance(String key, InsuranceType type) {
//        switch (type) {
//            case LIFE:
//                lifeMarkerHash.remove(key);
//                lifeCircleHash.remove(key);
//                lifeTime.remove(key);
////                lifeAnim.remove(key);
//                //insuranceSpots.removeInsurance(key, type);
//                break;
//            case CAR:
//                carMarkerHash.remove(key);
//                carMarkerHash.remove(key);
//                carTime.remove(key);
////                carAnim.remove(key);
//                //insuranceSpots.removeInsurance(key, type);
//                break;
//            case HOME:
//                homeMarkerHash.remove(key);
//                homeMarkerHash.remove(key);
//                homeTime.remove(key);
////                homeAnim.remove(key);
//                //insuranceSpots.removeInsurance(key, type);
//                break;
//        }
//    }

    // TODO GEOFENCING

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        } else askPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
        Toast.makeText(
                this,
                "Geofencing connection failed..!",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        savePreferences();

        client.disconnect();

    }

    public void savePreferences(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if(settings.getString("homeLocLat",null)!= null ) {

            editor.putString("homeLocLat", String.valueOf(homeMarker.getPosition().latitude));
        }
        if(settings.getString("homeLocLong",null) !=null) {
            editor.putString("homeLocLong", String.valueOf(homeMarker.getPosition().longitude));
        }
        if(settings.getString("carLocLat",null)!= null )
        {

            editor.putString("carLocLat", String.valueOf(carMarker.getPosition().latitude));
        }
        if(settings.getString("carLocLong",null) !=null)
        {
            editor.putString("carLocLong", String.valueOf(carMarker.getPosition().longitude));
        }
        if(settings.getString("lifeLocLat",null)!= null ) {
            editor.putString("lifeLocLat", String.valueOf(lifeMarker.getPosition().latitude));

        }
        if(settings.getString("lifeLocLong",null)!= null ) {
            editor.putString("lifeLocLong", String.valueOf(lifeMarker.getPosition().longitude));

        }

        editor.commit();
    }


    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(mGeofenceList);
//        return builder.build();
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
//        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }

    // Start Geofence creation process
    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if (geoFenceMarker != null) {
            Geofence geofence = createGeofence(geoFenceMarker.getPosition(), GEOFENCE_RADIUS);
            GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
            addGeofence(geofenceRequest);
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    // Start location Updates
    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (checkPermission())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
        if(lifeMarker!=null){
            lifeMarker.remove();
            lifeMarker = mMap.addMarker( new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .title("You")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.life_icon)));
        }

    }


    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(this,new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, 1);
    }


    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }


    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofence Added",
                    Toast.LENGTH_SHORT
            ).show();
            drawGeofence();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(status.getStatusCode());
        }
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;

    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if (geoFenceLimits != null)
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center(geoFenceMarker.getPosition())
                .strokeColor(argb(50, 70, 70, 70))
                .fillColor(argb(100, 150, 150, 150))
                .radius(GEOFENCE_RADIUS);
        geoFenceLimits = mMap.addCircle(circleOptions);
    }





    private Marker geoFenceMarker;

    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if (mMap != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = mMap.addMarker(markerOptions);
        }
    }


    // Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(Geofence geofence) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick(" + latLng + ")");
        markerForGeofence(latLng);
    }

    ////
    @Override
    public void onMapLongClick(final LatLng latLng) {
        final LatLng point = latLng;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // set title
        alertDialogBuilder.setTitle("Place Manually");

        // set dialog message
        alertDialogBuilder
                .setTitle("Place the location of your car or home manually")
                .setItems(R.array.manual_place, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            // home
                            if(homeMarker != null){
                                homeMarker.remove();
                            }

                            homeMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Home").icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon)));

                        }
                        else {
                            // car

                            if(carMarker != null){
                                carMarker.remove();
                            }

                            carMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Car").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
                        }
                        savePreferences();
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


//
//        markerForGeofence(latLng);
    }



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }




    MqttAndroidClient mqttAndroidClient;
    final String serverUri = "mqtt://test.mosquito.org";

    final String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "asuran-geo";
    final String publishTopic = "exampleAndroidPublishTopic";
    final String publishMessage = "Hello World!";

    public void initMqtt() {

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                clientId);

        Log.d(TAG, "MQTT");


        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        //mqttConnectOptions.set(true);
        mqttConnectOptions.setCleanSession(false);

        String clientId = MqttClient.generateClientId();

        try {
            IMqttToken token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    subscribeToTopic();
                    Toast.makeText(MainActivity.this,
                            "Loading..",
                            Toast.LENGTH_SHORT).show();
                    subscribeToTopic();
                    mqttAndroidClient.setCallback(new MqttCallback() {
                        public void connectComplete(boolean reconnect, String serverURI) {

                            if (reconnect) {
                                // Because Clean Session is true, we need to re-subscribe
                                //subscribeToTopic();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Connected to: " + serverURI,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void connectionLost(Throwable cause) {
                            Log.d(TAG, "The Connection was lost.");
                            Toast.makeText(MainActivity.this,
                                    "The Connection was lost.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {

                            Log.d(TAG, "Incoming message: " + new String(message.getPayload()));
//                            Toast.makeText(MainActivity.this,
//                                    "Incoming message: " + new String(message.getPayload()),
//                                    Toast.LENGTH_SHORT).show();
                            processGPSData(new String(message.getPayload()));

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed !");

                    Toast.makeText(MainActivity.this,
                            "Loading..",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to subscribe !");

                    Toast.makeText(MainActivity.this,
                            "Failed to subscribe !",
                            Toast.LENGTH_SHORT).show();
                }
            });


        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }


    public void processGPSData(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            dataDump.put((String)obj.get("_id"),obj);
            String iType = obj.getString("type");
            Log.d(TAG, "DEBUG: iType " + iType);
            Double latitude = obj.getDouble("latitude");
            Log.d(TAG, "DEBUG: latitude " + latitude);
            Double longitude = obj.getDouble("longitude");
            Log.d(TAG, "DEBUG: longitude " + longitude);
            Double radius = obj.getDouble("radius");
            Log.d(TAG, "DEBUG: radius " + radius);
            String titleName = obj.getString("name");
            Log.d(TAG, "DEBUG: name " + titleName);
            String endTimeString = obj.getString("end");

            String idI = obj.getString("_id");

            Long endTime=Long.parseLong(endTimeString);

            Log.d(TAG, "DEBUG: idI " + idI);
            Long curTime = new Date().getTime();
            Log.d(TAG, "DEBUG: curTime " + curTime);
            List<String> idOfGeofence = new ArrayList<String>();
            idOfGeofence.add(idI);

            if(iType.equals("life")){
                // check member


                        if(lifeMarkerHash.containsKey(idI)){

                            Log.d(TAG, "COUNTING " + String.valueOf(curTime - lifeTime.get(idI)));
                                if (curTime.compareTo(lifeTime.get(idI)) > 0) {
                                    lifeMarkerHash.get(idI).remove();
                                    lifeMarkerHash.remove(idI);
                                    lifeCircleHash.get(idI).remove();
                                    lifeCircleHash.remove(idI);
                                    lifeTime.remove(idI);

                                    dataDump.remove(idI);

                                    LocationServices.GeofencingApi.removeGeofences(
                                            googleApiClient,
                                            idOfGeofence
                                    );
                                    mGeofenceList.remove(idI);
//                                    lifeAnim.get(idI).end();
//                                    lifeAnim.remove(idI);
                                    Log.d(TAG,"Remove life");
                                } else {
                                    // update
                                    lifeMarkerHash.get(idI).setPosition(new LatLng(latitude, longitude));
                                    lifeMarkerHash.get(idI).setTitle(titleName);
                                    lifeCircleHash.get(idI).setRadius(radius);

                                    Log.d(TAG,"Update life");
                                }

                        }
                        else{
                            // Create new
                            addNewCircle(mMap,InsuranceType.LIFE, radius,new LatLng(latitude,longitude), idI,titleName,endTime);

                            Log.d(TAG,"New life");
                        }

            }
            else if(iType.equals("car")){


                        // check member
                        if(carMarkerHash.containsKey(idI)){
                                if (curTime.compareTo(carTime.get(idI)) > 0) {
                                    carMarkerHash.get(idI).remove();
                                    carMarkerHash.remove(idI);
                                    carCircleHash.get(idI).remove();
                                    carCircleHash.remove(idI);
                                    carTime.remove(idI);


                                    dataDump.remove(idI);
//                                    carAnim.get(idI).end();
//                                    carAnim.remove(idI);
                                    LocationServices.GeofencingApi.removeGeofences(
                                            googleApiClient,
                                            idOfGeofence
                                    );
                                    mGeofenceList.remove(idI);
                                    Log.d(TAG,"Remove car");
                                } else {
                                    // update
                                    carMarkerHash.get(idI).setPosition(new LatLng(latitude, longitude));
                                    carMarkerHash.get(idI).setTitle(titleName);
                                    carCircleHash.get(idI).setRadius(radius);

                                    Log.d(TAG,"Update car");
                                }

                        }
                        else{
                            // Create new
                            addNewCircle(mMap,InsuranceType.CAR, radius,new LatLng(latitude,longitude),idI,titleName, endTime);

                            Log.d(TAG,"New car");
                        }



            }
            else if(iType.equals("home")){


                        // check member
                        if(homeMarkerHash.containsKey(idI)){
                                if (curTime.compareTo(homeTime.get(idI)) > 0) {
                                    homeMarkerHash.get(idI).remove();
                                    homeMarkerHash.remove(idI);
                                    homeCircleHash.get(idI).remove();
                                    homeCircleHash.remove(idI);
                                    homeTime.remove(idI);


                                    dataDump.remove(idI);

                                    Log.d(TAG,"Remove home");
                                    LocationServices.GeofencingApi.removeGeofences(
                                            googleApiClient,
                                            idOfGeofence
                                    );
                                    mGeofenceList.remove(idI);
                                } else {
                                    // update
                                    homeMarkerHash.get(idI).setPosition(new LatLng(latitude, longitude));
                                    homeMarkerHash.get(idI).setTitle(titleName);
                                    homeCircleHash.get(idI).setRadius(radius);

//                                    homeAnim.get(idI).end();
//                                    homeAnim.remove(idI);

                                    Log.d(TAG,"Update home");
                                }

                        }
                        else{
                            // Create new
                            addNewCircle(mMap,InsuranceType.HOME, radius,new LatLng(latitude,longitude),idI,titleName, endTime);


                            Log.d(TAG,"New home");
                        }


            }
            else{

            }
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(latitude, longitude))
//                    .zoom(15)
//                    .bearing(0)
//                    .tilt(0)
//                    .build();
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
           //mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(50.9368923,6.9386854) , 12.0f) );
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }



    @Override
    public boolean onMarkerClick(Marker marker) {


            String id = (String) marker.getTag();

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("data", dataDump.get(id).toString());
//            InsuranceType insType = null;
//            if (lifeMarkerHash.containsKey(id)) {
//                insType = InsuranceType.LIFE;
//            } else if (carMarkerHash.containsKey(id)) {
//                insType = InsuranceType.CAR;
//            } else if (homeMarkerHash.containsKey(id)) {
//                insType = InsuranceType.HOME;
//            } else {
//
//            }
//            if (insType != null) {
//                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                intent.putExtra("position", marker.getPosition());
//                intent.putExtra("id", id);
//                switch (insType) {
//                    case LIFE:
//                        intent.putExtra("radius", lifeCircleHash.get(id).getRadius());
//
//                        intent.putExtra("end", lifeTime.get(id).longValue());
//                        break;
//                    case HOME:
//                        intent.putExtra("radius", homeCircleHash.get(id).getRadius());
//                        intent.putExtra("end", homeTime.get(id).longValue());
//                        break;
//                    case CAR:
//                        intent.putExtra("radius", carCircleHash.get(id).getRadius());
//                        intent.putExtra("end", carTime.get(id).longValue());
//                        break;
//                }
//                if(insType == InsuranceType.HOME){
//                    intent.putExtra("type", "home");
//                }
//                else if(insType == InsuranceType.CAR){
//                    intent.putExtra("type", "car");
//
//                }
//                else if(insType == InsuranceType.LIFE){
//                    intent.putExtra("type", "life");
//                }
//                MainActivity.this.startActivity(intent);
//                finish();
//            }
        MainActivity.this.startActivity(intent);

        return false;
    }

    @Override
    public void onCameraMove() {
        if(mMap.getCameraPosition().zoom > 13)
        {
            for (Marker m : lifeMarkerHash.values()) {
                m.setVisible(true);
            }
            for (Marker m : carMarkerHash.values()) {
                m.setVisible(true);
            }
            for (Marker m : homeMarkerHash.values()) {
                m.setVisible(true);
            }
        }
        else
        {
            for (Marker m : lifeMarkerHash.values()) {
                m.setVisible(false);
            }
            for (Marker m : carMarkerHash.values()) {
                m.setVisible(false);
            }
            for (Marker m : homeMarkerHash.values()) {
                m.setVisible(false);
            }
        }
    }

//    public String getCircleAsuranIdFromJavaId(String circleid){
//
//        for(Map.Entry<String, Circle> c : lifeCircleHash.entrySet()) {
//            String key = c.getKey();
//            Circle value = c.getValue();
//
//            if(circleid == value.getId()){
//                return key;
//            }
//        }
//            for(Map.Entry<String, Circle> c : carCircleHash.entrySet()) {
//                String key = c.getKey();
//                Circle value = c.getValue();
//
//                if(circleid == value.getId()){
//                    return key;
//                }
//            }
//                for(Map.Entry<String, Circle> c : homeCircleHash.entrySet()) {
//                    String key = c.getKey();
//                    Circle value = c.getValue();
//
//                    if(circleid == value.getId()){
//                        return key;
//                    }
//                    else{
//                        return null;
//                    }
//                }
//
//        return null;
//    }
}
