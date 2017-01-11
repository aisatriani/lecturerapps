package com.tenilodev.lecturermaps.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.tenilodev.lecturermaps.Pref;
import com.tenilodev.lecturermaps.api.ApiGenerator;
import com.tenilodev.lecturermaps.api.ClientServices;
import com.tenilodev.lecturermaps.model.LokasiDosen;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateLocationService extends Service {

    private static final int INTERVAL = 30;
    private static final String TAG = UpdateLocationService.class.getSimpleName();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private SharedPreferences sharedPreferences;

    public UpdateLocationService() {
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //doUpdateLocation();



        return START_NOT_STICKY;
    }

    private void doUpdateLocation(Location location) {

        ClientServices services = ApiGenerator.createService(ClientServices.class);
        Call<LokasiDosen> call = services.updateLokasiDosen(Pref.getInstance(this).getDataDosen().getNIDN(),
                location.getLatitude(), location.getLongitude(), getStatusActive()
        );

        call.enqueue(new Callback<LokasiDosen>() {
            @Override
            public void onResponse(Call<LokasiDosen> call, Response<LokasiDosen> response) {
                if(response.isSuccessful()){
                    System.out.println("berhasil memperbaharui lokasi");
                    stopSelf();
                }
            }

            @Override
            public void onFailure(Call<LokasiDosen> call, Throwable t) {
                System.out.println("gagal memperbaharui lokasi");
                stopSelf();
            }
        });
    }

    private int getStatusActive() {
        boolean isAutoUpdateLocation = sharedPreferences.getBoolean("location_switch", true);
        if(isAutoUpdateLocation)
            return 1;
        else
            return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }

        boolean isAutoUpdateLocation = sharedPreferences.getBoolean("location_switch", true);

        if(isAutoUpdateLocation)
            retryService();
    }

    private void retryService() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, INTERVAL);

        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                //System.currentTimeMillis() + (1000 * 60 * 60),
                calendar.getTimeInMillis(),
                PendingIntent.getService(this, 0, new Intent(this, UpdateLocationService.class), 0)
        );
    }

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            doUpdateLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
