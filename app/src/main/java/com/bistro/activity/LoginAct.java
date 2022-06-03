package com.bistro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.bistro.database.Const;
import com.bistro.database.SharedManager;
import com.bistro.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.Profile;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * summary 로그인 화면
 */
public class LoginAct extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mEtEmail, mEtPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        setInitialize();
    }

    private void setInitialize() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bistro");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        findViewById(R.id.btn_login_email).setOnClickListener(this);
        findViewById(R.id.btn_login_kakao).setOnClickListener(this);
        findViewById(R.id.btn_register)   .setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_email:
                // 이메일 로그인 (Firebase)
                setEmailLogin();
                break;

            case R.id.btn_login_kakao:
                // 카카오 로그인
                setKakoLogin();
                break;

            case R.id.btn_register:
                // 회원가입
                startActivity(new Intent(this, JoinAct.class));
                break;
        }
    }

    private void setEmailLogin() {
        String strEmail = mEtEmail.getText().toString();
        String strPwd = mEtPwd.getText().toString();

        // check validation
        if (strEmail.length() == 0 || strPwd.length() == 0) {
            // empty value
            Toast.makeText(this, "비어있는 입력 값이 존재합니다", Toast.LENGTH_SHORT).show();
            return;
        }


        mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 로그인 성공
                mDatabaseRef.child("userInfo").child(task.getResult().getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            // 로그인 타입 (Firebase Auth)

                            /** 하루 5번만 글쓰기 제한인데 처음 앱 설치했을경우만 실행 **/
                            SharedManager.init(getApplicationContext());
                            if(SharedManager.read(SharedManager.TODAY,"").equals(""))
                            {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
                                String date = simpleDateFormat.format(new Date());
                                SharedManager.write(SharedManager.TODAY, date);
                                SharedManager.write(SharedManager.WRITE_COUNT,"0");
                            }

//                            SharedManager.write(SharedManager.LOGIN_TYPE, Const.LOGIN_TYPE_FIREBASE_EMAIL);
                            SharedManager.write(SharedManager.LOGIN_ID, userModel.getId());
                            SharedManager.write(SharedManager.AUTH_TOKEN, userModel.getAuthToken());
                            SharedManager.write(SharedManager.FCM_TOKEN, userModel.getFcmToken());
                            SharedManager.write(SharedManager.USER_NAME, userModel.getName());
                            SharedManager.write(SharedManager.PROFILE_STORAGE_PATH, userModel.getProfileImgPath());
                        }

                        // move activity to MainAct
                        startActivity(new Intent(LoginAct.this, MainAct.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginAct.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // 로그인 실패
                Toast.makeText(LoginAct.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // ====================================== KAKAO LOGIN START ====================================== //
    private void setKakoLogin() {
        // 디바이스에 카카오톡 설치 여부 확인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            // 카카오톡이 설치되어 있으면 카톡으로 로그인 확인 요청
            UserApiClient.getInstance().loginWithKakaoTalk(this, (oAuthToken, error) -> {
                if (error != null) {
                    Log.e(getClass().getSimpleName(), "로그인 실패" + error);
                } else if (oAuthToken != null) {
                    Log.i(getClass().getSimpleName(), "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                    getKakaoAuthInfo(oAuthToken.getAccessToken());
                }
                return null;
            });
        } else {
            // 카카오톡이 설치되어 있지 않은 경우 앱 내장 웹뷰 방식으로 카카오계정 확인 요청
            UserApiClient.getInstance().loginWithKakaoAccount(this, (oAuthToken, error) -> {
                if (error != null) {
                    Log.e(getClass().getSimpleName(), "로그인 실패" + error);
                } else if (oAuthToken != null) {
                    Log.i(getClass().getSimpleName(), "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                    getKakaoAuthInfo(oAuthToken.getAccessToken());
                }
                return null;
            });
        }


    }

    private void getKakaoAuthInfo(String _strUserToken) {
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(getClass().getSimpleName(), "사용자 정보 요청 실패 : " +meError);
            } else {
                // 로그인 타입 (카카오)
                SharedManager.write(SharedManager.LOGIN_TYPE, Const.LOGIN_TYPE_KAKAO);
                // 사용자 계정 토큰
                SharedManager.write(SharedManager.AUTH_TOKEN, _strUserToken);
                // 사용자 아이디
                SharedManager.write(SharedManager.LOGIN_ID, String.valueOf(user.getId()));

                UserModel userModel = new UserModel();
                userModel.setAuthToken(_strUserToken);
                userModel.setId(String.valueOf(user.getId()));

                Account account = user.getKakaoAccount();
                if (account != null) {
                    // 프로필
                    Profile profile = account.getProfile();
                    if (profile != null) {
                        SharedManager.write(SharedManager.USER_NAME, profile.getNickname());
                        SharedManager.write(SharedManager.PROFILE_STORAGE_PATH, profile.getProfileImageUrl());

                        userModel.setName(profile.getNickname());
                        userModel.setProfileImgPath(profile.getProfileImageUrl());
                    }
                }

                // save firebase database
                mDatabaseRef.child(Const.FIREBASE_KEY_USER_INFO).child(_strUserToken).setValue(userModel).addOnSuccessListener(unused -> {
                    startActivity(new Intent(this, MainAct.class));
                });
            }
            return null;
        });
    }
    // ====================================== KAKAO LOGIN END ====================================== //


}