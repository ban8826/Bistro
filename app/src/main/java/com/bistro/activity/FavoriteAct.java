package com.bistro.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bistro.R;
import com.bistro.adapter.ViewPagerAdapter;
import com.bistro.database.SharedManager;
import com.bistro.model.PostLikeModel;
import com.bistro.model.PostModel;
import com.bistro.model.UserLikeModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FavoriteAct extends AppCompatActivity implements View.OnClickListener {



    private ImageView iv_back, iv_remove;
    private Context context;
    private Intent intent;
    private PostModel postModel;
    private TextView tv_title, tv_menu, tv_nickName, tv_store_name, tv_content;
    private String title, store_name, menu, content, key, userKey, post_id,postKey;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ArrayList imageList;
    private ArrayList<PostModel> list_post;
    private ViewPager viewPager ;
    private TabLayout tabLayout_dot;
    private ArrayList<UserLikeModel> list_user_like;
    private ArrayList<PostLikeModel> list_post_like, list_post_dislike;
    private boolean b_favorite;

    boolean like_exist, dislike_exist ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_favorite);

        context = getApplicationContext();
        intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("post");
        key = intent.getStringExtra("key"); // 게시글의 파베에서 랜덤키
        post_id = postModel.getId();  /** 게시글만의 유니크한 아이디 (랜덤키 + 날짜) **/
        databaseReference = FirebaseDatabase.getInstance().getReference("bistro");
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bistro-5bc79.appspot.com");

        tv_menu = findViewById(R.id.tv_menu_value);
        tv_content = findViewById(R.id.tv_content);
        tv_store_name = findViewById(R.id.tv_store_name);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title = postModel.getTitle();
        store_name = postModel.getStoreName();
        menu = postModel.getMenu();
        content = postModel.getContent();

        userKey = SharedManager.read(SharedManager.AUTH_TOKEN,"");
        list_post = new ArrayList<>();


        tv_store_name.setText(store_name);
        tv_menu.setText(menu);
        tv_content.setText(content);


        iv_back = findViewById(R.id.iv_back_arrow);
        iv_back.setOnClickListener(this);
        iv_remove = findViewById(R.id.iv_remove);
        iv_remove.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.iv_remove:

                databaseReference.child("userInfo").child(userKey).child("favorite").orderByChild("id").equalTo(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            postKey = dataSnapshot.getKey();
                        }

                        databaseReference.child("userInfo").child(userKey).child("favorite").child(postKey).removeValue();
                        finish();
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
            final String[] acaPicName = new String[6];
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
                                 PagerAdapter adapter = new ViewPagerAdapter(FavoriteAct.this, imageList);
                                 viewPager.setAdapter(adapter);
                                 adapter.notifyDataSetChanged();
                             }
                         }
                        );
            }
        } else {

        }
    }
}
