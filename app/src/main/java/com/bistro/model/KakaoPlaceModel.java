package com.bistro.model;

import java.util.ArrayList;

/**
 * 날짜   : 2022/05/11
 * 작성자 : 한재훈
 * 내용   : 카카오 로컬 API 결과값 모델
 */
public class KakaoPlaceModel {

    public class PoiResult {
        private PoiMeta                 poiMeta;
        private ArrayList<PoiPlace>     poiPlace;

        //region getter & setter
        public PoiMeta getPoiMeta() {
            return poiMeta;
        }

        public void setPoiMeta(PoiMeta poiMeta) {
            this.poiMeta = poiMeta;
        }
        //endregion
    }

    public class PoiMeta {
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

    public class PoiRegionInfo {
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

    public class PoiPlace {
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
        //endregion
    }
}
