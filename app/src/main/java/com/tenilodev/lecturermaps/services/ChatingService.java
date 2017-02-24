package com.tenilodev.lecturermaps.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tenilodev.lecturermaps.ChatingActivity;
import com.tenilodev.lecturermaps.Config;
import com.tenilodev.lecturermaps.Pref;
import com.tenilodev.lecturermaps.R;
import com.tenilodev.lecturermaps.model.ChatMessage;


public class ChatingService extends Service {
    private static final String TAG = "Chatingservice";
    private DatabaseReference mDatabase;
    private int req_id = 1;


    public ChatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mDatabase = FirebaseDatabase.getInstance().getReference();


        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_DOSEN)
            mDatabase.child(Pref.getInstance(this).getDataDosen().getNIDN()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG, dataSnapshot.getKey());
                    showNotification("Ada pesan masuk", "Ada pesan masuk uti");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
//        mDatabase.child(Pref.getInstance(this).getDataDosen().getNIDN()).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.e(TAG, dataSnapshot.getKey());
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                showNotification("Ada pesan masuk", "Ada pesan masuk uti");
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        return START_STICKY;
    }

    public void showNotification(String title, String msg){
        Intent intent = new Intent(this, ChatingActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,req_id,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentText(title);
        builder.setContentText(msg);
        builder.setSmallIcon(R.drawable.lecture_apps);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(req_id,notification);
        req_id++;
    }
}
