package com.bistro.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 날짜   : 2022/05/11
 * 작성자 : 한재훈
 * 내용   : 카카오 로컬 API 결과값 모델
 */
public class KakaoPlaceModel {

    public static class PoiResult {
        @SerializedName("meta")
        private PoiMeta                 poiMeta;
        @SerializedName("documents")
        private ArrayList<PoiPlace> poiPlaceList;

        //region getter & setter
        public PoiMeta getPoiMeta() {
            return poiMeta;
        }

        public void setPoiMeta(PoiMeta poiMeta) {
            this.poiMeta = poiMeta;
        }

        public ArrayList<PoiPlace> getPoiPlaceList() {
            return poiPlaceList;
        }

        public void setPoiPlaceList(ArrayList<PoiPlace> poiPlaceList) {
            this.poiPlaceList = poiPlaceList;
        }

        //endregion
    }

    public static class PoiMeta {
        private int             totalCount;             // 검색어에 검색된 문서 수
        private int             pageableCount;          // total_count 중 노출 가능 문서 수 (최대: 45)
        private boolean         isEnd;                  // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 다음 요청 시 page 값을 증가시켜 다음 페이지 요청 가능
        private PoiRegionInfo   sameName;               // 질의어의 지역 및 키워드 분석 정보

        //region getter & setter
        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPageableCount() {
            return pageableCount;
        }

        public void setPageableCount(int pageableCount) {
            this.pageableCount = pageableCount;
        }

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public PoiRegionInfo getSameName() {
            return sameName;
        }

        public void setSameName(PoiRegionInfo sameName) {
            this.sameName = sameName;
        }
        //endregion
    }

    public static class PoiRegionInfo {
        private String[]        region;                 // 전체 지번 주소 또는 전체 도로명 주소, 입력에 따라 결정됨
        private String          keyword;                // address_name의 값의 타입(Type), 다음 중 하나 REGION(지명), ROAD(도로명), REGION_ADDR(지번 주소), ROAD_ADDR(도로명 주소)
        private String          selectedRegion;         // X 좌표값, 경위도인 경우 경도(longitude)

        //region getter & setter
        public String[] getRegion() {
            return region;
        }

        public void setRegion(String[] region) {
            this.region = region;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getSelectedRegion() {
            return selectedRegion;
        }

        public void setSelectedRegion(String selectedRegion) {
            this.selectedRegion = selectedRegion;
        }
        //endregion
    }

    public static class PoiPlace implements Serializable {
        private String          id;                     // 장소 ID
        private String          place_name;             // 장소명, 업체명
        private String          category_name;          // 장소명, 업체명
        private String          category_group_code;    // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
        private String          category_group_name;    // 중요 카테고리만 그룹핑한 카테고리 그룹명
        private String          phone;                  // 전화번호
        private String          address_name;           // 전체 지번 주소
        private String          road_address_name;      // 전체 도로명 주소
        private String          x;                      // X 좌표값, 경위도인 경우 경도(longitude)
        private String          y;                      // Y 좌표값, 경위도인 경우 위도(latitude)
        private String          place_url;              // 장소 상세페이지 URL
        private String          distance;               // 중심좌표까지의 거리 (단, x,y 파라미터를 준 경우에만 존재) 단위 meter
        private boolean         isClick;

        public PoiPlace() { }

        @Override
        public String toString() {
            return "PoiPlace{" +
                    "id='" + id + '\'' +
                    ", place_name='" + place_name + '\'' +
                    ", category_name='" + category_name + '\'' +
                    ", category_group_code='" + category_group_code + '\'' +
                    ", category_group_name='" + category_group_name + '\'' +
                    ", phone='" + phone + '\'' +
                    ", address_name='" + address_name + '\'' +
                    ", road_address_name='" + road_address_name + '\'' +
                    ", lon='" + x + '\'' +
                    ", lat='" + y + '\'' +
                    ", place_url='" + place_url + '\'' +
                    ", distance='" + distance + '\'' +
                    ", isCLick='" + isClick + '\'' +
                    '}';
        }

