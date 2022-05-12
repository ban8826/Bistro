package com.bistro.activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bistro.R;
import com.bistro.adapter.BulletinAdapter;
import com.bistro.adapter.RelatedAdapter;
import com.bistro.adapter.ViewPagerAdapter;
import com.bistro.database.SharedManager;
import com.bistro.fragment.ListFragment;
import com.bistro.fragment.NaverMapFragment;
import com.bistro.model.PostLikeModel;
import com.bistro.model.PostModel;
import com.bistro.model.UserLikeModel;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.DuplicateTaskCompletionException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import java.io.Serializable;
import java.util.ArrayList;

public class ShowPostAct extends AppCompatActivity implements View.OnClickListener {


    private ImageView iv_back, iv_like, iv_dislike, iv_favorite;
    private Context context;
    private Intent intent;
    private PostModel postModel;
    private TextView tv_title, tv_menu, tv_nickName, tv_store_name, tv_content;
    private String title, store_name, menu, content, key, userKey, post_id;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ArrayList imageList;
    private ArrayList<String> list_key;
    private ArrayList<PostModel> list_post;
    private ViewPager viewPager;
    private TabLayout tabLayout_dot;
    private ArrayList<UserLikeModel> list_user_like;
    private ArrayList<PostLikeModel> list_post_like, list_post_dislike;
    private boolean b_favorite;

    private RecyclerView rv_related;
    private RelatedAdapter relatedAdapter;

    boolean like_exist, dislike_exist;

    private Fragment mapFragment;
    private ListFragment likeInterface;
    private NaverMapFragment naverMapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_show_post);

        context = getApplicationContext();
        intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("post");
        key = postModel.getId(); // 게시글의 파베에서 랜덤키
        likeInterface = (ListFragment) intent.getSerializableExtra("a");
        post_id = postModel.getId();  /** 게시글만의 유니크한 아이디 (랜덤키 + 날짜) **/
        databaseReference = FirebaseDatabase.getInstance().getReference("bistro");
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bistro-5bc79.appspot.com");

        tv_menu = findViewById(R.id.tv_menu_value);
        tv_content = findViewById(R.id.tv_content);
        tv_title = findViewById(R.id.tv_title);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_nickName = findViewById(R.id.tv_nickName);
        tv_nickName.setText(postModel.getNickName());

        title = postModel.getTitle();
        store_name = postModel.getStoreName();
        menu = postModel.getMenu();
        content = postModel.getContent();

        userKey = SharedManager.read(SharedManager.AUTH_TOKEN, "");
        list_post = new ArrayList<>();
        list_key = new ArrayList<>();

//        tv_title.setText(title);
        tv_store_name.setText(store_name);
        tv_menu.setText(menu);
        tv_content.setText(content);

        rv_related = findViewById(R.id.rv_related);

        iv_back = findViewById(R.id.iv_back_arrow);
        iv_back.setOnClickListener(this);
        iv_like = findViewById(R.id.iv_like);
        iv_like.setOnClickListener(this);
        iv_dislike = findViewById(R.id.iv_dislike);
        iv_dislike.setOnClickListener(this);
        iv_favorite = findViewById(R.id.iv_favorite);
        iv_favorite.setOnClickListener(this);

        list_user_like = new ArrayList<>();
        list_post_like = new ArrayList<>();
        list_post_dislike = new ArrayList<>();

        viewPager = findViewById(R.id.viewPager);
        viewPager.setClipToPadding(false);
