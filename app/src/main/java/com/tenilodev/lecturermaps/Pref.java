package com.tenilodev.lecturermaps;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by azisa on 12/12/2016.
 */
public class Pref {
    private static final String PREF_NAME = "lecturerapps";
    private static final String KEY_LATITUDE = "key_latitude";
    private static final String KEY_LONGITUDE = "key_longitude";
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

    private void storeMyPosition(double latitude, double longitude){
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(KEY_LATITUDE,Double.doubleToLongBits(latitude));
        editor.putLong(KEY_LONGITUDE, Double.doubleToLongBits(longitude));
        editor.apply();
    }

    private LatLng getMyLatLng(){
        double lat = Double.longBitsToDouble(pref.getLong(KEY_LATITUDE,0));
        double lon = Double.longBitsToDouble(pref.getLong(KEY_LONGITUDE,0));
        LatLng latLng = new LatLng(lat, lon);
        return latLng;
    }

}
