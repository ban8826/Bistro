package com.bistro.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;


import com.bistro.database.Const;
import com.bistro.database.SharedManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * summary 설정 화면
 */
public class SettingAct extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_setting);
        setInitialize();
    }

    private void setInitialize() {
        SharedManager.init(getApplicationContext());
        findViewById(R.id.btn_back)  .setOnClickListener(this);
//        findViewById(R.id.btn_logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                // 뒤로가기
                finish();
                break;

//            case R.id.btn_logout:
//                // 로그 아웃
//                setLogout();

        }
    }

    private void setLogout() {
        int loginType = SharedManager.read(SharedManager.LOGIN_TYPE, -1);
        if (loginType == Const.LOGIN_TYPE_KAKAO) {
            // kakao logout
//            UserApiClient.getInstance().logout(error -> {
//                if (error != null) {
//                    Log.e(getClass().getSimpleName(), "로그아웃 실패, SDK에서 토큰 삭제됨", error);
//                    Toast.makeText(this, "로그아웃 시도 중 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
//                }else{
//                    Log.e(getClass().getSimpleName(), "로그아웃 성공, SDK에서 토큰 삭제됨");
//                    Toast.makeText(this, "성공적으로 로그아웃 하였습니다", Toast.LENGTH_SHORT).show();
//                    setResult(RESULT_OK);
//                    finish();
//                }
//                return null;
//            });
        } else {
            // firebase auth logout
            FirebaseAuth.getInstance().signOut();
        }

        // clear shared manager
        SharedManager.clear();
        setResult(RESULT_OK);
        finish();
    }
}