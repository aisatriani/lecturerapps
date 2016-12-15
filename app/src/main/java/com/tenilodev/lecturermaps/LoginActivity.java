package com.tenilodev.lecturermaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class LoginActivity extends AppCompatActivity {

    private AppCompatSpinner spinnerLevel;
    private AppCompatButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spinnerLevel = (AppCompatSpinner) findViewById(R.id.spinnerLevel);
        btnLogin = (AppCompatButton) findViewById(R.id.btn_login);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.select_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);

    }

    private EditText getInputEmail(){
        return (EditText) findViewById(R.id.input_email);
    }

    private EditText getInputPassword(){
        return (EditText) findViewById(R.id.input_password);
    }


}
