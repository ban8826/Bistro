package com.bistro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bistro.R;
import com.bistro.database.SharedManager;
import com.bistro.fragment.FavoriteFragment;
import com.bistro.fragment.ListFragment;
import com.bistro.fragment.MyInfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainAct extends AppCompatActivity implements View.OnClickListener {


    // test comment
//    private AppDatabase mLocalDatabase;
    public static Activity _Main_Activity;
    public ListFragment bulletinFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        _Main_Activity = MainAct.this;
        init();
//       setInitialize();
    }

    private void init() {

        SharedManager.init(getApplicationContext());
        bulletinFragment = new ListFragment();
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        MyInfoFragment myInfoFragment = new MyInfoFragment();

        // set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, bulletinFragment).commit();

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


//    private void setInitialize() {
//        mLocalDatabase = AppDatabase.getInstance(getApplicationContext());
//
//        findViewById(R.id.btn_insert_db).setOnClickListener(this);
//        findViewById(R.id.btn_select_db).setOnClickListener(this);
//    }

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


    }
}