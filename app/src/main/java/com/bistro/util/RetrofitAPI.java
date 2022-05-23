package com.bistro.util;

import com.bistro.model.KakaoPlaceModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RetrofitAPI {
//    @GET("v2/local/search/keyword.json")
//    Call<KakaoPlaceModel.PoiResult> getSearchPoi(@Header("Authorization") String key,
//                                                 @Query("query") String query,
//                                                 @Query("x") String lon,
//                                                 @Query("y") String lat);

    @GET("v2/local/search/keyword.json")
    Call<KakaoPlaceModel.PoiResult> getSearchPoiFood(@Header("Authorization") String key,
                                                     @Query("query") String query,
                                                     @Query("category_group_code") String category);

    @GET("v2/local/search/keyword.json")
    Call<KakaoPlaceModel.PoiResult> getSearchPoiCafe(@Header("Authorization") String key,
                                                     @Query("query") String query,
                                                     @Query("category_group_code") String category);
}
