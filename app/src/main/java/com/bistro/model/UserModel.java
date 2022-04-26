package com.bistro.model;

public class UserModel {
    private String id;              // 사용자 아이디
    private String name;            // 닉네임
    private String profileImgPath;  // 프로필 이미지 경로
    private String fcmToken;        // 사용자 푸시토큰
    private String authToken;       // 계정토큰
    private String area;            // 동네 인증 정보
    private String mbti;            // MBTI 정보
    private String introduce;       // 자기소개 한마디, 칭호
    private String favorite;

    public UserModel() { }

    public String getId() {
        return id;
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

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getFavorite() {
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

    public String getMbti() {
        return mbti;
    }

    public void setMbti(String mbti) {
        this.mbti = mbti;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }


}
