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

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.IllegalArgumentException;
import java.lang.Number;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


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

        } else if (action.equals("uploadDatabase")) {

            uploadDB(data, callbackContext);
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

			    public void onStatusChanged(String provider, int status, Bundle extras) {
			    	Log.d(TAG,String.format("locationListener=>onStatusChanged: %1 %2$d", provider, status));
			    }

			    public void onProviderEnabled(String provider) {
			    	Log.d(TAG,String.format("locationListener=>onProviderEnabled: %1", provider));
			    }

			    public void onProviderDisabled(String provider) {
			    	Log.d(TAG,String.format("locationListener=>onProviderDisabled: %1", provider));
			    }
			  };
			if (isNetworkEnabled) {
				Log.d(TAG,"requestLocationUpdates: LocationManager.NETWORK_PROVIDER");
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			} else if (isGPSEnabled) {
				Log.d(TAG,"requestLocationUpdates: LocationManager.GPS_PROVIDER");
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			}
        }

    }

    /**
     * Upload db
     *
     * @param args   args
     *        cbc callbackcontext
     */
    private void uploadDB(JSONArray args, CallbackContext cbc) throws JSONException, IOException {
        JSONObject j = (JSONObject) args.get(0);
        int servercode = j.getInt("servercode");
        int techid = j.getInt("techid");
        int dt = j.getInt("dt");
        File dir = this.cordova.getActivity().getExternalCacheDir();
        String storage  = dir.toString() + "/MFS";
        new File(storage).mkdir();
        String zipname = storage + "/db.zip";
        String dbname = "MFS1Job.db";
        String dblogname = "MFS1Logs.db";

        File dbfile = this.cordova.getActivity().getDatabasePath(dbname);
        File dblogfile = this.cordova.getActivity().getDatabasePath(dblogname);
        /*File dbfileTarget = new File(storage, dbname);
        File dblogfileTarget = new File(storage, dblogname);

        if (dbfile.exists()) {
            FileChannel src = new FileInputStream(dbfile).getChannel();
            FileChannel dst = new FileOutputStream(dbfileTarget).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }*/
        Compress comp = new Compress(new String[]{dbfile.getAbsolutePath(), dblogfile.getAbsolutePath()}, zipname);
        comp.zip();
        String burl = "http://mfshub.datumcorp.com/Hub/upload";
        //String burl = "http://192.168.10.163:2901/Hub/upload";
        File zipfile = new File(zipname);

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        int serverResponseCode = 0;
        try{
            FileInputStream fileInputStream = new FileInputStream(zipfile);
            URL url = new URL(burl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);//Allow Inputs
            connection.setDoOutput(true);//Allow Outputs
            connection.setUseCaches(false);//Don't use a cached Copy
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("uploaded_file","db.zip");
            connection.setRequestProperty("servercode",Integer.toString(servercode));
            connection.setRequestProperty("techid",Integer.toString(techid));
            connection.setRequestProperty("dt",Integer.toString(dt));

            //creating new dataoutputstream
            dataOutputStream = new DataOutputStream(connection.getOutputStream());

            //writing bytes to data outputstream
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                    + "db.zip" + "\"" + lineEnd);

            dataOutputStream.writeBytes(lineEnd);

            //returns no. of bytes present in fileInputStream
            bytesAvailable = fileInputStream.available();
            //selecting the buffer size as minimum of available bytes or 1 MB
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
            //setting the buffer as byte array of size of bufferSize
            buffer = new byte[bufferSize];

            //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
            bytesRead = fileInputStream.read(buffer,0,bufferSize);

            //loop repeats till bytesRead = -1, i.e., no bytes are left to read
            while (bytesRead > 0){
                //write the bytes read from inputstream
                dataOutputStream.write(buffer,0,bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = fileInputStream.read(buffer,0,bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            Log.i("SQLitePlugin=>upload", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

            //response code of 200 indicates the server status OK
            if(serverResponseCode == 200){
                cbc.success(zipname);
            }else{
                cbc.error("Server response is " + serverResponseMessage + " ("+serverResponseCode+")");
            }

            //closing the input and output streams
            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cbc.error("File Not found " + e);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            cbc.error("Malformed URL Exception " + e);

        } catch (IOException e) {
            e.printStackTrace();
            cbc.error("IO Operation Exception " + e);
        }

    }
}