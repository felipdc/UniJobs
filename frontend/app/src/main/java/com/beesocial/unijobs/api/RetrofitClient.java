package com.beesocial.unijobs.api;

public class RetrofitClient {

    private static final String BASE_URL = "https://micro-unijobs.now.sh/api/";
    private static final String BASE_URL2 = "https://micro-unijobs-service.felipetiagodecarli.now.sh/api/";
    private static RetrofitClient mInstance;
    private String AUTH = "";
    /*
    private Retrofit retrofit;


    private RetrofitClient(int i) {
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
        } else if(i==2) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        else{
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public static synchronized RetrofitClient getInstance(int i) {
        if (mInstance == null) {
            mInstance = new RetrofitClient(i);
        }
        return mInstance;
    }
    public static synchronized RetrofitClient createInstance(int i) {
        if (mInstance!=null) {
            mInstance = null;
        }
        mInstance = new RetrofitClient(i);
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
*/
}