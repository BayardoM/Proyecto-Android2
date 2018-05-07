package com.example.bayardomoraga.aplicacionandroid.api;

import com.example.bayardomoraga.aplicacionandroid.model.MarketModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("markets")
    Call<List<MarketModel>> getmarkets();

    @POST("markets")
    Call<MarketModel> createmarkets(@Body MarketModel marketModel);

    @PUT("markets/{id}")
    Call<MarketModel> updatemarkets(@Path("id") String id, @Body MarketModel marketModel);

    @DELETE("markets/{id}")
    Call<MarketModel> deletemarkets(@Path("id") String id);
}
