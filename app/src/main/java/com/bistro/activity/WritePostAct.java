package com.bistro.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bistro.R;
import com.bistro.dialog.SearchAddressDialog;
import com.bistro.model.KakaoPlaceModel;
import com.bistro.util.RetrofitMain;
import com.bistro.adapter.PictureAdapter;
import com.bistro.database.SharedManager;
import com.bistro.model.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WritePostAct extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "WritePostAct";

    private DatabaseReference
            mDatabaseRef;
    private Context context;
    private String storeAddress , strName, randomKey;
    private InputMethodManager imm;
    private RetrofitMain retrofitMain;
    private TextView tv_complete, tv_search_store;  // ???????????? ????????? ??????
    private EditText et_title, et_search_store, et_menu, et_address;
    private TextView tv_name_of_store;
    private EditText et_content; // ????????? ??????
    private LinearLayout layoutAddress;
    private MapView mapView;
    private Uri[] pictureUri;
    private ImageView iv_pick_images, iv_search_poi;
    private RecyclerView rv_images;
    private PictureAdapter pictureAdapter;
    private ArrayList<Uri> listImages;
    private ArrayList<PostModel> todayList;
    private boolean todayCheck;

    private KakaoPlaceModel.PoiPlace mPoiPlace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.act_write_post_copy);
        setInitialize();
    }

    private void setInitialize() {
        SharedManager.init(getApplicationContext());

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bistro");
        context = getApplicationContext();

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        listImages = new ArrayList<>();

        todayList = new ArrayList<>();  //  ?????? ????????? ??????????????? ?????? ??????
        layoutAddress = findViewById(R.id.a_write_post_layout_address);
        mapView = findViewById(R.id.a_write_post_map);

        randomKey = SharedManager.read(SharedManager.AUTH_TOKEN,""); // ?????? ?????????, ??? ???????????? ????????????
        todayCheck = false;
        tv_complete = findViewById(R.id.btn_complete);
        et_title = findViewById(R.id.a_write_post_et_title);
        et_search_store = findViewById(R.id.a_write_post_et_name);   // ?????? ????????? ???????????? ???????????? ????????????
        findViewById(R.id.tv_search_store).setOnClickListener(this);  // '?????? ??????' ??????
        et_menu = findViewById(R.id.a_write_post_et_menu);
        et_content = findViewById(R.id.a_write_post_et_content);
        et_address = findViewById(R.id.a_write_post_et_address);

        findViewById(R.id.btn_back).setOnClickListener(this);
        tv_complete.setOnClickListener(this);

            // postmode : ????????????
//            tv_complete.setVisibility(View.VISIBLE);
//            setEnabledInputField(true);

        // ?????? ????????? ???????????? ?????? ??????
        rv_images = findViewById(R.id.a_write_post_rv_picture);
        iv_pick_images = findViewById(R.id.a_write_post_iv_picture);
        iv_pick_images.setOnClickListener(this);

//        iv_search_poi = findViewById(R.id.a_write_post_iv_search_poi);
//        iv_search_poi.setOnClickListener(this);

        et_search_store.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                iv_search_poi.performClick();
            }

            return false;
        });

        showKeyboard(et_search_store, true);
        showKeyboard(et_title, true);
    }

    public void showKeyboard(EditText editText, boolean isShow) {
        if (imm != null) {
            editText.requestFocus();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isShow) {
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    } else {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }, 300);

        }
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {

            //  ????????????
            case R.id.btn_back:
                finish();
                break;

            case R.id.a_write_post_iv_picture:

                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
                break;

            // ????????? ?????? ?????????
//            case R.id.iv_image1:
//
//                intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//                //????????? ????????? ???????????? ????????? ??????
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
//                break;

            case R.id.btn_complete:
                // ????????????

                todayCheck = false;  // ????????? ????????? ?????? ?????????????????? ?????? ?????? false???.

                String strTitle = et_title.getText().toString();
                String strContent = et_content.getText().toString();
                String strMenu = et_menu.getText().toString();
                // check validation of input field
                if ( strTitle  .length() == 0 ||
                        strName  . length() == 0 ||
                        strMenu . length() == 0 ||
                        strContent.length() == 0
                ) {
                    Toast.makeText(this, "???????????? ?????? ?????? ?????? ?????????", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDatabaseRef.child("userInfo").child(randomKey).child("todayList").orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        todayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            if(dataSnapshot.getChildrenCount() != 0)
                            { PostModel postModel = dataSnapshot.getValue(PostModel.class);
                                todayList.add(postModel); }
                        }

                        /**  ?????? ?????? ?????? ??????????????? ??????. true??? ??????????????? ???   **/
                        for(PostModel postModel : todayList)
                        {
                            if(postModel.getAddress().equals(storeAddress)) {todayCheck =  true; break;}
                        }

                        /** ?????? ????????? ?????? ?????? ??????.   ??? ?????? ??????  **/
                        if (!todayCheck)
                        {
                            // check current login mode
                            int loginType = SharedManager.read(SharedManager.LOGIN_TYPE, -1);
//                if (loginType == -1)
//                    Toast.makeText(this, "????????? ????????? ????????????\n?????? ??????????????? ????????? ????????????", Toast.LENGTH_SHORT).show();

                            String strCurrentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                            String strNickName = SharedManager.read(SharedManager.USER_NAME,"");
                            String strAuthToken = SharedManager.read(SharedManager.AUTH_TOKEN,"");
                            String strFcmToken = SharedManager.read(SharedManager.FCM_TOKEN, "");
                            String strPostId = strAuthToken + "_" + strCurrentDate; // (?????? !) ????????? ???????????? (????????? ?????? + "_" + ??????????????????????????????) ?????? ????????????.
                            String userId = SharedManager.read(SharedManager.LOGIN_ID, "");

                            // create post model
                            PostModel postModel = new PostModel();
                            postModel.setTitle(strTitle);
                            postModel.setNickName(strNickName);
                            postModel.setStoreName(strName);
                            postModel.setContent(strContent);
                            postModel.setDate(strCurrentDate);
                            postModel.setAuthToken(strAuthToken);
                            postModel.setFcmToken(strFcmToken);
                            postModel.setAddress(storeAddress);  // ?????? ?????? ('????????????'?????? ?????? ?????? )
                            postModel.setId(strPostId);    // ???????????? ???????????? ID
                            postModel.setUserId(userId);   // ????????? ????????? ID
                            postModel.setMenu(strMenu);
                            postModel.setClick("1");   // ?????????
                            postModel.setLike("0");    // ?????????
                            postModel.setPoiPlace(mPoiPlace);

                            // userInfo??? todayList ??? ?????? ??????. ??????????????? ????????? ??????
                            PostModel modelToday = new PostModel();
                            modelToday.setDate(strCurrentDate);
                            modelToday.setAddress(storeAddress);

                            mDatabaseRef.child("postInfo").child(strPostId).setValue(postModel);

                            // ???????????? ?????? ??? ??????????????? ???????????? ??? ??????
                            mDatabaseRef.child("userInfo").child(randomKey).child("todayList").push().setValue(modelToday);


                            /** ????????? ?????? +1 ?????? ???????????? ???????????? ?????? **/
                            int count =  Integer.parseInt( SharedManager.read(SharedManager.WRITE_COUNT,"") );
                            // ????????? ????????? 1??? ??????
                            count += 1;
                            SharedManager.write(SharedManager.WRITE_COUNT, String.valueOf(count));

                            final ProgressDialog progressDialog = new ProgressDialog(WritePostAct.this);
                            progressDialog.setTitle("????????????...");
                            progressDialog.show();

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            String[] picName = new String [6];

                            /** ??????????????? ?????? **/
                            picName[0] = strNickName + '1';
                            picName[1] = strNickName + '2';
                            picName[2] = strNickName + '3';
                            picName[3] = strNickName + '4';
                            picName[4] = strNickName + '5';
                            picName[5] = strNickName + '6';


                            String imgFolderName = strNickName + strCurrentDate; // ?????? ???????????? ????????? ????????? ????????? ???????????? ??????

                            for(int i=0; i<listImages.size(); i++)
                            {


                                /** ????????? else ?????? ???????????? ???????????? ???????????? ???????????? ??????????????? ????????????
                                 *  listFragment?????? Storage?????? ???????????? ????????????
                                 */
                                if(listImages.get(i) != null)
                                {

                                    //storage ????????? ?????? ???????????? ????????? ??????.
                                    //        storage.getReferenceFromUrl("gs://arcademy-241eb.appspot.com").child("aca_ad_images/" + pictureName).putFile(filePath)
                                    storage.getReferenceFromUrl("gs://bistro-5bc79.appspot.com").child(imgFolderName).child(picName[i]).putFile(listImages.get(i))
                                            //?????????
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                                {

                                                    /**     ????????? ????????? ??????    **/
                                                    progressDialog.dismiss(); //????????? ?????? Dialog ?????? ??????


                                                    finish();


//                                        final DialogUseful dialogUseful = new DialogUseful("ad_complete",AdRequestBossAct.this);
//                                        dialogUseful.setCancelable(true);
//                                        dialogUseful.btn_ad_complete.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//
//                                                dialogUseful.cancel();
//                                                Intent intent = new Intent(context, MainAct.class);
//                                                startActivity(intent);
//                                                finish();
//                                            }
//                                        });
//                                        dialogUseful.show();

                                                }
                                            })
                                            //?????????
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                }
                                            })
                                            //?????????
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                    //dialog??? ???????????? ???????????? ????????? ??????
                                                    progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                                                }
                                            });
                                }
                            } // for??? ????????? ??????
                        }
                        /**  ?????? ????????? ?????? ?????? ??????.   ??? ?????? ?????????   **/
                        else
                        {
                            Toast.makeText(context, "?????? ?????? ????????? ?????? ?????? ??????????????????. 1?????? ????????? ?????? ????????? 1?????? ?????? ????????? ??? ????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }});

                break;


                /** ???????????? ?????? **/
            case R.id.tv_search_store:

                SearchAddressDialog searchDialog ;

                if (et_search_store.getText().toString().isEmpty()) {
                    searchDialog = new SearchAddressDialog(this, getSupportFragmentManager());
                } else {
                    searchDialog = new SearchAddressDialog(this, getSupportFragmentManager(), et_search_store.getText().toString());
                }

//
//                String strAddress = place.getAddress_name();
//                String strRoadAddress = place.getRoad_address_name();

                searchDialog.setResultListener(new SearchAddressDialog.ResultListener() {
                    @Override
                    public void onResult(KakaoPlaceModel.PoiPlace place, String name) {
                        Log.d(TAG, "onResult !!");

                        mPoiPlace = place;
                        strName = name;
                        et_search_store.setText(name);  // ???????????? ?????? ??????

                        String strAddress = place.getAddress_name();
                        String strRoadAddress = place.getRoad_address_name();

                        layoutAddress.setVisibility(View.VISIBLE);

                        if (strRoadAddress.equals("")) {
                            et_address.setText(strAddress);
                            storeAddress = strAddress;
                        } else {
                            et_address.setText(strRoadAddress);
                            storeAddress = strRoadAddress;
                        }

                        Toast.makeText(context, storeAddress, Toast.LENGTH_SHORT).show();

                        mapView.setVisibility(View.VISIBLE);
                        mapView.getMapAsync(naverMap -> {
                            naverMap.getUiSettings().setZoomControlEnabled(false);
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

                        showKeyboard(et_menu, true);
                    }
                });
                searchDialog.show();

                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);

                searchDialog.getWindow().setLayout(width, height);
                break;

        }
    }

    /** startActivityForResult ???????????? ???????????? ?????? **/
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){   // ?????? ???????????? ???????????? ?????? ??????
            Toast.makeText(getApplicationContext(), "???????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
        }
        else{   // ???????????? ???????????? ????????? ??????
            if(data.getClipData() == null)
            {     // ???????????? ????????? ????????? ??????
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                listImages.add(imageUri);

                pictureAdapter = new PictureAdapter( getApplicationContext(),listImages);
                rv_images.setAdapter(pictureAdapter);
//                rv_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                pictureAdapter.notifyDataSetChanged();

            }
            else{      // ???????????? ????????? ????????? ??????
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 6){   // ????????? ???????????? 11??? ????????? ??????
                    Toast.makeText(getApplicationContext(), "????????? 6????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                }
                else{   // ????????? ???????????? 1??? ?????? 10??? ????????? ??????

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                        try {
                            listImages.add(imageUri);  //uri??? list??? ?????????.

                        } catch (Exception e) {
                        }
                    }

                    pictureAdapter = new PictureAdapter( getApplicationContext(),listImages);
                    rv_images.setAdapter(pictureAdapter);
//                    rv_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    pictureAdapter.notifyDataSetChanged();
                }
            }
        }

    }

}
