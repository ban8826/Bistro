package com.bistro.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 를 관리할 수 있는 유틸 클래스이다.
 * 사용을 원하는 액티비티에서 SharedManager.init(getApplicationContext()); 로 초기화 한 이후에
 * 예시 - SharedManager.write(SharedManager.ID, "저장하고 싶은 값"); 의 형태로 DB에 쓰고
 * SharedManager.read(SharedManager.ID, "저장된 기존 값이 없을때 default value"); 로 읽어 올 수 있다.
 */

public class SharedManager
{
    private static SharedPreferences mSharedPref;
    public static final String LOGIN_TYPE            = "LOGIN_TYPE";            // 현재 로그인 한 TYPE (파베로그인 0, 카카오 로그인 1)
    public static final String LOGIN_ID              = "LOGIN_ID";              // 현재 로그인 한 ID Token
    public static final String AUTH_TOKEN            = "AUTH_TOKEN";            // 파베에서 랜덤키, 현재 ID Token 값 (파베 로그인인 경우 Uid, 카카오 로그인인 경우 자체 토큰)
    public static final String USER_NAME             = "USER_NAME";             // 현재 로그인 한 닉네임
    public static final String FCM_TOKEN             = "FCM_TOKEN";             // 로그인이 될 때마다 새롭게 받아오는 FCM Token 데이터이다.
    public static final String PROFILE_STORAGE_PATH  = "PROFILE_STORAGE_PATH";  // 이미지 저장 경로 (Firebase Storage)
    public static final String WRITE_COUNT           = "WRITE_COUNT";
    public static final String TODAY                 = "TODAY";                 // 오늘 날짜 저장 (같은 상점은 하루에 글 1개 작성을 위해)

    private SharedManager() { }

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).apply();
    }

    public static Long read(String key, long defValue) {
        return mSharedPref.getLong(key, defValue);
    }

    public static void write(String key, Long value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putLong(key, value).apply();
    }

    public static void clear() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }
}
