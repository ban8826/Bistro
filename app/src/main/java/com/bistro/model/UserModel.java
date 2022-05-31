package com.bistro.model;

import java.util.HashMap;

public class UserModel {
    private String id;              // 사용자 아이디
    private String name;            // 닉네임
    private String profileImgPath;  // 프로필 이미지 경로
    private String fcmToken;        // 사용자 푸시토큰
    private String authToken;       // 계정토큰
    private String area;            // 동네 인증 정보
    private String introduce;       // 자기소개 한마디, 칭호
    private HashMap<String, PostModel> favorite;
    private String like;
    public UserModel() { }

    public String getId() {
        return id;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getLike() {
        return like;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImgPath() {
        return profileImgPath;
    }

    public void setFavorite(HashMap<String, PostModel> favorite) {
        this.favorite = favorite;
    }


    public HashMap<String, PostModel> getFavorite() {
        return favorite;
    }

    public void setProfileImgPath(String profileImgPath) {
        this.profileImgPath = profileImgPath;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }


}
