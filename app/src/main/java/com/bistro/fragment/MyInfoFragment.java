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
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private TextView tv_nickName, tv_verify, tv_like, tv_privacy, tv_logout;
    private DatabaseReference databaseReference;
    private ConstraintLayout const_nickName;
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

        _view.findViewById(R.id.const_setting).setOnClickListener(this);
        tv_nickName = _view.findViewById(R.id.tv_nickName_value);
        tv_verify = _view.findViewById(R.id.tv_verify);
        tv_nickName.setText(SharedManager.read(SharedManager.USER_NAME,""));
        tv_like = _view.findViewById(R.id.tv_like_value);
        const_nickName = _view.findViewById(R.id.const_profile);
        const_nickName.setOnClickListener(this);

        tv_verify.setOnClickListener(this);

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
            case R.id.const_setting:
                // ?????? ??????
                Intent settingIntent = new Intent(getContext(), SettingAct.class);
                getLogoutResult.launch(settingIntent);
                break;

            case R.id.const_profile:
                Intent settingIntent1 = new Intent(getContext(), AccountAct.class);
                getLogoutResult.launch(settingIntent1);
                break;

            case R.id.tv_verify:

                break;


            case R.id.tv_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                        Toast.makeText(getContext(), "??????????????? ???????????? ?????? ???????????????", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getContext(), LoginAct.class));  // ?????? ????????? ????????? ??????
                        getActivity().finish(); // ?????? ?????? ????????????
                    }
                });

                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // ???????????? ???????????? ??????
                    }
                });

                builder.show();  // builder ?????? show() ????????? ??????????????? ?????????.
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
     * ???????????? ????????? ??????????????? FCM ???????????? ?????? ????????? ????????? ????????? ?????????????????? FCM Token??? ???????????????.
     */
    private void clearUserFCMData()
    {

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {

            Toast.makeText(getContext(), "???????????? ????????? ?????? ?????? ?????????????????????. ?????? ??????????????? ??????????????????.",Toast.LENGTH_LONG).show();

            SharedManager.clear(); // ?????? db??? ???????????? ??????????????? ?????????
            startActivity(new Intent(getContext(), LoginAct.class));  // ?????? ????????? ????????? ??????
            getActivity().finish(); // ?????? ?????? ????????????
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
                        databaseReference.child("userInfo").child(curUid).setValue(userModel); // Uid??? ?????? UserAccount??? ?????? ?????? ????????? ????????? ?????????.

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
