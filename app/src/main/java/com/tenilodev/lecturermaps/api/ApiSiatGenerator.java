package com.tenilodev.lecturermaps.api;

import android.util.Base64;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
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

public class ApiSiatGenerator {

    //public
    public static final String API_BASE_URL = "http://siat.ung.ac.id/api/";
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
            httpClient.writeTimeout(120, TimeUnit.SECONDS);
            httpClient.readTimeout(120, TimeUnit.SECONDS);
            httpClient.connectTimeout(120, TimeUnit.SECONDS);
        }

        httpClient.addInterceptor(loggingInterceptor());
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                RequestBody body = new FormBody.Builder()
                        .add("username","wsinformatika")
                        .add("pass","FAT3KdiZOn@rekayaza")
                        .add("is_compressed","0")
                        .build();

                Request request = original.newBuilder()
                        .header("User-Agent", "TeniloDev-App")
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .post(body)
                        .build();

                return chain.proceed(request);
            }
        });

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response origin = chain.proceed(chain.request());
                String origin_body = origin.body().string();
                ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), Base64.decode(origin_body,Base64.DEFAULT));
                Response build = origin.newBuilder()
                        .body(body)
                        .build();
                return build;
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
