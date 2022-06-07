package com.bistro.util;

import android.content.Context;
import android.util.Log;

import com.bistro.R;
import com.bistro.model.KakaoPlaceModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitMain {

    private ResultListener resultListener;

    public interface ResultListener {
        void onResult(KakaoPlaceModel.PoiResult model);
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    private Context mContext;

    public RetrofitMain(Context mContext) {
        this.mContext = mContext;
    }

    private final String TAG = "RetrofitMain";

    public void getSearchPoi(String poi) {
        Log.d(TAG, "RetrofitMain getSearchPoi !!");

        Call<KakaoPlaceModel.PoiResult> call = RetrofitClient.getApiService().getSearchPoi(mContext.getResources().getString(R.string.KAKAO_REST_API_KEY), poi);
        call.enqueue(new Callback<KakaoPlaceModel.PoiResult>() {
            @Override
            public void onResponse(Call<KakaoPlaceModel.PoiResult> call,
                                   Response<KakaoPlaceModel.PoiResult> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "error code : " + response.code());
                } else {
                    KakaoPlaceModel.PoiResult result = response.body();

                    if (resultListener != null) {
                        resultListener.onResult(result);
                    }
                }
            }

            @Override
            public void onFailure(Call<KakaoPlaceModel.PoiResult> call, Throwable t) {
                Log.e(TAG, "error code : " + t.toString());
            }

        });
    }
}
