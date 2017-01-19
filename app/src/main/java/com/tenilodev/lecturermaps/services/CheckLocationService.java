package com.tenilodev.lecturermaps.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.tenilodev.lecturermaps.Pref;
import com.tenilodev.lecturermaps.api.ApiGenerator;
import com.tenilodev.lecturermaps.api.ClientServices;
import com.tenilodev.lecturermaps.model.LokasiDosen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckLocationService extends Service {
    public static final int INTERVAL = 30;
    public static final String ACTION_LOCATION_RESULT = "com.tenilodev.lecturermaps.services.LOCATION_RESULT";
    public static final String ACTION_LOCATION_DATA = "com.tenilodev.lecturermaps.services.LOCATION_DATA";

    private LocalBroadcastManager broadcastManager;

    public CheckLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //code here
        doGetActiveLocation();

        return START_NOT_STICKY;
    }

    private void doGetActiveLocation() {
        ClientServices clientServices = ApiGenerator.createService(ClientServices.class);
        Call<ArrayList<LokasiDosen>> call = clientServices.getActiveLokasiDosen();
        call.enqueue(new Callback<ArrayList<LokasiDosen>>() {
            @Override
            public void onResponse(Call<ArrayList<LokasiDosen>> call, Response<ArrayList<LokasiDosen>> response) {
                if(response.isSuccessful()){
                    sendResult(response.body());
                }
                stopSelf();
            }

            @Override
            public void onFailure(Call<ArrayList<LokasiDosen>> call, Throwable t) {
                stopSelf();
            }
        });
    }

    private void sendResult(ArrayList<LokasiDosen> listDosen){
        Intent intent = new Intent(ACTION_LOCATION_RESULT);
        intent.putParcelableArrayListExtra(ACTION_LOCATION_DATA, listDosen);
        //if(listDosen.size() > 0)
            broadcastManager.sendBroadcast(intent);
    }

    private void retryService() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, INTERVAL);

        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                //System.currentTimeMillis() + (1000 * 60 * 60),
                calendar.getTimeInMillis(),
                PendingIntent.getService(this, 0, new Intent(this, CheckLocationService.class), 0)
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Pref.getInstance(this).isLoginIn())
            retryService();

    }
}
