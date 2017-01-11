package com.datum.mfs.utils;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;
import android.content.Context;
import android.os.Bundle;

public class GPSCheck extends CordovaPlugin {

	private static CordovaWebView webView = null;

    private static CordovaInterface cordova = null;
    private static String TAG = "MFSUtils";

    @Override
    public void initialize (CordovaInterface cordova, CordovaWebView webView) {
        //LocalNotification.webView = super.webView;
        //LocalNotification.cordova = cordova;
        /*if(webView != null){
            String webUrl = webView.getUrl();
            Log.d("lNtfy","initializing - "+ webUrl);
        }*/

        try {
            if(this.webView == null){
                Log.d(TAG,"initialize - referencing instance webView");
                this.webView = webView;
            }
        } catch(Exception e) {
            //throw e;
            Log.e(TAG,"initialize - referencing instance webView, thrown exception "+ e);

        }
        try {
            if(this.cordova == null){
                Log.d(TAG,"initialize - referencing instance cordova");
                this.cordova = cordova;
            }
        } catch(Exception e) {
            //throw e;
            Log.e(TAG,"initialize - referencing instance cordova, thrown exception "+ e);

        }
    }

	@Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {

            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        } else if (action.equals("isGPSEnabled")) {

            callbackContext.success(String.valueOf(getGPSEnable()));

            return true;

        } else if (action.equals("isNetworkEnabled")) {

            callbackContext.success(String.valueOf(getNetworkEnable()));
            return true;

        } else if (action.equals("getLocation")) {

            getLocation(data,callbackContext);
            return true;

        } else {
            
            return false;

        }
    }

    private boolean getGPSEnable(){
    	Context context = this.cordova.getActivity().getApplicationContext();
    	LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled = false;
		try {
		    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch(Exception ex) {
			Log.e(TAG,"Unable to get gps status, thrown exception "+ ex);
		}
		return gps_enabled;
    }

    private boolean getNetworkEnable(){
    	Context context = this.cordova.getActivity().getApplicationContext();
    	LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		boolean network_enabled = false;
		try {
		    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch(Exception ex) {
			Log.e(TAG,"Unable to get network status, thrown exception "+ ex);
		}
		return network_enabled;
    }

    private  LocationManager mLocationManager;
    private  LocationListener locationListener;
    
    private void makeUseOfNewLocation(Location location, CallbackContext callbackContext){
    	Log.d(TAG,"makeUseOfNewLocation"+ location);
        mLocationManager.removeUpdates(locationListener);
    	String value = String.format("[%1$.6f,%1$.6f]",location.getLatitude(),location.getLongitude());
    	callbackContext.success(value);
    }


    private void getLocation(JSONArray data, final CallbackContext callbackContext){
    	Context context = this.cordova.getActivity().getApplicationContext();
    	mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    	double lat = 0;
    	double lng = 0;
    	// getting GPS status
        boolean isGPSEnabled = getGPSEnable();

        // getting network status
        boolean isNetworkEnabled = getNetworkEnable();

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        } else {
        	locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) {
			      // Called when a new location is found by the network location provider.
			      makeUseOfNewLocation(location, callbackContext);
			    }

			    public void onStatusChanged(String provider, int status, Bundle extras) {}

			    public void onProviderEnabled(String provider) {}

			    public void onProviderDisabled(String provider) {}
			  };
			if (isNetworkEnabled) {
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			} else if (isGPSEnabled) {
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			}
        }

    }
}