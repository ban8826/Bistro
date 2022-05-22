package com.bistro.adapter;

import android.animation.ValueAnimator;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bistro.R;
import com.bistro.dialog.SearchAddressDialog;
import com.bistro.model.KakaoPlaceModel;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.util.ArrayList;

public class SearchAddressAdapter extends RecyclerView.Adapter<SearchAddressAdapter.ViewHolder> {
    private final String TAG = "SearchAddressAdapter";

    private static View view;

    private FragmentManager fm;

    private ArrayList<KakaoPlaceModel.PoiPlace> addressList;

    private SearchAddressDialog dialog;

    public SearchAddressAdapter(SearchAddressDialog dialog, FragmentManager fm, ArrayList<KakaoPlaceModel.PoiPlace> addressList) {
        this.dialog = dialog;
        this.fm = fm;
        this.addressList = addressList;
    }

    @NonNull
    public SearchAddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_address, parent, false);
        return new SearchAddressAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAddressAdapter.ViewHolder holder, int position) {
        holder.onBind(addressList.get(position));

        if (addressList.get(position).isClick()) {
            holder.layoutMap.setVisibility(View.VISIBLE);
        } else {
            holder.layoutMap.setVisibility(View.GONE);
        }

        holder.mapView.getMapAsync(naverMap -> {
            naverMap.getUiSettings().setZoomControlEnabled(false);

            KakaoPlaceModel.PoiPlace place = addressList.get(position);
            double x = Double.parseDouble(place.getX());
            double y = Double.parseDouble(place.getY());
            LatLng latLng = new LatLng(y, x);
            CameraUpdate cam = CameraUpdate.scrollAndZoomTo(latLng, 18.0);
            naverMap.moveCamera(cam);

            Marker marker = new Marker();
            marker.setPosition(latLng);
            marker.setMap(naverMap);
            marker.setIcon(OverlayImage.fromResource(R.drawable.img_pin_copy));
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void setList(ArrayList<KakaoPlaceModel.PoiPlace> list) {
        addressList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private LinearLayout layoutMap;
        private Button btnSelect;

        private MapView mapView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.i_search_address_tv_address);
            layoutMap = itemView.findViewById(R.id.i_search_address_layout_map);
            btnSelect = itemView.findViewById(R.id.i_search_address_btn_select);
            mapView = itemView.findViewById(R.id.i_search_address_map);

            tvName.setOnClickListener(view -> {
                for (int i = 0; i < addressList.size(); i++) {
                    addressList.get(i).setClick(false);
                }

                addressList.get(getAdapterPosition()).setClick(true);

                notifyDataSetChanged();
            });

            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "btnSelect onClick !! dialog onResult !");
                    dialog.onResult(addressList.get(getAdapterPosition()));
                }
            });
        }

        public void onBind(KakaoPlaceModel.PoiPlace result){
            String roadAddress = result.getRoad_address_name();
            String address = result.getAddress_name();

            if (roadAddress.equals("")) {
                tvName.setText(address);
            } else {
                tvName.setText(roadAddress);
            }


        }
    }
}
