package com.bistro.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.MapView;

public class NaverMapAct extends AppCompatActivity implements OnMapReadyCallback {

        private MapView mapView;
        private static NaverMap naverMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_naver_map);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync( this);
        
    }



    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {

        // 현재 위치 버튼 안보이게 설정
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false);
        LatLng location = new LatLng(37.487936, 126.825071);
        CameraPosition cameraPosition =new CameraPosition(location, 18);
        naverMap.setMinZoom(10.0);   //최소
        naverMap.setMaxZoom(18.0);  //최대
        naverMap.setCameraPosition(cameraPosition);


        // 지도 유형 위성사진으로 설정
        naverMap.setMapType(NaverMap.MapType.Basic);
    }
}
