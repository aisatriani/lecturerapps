package com.tenilodev.lecturermaps;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;


import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tenilodev.lecturermaps.api.ApiGenerator;
import com.tenilodev.lecturermaps.api.ClientServices;
import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.model.LokasiDosen;
import com.tenilodev.lecturermaps.model.Mahasiswa;
import com.tenilodev.lecturermaps.services.CheckLocationService;
import com.tenilodev.lecturermaps.services.UpdateLocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 200;
    private GoogleMap mMap;
    private NavigationView navigationView;
    private Mahasiswa currentMahasiswa;
    private HashMap<Dosen, Marker> markerHashMap = new HashMap<>();
    private int state_login;
    private Dosen currentDosen;
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver receiver;
    private HashMap<Marker, String> markerLokasiDosen = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        if (!Pref.getInstance(this).isLoginIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        checkStateLogin();

        currentMahasiswa = Pref.getInstance(this).getDataMahasiswa();
        currentDosen = Pref.getInstance(this).getDataDosen();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setContentHeader();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        receiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                removeActiveMarker();
                System.out.println("receive lokasi dosen");
                ArrayList<LokasiDosen> lokasiDosens = intent.getParcelableArrayListExtra(CheckLocationService.ACTION_LOCATION_DATA);
                for (LokasiDosen ldosen : lokasiDosens) {
                      Marker dosenMarker = mMap.addMarker(new MarkerOptions()
                                .title(ldosen.getNama())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dosen))
                                .position(new LatLng(ldosen.getLatitude(), ldosen.getLongitude()))
                        );

                    markerLokasiDosen.put(dosenMarker, ldosen.getNidn());
                }
            }
        };
    }

    private void removeActiveMarker() {

        if(markerLokasiDosen.size() > 0)
            for(Marker marker : markerLokasiDosen.keySet()){
                marker.remove();
                System.out.println("remove active marker");
            }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(CheckLocationService.ACTION_LOCATION_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStateLogin();
    }

    private void checkStateLogin() {

        boolean isAutoUpdateLocation = sharedPreferences.getBoolean("location_switch", true);

        int state = Pref.getInstance(this).getLoginState();

        if(state == Config.LOGIN_STATE_DOSEN) {
            if (isAutoUpdateLocation) {
                startService(new Intent(this, UpdateLocationService.class));
            } else {
                stopService(new Intent(this, UpdateLocationService.class));
                doDisableUpdateLocation();
            }
        }

        if(state == Config.LOGIN_STATE_MAHASISWA){
            startService(new Intent(this, CheckLocationService.class));
        }
    }

    private void doDisableUpdateLocation() {

        ClientServices services = ApiGenerator.createService(ClientServices.class);
        Call<LokasiDosen> call = services.updateLokasiDosen(Pref.getInstance(this).getDataDosen().getNIDN(), Pref.getInstance(this).getDataDosen().getNAMA(),
                Pref.getInstance(this).getMyLatLng().latitude,Pref.getInstance(this).getMyLatLng().longitude , 0
        );

        call.enqueue(new Callback<LokasiDosen>() {
            @Override
            public void onResponse(Call<LokasiDosen> call, Response<LokasiDosen> response) {
                if(response.isSuccessful()){
                    System.out.println("update disable user dosen");

                }
            }

            @Override
            public void onFailure(Call<LokasiDosen> call, Throwable t) {
                System.out.println("gagal update disable user dosen");
            }
        });

        removeActiveMarker();
    }

    private void doDisableUpdateLocationLogout() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Waiting for logout");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        ClientServices services = ApiGenerator.createService(ClientServices.class);
        Call<LokasiDosen> call = services.updateLokasiDosen(Pref.getInstance(this).getDataDosen().getNIDN(), Pref.getInstance(this).getDataDosen().getNAMA(),
                Pref.getInstance(this).getMyLatLng().latitude,Pref.getInstance(this).getMyLatLng().longitude , 0
        );

        call.enqueue(new Callback<LokasiDosen>() {
            @Override
            public void onResponse(Call<LokasiDosen> call, Response<LokasiDosen> response) {
                pd.dismiss();
                if(response.isSuccessful()){
                    stopService(new Intent(MainActivity.this, UpdateLocationService.class));
                    System.out.println("update disable user dosen");
                    //Pref.getInstance(MainActivity.this).clearAllData();
                    Pref.getInstance(MainActivity.this).setLoginIn(false);
                    sharedPreferences.edit().putBoolean("location_switch", false).apply();
                    finish();
                    startActivity(getIntent());

                }
            }

            @Override
            public void onFailure(Call<LokasiDosen> call, Throwable t) {
                pd.dismiss();
                System.out.println("gagal update disable user dosen");
            }
        });

        removeActiveMarker();
    }

    private void setContentHeader() {
       TextView textHeader = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_name);
       TextView textNim = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_nim);

        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_MAHASISWA)
            if(currentMahasiswa != null) {
                textHeader.setText(currentMahasiswa.getNAMA());
                textNim.setText(Pref.getInstance(this).getNim());
            }

        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_DOSEN)
            if(currentDosen != null){
                textHeader.setText(currentDosen.getNAMA());
                textNim.setText(Pref.getInstance(this).getNim());
            }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dosen_prodi) {
            // Handle the camera action
            Intent intent = new Intent(this, ProdiActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_dosen_domisili) {
            Intent intent = new Intent(this, DomisiliActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_pengaturan) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            doActionLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void doActionLogout() {
        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_MAHASISWA){
            Pref.getInstance(MainActivity.this).setLoginIn(false);
            sharedPreferences.edit().putBoolean("location_switch", false).apply();
            stopService(new Intent(this, CheckLocationService.class));
            finish();
            startActivity(getIntent());
        }
        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_DOSEN) {
            doDisableUpdateLocationLogout();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initMaps();

        // Add a marker in Sydney and move the camera
        LatLng gorontalo = new LatLng(0.553042, 123.063151);
        //mMap.addMarker(new MarkerOptions().position(gorontalo).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gorontalo, 14));

        loadAllMarkerDosen();
    }

    private void initMaps() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(markerLokasiDosen.containsKey(marker)){
                    return false;
                }

                if(markerHashMap.size() > 0){
                    for(Map.Entry<Dosen, Marker> entry : markerHashMap.entrySet()){
                        if(entry.getValue().equals(marker)){
                            //Toast.makeText(MainActivity.this, entry.getValue().getTitle(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, DosenMapsActivity.class);
                            intent.putExtra("dosen",entry.getKey());
                            startActivity(intent);
                        }
                    }
                }

                return true;
            }
        });


    }

    private void loadAllMarkerDosen() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memuat data");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        ClientServices services = ApiGenerator.createService(ClientServices.class);
        Call<List<Dosen>> call = services.getAllDosen();
        call.enqueue(new Callback<List<Dosen>>() {
            @Override
            public void onResponse(Call<List<Dosen>> call, Response<List<Dosen>> response) {
                if (response.isSuccessful()) {
                    //for (Dosen d : response.body()) {
                    for(int i = 0; i < response.body().size(); i++){

                        LatLng ll = new LatLng(response.body().get(i).getLATITUDE(), response.body().get(i).getLONGITUDE());

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(ll)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                                .title(response.body().get(i).getNAMA()));

                        markerHashMap.put(response.body().get(i),marker);
                    }

                } else {
                    Snackbar.make(findViewById(R.id.content_main), getString(R.string.error_respon), Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ulangi", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadAllMarkerDosen();
                                }
                            })
                            .show();
                }

                pd.dismiss();
            }

            @Override
            public void onFailure(Call<List<Dosen>> call, Throwable t) {
                pd.dismiss();
                t.printStackTrace();
                Snackbar.make(findViewById(R.id.content_main), getString(R.string.error_connect), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ulangi", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadAllMarkerDosen();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }
}
