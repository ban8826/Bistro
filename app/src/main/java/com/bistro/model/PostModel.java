package com.bistro.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * summary 게시판 글 1개의 정보를 담는 클래스.
 */
public class PostModel implements Serializable {

    private String title;       // 제목
    private String content;     // 내용
    private String date;        // 게시일자
    private String storeName;        // 가게이름, 상호명
    private String nickName;    // 닉네임
    private String authToken;   // 계정 토큰
    private String fcmToken;    // 푸시 토큰
    private String userId;      // 사용자 ID
    private String id;          // 게시글 ID (랜덤생성키)
    private String menu;
    private String address;
    // 임시
    private String click; // 조회수
    private String like;  // 공감수

//    private HashMap<String, PostLikeModel> list_list;
    private KakaoPlaceModel.PoiPlace poiPlace;

    // 싫어요는 일단 삭제하기로
//    private String dislike;

    public PostModel() { }

    public void setLike(String like) {
        this.like = like;
    }

    public String getLike() {
        return like;
    }

//    public void setList_list(HashMap<String, PostLikeModel> list_list) {
//        this.list_list = list_list;
//    }
//
//    public HashMap<String, PostLikeModel> getList_list() {
//        return list_list;
//    }

    //    public void setDislike(String dislike) {
//        this.dislike = dislike;
//    }
//
//    public String getDislike() {
//        return dislike;
//    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getClick() {
        return click;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMenu() {
        return menu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public KakaoPlaceModel.PoiPlace getPoiPlace() {
        return poiPlace;
    }

    public void setPoiPlace(KakaoPlaceModel.PoiPlace poiPlace) {
        this.poiPlace = poiPlace;
    }
}
