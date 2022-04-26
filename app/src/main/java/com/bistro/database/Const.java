package com.bistro.database;

/**
 * 공통 상수 정의
 */
public class Const {

    // ========================================= STRING =========================================== //

    // [ About ] Firebase Storage URL
    public static final String FIREBASE_STORAGE_URL     = "gs://ricemate-bf576.appspot.com";

    // [ About ] Firebase Key
    public static final String FIREBASE_KEY_ROOT        = "RiceMate";
    public static final String FIREBASE_KEY_USER_INFO   = "UserInfo";
    public static final String FIREBASE_KEY_POST_INFO   = "PostInfo";

    // [ About ] Firebase UserInfo Column
    public final static String FIREBASE_KEY_PROFILE_URL = "profileUrl";
    public final static String FIREBASE_KEY_USER_NAME   = "userName";

    // [ About ] FCM Type

    // [ About ] Intent Key
    public final static String INTENT_KEY_ADDRESS       = "address";
    public final static String INTENT_KEY_POST_MODEL    = "post_model";

    // ========================================= INTEGER =========================================== //

    // [ About ] Login Type (e-mail, kakao)
    public static final int LOGIN_TYPE_FIREBASE_EMAIL = 0;
    public static final int LOGIN_TYPE_KAKAO          = 1;

}
