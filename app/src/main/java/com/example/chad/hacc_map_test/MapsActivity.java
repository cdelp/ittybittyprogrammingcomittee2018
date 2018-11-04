package com.example.chad.hacc_map_test;

// references:
// https://javapapers.com/android/android-location-fused-provider/

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    private SensorManager sensorManager;
    private int toastDuration = Toast.LENGTH_SHORT;
    private ArrayList locationsList;
    private int northernMostIndex;
    private int southernMostIndex;
    private int westernMostIndex;
    private int easternMostIndex;

    private boolean logGPS = false;

    /**
     *
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // list of all locations
        locationsList = new ArrayList();

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("HACC", "Location permission not granted");
            return;
        } else {
            Log.e("HACC", "Location permission granted");
        }


        //TODO testing only, use sensors if needed
        // Get list of sensors
//        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
//        List<Sensor> msensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
//        Log.d("HACC", msensorList.toString());

        Button startButton = findViewById(R.id.Start);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // trigger logging of gps and sensors
                logGPS = true;
                //onLocationChanged(location);

                Log.d("HACC", "start button pressed");
                CharSequence startText = "Starting Tracking";
                Toast toast = Toast.makeText(getApplicationContext(), startText, toastDuration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0 );
                toast.show();
            }
        });

        Button stopButton = findViewById(R.id.Stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // stop logging of gps and sensors
                logGPS = false;

                Log.d("HACC", "stop button pressed");
                CharSequence stopText = "Stopping Tracking";
                Toast toast = Toast.makeText(getApplicationContext(), stopText, toastDuration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0 );
                toast.show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Triggered when GPS or Network location changes
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("HACC", "onLocationChanged");

        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.d("HACC", "last update: " + mLastUpdateTime);

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        LatLng latLng = new LatLng( lat, lon );
        Log.d("HACC", "lat: " + lat + ", lon: " + lon);
        Log.d("HACC", "location list: " + locationsList.size());

        if (logGPS) {
            Log.d("HACC", "GPS being logged");
            //TODO, consider replacing arraylist with db
            locationsList.add(location);
            mMap.addMarker(new MarkerOptions().position(latLng).title("current position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        } else {
            Log.d("HACC", "GPS NOT being logged");
        }
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d("HACC", "Location update started");
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped");
    }

    /**
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("HACC", "onConnected - isConnected: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    /**
     *
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("HACC", "onStart");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("HACC", "onStop");
        mGoogleApiClient.disconnect();
        Log.d("HACC", "isConnected: " + mGoogleApiClient.isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed");
        }
    }

}
