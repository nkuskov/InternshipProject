package com.internship.nkuskov.socialmap;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location sourceLocation;
    Location stopLocation;
    Marker mMarker;
    Geocoder mGeocoder;
    List<Address> mAddressList = null;

    TextView currentLocation;
    TextView currentSpeed;
    TextView currentTime;
    TextView fireBaseLogin;

    double speed;
    Place place;
    Intent intent;
    PathCreator mPathCreator;
    PathStopWatch pathStopWatch = null;
    Stopwatch timer;
    boolean startStopWatchFlag = false;
    boolean stopStopWatchFlag = false;
    boolean updateStopWatchFlag = false;

    Intent fireBaseAuth;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser mUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;


    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int MY_PERMISSION_REQUEST = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeocoder = new Geocoder(this, Locale.getDefault());
        currentLocation = (TextView) findViewById(R.id.current_location);
        currentSpeed = (TextView) findViewById(R.id.current_speed);
        currentTime = (TextView) findViewById(R.id.current_time);
        fireBaseLogin = (TextView) findViewById(R.id.firebase_login);
        mPathCreator = new PathCreator(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    fireBaseLogin.setText(user.getEmail());
                } else {

                }
            }
        };


        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler("/mnt/sdcard/"));
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

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

    public void startStopWatch() {
        double distance = sourceLocation.distanceTo(mLastLocation);
        timer = new Stopwatch();
        if (distance >= 50.0f) {
            this.updateStopWatchFlag = true;
            this.stopStopWatchFlag = true;
            this.startStopWatchFlag = false;
            timer.start();
        }
    }

    public void stopStopWatch() {
        double distance = mLastLocation.distanceTo(stopLocation);
        if (distance <= 50.0f) {
            timer.stop();
            this.updateStopWatchFlag = false;
            this.startStopWatchFlag = false;
        }
    }

    public void updateStopWatch() {
        currentTime.setText(getStopWatchText(timer.getElapsedTime()));
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateUI();

        if (startStopWatchFlag) {
            startStopWatch();
        }

        if (updateStopWatchFlag) {
            updateStopWatch();
        }

        if (stopStopWatchFlag) {
            stopStopWatch();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setMaxWaitTime(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location and Storage Permission Needed")
                        .setMessage("This app needs the Location and Storage permission, please accept them")
                        .setPositiveButton("OK", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                            }
                        })
                        .create()
                        .show();
            } else {

                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    }
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                return;
            }

        }
    }

    public void authFireBase(View view) {
        fireBaseAuth = new Intent(this, FireBaseLoginActivity.class);
        startActivity(fireBaseAuth);
    }

    public void fireDatabase(View view) {
        myRef = mFirebaseDatabase.getReference(mUser.getUid());
        myRef.setValue(new DatabaseUser(new ArrayList<String>(), new DatabaseLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
        myRef.child("Place1").setValue(new DatabasePlaces("", new ArrayList<Integer>()));
    }

    public void chooseLocationButton(View view) {
        if (!isOnline()) {
            new AlertDialog.Builder(this)
                    .setTitle("Internet connection needed")
                    .setMessage("For using Draw Path function you need internet connection.")
                    .setPositiveButton("OK", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case Dialog.BUTTON_POSITIVE:
                                    break;
                            }
                        }
                    })
                    .create()
                    .show();
        } else {
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MapsActivity.this);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        }
    }

    public void updateUI() {
        if (mMarker != null) {
            mMarker.remove();
        }

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        if (isOnline()) {
            try {
                mAddressList = mGeocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (mAddressList != null) {
                    currentLocation.setText("Location: " + mAddressList.get(0).getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mUser != null) {
                myRef = mFirebaseDatabase.getReference(mUser.getUid());
                myRef.child("currentLocation").setValue(new DatabaseLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMarker = mMap.addMarker(markerOptions);
        speed = mLastLocation.getSpeed() * 18 / 5;
        currentSpeed.setText("Speed: " + new DecimalFormat("#.##").format(speed) + " km/h");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pathStopWatch != null) {
            pathStopWatch.cancel(true);
        }
        Toast.makeText(getApplicationContext(), "Activity Result", Toast.LENGTH_SHORT).show();
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                sourceLocation = mLastLocation;
                stopLocation = new Location("");
                stopLocation.setLongitude(place.getLatLng().longitude);
                stopLocation.setLatitude(place.getLatLng().latitude);

                mPathCreator.makeURL(mLastLocation.getLatitude(), mLastLocation.getLongitude(), place.getLatLng().latitude, place.getLatLng().longitude);
                this.startStopWatchFlag = true;
//                pathStopWatch = new PathStopWatch(mLastLocation, place, this);
//                pathStopWatch.execute();
            } else if (requestCode == RESULT_CANCELED) {
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private class Stopwatch {
        private long startTime = 0;
        private long stopTime = 0;
        private boolean running = false;

        public void start() {
            this.startTime = System.currentTimeMillis();
            this.running = true;
        }

        public void stop() {
            this.stopTime = System.currentTimeMillis();
            this.running = false;
        }

        public long getElapsedTime() {
            if (running) {
                return System.currentTimeMillis() - startTime;
            }
            return stopTime - startTime;
        }


    }

    public static String getStopWatchText(long timeInMs) {
        long hours = timeInMs / (1000 * 60 * 60);
        long minutes = (timeInMs / (1000 * 60)) % 60;
        long seconds = (timeInMs / (1000)) % 60;

        String minutesSeconds;
        if (seconds < 10) {
            minutesSeconds = "" + minutes + ":0" + seconds;
        } else {
            minutesSeconds = "" + minutes + ":" + seconds;
        }

        if (hours != 0) {
            return "" + hours + ":" + minutesSeconds;
        } else {
            return minutesSeconds;
        }
    }

}