//        float density = getResources().getDisplayMetrics().density;
//        int margin = (int) (DP * density);
        viewPager.setPadding(0, 0, 0, 0);
        viewPager.setPageMargin(0);

        tabLayout_dot = findViewById(R.id.tab_layout2);
        tabLayout_dot.setupWithViewPager(viewPager, true);

        /** 스토리지에서 사진 불러오는 중요 함수 **/
        getImages();

        /** 파베에서 관련글 데이터 가져오는 부분 **/
        getFirebaseBoardList();


    }

    @Override
    protected void onResume() {
        super.onResume();

        // 네이버지도 불러오는 부분
        FragmentManager fragmentManager = getSupportFragmentManager();
//            String address = postModel.getAddress();
        // 예시.
        String address = "04058, 서울 마포구 서강로 131 (노고산동)";
        naverMapFragment = new NaverMapFragment(address);
        fragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit();
    }

    public void changeAct()
    {
        Intent intent = new Intent(ShowPostAct.this, NaverMapAct.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;

            /**
             *              좋아요 버튼
             */
            case R.id.iv_like:

                databaseReference.child("postInfo").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        /** 여기서 postModel에 다시 데이터를 담는 이유는 상세화면에 들어와서 싫어요 버튼을 먼저 눌렀을 경우
                         *   데이터가 변해있기 때문.
                         */
                        postModel = snapshot.getValue(PostModel.class);

                        String str_like = postModel.getLike();
                        int int_like = Integer.parseInt(str_like) + 1;
                        String user_id = SharedManager.read(SharedManager.LOGIN_ID, "");

                        // like_list에 추가할 PostLikeModel 인스턴스
                        PostLikeModel likeModel = new PostLikeModel();
                        likeModel.setId(user_id);

                        databaseReference.child("postInfo").child(key).child("like_list").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                list_post_like.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    PostLikeModel postLikeModel = dataSnapshot.getValue(PostLikeModel.class);
                                    list_post_like.add(postLikeModel);
                                }

                                // 이미 좋아요를 눌렀는지 검사.
                                for (PostLikeModel postLikeModel : list_post_like) {
                                    if (user_id.equals(postLikeModel.getId())) {
                                        like_exist = true;
                                        break;
                                    }
                                }

                                // 전에 공감을 누른경우. "이미 공감했습니다" 표시
                                if (like_exist) {
                                    Toast.makeText(context, "이미 선택했습니다", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                // 공감 누른적 없음 (비공감 누른여부 확인해야함)
                                else {

                                    /** UserInfo의 해당 유저에 공감수를 넣는다면 여기에 추가.
                                     *  처음부터 누적시킬것인지 기간별로 나눠서 삭제할것인지 기획적으로 생각해볼것.
                                     *  2022.4.22
                                     */

                                    // 공감 +1 하기
                                    databaseReference.child("postInfo").child(key).child("like").setValue(String.valueOf(int_like));
                                    // 공감 리스트에 추가
//                                    databaseReference.child("postInfo").child(key).child("like_list").push().setValue(likeModel);

                                    Toast.makeText(context, "공감했습니다", Toast.LENGTH_SHORT).show();
//                                    MainAct MA = (MainAct)MainAct._Main_Activity;
////                                    MA.bulletinFragment.getFirebaseBoardList();
//                                   MA.finish();
//                                    Intent intent = new Intent(ShowPostAct.this, MainAct.class);
//                                    startActivity(intent);
//                                    finish();

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }});
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                break;


            /**
             *          싫어요 버튼
             */
            case R.id.iv_dislike:

                /** 게시글의 좋아요가 0이면 반영되지 않는다. 좋아요 0보다 크면 -1을 시키지만 이미 비공감을 눌렀으면 반영되지 않는다.
                 * 좋아요를 누른적 없고 비공감이 정상 반영되는 좋아요의 경우처럼 파베의 like_list에 추가된다.
                 * like_list에서 좋아요를 누르든 비공감을 누르든 같이 관리함으로써 코드 용이성 확보.
                 * 2022.5.10
                 *
                 */
                databaseReference.child("postInfo").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        /** 여기서 postModel에 다시 데이터를 담는 이유는 상세화면에 들어와서 좋아요 버튼을 먼저 눌렀을 경우
                         *   데이터가 변해있기 때문.
                         */
                        postModel = snapshot.getValue(PostModel.class);

                        String user_id = SharedManager.read(SharedManager.LOGIN_ID, "");

                        // like_list에 추가할 PostLikeModel 인스턴스
                        PostLikeModel likeModel = new PostLikeModel();
                        likeModel.setId(user_id);

                        databaseReference.child("postInfo").child(key).child("like_list").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                // list_post_like 변수는 좋아요, 싫어요 데이터 가져올때 모두 사용
                                list_post_like.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    PostLikeModel postLikeModel = dataSnapshot.getValue(PostLikeModel.class);
                                    list_post_like.add(postLikeModel);
                                }

                                // 이미 싫어요를 눌렀는지 검사.
                                for (PostLikeModel postLikeModel : list_post_like) {
                                    if (user_id.equals(postLikeModel.getId())) {
                                        dislike_exist = true;
                                        break;
                                    }
                                }

                                // 이미 비공감을 누른경우. "이미 비공감했습니다" 표시
                                if (dislike_exist) {
                                    Toast.makeText(context, "이미 선택했습니다", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // 비공감 누른적 없음
                                else {

                                    /** UserInfo의 해당 유저에 공감수를 넣는다면 여기에 추가.
                                     *  처음부터 누적시킬것인지 기간별로 나눠서 삭제할것인지 기획적으로 생각해볼것.
                                     *  2022.4.22
                                     */

                                    String like = postModel.getLike();
                                    int like_int = Integer.parseInt(like);

                                    // 좋아요가 0인경우
                                    if (like_int == 0) {
                                        Toast.makeText(context, "공감 숫자가 0이라 반영되지 않습니다", Toast.LENGTH_SHORT).show();

                                    }
                                    // 정상진행
                                    else {
                                        // 기존의 좋아요에서 1을 빼고 다시 디비에 저장
                                        like_int -= 1;  // 마이너스 1
                                        Log.d("a", "실행");
                                        databaseReference.child("postInfo").child(key).child("like").setValue(String.valueOf(like_int));

                                        // 공감 리스트에 추가
                                        databaseReference.child("postInfo").child(key).child("like_list").push().setValue(likeModel);

                                        Toast.makeText(context, "비공감했습니다", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                break;


            // 즐겨찾기
            case R.id.iv_favorite:

                databaseReference.child("userInfo").child(userKey).child("favorite").orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list_post.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            PostModel postModel = dataSnapshot.getValue(PostModel.class);
                            list_post.add(postModel);
                        }
                        // 즐겨찾기 했는지 확인하기
                        for (PostModel post : list_post) {
                            if (post.getId().equals(post_id)) {
                                b_favorite = true;
                                break;
                            }
                        }
                        // 이미 즐겨찾기 되어있음.
                        if (b_favorite) {
                            Toast.makeText(context, "이미 즐겨찾기에 추가했습니다.", Toast.LENGTH_SHORT).show();
                        }
                        // 새롭게 즐겨찾기에 추가
                        else {
                            // 즐겨찾기 했을때 유저의 계정 favorite 브랜치에 해당화면 postModel 추가되는 부분
                            databaseReference.child("userInfo").child(SharedManager.read(SharedManager.AUTH_TOKEN, "")).child("favorite").push().setValue(postModel);
                            iv_favorite.setBackgroundColor(getResources().getColor(R.color.orange));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                break;

//            case R.id.map:
//
//                Intent intent = new Intent(context, NaverMapAct.class);
//                startActivity(intent);
//
//                 break;
        }
    }

    public void getImages() {

        if (storageReference != null) {

            imageList = new ArrayList();
            final String[] acaPicName = new String[6];
            String userName = SharedManager.read(SharedManager.USER_NAME, "");
            String fileName = userName + postModel.getDate();

            acaPicName[0] = userName + '1';
            acaPicName[1] = userName + '2';
            acaPicName[2] = userName + '3';
            acaPicName[3] = userName + '4';
            acaPicName[4] = userName + '5';
            acaPicName[5] = userName + '6';

            for (int i = 0; i < acaPicName.length; i++) {

                final int ii = i;
                storageReference.child(fileName).child(acaPicName[i]).getDownloadUrl().addOnSuccessListener
                        (new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {

                                 imageList.add(uri);

                                 /** 2021. 10. 23 어댑터를 한번만 실행시키도록 수정하려했으나 실패 **/
                                 PagerAdapter adapter = new ViewPagerAdapter(ShowPostAct.this, imageList);
                                 viewPager.setAdapter(adapter);
                                 adapter.notifyDataSetChanged();
                             }
                         }
                        );
            }
        } else {

        }
    }

    // 관련글에 들어갈 데이터를 파베에서 가져와야하는 부분
    private void getFirebaseBoardList() {
        // load firebase db list

        databaseReference.child("postInfo").orderByChild("storeName").equalTo(store_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list_post.clear();
                list_key.clear();
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel postModel = snapshot.getValue(PostModel.class);
                        // 최신글 역순 정렬을 위한 역순 add
                        list_post.add(0, postModel);
                        list_key.add(0, snapshot.getKey());
                    }

                    relatedAdapter = new RelatedAdapter(ShowPostAct.this, list_post, list_key);
                    rv_related.setAdapter(relatedAdapter);
                    relatedAdapter.notifyDataSetChanged();
//                   setProgressDialog(null, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(getClass().getSimpleName(), "onCancelled Database error", error.toException().fillInStackTrace());
            }
        });

    }
}
