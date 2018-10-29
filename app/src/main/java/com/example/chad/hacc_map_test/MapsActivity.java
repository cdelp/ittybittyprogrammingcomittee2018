package com.example.chad.hacc_map_test;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location location;
    private TextView mTextMessage;
    private SensorManager sensorManager;
    private int toastDuration = Toast.LENGTH_SHORT;

    private boolean logGPS = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.Stop:
                    //mTextMessage.setText(R.string.title_home);
                    return true;

                case R.id.Start:

                    mTextMessage.setText("Tracking");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        }
        // move where needed
        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        //onLocationChanged(location);

        //TODO testing only, use sensors if needed
        // Get list of sensors
        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> msensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d("HACC", msensorList.toString());

        Button startButton = findViewById(R.id.Start);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // trigger logging of gps and sensors
                logGPS = true;
                onLocationChanged(location);

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

        mTextMessage = (TextView) findViewById(R.id.message);
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

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        LatLng latLng = new LatLng( lat, lon );
        Log.d("HACC", "lat: " + lat + ", lon: " + lon);

        if (logGPS) {
            Log.d("HACC", "GPS being logged");
            mMap.addMarker(new MarkerOptions().position(latLng).title("current position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        } else {
            Log.d("HACC", "GPS NOT being logged");
        }
    }
}
