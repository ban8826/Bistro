package com.bistro.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bistro.R;
import com.bistro.activity.DetailPostAct;
import com.bistro.database.SharedManager;
import com.bistro.model.PostModel;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.BoardViewHolder> {

    private final ArrayList<PostModel> list_post;
    private final ArrayList<String> list_key;
    private Context mContext;
    private final StorageReference storageReference;
    private String randomKey;
    private String type;  // 게시판인지 즐겨찾기인지 구분하는 변수
    private final Activity activity;


    public RelatedAdapter (Activity activity, ArrayList list, ArrayList key) {
        list_post = list;
        list_key = key;
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bistro-5bc79.appspot.com");
        this.randomKey = randomKey;
        this.activity = activity;

    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_related_post, parent, false);
        return new RelatedAdapter.BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedAdapter.BoardViewHolder holder, int position)
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
        // 관련글을 3개로 제한하는 부분. 오류나면 고칠것 . 2022.4.27
        if(list_post.size() > 3)
        {
          return 3;
        }
          else
        {
           return list_post.size();
        }
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

                    Intent intent = new Intent(activity, DetailPostAct.class);
                    intent.putExtra("post", list_post.get(adapterPosition));
                    intent.putExtra("key", key);
                    activity.startActivity(intent);

            });

        }


    }
}
