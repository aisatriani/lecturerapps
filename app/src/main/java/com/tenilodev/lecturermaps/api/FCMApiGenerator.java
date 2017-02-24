package com.tenilodev.lecturermaps.api;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMApiGenerator {

    //public
    public static final String API_BASE_URL = "https://fcm.googleapis.com/";
    //local
    //public static final String API_BASE_URL = "http://10.0.2.2";

    private static OkHttpClient.Builder httpClient = null;

    private static Retrofit.Builder builder = new Retrofit.Builder();
    private static Retrofit retrofit = null;


    private static HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    public static <S> S createService(Class<S> serviceClass) {

        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder();
        }

        httpClient.addInterceptor(loggingInterceptor());
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "key=AAAArzWMtF8:APA91bGxOTnjIFCK_x5FMFZGe4HL3s2hhpzZcS6qn7d85CeOp3d2DhPCtPj82ncDcO0a7sqcVYkuad34SHRc-L9nAFHVSW_XlUi9xoZ3uSd8_QaHhKWTlVBDnJ_accB-dVWN9K9NZ2XEk3jsYKJaokVHgnBxVJwSsg"); // <-- this is the important line
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        builder.baseUrl(API_BASE_URL);
        builder.addConverterFactory(new ToStringConverterFactory());
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient.build());

        if (retrofit == null) {
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }


    static class ToStringConverterFactory extends Converter.Factory {
        private final MediaType MEDIA_TYPE = MediaType.parse("text/plain");


        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            if (String.class.equals(type)) {
                return new Converter<ResponseBody, String>() {
                    @Override
                    public String convert(ResponseBody value) throws IOException {
                        return value.string();
                    }
                };
            }
            return null;
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                              Annotation[] methodAnnotations, Retrofit retrofit) {

            if (String.class.equals(type)) {
                return new Converter<String, RequestBody>() {
                    @Override
                    public RequestBody convert(String value) throws IOException {
                        return RequestBody.create(MEDIA_TYPE, value);
                    }
                };
            }
            return null;
        }
    }

}
