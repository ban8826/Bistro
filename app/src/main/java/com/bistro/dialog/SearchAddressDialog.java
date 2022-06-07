package com.bistro.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bistro.R;
import com.bistro.adapter.SearchAddressAdapter;
import com.bistro.model.KakaoPlaceModel;
import com.bistro.util.RetrofitMain;

import java.util.ArrayList;

public class SearchAddressDialog extends Dialog {
    private final String TAG = "SearchAddressDialog";

    private String strName;
    private ArrayList<KakaoPlaceModel.PoiPlace> resultList;

    private EditText etName;
    private ImageView ivSearch;
    private RecyclerView rvList;

    private RetrofitMain retrofitMain;

    private final FragmentManager fm;
    private SearchAddressAdapter adapter;

    private ResultListener listener;

    public interface ResultListener {
        void onResult(KakaoPlaceModel.PoiPlace place, String name);
    }

    public void setResultListener(ResultListener listener) {
        this.listener = listener;
    }

    public SearchAddressDialog(@NonNull Context context, FragmentManager fm) {
        super(context);
        this.fm = fm;
    }

    public SearchAddressDialog(@NonNull Context context, FragmentManager fm, String strName) {
        super(context);
        this.fm = fm;
        this.strName = strName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.dialog_search_address);

        resultList = new ArrayList<>();

        etName = findViewById(R.id.d_search_address_et_name);
        ivSearch = findViewById(R.id.d_search_address_iv_search);
        rvList = findViewById(R.id.d_search_address_rv_result);

        retrofitMain = new RetrofitMain(getContext());
        retrofitMain.setResultListener(model -> {
            resultList = model.getPoiPlace();
            adapter = new SearchAddressAdapter(SearchAddressDialog.this, fm, resultList);
            rvList.setAdapter(adapter);

            for (KakaoPlaceModel.PoiPlace place : resultList) {
                Log.d(TAG, place.toString() + "\n");
            }
        });

//        if (strName != null && !strName.isEmpty()) {
//            etName.setText(strName);
//            retrofitMain.getSearchPoiFood(strName);
//        }

        ivSearch.setOnClickListener(view -> {
            // 검색
            String name = etName.getText().toString();
            if (name.equals("")) {
                Toast.makeText(getContext(), "상호명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                resultList.clear();
                retrofitMain.getSearchPoi(name);
            }
        }
        );
    }

    public void onResult(KakaoPlaceModel.PoiPlace place) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    Log.d(TAG, "onResult !! listener onResult !!");
                    listener.onResult(place,etName.getText().toString());
                    dismiss();
                }
            }
        });

    }
}
