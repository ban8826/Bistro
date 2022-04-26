package com.bistro.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.bistro.R;
import com.bistro.activity.FavoriteAct;
import com.bistro.activity.ShowPostAct;
import com.bistro.database.SharedManager;
import com.bistro.model.PostModel;
import com.bumptech.glide.Glide;
import com.google.android.datatransport.runtime.dagger.multibindings.StringKey;
import com.google.android.material.shadow.ShadowRenderer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BulletinAdapter extends RecyclerView.Adapter<BulletinAdapter.BoardViewHolder> {

    private ArrayList<PostModel> list_post;
    private ArrayList<String> list_key;
    private Context mContext;
    private DatabaseReference mDatabaseRef;
    private StorageReference storageReference;
    private String randomKey;
    private String type;  // 게시판인지 즐겨찾기인지 구분하는 변수
    private Fragment fragment;


    public BulletinAdapter(Fragment fragment, ArrayList list, ArrayList key, String type) {
        list_post = list;
        list_key = key;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bistro");
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bistro-5bc79.appspot.com");
        this.randomKey = randomKey;
        this.fragment = fragment;
        this.type = type;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_food, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position)
    {
        holder.tv_title.setText(list_post.get(position).getTitle());
        holder.tv_store_name.setText(list_post.get(position).getStoreName());
        holder.tv_like.setText(list_post.get(position).getLike());

        if(storageReference!=null)
        {
            String nickName = SharedManager.read(SharedManager.USER_NAME, "");
            String fileName = nickName + list_post.get(position).getDate();
            String name_img = nickName + '1';

            storageReference.child(fileName).child(name_img).getDownloadUrl().addOnSuccessListener(uri -> {

                //다운로드 URL이 파라미터로 전달되어 옴.
                Glide.with(mContext).load(uri).into(holder.iv_img);
            });
        }
    }

    @Override
    public int getItemCount() {
        return list_post.size();
    }


    public void setListItem(ArrayList<PostModel> _lstPostInfo) {
        if (!list_post.isEmpty())
            list_post.clear();

        list_post = _lstPostInfo;
    }





    public class BoardViewHolder extends RecyclerView.ViewHolder {
        protected TextView tv_title, tv_store_name, tv_click, tv_like;
        protected ImageView iv_img, iv_like;

        public BoardViewHolder(View itemView) {
            super(itemView);

            iv_img = itemView.findViewById(R.id.iv_image);
             tv_title = itemView.findViewById(R.id.tv_title);
             tv_store_name = itemView.findViewById(R.id.tv_store_name);
             tv_like = itemView.findViewById(R.id.tv_like);
             iv_like = itemView.findViewById(R.id.iv_like);

            itemView.setOnClickListener(view1 -> {

                int adapterPosition = getAdapterPosition();
                String key = list_key.get(adapterPosition);

                if(type.equals("favorite"))
                {
                    Intent intent = new Intent(fragment.getActivity(), FavoriteAct.class);
                    intent.putExtra("post", list_post.get(adapterPosition));
                    intent.putExtra("key", key);
                    fragment.startActivity(intent);
                }

                else {
                    Intent intent = new Intent(fragment.getActivity(), ShowPostAct.class);
                    intent.putExtra("post", list_post.get(adapterPosition));
                    intent.putExtra("key", key);
                    fragment.startActivity(intent);
                }
                });

//            mTvAgree.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    PostModel postModel = mLstPost.get(getAdapterPosition());
//                    int agree = Integer.parseInt(postModel.getAgree()) + 1;
//                    postModel.setAgree(String.valueOf(agree));
//                    mDatabaseRef.child(Const.FIREBASE_KEY_POST_INFO).child(mLstPost.get(getAdapterPosition()).getId()).setValue(postModel);
//
//                    mTvAgree.setBackgroundResource(R.drawable.border_black_inside_10);
//                    mTvAgree.setTextColor(Color.WHITE);
//                }
//            });

//            tv_1 = view.findViewById(R.id.tv_1);
//            tv_like  = view.findViewById(R.id.tv_2);
//            tv_rank = view.findViewById(R.id.tv_rank);
//            iv_like = view.findViewById(R.id.iv_like);
//
//            iv_like.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition();
//
//                    databaseReference.child("list").child(key).child("rank").orderByChild("name").equalTo(list.get(position).getName()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                            {
//                                if (dataSnapshot.getChildrenCount() != 0)
//                                {
//                                    rankKey = dataSnapshot.getKey();
//                                    RankInfo rankInfo = dataSnapshot.getValue(RankInfo.class);
//                                    long like = rankInfo.getLike() + 1;
//                                    rankInfo.setLike(like);
//                                    databaseReference.child("list").child(key).child("rank").child(rankKey).setValue(rankInfo);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//            });
        }


    }
}