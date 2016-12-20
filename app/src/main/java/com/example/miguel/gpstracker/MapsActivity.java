package com.example.miguel.gpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    IntentFilter intentFilter;
    AsyncJSON asyncJSON;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        asyncJSON = new AsyncJSON(this);
        //---intent to filter for SMS messages received---
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        ViewGroup layout = (ViewGroup) findViewById(R.id.map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Getting reference to rg_views of the layout activity_main
        RadioGroup rgViews = (RadioGroup) findViewById(R.id.rg_views);
        // Defining Checked Change Listener for the RadioGroup
        RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Currently checked is rb_map
                if(checkedId==R.id.rb_map){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }

                // Currently checked is rb_satellite
                if(checkedId==R.id.rb_satellite){
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        };

        // Setting Checked ChangeListener
        rgViews.setOnCheckedChangeListener(checkedChangeListener);
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
        //Create GoogleMap
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        //ISEP Coordinates
        double defaultlat = 41.17784570000001;
        double defaultlng = -8.608100599999943;
        LatLng defaultposition = new LatLng(defaultlat, defaultlng);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(defaultposition));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultposition,15));
    }

    BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("JSONObject");
            //Call asyncJSON to get Latitude and Longitude
            asyncJSON.execute(json);
        }
    };

    @Override
    protected void onResume() {
        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }
    @Override
    protected void onPause() {
        //---unregister the receiver---
        unregisterReceiver(intentReceiver);
        super.onPause();
    }

    public void CreateMarker(String result)
    {
        asyncJSON.cancel(true);
        asyncJSON = new AsyncJSON(this);
        //---Split the str in two tokens - Latitude and Longitude---
        String[] tokens = result.split("/");
        double latitude = Double.parseDouble(tokens[0]);
        double longitude = Double.parseDouble(tokens[1]);
        LatLng latLng = new LatLng(latitude, longitude);
        // create marker
        MarkerOptions marker = new MarkerOptions().position(latLng).title("Marker");
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.markericon));
        mMap.addMarker(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }
}