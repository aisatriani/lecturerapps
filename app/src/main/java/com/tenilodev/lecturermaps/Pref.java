package com.tenilodev.lecturermaps;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by azisa on 12/12/2016.
 */
public class Pref {
    private static final String PREF_NAME = "lecturerapps";
    private static Pref ourInstance;
    private final Context context;
    private final SharedPreferences pref;

    public static Pref getInstance(Context ctx) {
        if(ourInstance == null){
            ourInstance = new Pref(ctx);
        }
        return ourInstance;
    }

    private Pref(Context ctx) {
        this.context = ctx;
        this.pref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private void storePosition(){

    }

}
