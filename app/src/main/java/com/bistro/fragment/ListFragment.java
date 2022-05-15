package com.bistro.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.bistro.R;
import com.bistro.activity.SearchAct;
import com.bistro.activity.WritePostAct;
import com.bistro.adapter.BulletinAdapter;
import com.bistro.database.SharedManager;
import com.bistro.model.PostModel;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.melnykov.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.util.ArrayList;
/**
 * author ban8826
 * summary 메인 화면 프래그먼트
 */
public class ListFragment extends Fragment implements  View.OnClickListener, Serializable {

    private RecyclerView recycler;
    public BulletinAdapter bulletinAdapter;
    private DatabaseReference databaseReference;

    private ArrayList<PostModel> list_post;       // real list
    private ArrayList<PostModel> mLstPostGet;    // 리스트를 20개씩만 담을 임시 array
    private ArrayList<String>    list_key; // 마지막에 불러온 item 을 find 하기 위한 임시 array
    private String               mStrOldBoardId; // 마지막에 불러온 item id
    private String randomKey;
    private ProgressDialog mProgressDialog;     // 로딩 프로그레스
    private TextView tv_like_order, tv_recent_order, mTvMsgEmpty;               // 게시글이 0개일 때 empty text
    private View view;
    private ProgressBar loadingProgress;

    private StorageReference storageReference;
    private ArrayList<Uri> list_uri;

    // 검색 버튼
    private ImageView iv_search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_list, container, false);
        // onCreateView 에서 findviewbyid()를 사용하면 충돌이 일어날수 있기에 사용하면 안된다
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // onViewCreated() 에서는 뷰가 모두 완전히 생성되었음을 의미하기 때문에 여기서 findViewByid() 함수를 사용해야 한다
        this.view = view;
        startLoadingProgress();
        setInitialize(view);
    }

    private void setInitialize(View view) {
        mLstPostGet = new ArrayList<>();
        list_key = new ArrayList<>();
        list_post = new ArrayList<>();

        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        iv_search = view.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("bistro");

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bistro-5bc79.appspot.com");
        list_uri = new ArrayList<>();

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
                    if (mLstPostGet.size() == 20)
                        setProgressDialog("더 불러오는 중...", true);

                }
            }
        });


        // init fab button
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
//        fab.attachToRecyclerView(mRvPromise);
        fab.show();

        getFirebaseBoardList();
    }

    public void getFirebaseBoardList() {
        // load firebase db listk

        databaseReference.child("postInfo").orderByChild("date").addValueEventListener(new ValueEventListener() {
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

                    /** Storage에서 이미지를 가져온 다음에 어댑터에 전달해서 리스트의 글과 사진이 함께 화면에 보이도록.
                     * 이미지 개수가 많아지면 개수를 정해서 몇개씩만 로드하고 아래로 스크롤 하면 추가로 보이도록 코딩해야할것
                     */
                    for(PostModel postModel : list_post)
                    {
                        String nickName = postModel.getNickName(); // 이 부분을 쉐어드에서 유저아이디를 가져와서 리스트 안보였었음.
                        String fileName = nickName + postModel.getDate();
                        String name_img = nickName + '1';



//                        storageReference.child(fileName).child(name_img).getDownloadUrl().addOnSuccessListener(uri -> {
//
//                            //다운로드 URL이 파라미터로 전달되어 옴.
//                            list_uri.add(uri);
//
//                            if(list_post.size() == list_uri.size())
//                            {

                            bulletinAdapter = new BulletinAdapter(ListFragment.this, list_post, list_uri, list_key, "list");
                            recycler.setAdapter(bulletinAdapter);
                            bulletinAdapter.notifyDataSetChanged();
//                   setProgressDialog(null, false);


                        finishLoadingProgress();
//                            }
//                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(getClass().getSimpleName(), "onCancelled Database error", error.toException().fillInStackTrace());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (bulletinAdapter != null)  bulletinAdapter.notifyDataSetChanged();

    }

    // register for activity result (게시글 상세에서 돌아올 때 이곳 수행)
//    private final ActivityResultLauncher<Intent> getPostDetailResult = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if ( result.getResultCode() == RESULT_OK) {
//                    // 조회 실패하여 아무것도 가져오기 못했음
//                    if (mAdapter.getItemCount() == 1) {
//                        setListAdapterWithData(new ArrayList<>());
//                    } else {
//                        getFirebaseBoardList();
//                    }
//                }
//            });
//
//    // register for activity result (게시글 작성에서 돌아올 때 이곳 수행)
//    private final ActivityResultLauncher<Intent> getPostEditResult = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if ( result.getResultCode() == RESULT_OK) {
//                    getFirebaseBoardList();
//                }
//            });


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                // 플로팅 버튼
                Intent contentIntent = new Intent(getContext(), WritePostAct.class);
                startActivity(contentIntent);
                break;

                // 공감순 버튼
            case R.id.tv_like:

                databaseReference.child("postInfo").orderByChild("like").addValueEventListener(new ValueEventListener() {
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

                            bulletinAdapter = new BulletinAdapter(ListFragment.this, list_post, list_key, "list");
                            bulletinAdapter.setListener(new ListFragment.LikeInterface());
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

                break;

                // 최근순 버튼
            case R.id.tv_recent:

                getFirebaseBoardList();

                break;

            case R.id.iv_search:

                Intent intent = new Intent(getContext(), SearchAct.class);
                startActivity(intent);

                break;
        }
    }

    private void setProgressDialog(String _strMsg, boolean _isProgress) {
        if (_strMsg != null)
            mProgressDialog.setMessage(_strMsg);

        if( _isProgress )
            mProgressDialog.show();
        else
            mProgressDialog.hide();
    }
    /**
     * 로딩 프로그레스바를 활성화 시킨다.
     */
    private void startLoadingProgress() {

        loadingProgress = view.findViewById(R.id.loadingProgress);
        loadingProgress.setVisibility(View.VISIBLE);
    }


    /**
     * 로딩 프로그레스바를 비활성화 시킨다.
     */
    private void finishLoadingProgress() {
        loadingProgress = view.findViewById(R.id.loadingProgress);
        loadingProgress.setVisibility(View.INVISIBLE);
    }


    public class LikeInterface implements Serializable
    {
        public void activate() {
            getFirebaseBoardList();
        }
    }
}
