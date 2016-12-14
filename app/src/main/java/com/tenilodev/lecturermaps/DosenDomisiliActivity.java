package com.tenilodev.lecturermaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tenilodev.lecturermaps.adapter.DosenAdapter;
import com.tenilodev.lecturermaps.api.ApiGenerator;
import com.tenilodev.lecturermaps.api.ClientServices;
import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.model.Fakultas;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DosenDomisiliActivity extends AppCompatActivity {

    private RecyclerView rv;
    private String domisili;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = (RecyclerView)findViewById(R.id.rv_dosen);
        rv.setLayoutManager(new LinearLayoutManager(this));

        handleIntent(getIntent());
        loadDataDosen();
    }

    private void handleIntent(Intent intent) {
        domisili = intent.getStringExtra("domisili");
        setTitle("Wilayah "+ domisili);

    }

    private void loadDataDosen() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memuat data");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        ClientServices services = ApiGenerator.createService(ClientServices.class);
        Call<List<Dosen>> call = services.getDosenByDomisili(domisili);
        call.enqueue(new Callback<List<Dosen>>() {
            @Override
            public void onResponse(Call<List<Dosen>> call, final Response<List<Dosen>> response) {
                pd.dismiss();
                if(response.isSuccessful()){
                    DosenAdapter adapter = new DosenAdapter(DosenDomisiliActivity.this, response.body());
                    rv.setAdapter(adapter);
                    adapter.setClickListener(new DosenAdapter.ClickListener() {
                        @Override
                        public void onClick(View v, int position) {
                            Intent intent = new Intent(DosenDomisiliActivity.this, DosenMapsActivity.class);
                            intent.putExtra("dosen", response.body().get(position));
                            startActivity(intent);
                        }
                    });
                }else{
                    Snackbar.make(findViewById(R.id.dosen_activity), getString(R.string.error_connect), Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ulangi", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadDataDosen();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Dosen>> call, Throwable t) {
                pd.dismiss();
                Snackbar.make(findViewById(R.id.dosen_activity), getString(R.string.error_connect), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ulangi", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadDataDosen();
                            }
                        })
                        .show();
            }
        });
    }
}
