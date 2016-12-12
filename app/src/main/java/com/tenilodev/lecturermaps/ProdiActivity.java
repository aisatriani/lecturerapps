package com.tenilodev.lecturermaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.tenilodev.lecturermaps.adapter.RVAdapter;
import com.tenilodev.lecturermaps.api.ApiGenerator;
import com.tenilodev.lecturermaps.api.ApiResponse;
import com.tenilodev.lecturermaps.api.ClientServices;
import com.tenilodev.lecturermaps.model.Fakultas;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdiActivity extends AppCompatActivity {

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = (RecyclerView)findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        loadAllProdi();


    }

    private void loadAllProdi() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memuat data");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        ClientServices services = ApiGenerator.createService(ClientServices.class);
        Call<ApiResponse<List<Fakultas>>> call = services.getAllProdi();
        call.enqueue(new Callback<ApiResponse<List<Fakultas>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Fakultas>>> call, final Response<ApiResponse<List<Fakultas>>> response) {
                pd.dismiss();
                if(response.isSuccessful()){
                    RVAdapter adapter = new RVAdapter(ProdiActivity.this, response.body().getData());
                    rv.setAdapter(adapter);
                    adapter.setClickListener(new RVAdapter.ClickListener() {
                        @Override
                        public void onClick(View v, int position) {
                            Fakultas fakultas = response.body().getData().get(position);
                            Intent intent = new Intent(ProdiActivity.this, DosenActivity.class);
                            intent.putExtra("fakultas",fakultas);
                            startActivity(intent);
                        }
                    });
                }else{
                    Snackbar.make(findViewById(R.id.prodi_activity), getString(R.string.error_respon), Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ulangi", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadAllProdi();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Fakultas>>> call, Throwable t) {
                pd.dismiss();
                Snackbar.make(findViewById(R.id.prodi_activity), getString(R.string.error_connect), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ulangi", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadAllProdi();
                            }
                        })
                        .show();
            }
        });
    }
}
