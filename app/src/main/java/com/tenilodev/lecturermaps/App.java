package com.tenilodev.lecturermaps;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.tenilodev.lecturermaps.services.ChatingService;

/**
 * Created by azisa on 2/24/2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //startService(new Intent(this, ChatingService.class));
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