        //region getter & setter
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlace_name() {
            return place_name;
        }

        public void setPlace_name(String place_name) {
            this.place_name = place_name;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public String getCategory_group_code() {
            return category_group_code;
        }

        public void setCategory_group_code(String category_group_code) {
            this.category_group_code = category_group_code;
        }

        public String getCategory_group_name() {
            return category_group_name;
        }

        public void setCategory_group_name(String category_group_name) {
            this.category_group_name = category_group_name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress_name() {
            return address_name;
        }

        public void setAddress_name(String address_name) {
            this.address_name = address_name;
        }

        public String getRoad_address_name() {
            return road_address_name;
        }

        public void setRoad_address_name(String road_address_name) {
            this.road_address_name = road_address_name;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getPlace_url() {
            return place_url;
        }

        public void setPlace_url(String place_url) {
            this.place_url = place_url;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public boolean isClick() {
            return isClick;
        }

        public void setClick(boolean click) {
            isClick = click;
        }

        //endregion
    }

    public static class RegionResult {
        @SerializedName("meta")
        private RegionMeta                  regionMeta;
        @SerializedName("documents")
        private ArrayList<RegionInfo>       regionList;

        //region getter & setter
        public RegionMeta getRegionMeta() {
            return regionMeta;
        }

        public void setRegionMeta(RegionMeta regionMeta) {
            this.regionMeta = regionMeta;
        }

        public ArrayList<RegionInfo> getRegionList() {
            return regionList;
        }

        public void setRegionList(ArrayList<RegionInfo> regionList) {
            this.regionList = regionList;
        }
        //endregion
    }

    public static class RegionMeta {
        private int totalCount;

        //region getter & setter
        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
        //endregion
    }

    public static class RegionInfo {
        @SerializedName("region_type")
        private String regionType;          // H(행정동) 또는 B(법정동)

        @SerializedName("address_name")
        private String addressName;         // 전체 지역 명칭

        @SerializedName("region_1depth_name")
        private String region1DepthName;    // 지역 1Depth, 시도 단위

        @SerializedName("region_2depth_name")
        private String region2DepthName;    // 지역 2Depth, 구 단위

        @SerializedName("region_3depth_name")
        private String region3DepthName;    // 지역 3Depth, 동 단위

        @SerializedName("region_4depth_name")
        private String region4DepthName;    // 지역 4Depth, region_type이 법정동이며, 리 영역인 경우만 존재

        @SerializedName("code")
        private String code;                // region 코드

        @SerializedName("x")
        private double x;                   // X 좌표값, 경위도인 경우 경도(longitude)

        @SerializedName("y")
        private double y;                   // Y 좌표값, 경위도인 경우 위도(latitude)

        //region getter & setter
        public String getRegionType() {
            return regionType;
        }

        public void setRegionType(String regionType) {
            this.regionType = regionType;
        }

        public String getAddressName() {
            return addressName;
        }

        public void setAddressName(String addressName) {
            this.addressName = addressName;
        }

        public String getRegion1DepthName() {
            return region1DepthName;
        }

        public void setRegion1DepthName(String region1DepthName) {
            this.region1DepthName = region1DepthName;
        }

        public String getRegion2DepthName() {
            return region2DepthName;
        }

        public void setRegion2DepthName(String region2DepthName) {
            this.region2DepthName = region2DepthName;
        }

        public String getRegion3DepthName() {
            return region3DepthName;
        }

        public void setRegion3DepthName(String region3DepthName) {
            this.region3DepthName = region3DepthName;
        }

        public String getRegion4DepthName() {
            return region4DepthName;
        }

        public void setRegion4DepthName(String region4DepthName) {
            this.region4DepthName = region4DepthName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
        //endregion
    }

}
