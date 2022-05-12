package com.bistro.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bistro.R;
import com.bistro.activity.SettingAct;


public class MyInfoFragment extends Fragment implements View.OnClickListener {

    private TextView tv_nickName, tv_rank, tv_setting, tv_privacy;


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
        _view.findViewById(R.id.btn_setting).setOnClickListener(this);
        tv_nickName = _view.findViewById(R.id.tv_nickName);
        tv_rank = _view.findViewById(R.id.tv_rank);
        tv_setting = _view.findViewById(R.id.tv_setting);
        tv_privacy = _view.findViewById(R.id.tv_privacy);

        tv_nickName.setOnClickListener(this);
        tv_rank.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        tv_privacy.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting:
                // 설정 화면
                Intent settingIntent = new Intent(getContext(), SettingAct.class);
                getLogoutResult.launch(settingIntent);
                break;

            case R.id.tv_nickName:

                break;

            case R.id.tv_rank:

                break;

            case R.id.tv_setting:

                break;

            case R.id.tv_privacy:

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
}
