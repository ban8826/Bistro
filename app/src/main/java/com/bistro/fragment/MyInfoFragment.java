package com.bistro.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.bistro.R;
import com.bistro.activity.AccountAct;
import com.bistro.activity.LoginAct;
import com.bistro.activity.SettingAct;
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


public class MyInfoFragment extends Fragment implements View.OnClickListener {

    private TextView tv_nickName, tv_rank, tv_like, tv_privacy, tv_logout;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInitialize(view);
    }

    private void setInitialize(View _view) {

        databaseReference = FirebaseDatabase.getInstance().getReference("bistro");
        String authToken = SharedManager.read(SharedManager.AUTH_TOKEN, "");

        _view.findViewById(R.id.btn_setting).setOnClickListener(this);
        tv_nickName = _view.findViewById(R.id.tv_nickName_value);
        tv_rank = _view.findViewById(R.id.tv_rank);
        tv_nickName.setText(SharedManager.read(SharedManager.USER_NAME,""));
        tv_like = _view.findViewById(R.id.tv_like_value);

        tv_nickName.setOnClickListener(this);
        tv_rank.setOnClickListener(this);

        databaseReference.child("userInfo").child(authToken).child("like").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String like = (String) snapshot.getValue();
                tv_like.setText(like);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting:
                // 설정 화면
                Intent settingIntent = new Intent(getContext(), SettingAct.class);
                getLogoutResult.launch(settingIntent);
                break;

            case R.id.tv_nickName_value:
                Intent settingIntent1 = new Intent(getContext(), AccountAct.class);
                getLogoutResult.launch(settingIntent1);
                break;

            case R.id.tv_rank:

                break;


            case R.id.tv_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                        Toast.makeText(getContext(), "성공적으로 로그아웃 처리 되었습니다", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getContext(), LoginAct.class));  // 다시 로그인 액트로 귀환
                        getActivity().finish(); // 현재 화면 종료까지
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 아무것도 일어나지 않음
                    }
                });

                builder.show();  // builder 변수 show() 메소드 실행안하면 안보임.
                break;
        }
    }

    // register for activity result
    private final ActivityResultLauncher<Intent> getLogoutResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if ( result.getResultCode() == RESULT_OK) {
                    // return to LoginAct
                    requireActivity().finish();
                }
            });


    /**
     * 로그아웃 이후의 시점에서는 FCM 메시징을 받을 이유가 없으니 서버에 귀속되어있는 FCM Token도 제거해준다.
     */
    private void clearUserFCMData()
    {

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {

            Toast.makeText(getContext(), "사용자의 정보가 이미 모두 삭제되었습니다. 다시 회원가입을 진행해주세요.",Toast.LENGTH_LONG).show();

            SharedManager.clear(); // 내부 db로 관리되는 로그인정보 초기화
            startActivity(new Intent(getContext(), LoginAct.class));  // 다시 로그인 액트로 귀환
            getActivity().finish(); // 현재 화면 종료까지
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
                        databaseReference.child("userInfo").child(curUid).setValue(userModel); // Uid를 지운 UserAccount를 다시 해당 유저의 정보에 재저장.

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
