package com.tenilodev.lecturermaps.api;

import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.model.Fakultas;
import com.tenilodev.lecturermaps.model.Mahasiswa;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by azisa on 12/12/2016.
 */

public interface ClientServices {

    @FormUrlEncoded
    @POST("loginmahasiswa")
    public Call<ApiResponse<Mahasiswa>> login(@Field("nim") String nim, @Field("pwd") String pwd);

    @GET("prodi")
    public Call<ApiResponse<List<Fakultas>>> getAllProdi();

    @GET("dosen")
    public Call<List<Dosen>> getAllDosen();

    @GET("dosen/{kdprodi}/prodi")
    public Call<List<Dosen>> getDosenByProdi(@Path("kdprodi") String kdprodi);

}
