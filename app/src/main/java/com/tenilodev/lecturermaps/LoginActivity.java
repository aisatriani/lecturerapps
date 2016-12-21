package com.tenilodev.lecturermaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tenilodev.lecturermaps.api.ApiGenerator;
import com.tenilodev.lecturermaps.api.ApiResponse;
import com.tenilodev.lecturermaps.api.ClientServices;
import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.model.Mahasiswa;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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

        btnLogin.setOnClickListener(this);

    }

    private EditText getInputEmail(){
        return (EditText) findViewById(R.id.input_email);
    }

    private EditText getInputPassword(){
        return (EditText) findViewById(R.id.input_password);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_login){
            doActionLogin();
        }
    }

    private void doActionLogin() {
        Toast.makeText(this, spinnerLevel.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        if(validate()){
            if(spinnerLevel.getSelectedItem().toString().equals("MAHASISWA")){
                loginMahasiswa();
            }
            if(spinnerLevel.getSelectedItem().toString().equals("DOSEN")){
                loginDosen();
            }
        }
    }

    private void loginDosen() {
        final ProgressDialog pd = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        ClientServices services = ApiGenerator.createService(ClientServices.class);
        Call<ApiResponse<Dosen>> call = services.loginDosen(getInputEmail().getText().toString(), getInputPassword().getText().toString());
        call.enqueue(new Callback<ApiResponse<Dosen>>() {
            @Override
            public void onResponse(Call<ApiResponse<Dosen>> call, Response<ApiResponse<Dosen>> response) {
                pd.dismiss();
                if(response.isSuccessful()){

                    if(response.body().getData() != null){
                        Pref.getInstance(LoginActivity.this).setLoginIn(true);
                        Pref.getInstance(LoginActivity.this).storeDataDosen(response.body().getData());
                        Pref.getInstance(LoginActivity.this).storeNim(getInputEmail().getText().toString());
                        Pref.getInstance(LoginActivity.this).storeLoginState(Config.LOGIN_STATE_DOSEN);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }else {
                        Snackbar.make(findViewById(R.id.btn_login), "Username atau password salah", Snackbar.LENGTH_INDEFINITE)
                                .show();
                    }


                }else{



                    Snackbar.make(findViewById(R.id.btn_login), "Ada masalah terjadi di server", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ulangi", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loginMahasiswa();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Dosen>> call, Throwable t) {
                pd.dismiss();
                Snackbar.make(findViewById(R.id.btn_login), "Gagal terhubung", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ulangi", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginMahasiswa();
                            }
                        })
                        .show();
            }
        });
    }

    private void loginMahasiswa() {

        final ProgressDialog pd = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        ClientServices services = ApiGenerator.createService(ClientServices.class);
        Call<ApiResponse<Mahasiswa>> call = services.loginMahasiswa(getInputEmail().getText().toString(), getInputPassword().getText().toString());
        call.enqueue(new Callback<ApiResponse<Mahasiswa>>() {
            @Override
            public void onResponse(Call<ApiResponse<Mahasiswa>> call, Response<ApiResponse<Mahasiswa>> response) {
                pd.dismiss();
                if(response.isSuccessful()){

                    if(response.body().getData() != null){
                        Pref.getInstance(LoginActivity.this).setLoginIn(true);
                        Pref.getInstance(LoginActivity.this).storeDataMahasiswa(response.body().getData());
                        Pref.getInstance(LoginActivity.this).storeNim(getInputEmail().getText().toString());
                        Pref.getInstance(LoginActivity.this).storeLoginState(Config.LOGIN_STATE_MAHASISWA);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }else {
                        Snackbar.make(findViewById(R.id.btn_login), "Username atau password salah", Snackbar.LENGTH_INDEFINITE)
                                .show();
                    }


                }else{



                        Snackbar.make(findViewById(R.id.btn_login), "Ada masalah terjadi di server", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ulangi", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loginMahasiswa();
                                    }
                                })
                                .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Mahasiswa>> call, Throwable t) {
                pd.dismiss();
                Snackbar.make(findViewById(R.id.btn_login), "Gagal terhubung", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ulangi", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginMahasiswa();
                            }
                        })
                        .show();
            }
        });
    }

    private boolean validate() {
        EditText[] editTexts = {getInputEmail(), getInputPassword()};
        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().equalsIgnoreCase("")) {
                editText.setError("Harus disini");
                editText.requestFocus();
                return false;
            }
        }

        return true;
    }
}
