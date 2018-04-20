package com.mddn352.rhysangelojones.clientprototype;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class LocationTracker implements LocationListener {

    //The name of the location provider.
    String locationProvider;

    //The database references to be updated with the devices' current location.
    DatabaseReference latRef;
    DatabaseReference lonRef;

    @SuppressLint("MissingPermission")
    public LocationTracker(GoogleMap map, Activity activity, DatabaseReference latRef, DatabaseReference lonRef){


        this.latRef = latRef;
        this.lonRef = lonRef;


        map.setMyLocationEnabled(true);


        //Get the location services and find the best location provider avaliable to the device.
        LocationManager locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
        locationProvider = getBestProvider(locationManager);
        locationManager.requestLocationUpdates(locationProvider, 0, 0,
                this);


        //Centre the map's camera on the user's current location.
        Location currentLocation = locationManager.getLastKnownLocation(locationProvider);
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));


    }

    /**
     * Retruns the most accurate location provider avaliable by the device.
     * @param manager
     * @return
     */
    @SuppressLint("MissingPermission")
    private String getBestProvider(LocationManager manager){

        String result = null;

        List<String> providers = manager.getProviders(true);
        for(String provider : providers){
            if(result == null) result = provider;
            else{

                float currentProviderAccuracy = manager.getLastKnownLocation(result).getAccuracy();
                float otherLocationAccuracy = manager.getLastKnownLocation(provider).getAccuracy();

                if(otherLocationAccuracy < currentProviderAccuracy) result = provider;

            }
        }

        return result;

    }

    @Override
    public void onLocationChanged(Location location) {

        this.latRef.setValue(location.getLatitude());
        this.lonRef.setValue(location.getLongitude());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
