package com.example.leon.transitalarm20;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MapsActivity extends FragmentActivity implements

        FragmentBusSchedule.OnFragmentInteractionListener, AsyncTaskCompletedListener<JSONArray> {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private MarkerOptions marker;
    private EditText addressText;
    private Geocoder geocode;
    private Map map;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        addressText = (EditText) findViewById(R.id.address);
        addTextListener(addressText);
        marker = new MarkerOptions().draggable(true);
        map = new Map(this, getSupportFragmentManager(), marker);
        map.setUpMapIfNeeded();
        mMap = map.getMap();
        mGoogleApiClient = map.getApiClient();
        addMapListeners();
        geocode  = new Geocoder(this);
        fragmentManager = getFragmentManager();
    }
    @Override
    protected void onResume() {
        super.onResume();
        map.setUpMapIfNeeded();
        mMap = map.getMap();
        addMapListeners();
    }
    private void addMapListeners() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                marker.position(latLng);
                mMap.addMarker(marker);
                showStops(latLng);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int stopNo = Integer.parseInt(marker.getTitle());
                getStopInfo(stopNo);
                return false;
            }
        });
    }
    protected void showStops(LatLng latLng) {
        String url = "http://api.translink.ca/rttiapi/v1/stops?apikey=uqHksMgJHyOOpCRjXNKM&lat=" + (float)latLng.latitude + "&long=" + (float)latLng.longitude;
        Log.w("URL" , url);
        new Translink(this, Translink.TaskType.STOP_ARRAY).execute(url);
    }

    private void getStopInfo(int stopNo) {
        String url = "http://api.translink.ca/rttiapi/v1/stops/"+stopNo+"/estimates?apikey=uqHksMgJHyOOpCRjXNKM";
        Log.w("URL", url);
        new Translink(this, Translink.TaskType.STOP_DETAILS).execute(url);
    }
    @Override
    public void onTaskComplete(JSONArray result, Translink.TaskType type) {
        try {
            switch (type) {
                case STOP_ARRAY:
                    for(int i = 0; i < result.length(); i++) {
                        JSONObject temp = result.getJSONObject(i);
                        LatLng latLng = new LatLng(temp.getDouble("Latitude"), temp.getDouble("Longitude"));
                        MarkerOptions options = new MarkerOptions();
                        options.position(latLng);
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker));
                        options.title(temp.getString("StopNo"));
                        mMap.addMarker(options);
                    }
                    break;
                case STOP_DETAILS:
                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    FragmentBusSchedule fragmentBusSchedule = new FragmentBusSchedule();
                    fragmentBusSchedule.addJSONArray(result);
                    fragmentTransaction.add(R.id.frameLayoutBusSchedule, fragmentBusSchedule, "BusSchedule");
                    fragmentTransaction.commit();
                    break;
                case BUS_TIMES:

                    break;
                default:
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void showSchedule(View view) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        FragmentBusSchedule busSchedule = new FragmentBusSchedule();
        fragmentTransaction.add(R.id.frameLayoutBusSchedule, busSchedule, "BusSchedule");
        fragmentTransaction.commit();
    }
    private void addTextListener(final EditText text) {
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
                    LatLng enteredLocation = getLocation(text.getText().toString());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(enteredLocation, 15));
                    mMap.clear();
                    marker.position(enteredLocation);
                    mMap.addMarker(marker);
                    return true;
                }
                return false;
            }
        });
    }
    private LatLng getLocation(String address) {
        List<Address> temp;
        try {
            temp = geocode.getFromLocationName(address, 1);
            if(temp == null)
                return null;
            Address location = temp.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());
        }catch(Exception e){}
        return null;
    }
    @Override
    public void onFragmentInteraction(String id) {}
    @Override
    public void onBackPressed() {
        Fragment fragment = fragmentManager.findFragmentByTag("BusSchedule");
        if (fragment != null) {

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        } else {
            super.onBackPressed();
        }
    }
    public void setGeofence(View view) {
        GeoFence geoFence = new GeoFence(getApplicationContext(), mGoogleApiClient);
        geoFence.addGeoFence(marker.getPosition());
        geoFence.createRequest();
        Toast.makeText(this, "Your alarm has been set.", Toast.LENGTH_SHORT).show();
    }
}
