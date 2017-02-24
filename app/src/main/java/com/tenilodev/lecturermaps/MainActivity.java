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
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;


import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
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
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tenilodev.lecturermaps.api.ApiGenerator;
import com.tenilodev.lecturermaps.api.ApiResponse;
import com.tenilodev.lecturermaps.api.ApiSiatGenerator;
import com.tenilodev.lecturermaps.api.ClientServices;
import com.tenilodev.lecturermaps.api.SiatMethod;
import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.model.DosenResponse;
import com.tenilodev.lecturermaps.model.LokasiDosen;
import com.tenilodev.lecturermaps.model.Mahasiswa;
import com.tenilodev.lecturermaps.services.CheckLocationService;
import com.tenilodev.lecturermaps.services.UpdateLocationService;

import java.io.IOException;
import java.io.Serializable;
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
    private HashMap<LokasiDosen, Marker> markerLokasiDosen = new HashMap<>();
    private SearchView searchView;
    private List<String> mListDosen = new ArrayList<>();

    private String[] sAutocompleteColNames = new String[] {
            BaseColumns._ID,                         // necessary for adapter
            SearchManager.SUGGEST_COLUMN_TEXT_1};      // the full search term
    private HashMap<String, Marker> markerNama = new HashMap<>();
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        handler = new Handler();


        if (!Pref.getInstance(this).isLoginIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        checkStateLogin();

        FirebaseMessaging.getInstance().subscribeToTopic("news");

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

                    markerLokasiDosen.put(ldosen, dosenMarker);
                }
            }
        };
    }

    private void removeActiveMarker() {

        if(markerLokasiDosen.size() > 0)
        for(Map.Entry<LokasiDosen, Marker> entry : markerLokasiDosen.entrySet()){
            entry.getValue().remove();
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
            FirebaseMessaging.getInstance().subscribeToTopic(Pref.getInstance(this).getDataDosen().getNIDN());
        }

        if(state == Config.LOGIN_STATE_MAHASISWA){
            startService(new Intent(this, CheckLocationService.class));
            FirebaseMessaging.getInstance().subscribeToTopic(Pref.getInstance(this).getNim());
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

        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

       final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
        android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1});

        searchView.setSuggestionsAdapter(adapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!TextUtils.isEmpty(newText)){
                    String upperText = newText.toUpperCase();
                    List<String> list = Lists.newArrayList(Collections2.filter(
                            mListDosen, Predicates.containsPattern(upperText)));
                    populateCursorAdapter(list);
                    return true;
                }
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String term = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                cursor.close();

                for(Map.Entry<String, Marker> entry : markerNama.entrySet()){
                    if(entry.getKey().contains(term)){
                        entry.getValue().showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(entry.getValue().getPosition(),16));
                    }
                }
                //Toast.makeText(MainActivity.this, term, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                return onSuggestionSelect(position);
            }
        });

        return true;
    }

    private void populateCursorAdapter(List<String> list) {
        MatrixCursor cursor = new MatrixCursor(sAutocompleteColNames);
        for (int i = 0; i < list.size(); i++){
            Object[] row = new Object[]{Integer.toString(i),list.get(i)};
            cursor.addRow(row);
        }

        searchView.getSuggestionsAdapter().changeCursor(cursor);

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
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Pref.getInstance(this).getNim());
            finish();
            startActivity(getIntent());
        }
        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_DOSEN) {
            doDisableUpdateLocationLogout();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Pref.getInstance(this).getDataDosen().getNIDN());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //testMarkerLokasiDosen();

        initMaps();

        // Add a marker in Sydney and move the camera
        LatLng gorontalo = new LatLng(0.553042, 123.063151);
        //mMap.addMarker(new MarkerOptions().position(gorontalo).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gorontalo, 14));

        //loadAllMarkerDosen();

           // loadAllDosenSiat();
            //loadAllDosenSiat2();
    }

    private void testMarkerLokasiDosen() {
        LokasiDosen lokasiDosen = new LokasiDosen();
        lokasiDosen.setId(123);
        lokasiDosen.setLatitude(0.549008);
        lokasiDosen.setLongitude(123.084351);
        lokasiDosen.setNidn("0008127805");
        lokasiDosen.setNama("Tajudin Abdillah");

        Marker markerss = mMap.addMarker(new MarkerOptions()
                 .position(new LatLng(lokasiDosen.getLatitude(),lokasiDosen.getLongitude()))
                .title(lokasiDosen.getNama())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dosen))
        );

        markerLokasiDosen.put(lokasiDosen, markerss);

    }

    private void loadAllDosenSiat2() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memuat data");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    SiatMethod service = ApiSiatGenerator.createService(SiatMethod.class);
                    Call<ApiResponse<List<DosenResponse>>> call = service.getAllDosen();
                    Response<ApiResponse<List<DosenResponse>>> execute = call.execute();
                    ApiResponse<List<DosenResponse>> body = execute.body();
                    for(DosenResponse dosenResponse : body.getData()){
                        final List<Dosen> data = dosenResponse.getDOSEN();
                        for (int i = 0; i < data.size(); i++) {

                            ClientServices serviceLokasi = ApiGenerator.createService(ClientServices.class);
                            Call<LokasiDosen> lokasiDosenByNIDN = serviceLokasi.getLokasiDosenByNIDN(data.get(i).getNIDN());
                            Response<LokasiDosen> lokasiDosenResponse = lokasiDosenByNIDN.execute();
                            LokasiDosen lokasiDosen = lokasiDosenResponse.body();

                            data.get(i).setLATITUDE(lokasiDosen.getLatitude());
                            data.get(i).setLONGITUDE(lokasiDosen.getLongitude());

                            mListDosen.add(data.get(i).getNAMA());

                            final LatLng ll = new LatLng(data.get(i).getLATITUDE(), data.get(i).getLONGITUDE());

                            final int finalI = i;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(ll)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                                            .title(data.get(finalI).getNAMA()));

                                    markerHashMap.put(data.get(finalI), marker);
                                    markerNama.put(data.get(finalI).getNAMA(), marker);
                                }
                            });

                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    private void loadAllDosenSiat() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memuat data");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    SiatMethod service = ApiSiatGenerator.createService(SiatMethod.class);
                    Call<ApiResponse<List<DosenResponse>>> call = service.getAllDosen();
                    Response<ApiResponse<List<DosenResponse>>> execute = call.execute();
                    ApiResponse<List<DosenResponse>> body = execute.body();
                    for(DosenResponse dosenResponse : body.getData()){
                        final List<Dosen> data = dosenResponse.getDOSEN();
                        for (int i = 0; i < data.size(); i++) {

                            ClientServices serviceLokasi = ApiGenerator.createService(ClientServices.class);
                            Call<LokasiDosen> lokasiDosenByNIDN = serviceLokasi.getLokasiDosenByNIDN(data.get(i).getNIDN());
                            Response<LokasiDosen> lokasiDosenResponse = lokasiDosenByNIDN.execute();
                            LokasiDosen lokasiDosen = lokasiDosenResponse.body();

                            data.get(i).setLATITUDE(lokasiDosen.getLatitude());
                            data.get(i).setLONGITUDE(lokasiDosen.getLongitude());

                            mListDosen.add(data.get(i).getNAMA());

                            final LatLng ll = new LatLng(data.get(i).getLATITUDE(), data.get(i).getLONGITUDE());

                            final int finalI = i;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(ll)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                                            .title(data.get(finalI).getNAMA()));

                                    markerHashMap.put(data.get(finalI), marker);
                                    markerNama.put(data.get(finalI).getNAMA(), marker);
                                }
                            });

                        }
                    }


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        thread.start();

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

                    MatrixCursor cursor = new MatrixCursor(sAutocompleteColNames);

                    for(int i = 0; i < response.body().size(); i++){

                        mListDosen.add(response.body().get(i).getNAMA());

                        LatLng ll = new LatLng(response.body().get(i).getLATITUDE(), response.body().get(i).getLONGITUDE());

                        Object[] row = new Object[] {response.body().get(i).getNIDN(), response.body().get(i).getNAMA()};
                        cursor.addRow(row);

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(ll)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                                .title(response.body().get(i).getNAMA()));

                        markerHashMap.put(response.body().get(i),marker);
                        markerNama.put(response.body().get(i).getNAMA(), marker);
                    }

                    //searchView.getSuggestionsAdapter().changeCursor(cursor);

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

                if(markerLokasiDosen.size() > 0)
                //if(markerLokasiDosen.containsKey(marker)){
                    for(Map.Entry<LokasiDosen, Marker> entry : markerLokasiDosen.entrySet()){
                        if(entry.getValue().equals(marker)){
                            //Toast.makeText(MainActivity.this, entry.getValue().getTitle(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ChatingActivity.class);
                            intent.putExtra("lokasidosen", (Serializable) entry.getKey());
                            startActivity(intent);
                            System.out.println("marker lokasi dosen click");
                        }
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
