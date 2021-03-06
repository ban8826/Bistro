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

            /** ???????????? **/
            case R.id.iv_back_arrow: {
                finish();
                break;
            }

//             /** ???????????? ?????? **/
            /** ??????????????? ?????? ???????????? ??????????????? ?????? ???????????? ?????? **/
//                String emailAddress = "ban8826@naver.";
//               FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//                firebaseAuth.sendPasswordResetEmail(emailAddress)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    Toast.makeText(MyAccountInfoAct.this, "???????????? ???????????????.", Toast.LENGTH_LONG).show();
//                                    finish();
//                                    startActivity(new Intent(getApplicationContext(), LoginAct.class));
//                                } else {
//                                    Toast.makeText(MyAccountInfoAct.this, "?????? ????????? ??????!", Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });

//            case R.id.tv_password_change: {
//                Intent intent = new Intent(getApplicationContext(), PasswordChangeAct.class);
//                startActivity(intent);
//                break;
//
//            }


            /** ???????????? **/
            case R.id.tv_remove: {
                Intent intent = new Intent(getApplicationContext(), RemoveAccountAct.class);
                intent.putExtra("type", "account");
                startActivity(intent);
                break;
            }

            /**
             * ???????????? **/
            case R.id.tv_logout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountAct.this);
                builder.setTitle("????????????").setMessage("?????? ???????????? ???????????????????");
                builder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // ???????????? ??????
                        clearUserFCMData();    // ?????? db??? ???????????? ????????????????????? ????????? FCM Token ?????????
                        String id = SharedManager.read(SharedManager.LOGIN_ID,"");
                        SharedManager.clear(); // ?????? db??? ???????????? ??????????????? ?????????
                        SharedManager.write(SharedManager.LOGIN_ID,id);
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        currentFirebaseUser.reload();

                        Toast.makeText(context, "??????????????? ???????????? ?????? ???????????????", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(context, LoginAct.class));  // ?????? ????????? ????????? ??????
                        finish(); // ?????? ?????? ????????????
                    }
                });

                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // ???????????? ???????????? ??????
                    }
                });

                builder.create().show();  // builder ?????? show() ????????? ??????????????? ?????????.
                break;
            }
        }
    }

    /**
     * ???????????? ????????? ??????????????? FCM ???????????? ?????? ????????? ????????? ????????? ?????????????????? FCM Token??? ???????????????.
     */
    private void clearUserFCMData()
    {

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {

            Toast.makeText(context, "???????????? ????????? ?????? ?????? ?????????????????????. ?????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();

            SharedManager.clear(); // ?????? db??? ???????????? ??????????????? ?????????
            startActivity(new Intent(AccountAct.this, LoginAct.class));  // ?????? ????????? ????????? ??????
            finish(); // ?????? ?????? ????????????
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
