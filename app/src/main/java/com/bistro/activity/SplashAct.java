package com.bistro.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.bistro.database.SharedManager;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * author ban8826
 * summary 시작화면
 */
public class SplashAct extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        setInitialize();
    }

    private void setInitialize() {
        getKeyHash();
        setCheckPermission();


    }

    private void setCheckPermission() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Log.d(getClass().getSimpleName(), "권한 허용");
                        setCheckStartActivity();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(SplashAct.this, "요청하신 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setRationaleMessage("앱을 이용하시려면 권한을 허용해주세요")
                .setDeniedMessage("권한을 거부하셨습니다.\n앱을 원활히 이용하시려면 권한을 설정해주세요.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void setCheckStartActivity() {
        SharedManager.init(getApplicationContext());
        new Handler().postDelayed(() -> {
            if (!SharedManager.read(SharedManager.LOGIN_ID, "").equals("")) {
                // 자동 로그인을 할 수 있는 상태라면 바로 MainAct로 이동한다.
                Intent intent = new Intent(SplashAct.this, MainAct.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SplashAct.this, LoginAct.class);
                startActivity(intent);
            }
            overridePendingTransition(0, 0);
            finish();
        }, 500);
    }




    private void getKeyHash() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

}
