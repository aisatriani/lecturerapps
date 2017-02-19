package com.tenilodev.lecturermaps.api;

import com.tenilodev.lecturermaps.model.Dosen;
import com.tenilodev.lecturermaps.model.Mahasiswa;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by azisa on 2/12/2017.
 */

public interface SiatMethod {

    @POST("index.php")
    Call<SiatResponse> getMahasiswa(@Query("service") String service, @Query("nim") String nim);

    @POST("index.php?service=login_mahasiswa")
    public Call<ApiResponse<Mahasiswa>> loginMahasiswa(@Query("nim") String nim, @Query("pwd") String pwd);

    @POST("index.php?service=getlogindosen")
    public Call<ApiResponse<Dosen>> loginDosen(@Query("nidn") String nim, @Query("pwd") String pwd);

    @GET("index.php?service=getalldosen")
    public Call<ApiResponse<List<Dosen>>> getAllDosen();


}
