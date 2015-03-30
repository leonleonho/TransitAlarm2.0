package com.example.leon.transitalarm20;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;



/**
 * Created by Leon on 15/02/2015.
 */
public class GeoFence {
    GeofencingRequest geofencingRequest;
    Geofence geofence;
    GoogleApiClient mGoogleApiClient;
    public static final float RADIUS = 250;//Radius of when to fire geofence in meters
    public static final long HOUR = 3600000; //One hour in milliseconds
    public static final int RESPONSE_TIME = 0;
    private Context applicationContext;

    public GeoFence(Context applicationContext, GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.applicationContext = applicationContext;
    }
    public void addGeoFence(LatLng latlng) {
        geofence = new Geofence.Builder()
                .setCircularRegion(latlng.latitude, latlng.longitude, RADIUS)
                .setExpirationDuration(HOUR)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setNotificationResponsiveness(RESPONSE_TIME)
                .setRequestId("reqId1")
                .build();
    }
    public void createRequest() {
        geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
        Intent intent = new Intent(applicationContext, AlarmPage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofencingRequest, pendingIntent);
    }

}
