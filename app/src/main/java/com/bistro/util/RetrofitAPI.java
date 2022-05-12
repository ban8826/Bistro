package com.bistro.util;

import com.bistro.model.KakaoPlaceModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("v2/local/search/keyword.json")
    Call<KakaoPlaceModel.PoiResult> getSearchPoi(@Header("Authorization") String key,
                                                 @Query("query") String query);

    @GET("v2/local/search/keyword.json")
    Call<Object> getSearchPoi2(@Header("Authorization") String key,
                                                 @Query("query") String query);
}
