package com.tenilodev.lecturermaps;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.model.Mahasiswa;

/**
 * Created by azisa on 12/12/2016.
 */
public class Pref {
    private static final String PREF_NAME = "lecturerapps";
    private static final String KEY_LATITUDE = "key_latitude";
    private static final String KEY_LONGITUDE = "key_longitude";
    private static final String KEY_DATA_MAHASISWA = "key_data_mahasiswa";
    private static final String KEY_DATA_DOSEN = "key_dosen";
    private static final String KEY_LOGIN = "key_login";
    private static final String KEY_NIM = "key_nim";
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

    public void storeMyPosition(double latitude, double longitude){
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(KEY_LATITUDE,Double.doubleToLongBits(latitude));
        editor.putLong(KEY_LONGITUDE, Double.doubleToLongBits(longitude));
        editor.apply();
    }

    public LatLng getMyLatLng(){
        double lat = Double.longBitsToDouble(pref.getLong(KEY_LATITUDE,0));
        double lon = Double.longBitsToDouble(pref.getLong(KEY_LONGITUDE,0));
        LatLng latLng = new LatLng(lat, lon);
        return latLng;
    }

    public void storeDataMahasiswa(Mahasiswa mahasiswa){

        Gson gson = new Gson();
        String json = gson.toJson(mahasiswa);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_DATA_MAHASISWA, json);
        editor.apply();
    }

    public void storeDataDosen(Dosen dosen){

        Gson gson = new Gson();
        String json = gson.toJson(dosen);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_DATA_DOSEN, json);
        editor.apply();
    }

    public Mahasiswa getDataMahasiswa(){
        Gson gson = new Gson();
        String json = pref.getString(KEY_DATA_MAHASISWA, null);
        Mahasiswa mahasiswa = gson.fromJson(json, Mahasiswa.class);
        return mahasiswa;
    }

    public Dosen getDataDosen(){
        Gson gson = new Gson();
        String json = pref.getString(KEY_DATA_DOSEN, null);
        Dosen dosen = gson.fromJson(json, Dosen.class);
        return dosen;
    }

    public void setLoginIn(boolean loginIn){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_LOGIN, loginIn);
        editor.apply();
    }

    public boolean isLoginIn(){
        boolean login = pref.getBoolean(KEY_LOGIN, false);
        return login;
    }

    public void storeNim(String nim){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_NIM, nim);
        editor.apply();
    }

    public String getNim(){
        String nim = pref.getString(KEY_NIM, null);
        return nim;
    }

    public void clearAllData(){
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public void storeLoginState(int state)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Config.KEY_LOGIN_STATE, state);
        editor.apply();
    }

    public int getLoginState()
    {
        int state = pref.getInt(Config.KEY_LOGIN_STATE, 0);
        return state;
    }



}
