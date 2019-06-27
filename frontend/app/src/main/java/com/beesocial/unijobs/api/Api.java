package com.beesocial.unijobs.api;

import com.beesocial.unijobs.models.DefaultResponse;
import com.beesocial.unijobs.models.LoginResponse;
import com.beesocial.unijobs.models.ServiceRegister;
import com.beesocial.unijobs.models.ServiceResponse;
import com.beesocial.unijobs.models.ServiceUpdate;
import com.beesocial.unijobs.models.UserLogin;
import com.beesocial.unijobs.models.UserRegister;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @POST("user")
    Call<DefaultResponse> createUser(@Body UserRegister user);

    @GET("user")
    Call<DefaultResponse> getUser(@Header("Authorization") String token);

    @POST("auth/user")
    Call<LoginResponse> userLogin(@Body UserLogin user);

    @GET("user")
    Call<DefaultResponse> getUserById(@Query("id") String id);

    @PATCH("user")
    Call<DefaultResponse> updateUser(@Header("Authorization") String token, @Body UserRegister user);

    @PATCH("user")
    Call<DefaultResponse> updatePassword(
            @Field("currentpassword") String currentpassword,
            @Field("newpassword") String newpassword,
            @Field("email") String email
    );


    @DELETE("user")
    Call<DefaultResponse> deleteUser(@Path("id") int id);

    @GET("service?isOffer=true")
    Call<List<ServiceResponse>> getServiceOfferTrue();

    @GET("service?isOffer=false")
    Call<List<ServiceResponse>> getServiceOfferFalse();

    @POST("service")
    Call<ServiceResponse> createService(@Header("Authorization") String token, @Body ServiceRegister service);

    @PATCH("service")
    Call<ServiceResponse> updateService(@Header("Authorization") String token, @Body ServiceUpdate service);
}
