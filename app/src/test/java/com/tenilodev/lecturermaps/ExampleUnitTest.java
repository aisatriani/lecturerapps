package com.tenilodev.lecturermaps;

import com.tenilodev.lecturermaps.api.ApiResponse;
import com.tenilodev.lecturermaps.api.ApiSiatGenerator;
import com.tenilodev.lecturermaps.api.SiatMethod;
import com.tenilodev.lecturermaps.model.Dosen;

import org.junit.Test;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        SiatMethod service = ApiSiatGenerator.createService(SiatMethod.class);
        Response<ApiResponse<List<Dosen>>> execute = service.getAllDosen().execute();
        //System.out.println(execute.body());
    }
}