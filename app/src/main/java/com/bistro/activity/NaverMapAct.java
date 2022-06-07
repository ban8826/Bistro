package com.bistro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.MapView;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class NaverMapAct extends AppCompatActivity implements OnMapReadyCallback {

        private MapView mapView;
        private static NaverMap naverMap;
        private FusedLocationSource fusedLocationSource;
        private final int REQUEST_CODE = 1000;
        private Intent intent;
        private String latitude;

    private NaverMap map;
    private String longitude;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_naver_map);

        intent = getIntent();

        latitude = intent.getStringExtra("lat");
        longitude = intent.getStringExtra("long");

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync( this);

//        fusedLocationSource = new FusedLocationSource(this, REQUEST_CODE);
    }



    @Override
    public void onMapReady(@NonNull final NaverMap naverMap)
    {

        this.map = naverMap;

        // 현재 위치 버튼 안보이게 설정
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlEnabled(false);

        LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        Marker marker = new Marker();
        marker.setPosition(location);
        marker.setMap(map);

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(location);

        map.moveCamera(cameraUpdate);


//        naverMap.setLocationSource(fusedLocationSource);
//        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
//
//        // 지도 유형 위성사진으로 설정

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if(fusedLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults))
//        {
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//    }
}
