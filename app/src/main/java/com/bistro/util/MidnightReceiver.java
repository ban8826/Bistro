package com.bistro.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bistro.database.SharedManager;

public class MidnightReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 작성횟수를 0으로 만드는 부분
        SharedManager.write(SharedManager.WRITE_COUNT,"0");

        Toast.makeText(context, "자정실행" + SharedManager.read(SharedManager.WRITE_COUNT,""), Toast.LENGTH_SHORT).show();

    }
}
