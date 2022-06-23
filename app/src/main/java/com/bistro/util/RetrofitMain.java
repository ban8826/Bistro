package com.bistro.util;

import android.content.Context;
import android.util.Log;

import com.bistro.Define;
import com.bistro.R;
import com.bistro.model.KakaoPlaceModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitMain {

    private ResultListener resultListener;

    public interface ResultListener {
        void onPoiResult(KakaoPlaceModel.PoiResult model);
        void onRegionResult(String region);
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
                        resultListener.onPoiResult(result);
                    }
                }
            }

            @Override
            public void onFailure(Call<KakaoPlaceModel.PoiResult> call, Throwable t) {
                Log.e(TAG, "error code : " + t.toString());
            }

        });
    }

    public void getCurrentRegion(double x, double y) {
        Log.d(TAG, "RetrofitMain getCurrentRegion !!");

        Call<KakaoPlaceModel.RegionResult> call = RetrofitClient.getApiService().getCurrentRegion(mContext.getResources().getString(R.string.KAKAO_REST_API_KEY), Double.toString(x), Double.toString(y));
        call.enqueue(new Callback<KakaoPlaceModel.RegionResult>() {
            @Override
            public void onResponse(Call<KakaoPlaceModel.RegionResult> call,
                                   Response<KakaoPlaceModel.RegionResult> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "error code : " + response.code());
                } else {
                    KakaoPlaceModel.RegionResult result = response.body();
                    for (KakaoPlaceModel.RegionInfo info : result.getRegionList()){
                        if (info.getRegionType().equals("H")) {
                            Log.d(TAG, "현재 동 : " + info.getRegion3DepthName());
                            Define.curRegion = info.getRegion3DepthName();
                            resultListener.onRegionResult(info.getRegion3DepthName());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<KakaoPlaceModel.RegionResult> call, Throwable t) {
                Log.e(TAG, "error code : " + t.toString());
            }

        });
    }
}
