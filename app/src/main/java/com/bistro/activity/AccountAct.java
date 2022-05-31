package com.bistro.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.bistro.database.SharedManager;
import com.bistro.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.response.model.UserAccount;

public class AccountAct extends AppCompatActivity implements View.OnClickListener {


    private TextView tv_id, tv_name, tv_phone, tv_password, tv_remove_account, tv_logout, tv_location, tv_remove_aca, tv_remove, tv_academy;
    private ImageView iv_back_arrow;
    private DatabaseReference databaseReference;
    private Context context;
    private String hobby, loginType;
    private LinearLayout linear_remove_aca, linear_academy;
    private View view6, view3_1;

    private FirebaseAuth mAuth ;
//    Button btn_check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_account);

        init();


    }

    public void init() {
        context = getApplicationContext();
        SharedManager.init(context);
        loginType = SharedManager.read(SharedManager.LOGIN_TYPE,"");
        mAuth = FirebaseAuth.getInstance();


        tv_remove = findViewById(R.id.tv_remove);
        tv_remove.setOnClickListener(this);

        tv_id = findViewById(R.id.tv_id);
        tv_name = findViewById(R.id.tv_name);
//        tv_password = findViewById(R.id.tv_password_change);
        tv_phone = findViewById(R.id.tv_phone);
        tv_remove_account = findViewById(R.id.tv_remove);
        tv_logout = findViewById(R.id.tv_logout);

//        tv_password.setOnClickListener(this);
        tv_remove_account.setOnClickListener(this);
        tv_logout.setOnClickListener(this);

        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_back_arrow.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Academy");

        tv_id.setText(SharedManager.read(SharedManager.LOGIN_ID, ""));
        tv_name.setText(SharedManager.read(SharedManager.USER_NAME, ""));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /** 뒤로가기 **/
            case R.id.iv_back_arrow: {
                finish();
                break;
            }

//             /** 비밀번호 변경 **/
            /** 비번바꾸기 위해 이메일로 비번바꾸는 링크 보내주는 코드 **/
//                String emailAddress = "ban8826@naver.";
//               FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//                firebaseAuth.sendPasswordResetEmail(emailAddress)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    Toast.makeText(MyAccountInfoAct.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
//                                    finish();
//                                    startActivity(new Intent(getApplicationContext(), LoginAct.class));
//                                } else {
//                                    Toast.makeText(MyAccountInfoAct.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });

//            case R.id.tv_password_change: {
//                Intent intent = new Intent(getApplicationContext(), PasswordChangeAct.class);
//                startActivity(intent);
//                break;
//
//            }


            /** 회원탈퇴 **/
            case R.id.tv_remove: {
                Intent intent = new Intent(getApplicationContext(), RemoveAccountAct.class);
                intent.putExtra("type", "account");
                startActivity(intent);
                break;
            }

            /**
             * 로그아웃 **/
            case R.id.tv_logout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountAct.this);
                builder.setTitle("로그아웃").setMessage("정말 로그아웃 하시겠습니까?");
                builder.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 로그아웃 수행
                        clearUserFCMData();    // 서버 db로 관리되는 로그인되어있는 계정의 FCM Token 초기화
                        String id = SharedManager.read(SharedManager.LOGIN_ID,"");
                        SharedManager.clear(); // 내부 db로 관리되는 로그인정보 초기화
                        SharedManager.write(SharedManager.LOGIN_ID,id);
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        currentFirebaseUser.reload();

                        Toast.makeText(context, "성공적으로 로그아웃 처리 되었습니다", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(context, LoginAct.class));  // 다시 로그인 액트로 귀환
                        finish(); // 현재 화면 종료까지
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 아무것도 일어나지 않음
                    }
                });

                builder.create().show();  // builder 변수 show() 메소드 실행안하면 안보임.
                break;
            }
        }
    }

    /**
     * 로그아웃 이후의 시점에서는 FCM 메시징을 받을 이유가 없으니 서버에 귀속되어있는 FCM Token도 제거해준다.
     */
    private void clearUserFCMData()
    {

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {

            Toast.makeText(context, "사용자의 정보가 이미 모두 삭제되었습니다. 다시 회원가입을 진행해주세요.", Toast.LENGTH_SHORT).show();

            SharedManager.clear(); // 내부 db로 관리되는 로그인정보 초기화
            startActivity(new Intent(AccountAct.this, LoginAct.class));  // 다시 로그인 액트로 귀환
            finish(); // 현재 화면 종료까지
        }

        else
        {
            final String curUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            databaseReference.child("userInfo").child(curUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {

                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        userModel.setFcmToken(null);
                        databaseReference.child("userInfo").child(curUid).setValue(userModel);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void revokeAccess() {
        mAuth.getCurrentUser().delete();
    }
}
