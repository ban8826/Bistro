package com.bistro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bistro.util.MidnightReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainAct extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainAct";

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private DatabaseReference databaseReference;
    private RetrofitMain retrofitMain;

    // test comment
//    private AppDatabase mLocalDatabase;
    public static Activity _Main_Activity;
    public ListFragment bulletinFragment;

    //region ?????????????????? Callback ??????
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

        // ????????? ????????? ??????????????? ??????????????? 1??? 5??? ???????????? ??????????????? ?????? ??????????????? ?????? ??????.
//        resetAlarm(getApplicationContext());
//       setInitialize();


    }

    private void init() {
        initLocation();
        databaseReference = FirebaseDatabase.getInstance().getReference("bistro");
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
        String today = simpleDateFormat.format(new Date());  // ?????? ??????
        String date_from_shared = SharedManager.read(SharedManager.TODAY, ""); // ???????????? ????????? ??????
        if(date_from_shared.equals(""))  SharedManager.write(SharedManager.TODAY, today); // ??? ????????????????????? ?????????????????????????????? ??????


        // ????????? ????????? 0 ??????
        if (!date_from_shared.equals(today)) {
            SharedManager.write(SharedManager.WRITE_COUNT, "0"); // ????????? 0?????? ?????????
            SharedManager.write(today, "");  // ?????? (????????? ??????) ??????
        }


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

    private void initLocation()
    {
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
        locationRequest.setInterval(1000); // 1?????? ????????? ??????
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
            Log.d(TAG, "?????? ???????????? ??????!");
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


    public static void resetAlarm(Context context){
        AlarmManager resetAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent resetIntent = new Intent(context, MidnightReceiver.class);
        PendingIntent resetSender = PendingIntent.getBroadcast(context, 0, resetIntent, PendingIntent.FLAG_IMMUTABLE);

        // ?????? ??????
        Calendar resetCal = Calendar.getInstance();
        resetCal.setTimeInMillis(System.currentTimeMillis());
        resetCal.set(Calendar.HOUR_OF_DAY, 0);
        resetCal.set(Calendar.MINUTE,0);
        resetCal.set(Calendar.SECOND, 0);

        //????????? 0?????? ????????? ?????? 24????????? ????????? ????????? AlarmManager.INTERVAL_DAY??? ?????????.
        resetAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, resetCal.getTimeInMillis()
                +AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, resetSender);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd kk:mm:ss");
        String setResetTime = format.format(new Date(resetCal.getTimeInMillis()+AlarmManager.INTERVAL_DAY));

        Log.d("resetAlarm", "ResetHour : " + setResetTime);
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

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        Date currentTime = new Date();
        String randomKey = SharedManager.read(SharedManager.AUTH_TOKEN,"");
        String today = mSimpleDateFormat.format(currentTime);
        String savedDate = SharedManager.read(SharedManager.TODAY,"");

        /**   ???????????? (?????? ????????? ????????? ??????, ??????  ????????????)   **/
        if(!today.equals(savedDate))
        {
            // ????????? todayList ?????? ??????
            databaseReference.child("userInfo").child(randomKey).child("todayList").setValue(null);
            // ???????????? ?????? ?????? ????????????
            SharedManager.write(SharedManager.TODAY, today);
        }

    }
}