package com.tenilodev.lecturermaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

public class DomisiliActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout actionKota, actionKab, actionBone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domisili);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionKota = (LinearLayout) findViewById(R.id.action_kota);
        actionKab = (LinearLayout) findViewById(R.id.action_kab);
        actionBone = (LinearLayout) findViewById(R.id.action_bone);

        actionKota.setOnClickListener(this);
        actionKab.setOnClickListener(this);
        actionBone.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.action_kota){
            Intent intent = new Intent(this, DosenDomisiliActivity.class);
            intent.putExtra("domisili", Config.DOMISILI_KOTA);
            startActivity(intent);
        }
        if(v.getId() == R.id.action_kab){
            Intent intent = new Intent(this, DosenDomisiliActivity.class);
            intent.putExtra("domisili", Config.DOMISILI_KAB);
            startActivity(intent);
        }
        if(v.getId() == R.id.action_bone){
            Intent intent = new Intent(this, DosenDomisiliActivity.class);
            intent.putExtra("domisili", Config.DOMISILI_BONE);
            startActivity(intent);
        }
    }
}
