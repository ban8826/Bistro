package com.bistro.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.MapView;
import com.naver.maps.map.util.FusedLocationSource;

public class NaverMapAct extends AppCompatActivity implements OnMapReadyCallback {

        private MapView mapView;
        private static NaverMap naverMap;
        private FusedLocationSource fusedLocationSource;
        private final int REQUEST_CODE = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_naver_map);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync( this);

        fusedLocationSource = new FusedLocationSource(this, REQUEST_CODE);
    }



    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {

        // 현재 위치 버튼 안보이게 설정
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false);
        LatLng location = new LatLng(0, 0);
        CameraPosition cameraPosition =new CameraPosition(location, 16,60, 20);
        naverMap.setMinZoom(10.0);   //최소
        naverMap.setMaxZoom(18.0);  //최대
        naverMap.setCameraPosition(cameraPosition);

        naverMap.setLocationSource(fusedLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        // 지도 유형 위성사진으로 설정
        naverMap.setMapType(NaverMap.MapType.Basic);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(fusedLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults))
        {
            return;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
