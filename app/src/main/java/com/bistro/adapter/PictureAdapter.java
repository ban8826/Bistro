package com.bistro.adapter;

import android.content.Context;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bistro.R;
import com.bumptech.glide.Glide;

import java.net.URI;
import java.util.ArrayList;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<Uri> list_uri;


    public PictureAdapter (Context context, ArrayList list)
    {
        list_uri = list;
        mContext = context;
    }


    @NonNull
    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_picture, parent, false);
        return new PictureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureAdapter.ViewHolder holder, int position) {

        Glide.with(mContext).load(list_uri.get(position)).into(holder.iv_picture);
    }

    @Override
    public int getItemCount() {
        return list_uri.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView iv_picture;
        public ViewHolder(View view)
        {
         super(view);

         iv_picture = view.findViewById(R.id.iv_picture);


        }
    }
}
