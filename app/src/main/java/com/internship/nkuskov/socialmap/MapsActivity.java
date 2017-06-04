package com.internship.nkuskov.socialmap;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.internship.nkuskov.socialmap.RecyclerView.*;



import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    static Location mLastLocation;
    Location sourceLocation;
    Location stopLocation;
    Marker mMarker;
    Geocoder mGeocoder;
    List<LatLng> userPathLatLng;
    List<Address> mAddressList = null;
    private List<RecyclerListItem> mDestinationItems;
    private static Polyline userPathPolyline;
    private GridLayoutManager mGridLayoutManager;

    static TextView currentLocation;
    static TextView currentSpeed;
    static TextView currentTime;
    static TextView fireBaseLogin;

    double speed;
    Place place;
    Intent intent;
    Intent stopWatchService;
    PathCreator mPathCreator;
    PathStopWatch pathStopWatch = null;
    boolean startStopWatchFlag = false;
    boolean stopStopWatchFlag = false;

    private String headForDatabase;
    Intent fireBaseAuth;
    static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser mUser;
    static FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;


    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int MY_PERMISSION_REQUEST = 99;

    public static Polyline getUserPathPolyline() {
        return userPathPolyline;
    }

    public static void setUserPathPolyline(Polyline userPathPolyline) {
        MapsActivity.userPathPolyline = userPathPolyline;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeocoder = new Geocoder(this, Locale.getDefault());
        currentLocation = (TextView) findViewById(R.id.bottom_sheet_location);
        currentSpeed = (TextView) findViewById(R.id.current_speed);
        currentTime = (TextView) findViewById(R.id.current_time);
        fireBaseLogin = (TextView) findViewById(R.id.firebase_login);

        stopWatchService = new Intent(this, StopWatchService.class);
        mPathCreator = new PathCreator(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getHeadForDatabase();
        if (mUser != null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fireBaseLogin.setText(headForDatabase);
            }
        };

        //Initialize RecyclerView
        initializeDestinationItems();
        mGridLayoutManager = new GridLayoutManager(MapsActivity.this,6);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(mDestinationItems);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.dest_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);


        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler("/mnt/sdcard/"));
    }

    private void getHeadForDatabase() {
        if (mUser != null) {
            if (mUser.getDisplayName() != null) {
                headForDatabase = mUser.getDisplayName();
            } else {
                headForDatabase = mUser.getEmail();
            }
        }
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

        if (userPathLatLng != null) {
            userPathPolyline = mMap.addPolyline(new PolylineOptions()
                    .addAll(userPathLatLng)
                    .width(12)
                    .color(Color.rgb(200, 200, 200))
                    .geodesic(true));
        }

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
        if (distance >= 50.0f) {
            this.stopStopWatchFlag = true;
            this.startStopWatchFlag = false;
            startService(stopWatchService);


        }
    }

    public void stopStopWatch() {
        double distance = mLastLocation.distanceTo(stopLocation);
        if (distance <= 50.0f) {
            this.stopStopWatchFlag = false;
            this.startStopWatchFlag = false;
            stopService(stopWatchService);

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateUI();

        if (startStopWatchFlag) {
            startStopWatch();
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
        myRef = mFirebaseDatabase.getReference().child(mUser.getUid());
        myRef.child("DestList").push().setValue("abc");
    }

    public void destListButton(View view) {
        Intent intent = new Intent(this, DestinationList.class);
        startActivity(intent);
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
                    currentLocation.setText(mAddressList.get(0).getAddressLine(0));
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
        currentSpeed.setText(new DecimalFormat("#.##").format(speed) + " km/h");


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
                stopService(stopWatchService);
                mPathCreator.makeURL(mLastLocation.getLatitude(), mLastLocation.getLongitude(), place.getLatLng().latitude, place.getLatLng().longitude);
                this.startStopWatchFlag = true;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentLocation", currentLocation.getText().toString());
        outState.putString("currentSpeed", currentSpeed.getText().toString());
        if (userPathPolyline != null) {
            outState.putParcelableArrayList("userPathLatLng", (ArrayList<? extends Parcelable>) userPathPolyline.getPoints());
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentLocation.setText(savedInstanceState.getString("currentLocation"));
        currentSpeed.setText(savedInstanceState.getString("currentSpeed"));
        userPathLatLng = savedInstanceState.getParcelableArrayList("userPathLatLng");

    }

    private void initializeDestinationItems(){
        mDestinationItems = new ArrayList<>();
        mDestinationItems.add(new DestinationAddButton());
        mDestinationItems.add(new DestinationItem("Work",R.drawable.dest_icon_img));
        mDestinationItems.add(new DestinationItem("home",R.drawable.dest_icon_img));

    }

}

