package id.mainski.asuran;

import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import static id.mainski.asuran.R.id.mapDetail;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    LatLng pos;
    Double radius;
    Long end;
    String iTypeString;
    InsuranceType iType;
    String id;

    private GoogleMap mMap;
    MapView mapView;
    SupportMapFragment mapFragment;
    JSONObject objdata;
    TextView estpriceTV;
    TextView radiusTV;
    TextView addressTV;
    TextView placeTV;
    JSONObject objRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        String jsonString = bundle.getString("data");
        try {
            objRes = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        if (bundle.getString("id") != null) {
//            id = bundle.getString("id");
//            Toast.makeText(
//                    this,
//                    "id " + id,
//                    Toast.LENGTH_SHORT
//            ).show();
//        }
//        if (bundle.get("position") != null) {
//            pos = (LatLng) bundle.get("position");
//            Toast.makeText(
//                    this,
//                    "pos " + pos.toString(),
//                    Toast.LENGTH_SHORT
//            ).show();
//        }
//        radius = bundle.getDouble("radius");
//        Toast.makeText(
//                this,
//                "radius " + radius,
//                Toast.LENGTH_SHORT
//        ).show();
//        iTypeString = bundle.getString("type");
//        Toast.makeText(
//                this,
//                "iTypeString " + iTypeString,
//                Toast.LENGTH_SHORT
//        ).show();
//        if (iTypeString.equals("home")) {
//            iType = InsuranceType.HOME;
//        } else if (iTypeString.equals("car")) {
//            iType = InsuranceType.CAR;
//
//        } else if (iTypeString.equals("life")) {
//            iType = InsuranceType.LIFE;
//        }
//        Toast.makeText(
//                this,
//                "iType " + iType,
//                Toast.LENGTH_SHORT
//        ).show();


        // Initiate map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(mapDetail);
        mapFragment.getMapAsync(this);

        // init Component
        if(objRes != null) {
            estpriceTV = (TextView) findViewById(R.id.priceest);
            radiusTV = (TextView) findViewById(R.id.textView3);
            addressTV = (TextView) findViewById(R.id.address);
            try {
                double perHari = objRes.getInt("premium") / 30.0;
                DecimalFormat df = new DecimalFormat("#.##");
                String dx = df.format(perHari);

                estpriceTV.setText(dx + " ct / day");

                radius = objRes.getDouble("radius");
                radiusTV.setText(radius + " m");

                pos = new LatLng(objRes.getDouble("latitude"), objRes.getDouble("longitude"));
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {          //    ActivityCompat#requestPermissions
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(new Criteria(), true);

                Location locations = locationManager.getLastKnownLocation(provider);
                List<String> providerList = locationManager.getAllProviders();
                if (locations != null && providerList != null && providerList.size() > 0) {
                    double longitude = locations.getLongitude();
                    double latitude = locations.getLatitude();
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> listAddresses = null;
                    try {
                        listAddresses = geocoder.getFromLocation(pos.latitude, pos.longitude, 1);
                        String address = listAddresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = listAddresses.get(0).getLocality();
                        String state = listAddresses.get(0).getAdminArea();
                        String country = listAddresses.get(0).getCountryName();
                        String postalCode = listAddresses.get(0).getPostalCode();
                        String knownName = listAddresses.get(0).getFeatureName(); // Only if available else return


                        if (listAddresses.size() > 0) {
                            String prepare = "";

                            if(knownName != null && !knownName.equals("")){
                                prepare += "(";
                                prepare += knownName;
                                prepare += "), ";
                            }
                            if(address != null){
                                prepare += address;
                                prepare += ", ";
                            }
                            if(postalCode != null){
                                prepare += postalCode;
                                prepare += ", ";
                            }
                            if(city != null){
                                prepare += city;
                                prepare += ", ";
                            }
                            if(country != null){
                                prepare += country;
                            }
                            addressTV.setText(prepare);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null != listAddresses && listAddresses.size() > 0) {
                        String _Location = listAddresses.get(0).getAddressLine(0);
                    }

                }

                iTypeString = objRes.getString("type");
                if (iTypeString.equals("home")) {
                    iType = InsuranceType.HOME;
                } else if (iTypeString.equals("car")) {
                    iType = InsuranceType.CAR;

                } else if (iTypeString.equals("life")) {
                    iType = InsuranceType.LIFE;
                }

                    String place = objRes.getString("name");
                    placeTV = (TextView) findViewById(R.id.place);
                    placeTV.setText(place);

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }



//        final ActionBar actionBar = getActionBar();
//
//        TextView titleTextView = new TextView(actionBar.getThemedContext());
//
//        titleTextView.setText("Title");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        int fillC = 0;
        if(iType != null){
            switch (iType){
                case LIFE:
                    fillC = Color.argb(70, 221, 254, 10);
                case CAR:
                    fillC = Color.argb(70, 247, 76, 100);
                case HOME:
                    fillC = Color.argb(70, 91, 178, 100);
                    break;
            }
            mMap = googleMap;
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(pos.latitude,pos.longitude))
                    .fillColor(fillC)
                    .radius(radius)
                    .strokeWidth(1));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(pos.latitude,pos.longitude))
                    .zoom(13)
                    .bearing(2)
                    .tilt(5)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }
}
