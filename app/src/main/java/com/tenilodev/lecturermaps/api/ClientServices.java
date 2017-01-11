package com.tenilodev.lecturermaps.api;

import com.tenilodev.lecturermaps.model.DirectionResults;
import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.model.Fakultas;
import com.tenilodev.lecturermaps.model.LokasiDosen;
import com.tenilodev.lecturermaps.model.Mahasiswa;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by azisa on 12/12/2016.
 */

public interface ClientServices {

    @FormUrlEncoded
    @POST("loginmahasiswa")
    public Call<ApiResponse<Mahasiswa>> loginMahasiswa(@Field("nim") String nim, @Field("pwd") String pwd);

    @FormUrlEncoded
    @POST("logindosen")
    public Call<ApiResponse<Dosen>> loginDosen(@Field("nidn") String nidn, @Field("pwd") String pwd);

    @GET("prodi")
    public Call<ApiResponse<List<Fakultas>>> getAllProdi();

    @GET("dosen")
    public Call<List<Dosen>> getAllDosen();

    @GET("dosen/{kdprodi}/prodi")
    public Call<List<Dosen>> getDosenByProdi(@Path("kdprodi") String kdprodi);

    @GET("dosen/{domisili}/domisili")
    public Call<List<Dosen>> getDosenByDomisili(@Path("domisili") String domisili);

    @FormUrlEncoded
    @POST("lokasi")
    public Call<LokasiDosen> updateLokasiDosen(@Field("nidn") String nidn,
                                               @Field("latitude") double latitude,
                                               @Field("longitude") double longitude,
                                               @Field("active") int active);

    @GET("lokasi")
    public Call<ArrayList<LokasiDosen>> getActiveLokasiDosen();

    @GET("/maps/api/directions/json")
    public Call<DirectionResults> getDirection(@Query("origin") String origin,@Query("destination") String destination);

}
