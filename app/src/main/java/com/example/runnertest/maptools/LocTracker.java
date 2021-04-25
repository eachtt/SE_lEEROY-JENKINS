package com.example.runnertest.maptools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

public class LocTracker {
    private Context mContext;
    private LocationManager mLocationManager;
    private LocTrackerListener listener;

    public interface LocTrackerListener {
        void pushLoc(Location loc);
    }

    public LocTracker(Context context) {
        mContext = context;
        locListenerInit();
    }

    public void setOnLocTrackerListener(LocTrackerListener listener) { this.listener = listener; }

    private void locListenerInit() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = mLocationManager.getBestProvider(criteria, true);

        mLocationManager.requestLocationUpdates(provider, 1000, 5, locListener);
    }

    private final LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {
            if (listener != null) listener.pushLoc(loc);
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
    };
}
