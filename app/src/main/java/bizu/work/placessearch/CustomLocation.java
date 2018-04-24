package bizu.work.placessearch;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class CustomLocation {

    private SearchFragment sfInstance;
    LocationManager locationManager;
    public CustomLocation(SearchFragment sf) {

        this.sfInstance = sf;
        this.locationManager = (LocationManager)sf.getActivity().getSystemService(Context.LOCATION_SERVICE);
        setLocationListener();
    }
//

    public void setLocationListener() {

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) sfInstance.getActivity().
                        getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("setLocationListener------------------setLocationListener-------------: ", location.getLatitude() + "");
                sfInstance.setCurLocation(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }
}
