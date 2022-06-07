package com.bistro.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.bistro.model.KakaoPlaceModel;
import com.bistro.model.PostModel;
import com.bistro.util.RetrofitMain;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchAct extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "SearchAct";

    private ImageView iv_back, ivSearch;
    private EditText etKeyword;

    private DatabaseReference dbRef;
    private RetrofitMain retrofitMain;

    private ArrayList<KakaoPlaceModel.PoiPlace> resultList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);

        resultList = new ArrayList<>();

        dbRef = FirebaseDatabase.getInstance().getReference("bistro").child("postInfo");

        retrofitMain = new RetrofitMain(this);
        retrofitMain.setResultListener(new RetrofitMain.ResultListener() {
            @Override
            public void onResult(KakaoPlaceModel.PoiResult result) {
                //TODO : 카카오 api로 검색 -> firebase 검색 -> 겹치는 것 리스트에 표출
                dbRef.orderByChild("storeName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<PostModel> modelList = new ArrayList<>(); // MainAct로 전달할 리스트

                        for (KakaoPlaceModel.PoiPlace place : result.getPoiPlace()) {

                            for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                                if (singleSnapshot != null) {
                                    PostModel model = singleSnapshot.getValue(PostModel.class);
                                    Log.d(TAG, "상호명 비교 placeName : " + place.getPlace_name() + ", key : " + model.getStoreName());

                                    if (place.getPlace_name().equals(model.getStoreName())) {
                                        Log.d(TAG, "key값 같음 !! " + model.getStoreName());
                                        modelList.add(model);
                                    }
                                }
                            }
                        }

                        //TODO : MainAct Intent로 호출
                        //Intent intent ...
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        iv_back = findViewById(R.id.iv_back_arrow);
        ivSearch = findViewById(R.id.d_search_address_iv_search);

        etKeyword = findViewById(R.id.a_search_et_keyword);

        iv_back.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;

            case R.id.d_search_address_iv_search:
                // 검색
                String name = etKeyword.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(SearchAct.this, "상호명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    resultList.clear();
                    retrofitMain.getSearchPoi(name);
                }
                break;
        }
    }
}
