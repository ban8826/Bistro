package com.bistro.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bistro.R;
import com.bistro.activity.MainAct;
import com.bistro.activity.NaverMapAct;
import com.bistro.activity.SearchAct;
import com.bistro.activity.ShowPostAct;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.MapView;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NaverMapFragment extends Fragment implements OnMapReadyCallback {

    View rootView;
    MapView mapView;

    private Geocoder geocoder;
    private NaverMap map;
    private final String address;

    // 어드레스로 이루어진 리스트
    private List<Address> list;
    private Double latitude, longitude;
    float zoom ;

    ShowPostAct showPostAct;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        showPostAct = (ShowPostAct) getActivity();
    }

    private ConstraintLayout constraintLayout;
    public NaverMapFragment(String address)
    {
          this.address = address;
        Log.d("여기", "생성");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        geocoder = new Geocoder(this.getActivity().getApplicationContext(), Locale.getDefault());

        try {
            if(address != null) {
                list = geocoder.getFromLocationName(address, 1);

                if (list.get(0) != null) {
                    latitude = list.get(0).getLatitude();
                    longitude = list.get(0).getLongitude();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        zoom =17;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.navermap, container, false);
        mapView = rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        constraintLayout = rootView.findViewById(R.id.const_whole);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        this.map = naverMap;

        Marker marker = new Marker();
        LatLng myLatLng = new LatLng(latitude, longitude);
        marker.setPosition(myLatLng);
        marker.setMap(map);
        CameraPosition cameraPosition = new CameraPosition(myLatLng, 16);
        map.setCameraPosition(cameraPosition);

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                Log.d("여기2", String.valueOf(latitude));

                Intent intent = new Intent(getContext(), NaverMapAct.class);
                intent.putExtra("lat", String.valueOf(latitude));
                intent.putExtra("long", String.valueOf(longitude));
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart()
    {
        String addr;

        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
