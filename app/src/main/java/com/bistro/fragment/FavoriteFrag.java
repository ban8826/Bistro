package com.bistro.fragment;

import android.app.ProgressDialog;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bistro.R;
import com.bistro.adapter.BulletinAdapter;
import com.bistro.database.SharedManager;
import com.bistro.model.PostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;


/**
 * author hongdroid94
 * summary 채팅 프래그먼트
 */
public class FavoriteFrag extends Fragment implements View.OnClickListener {

    private RecyclerView recycler;
    private BulletinAdapter bulletinAdapter;
    private DatabaseReference databaseReference;

    private ArrayList<PostModel> list_post;       // real list
    private ArrayList<PostModel> mLstPostGet;    // 리스트를 20개씩만 담을 임시 array
    private ArrayList<String> list_key; // 마지막에 불러온 item 을 find 하기 위한 임시 array
    private String               mStrOldBoardId; // 마지막에 불러온 item id
    private String userKey;
    private ProgressDialog mProgressDialog;     // 로딩 프로그레스
    private TextView tv_like_order, tv_recent_order, mTvMsgEmpty;               // 게시글이 0개일 때 empty text


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInitialize(view);
    }

    private void setInitialize(View view) {
        mLstPostGet = new ArrayList<>();
        list_key = new ArrayList<>();
        list_post = new ArrayList<>();

        userKey = SharedManager.read(SharedManager.AUTH_TOKEN,"");

        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("bistro");

        // init progress dialog
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

        tv_like_order = view.findViewById(R.id.tv_like);
        tv_like_order.setOnClickListener(this);
        tv_recent_order = view.findViewById(R.id.tv_recent);
        tv_recent_order.setOnClickListener(this);

        // load more function..
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // check last list item
                if (!recycler.canScrollVertically(RecyclerView.VERTICAL)) {
                    if (mLstPostGet.size() == 20) {
//                        setProgressDialog("더 불러오는 중...", true);
                    }
                }
            }
        });

        getFirebaseBoardList();
    }

    private void getFirebaseBoardList() {
        // load firebase db list

        databaseReference.child("userInfo").child(userKey).child("favorite").orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list_post.clear();
                list_key.clear();
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel postModel = snapshot.getValue(PostModel.class);
                        // 최신글 역순 정렬을 위한 역순 add
                        list_post        .add(0, postModel);
                        list_key.add(0, snapshot.getKey());
                    }

                    bulletinAdapter = new BulletinAdapter(FavoriteFrag.this, list_post, list_key, "favorite");
                    recycler.setAdapter(bulletinAdapter);
                    bulletinAdapter.notifyDataSetChanged();
//                   setProgressDialog(null, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(getClass().getSimpleName(), "onCancelled Database error", error.toException().fillInStackTrace());
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_setting:
//                // 설정 화면
//                Intent settingIntent = new Intent(getContext(), SettingAct.class);
//                getLogoutResult.launch(settingIntent);
//                break;
        }


    }

}
