package com.bistro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bistro.Define;
import com.bistro.R;
import com.bistro.database.SharedManager;
import com.bistro.fragment.FavoriteFragment;
import com.bistro.fragment.ListFragment;
import com.bistro.fragment.MyInfoFragment;
import com.bistro.model.KakaoPlaceModel;
import com.bistro.util.RetrofitMain;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainAct extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainAct";

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private RetrofitMain retrofitMain;

    // test comment
//    private AppDatabase mLocalDatabase;
    public static Activity _Main_Activity;
    public ListFragment bulletinFragment;

    //region 메인액티비티 Callback 관련
    public interface MainCallback {
        void getCurrentRegion(String region);
    }

    private MainCallback mainCallback;

    public void setMainCallback(MainCallback mainCallback) {
        this.mainCallback = mainCallback;
    }

    public void getCurrentRegion(String region) {
        if (mainCallback != null) {
            mainCallback.getCurrentRegion(region);
        }
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        _Main_Activity = MainAct.this;
        init();
//       setInitialize();
    }

    private void init() {
        initLocation();
        retrofitMain = new RetrofitMain(this);
        retrofitMain.setResultListener(new RetrofitMain.ResultListener() {
            @Override
            public void onPoiResult(KakaoPlaceModel.PoiResult model) {

            }

            @Override
            public void onRegionResult(String region) {
                getCurrentRegion(region);
            }
        });

        SharedManager.init(getApplicationContext());
        bulletinFragment = new ListFragment();
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        MyInfoFragment myInfoFragment = new MyInfoFragment();

        // set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, bulletinFragment).commit();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String today = simpleDateFormat.format(new Date());  // 오늘 날짜
        String date_from_shared = SharedManager.read(SharedManager.TODAY, ""); // 쉐어드에 저장된 날짜


        // 글쓰기 카운트 0 으로
        if (!date_from_shared.equals(today)) {
            SharedManager.write(SharedManager.WRITE_COUNT, "0"); // 카운트 0으로 초기화
            SharedManager.write(today, "");  // 오늘 (새로운 날짜) 저장
        }

        Toast.makeText(_Main_Activity, SharedManager.read(SharedManager.WRITE_COUNT, ""), Toast.LENGTH_SHORT).show();

        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.menu_home:
                    transaction.replace(R.id.container, bulletinFragment).commit();
                    break;

                case R.id.menu_favorite:
                    transaction.replace(R.id.container, favoriteFragment).commit();
                    break;

                case R.id.menu_my_info:
                    transaction.replace(R.id.container, myInfoFragment).commit();
                    break;

                default:
                    transaction.replace(R.id.container, bulletinFragment).commit();
                    break;
            }
            return true;
        });

    }

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Define.curLon = location.getLongitude();
                    Define.curLat = location.getLatitude();

                    Log.d(TAG, "lon : " + location.getLongitude() + ", lat : " + location.getLatitude());

                    retrofitMain.getCurrentRegion(location.getLongitude(), location.getLatitude());
                }
            }
        };
        mLocationRequest = LocationRequest.create();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // 1분에 한번씩 갱신
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

//    private void setInitialize() {
//        mLocalDatabase = AppDatabase.getInstance(getApplicationContext());
//
//        findViewById(R.id.btn_insert_db).setOnClickListener(this);
//        findViewById(R.id.btn_select_db).setOnClickListener(this);
//    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "위치 권한설정 안됨!");
            return;
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    Looper.getMainLooper());
        }

    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


//            case R.id.btn_insert_db:
//                // todo - test isnert db
//                NotificationDto dto = new NotificationDto();
//                dto.setNotiType("test_type");
//                dto.setMsgTitle("test_title");
//                dto.setComment("test_comment");
//                dto.setMsgSenderIdToken("test_sender_token");
//                dto.setMsgSender("test_msg_sender");
//                dto.setMsgGetter("test_msg_getter");
//                dto.setBoardId("test_board_id");
//                dto.setNotiDate("test_noti_date");
//
//                new Thread(() -> {
//                    mLocalDatabase.notificationDao().insert(dto);
//                }).start();
//                Toast.makeText(MainAct.this, "insert database", Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.btn_select_db:
//                // todo - test select db
//                mLocalDatabase.notificationDao().loadAllDataLive().observe(this, notificationDtos -> {
//                    for (NotificationDto notificationDto : notificationDtos) {
//                        Log.d(getClass().getSimpleName(),
//                                notificationDto.getNotiType() + "\n"
//                                + notificationDto.getMsgTitle() + "\n"
//                                        + notificationDto.getComment() + "\n"
//                                        + notificationDto.getMsgSenderIdToken() + "\n"
//                                        + notificationDto.getMsgGetter() + "\n"
//                                        + notificationDto.getBoardId() + "\n"
//                                        + notificationDto.getNotiDate());
//                    }
//                });
//
//                Toast.makeText(MainAct.this, "select database", Toast.LENGTH_SHORT).show();
//                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause !");

        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume !");

        startLocationUpdates();
    }
}