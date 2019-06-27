package com.beesocial.unijobs.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://unijobs-user.now.sh/api/";
    private static final String BASE_URL2 = "https://unijobs-service.now.sh/api/";
    private static RetrofitClient mInstance;
    private String AUTH = "";
    private Retrofit retrofit;
    private static int j = 1;

    private RetrofitClient(int i) {
        j=i;
        if (i == 1) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(
                            new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();

                                    Request.Builder requestBuilder = original.newBuilder()
                                            .addHeader("Authorization", AUTH)
                                            .method(original.method(), original.body());

                                    Request request = requestBuilder.build();
                                    return chain.proceed(request);
                                }
                            }
                    ).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        } else {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(
                            new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();

                                    Request.Builder requestBuilder = original.newBuilder()
                                            .method(original.method(), original.body());

                                    Request request = requestBuilder.build();
                                    return chain.proceed(request);
                                }
                            }
                    ).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
    }

    public static synchronized RetrofitClient createInstance(int i) {
        mInstance = new RetrofitClient(i);
        return mInstance;
    }

    public static synchronized RetrofitClient getInstance(int i) {
        if (mInstance == null) {
            mInstance = new RetrofitClient(i);
        }
        if(j==i){
            return mInstance;
        }
        else{
            mInstance = new RetrofitClient(i);
            return mInstance;
        }
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }

}