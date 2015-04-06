package com.example.leon.transitalarm20;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Leon on 15/02/2015.
 */
public class Map implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context applicationContext;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private boolean mRequestingLocationUpdates;
    private LatLng currentPos;
    private FragmentManager fragmentManager;
    private LocationRequest mLocationRequest;
    private MapsActivity map;
    private MarkerOptions marker;
    public Map(MapsActivity map, FragmentManager fragmentManager, MarkerOptions marker) {
        this.applicationContext = map;
        this.map = map;
        this.fragmentManager = fragmentManager;
        this.marker = marker;
        mGoogleApiClient = new GoogleApiClient.Builder(this.applicationContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }
    /**
     * We are connected so we want to get our last known location and set the map at that position
     * Then we wnt to check if we are getting location updates; if we aren't, we want to start getting
     * them
     * @param bundle not used
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            currentPos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15));
        }
        if (!mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * If the connection is suspended stop the location servicesc
     * @param i not used
     */
    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    /**
     * If the connection failed stop the location services
     * @param connectionResult not used
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                mMap.setMyLocationEnabled(true);
            }
        }
    }
    /**
     * Creating a new location request, modify these values to define how much location data
     * we want
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    /**
     * Create a new location request and begin getting updates
     */
    protected void startLocationUpdates() {
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        mRequestingLocationUpdates = true;

    }
    @Override
    public void onLocationChanged(Location location) {
        currentPos = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        marker.position(currentPos);
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15));
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        map.showStops(new LatLng(location.getLatitude(), location.getLongitude()));
    }
    public GoogleMap getMap() {
        return mMap;
    }
    public GoogleApiClient getApiClient() {return mGoogleApiClient;}
}
