package com.bistro.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.bistro.database.SharedManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 *
 * 계정삭제 페이지
 */
public class RemoveAccountAct extends AppCompatActivity {


    private DatabaseReference databaseReference;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_remove_account);

        databaseReference = FirebaseDatabase.getInstance().getReference("bistro");

        // 랜덤키 아니면 바꿀것
        token = SharedManager.read(SharedManager.AUTH_TOKEN,"");

        Button btn_remove = findViewById(R.id.btn_remove_confirm);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context;
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setCancelable(true);
                builder.setTitle("정말 회원을 탈퇴하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // 파베의 userInfo에서 계정 정보 삭제
                        databaseReference.child("userInfo").child(token).setValue(null);


                        /**  파베의 이메일 계정 삭제  **/
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RemoveAccountAct.this, "회원정보가 모두 삭제되었습니다. 로그인을 위해선 다시 회원가입해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RemoveAccountAct.this, LoginAct.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.create().cancel();
                    }
                });
                builder.create().show();
            }
        });

    }
}
