package com.tenilodev.lecturermaps.api;

import com.tenilodev.lecturermaps.fcm.Message;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by azisa on 2/24/2017.
 */

public interface FCMMethod {

    @POST("/fcm/send")
    Call<Message> sendMessage(@Body Message message);

}
