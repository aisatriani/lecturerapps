package com.tenilodev.lecturermaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tenilodev.lecturermaps.api.ClientServices;
import com.tenilodev.lecturermaps.api.GoogleApiGenerator;
import com.tenilodev.lecturermaps.model.DirectionResults;
import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.utils.RouteDecode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DosenMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Dosen dosen;
    private NestedScrollView scrollv;
    private AppBarLayout appbar;
    private boolean isExpanded;
    private TextView textNamaDosen, textTempatLahir, textTglLahir, textStatusDosen, textStatusKerja, textJabatanAkademik, textEmail;
    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_details);

        mLatLng = Pref.getInstance(DosenMapsActivity.this).getMyLatLng();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appbar = (AppBarLayout)findViewById(R.id.app_bar);

        isExpanded = true;

        initView();
        handleIntent(getIntent());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpanded) {
                    appbar.setExpanded(false);
                    isExpanded = false;
                }else{
                    appbar.setExpanded(true);
                    isExpanded = true;
                }
            }
        });




    }

    private void initView() {
        textNamaDosen = (TextView) findViewById(R.id.nama_dosen);
        textTempatLahir = (TextView) findViewById(R.id.text_tempat_lahir);
        textTglLahir = (TextView) findViewById(R.id.text_tgl_lahir);
        textStatusDosen = (TextView) findViewById(R.id.text_status_dosen);
        textStatusKerja = (TextView) findViewById(R.id.text_status_kerja);
        textJabatanAkademik = (TextView) findViewById(R.id.text_jabatan_akademik);
        textEmail = (TextView) findViewById(R.id.text_email);
    }

    private void handleIntent(Intent intent) {
        dosen = (Dosen) intent.getSerializableExtra("dosen");
        setTitle(dosen.getNAMA());

        textNamaDosen.setText(dosen.getNAMA());
        textTempatLahir.setText(dosen.getTEMPAT_LAHIR());
        textTglLahir.setText(dosen.getTANGGAL_LAHIR());
        textStatusDosen.setText(dosen.getSTATUSDOSEN());
        textStatusKerja.setText(dosen.getSTATUSKERJA());
        textJabatanAkademik.setText(dosen.getJABATANKADEMIK());
        textEmail.setText(dosen.getEMAIL());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_direction){
            actionDirection();
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionDirection() {
        //0.547451, 123.086840
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memuat data");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        ClientServices services = GoogleApiGenerator.createService(ClientServices.class);
        String strDestination = String.format("%s,%s", dosen.getLATITUDE(), dosen.getLONGITUDE());
        String source = String.format("%s,%s", mLatLng.latitude, mLatLng.longitude);
        Call<DirectionResults> call = services.getDirection(source, strDestination);
        call.enqueue(new Callback<DirectionResults>() {
            @Override
            public void onResponse(Call<DirectionResults> call, Response<DirectionResults> response) {
                pd.dismiss();
                if(response.isSuccessful()){
                    DirectionResults directionResults = response.body();
                    ArrayList<LatLng> routelist = new ArrayList<LatLng>();
                    if(directionResults.getRoutes().size()>0){
                        ArrayList<LatLng> decodelist;
                        DirectionResults.Route routeA = directionResults.getRoutes().get(0);
                        //Log.i("zacharia", "Legs length : "+routeA.getLegs().size());
                        if(routeA.getLegs().size()>0){
                            List<DirectionResults.Steps> steps= routeA.getLegs().get(0).getSteps();
//                            Log.i("zacharia","Steps size :"+steps.size());
                            DirectionResults.Steps step;
                            DirectionResults.Location location;
                            String polyline;
                            for(int i=0 ; i<steps.size();i++){
                                step = steps.get(i);
                                location =step.getStart_location();
                                routelist.add(new LatLng(location.getLat(), location.getLng()));
                                polyline = step.getPolyline().getPoints();
                                decodelist = RouteDecode.decodePoly(polyline);
                                routelist.addAll(decodelist);
                                location =step.getEnd_location();
                                routelist.add(new LatLng(location.getLat() ,location.getLng()));
                            }
                        }
                    }
                    if(routelist.size()>0){
                        PolylineOptions rectLine = new PolylineOptions().width(10).color(
                                Color.RED);

                        for (int i = 0; i < routelist.size(); i++) {
                            rectLine.add(routelist.get(i));
                        }
                        // Adding route on the map
                        mMap.addPolyline(rectLine);
                        mMap.addMarker(new MarkerOptions()
                                //.position(new LatLng(0.547451, 123.086840))
                                .position(mLatLng)
                                .title("My location")
                        );

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(midPoint(mLatLng.latitude, mLatLng.longitude, dosen.getLATITUDE(), dosen.getLONGITUDE()))
                                .zoom(13)
                                .bearing((float) angleBteweenCoordinate(mLatLng.latitude, mLatLng.longitude, dosen.getLATITUDE(), dosen.getLONGITUDE())).build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<DirectionResults> call, Throwable t) {

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Gagal mendapatkan permission GPS", Snackbar.LENGTH_INDEFINITE).show();
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Pref.getInstance(DosenMapsActivity.this).storeMyPosition(location.getLatitude(), location.getLongitude());
                System.out.println("STORE LOCATION");
            }
        });

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(dosen.getLATITUDE(), dosen.getLONGITUDE());
        Marker markerDosen = mMap.addMarker(new MarkerOptions()
                .position(latLng).title(dosen.getNAMA())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
        );
        markerDosen.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));



    }

    private LatLng midPoint(double lat1, double long1, double lat2,double long2)
    {

        return new LatLng((lat1+lat2)/2, (long1+long2)/2);

    }

    private double angleBteweenCoordinate(double lat1, double long1, double lat2,  double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return brng;
    }
}
