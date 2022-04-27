package com.bistro.activity;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bistro.R;
import com.bistro.adapter.BulletinAdapter;
import com.bistro.adapter.RelatedAdapter;
import com.bistro.adapter.ViewPagerAdapter;
import com.bistro.database.SharedManager;
import com.bistro.fragment.ListFragment;
import com.bistro.model.PostLikeModel;
import com.bistro.model.PostModel;
import com.bistro.model.UserLikeModel;
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
    private ViewPager viewPager ;
    private TabLayout  tabLayout_dot;
    private ArrayList<UserLikeModel> list_user_like;
    private ArrayList<PostLikeModel> list_post_like, list_post_dislike;
    private boolean b_favorite;

    private RecyclerView rv_related;
    private RelatedAdapter relatedAdapter;

    boolean like_exist, dislike_exist ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_show_post);

        context = getApplicationContext();
        intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("post");
        key = intent.getStringExtra("key"); // 게시글의 파베에서 랜덤키
        post_id = postModel.getId();  /** 게시글만의 유니크한 아이디 (랜덤키 + 날짜) **/
        databaseReference = FirebaseDatabase.getInstance().getReference("bistro");
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bistro-5bc79.appspot.com");

        tv_menu = findViewById(R.id.tv_menu_value);
        tv_content = findViewById(R.id.tv_content);
        tv_title = findViewById(R.id.tv_title);
        tv_store_name = findViewById(R.id.tv_store_name);

        title = postModel.getTitle();
        store_name = postModel.getStoreName();
        menu = postModel.getMenu();
        content = postModel.getContent();

        userKey = SharedManager.read(SharedManager.AUTH_TOKEN,"");
        list_post = new ArrayList<>();
        list_key = new ArrayList<>();

        tv_title.setText(title);
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

        /** 스토리지에서 사진 불로오는 중요 함수 **/
        getImages();

        /** 파베에서 관련글 데이터 가져오는 부분 **/
        getFirebaseBoardList();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;

            // 좋아요 버튼
            case R.id.iv_like:

                String str_like = postModel.getLike();
                int int_like = Integer.parseInt(str_like) + 1;
                String user_id = SharedManager.read(SharedManager.LOGIN_ID, "");

                // like_list에 추가할 PostLikeModel 인스턴스
                PostLikeModel likeModel = new PostLikeModel();
                likeModel.setId(user_id);

                databaseReference.child("postInfo").child(key).child("like_list").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                               PostLikeModel postLikeModel = dataSnapshot.getValue(PostLikeModel.class);
                               list_post_like.add(postLikeModel);
                            }

                            for(PostLikeModel postLikeModel : list_post_like) {
                                if(user_id.equals(postLikeModel.getId()))
                                {  like_exist = true;
                                break;  }
                            }

                            // 전에 공감을 누른경우. "이미 공감했습니다" 표시
                            if(like_exist)
                            {
                                Toast.makeText(context, "이미 공감했습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // 공감 누른적 없음 (비공감 누른여부 확인해야함)
                            else{

                                databaseReference.child("postInfo").child(key).child("dislike_list").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                            {
                                                PostLikeModel postLikeModel = dataSnapshot.getValue(PostLikeModel.class);
                                                list_post_dislike.add(postLikeModel);
                                            }

                                            for(PostLikeModel postLikeModel : list_post_dislike) {
                                                if(user_id.equals(postLikeModel.getId()))
                                                {  dislike_exist = true;
                                                    break;  }
                                            }

                                            // 비공감 누른적 있음 (비공감리스트에서 삭제와 공감리스트 추가)
                                            if(dislike_exist)
                                            {
                                                databaseReference.child("postInfo").child(key).child("dislike_list").orderByChild("id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String dislike_key = "";
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                                        {
                                                            dislike_key = dataSnapshot.getKey();
                                                        }

                                                        /** UserInfo의 해당 유저에 공감수를 넣는다면 여기에 추가.
                                                         *  처음부터 누적시킬것인지 기간별로 나눠서 삭제할것인지 기획적으로 생각해볼것.
                                                         *  2022.4.22
                                                         */

                                                        // 비공감 리스트에서 삭제
                                                        databaseReference.child("postInfo").child(key).child("dislike_list").child(dislike_key).removeValue();
                                                        // 공감 +1 하기
                                                        databaseReference.child("postInfo").child(key).child("like").setValue(String.valueOf(int_like));
                                                        // 공감 리스트에 추가
                                                        databaseReference.child("postInfo").child(key).child("like_list").push().setValue(likeModel);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                            // 비공감 누른적 없음 ( 공감리스트만 정상추가)
                                            else
                                            {
                                                // 공감 +1 하기
                                                databaseReference.child("postInfo").child(key).child("like").setValue(String.valueOf(int_like));

                                                // 공감 리스트에 추가
                                                databaseReference.child("postInfo").child(key).child("like_list").push().setValue(likeModel);
                                            }
                                        }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                            }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                break;

            // 싫어요 버튼
            case R.id.iv_dislike:

                String str_dislike = postModel.getDislike();
                int int_dislike = Integer.parseInt(str_dislike) + 1;
                databaseReference.child("postInfo").child(key).child("dislike").setValue(String.valueOf(int_dislike));
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
                            for(PostModel post : list_post)
                        {
                            if(post.getId().equals(post_id))
                            {
                                b_favorite = true;
                                break;
                            }
                        }
                            // 이미 즐겨찾기 되어있음.
                            if(b_favorite)
                            {
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
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
                break;
        }
    }

    public void getImages() {

        if (storageReference != null) {

            imageList = new ArrayList();
            final String acaPicName[] = new String[6];
            String userName = SharedManager.read(SharedManager.USER_NAME,"");
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
                        list_post        .add(0, postModel);
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
