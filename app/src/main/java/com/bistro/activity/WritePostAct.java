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
    private TextView tv_complete, tv_search_store;  // 작성완료 텍스트 버튼
    private EditText et_title, et_search_store, et_menu, et_address;
    private TextView tv_name_of_store;
    private EditText et_content; // 게시글 내용
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

        todayList = new ArrayList<>();  //  오늘 작성한 상점리스트 담는 변수
        layoutAddress = findViewById(R.id.a_write_post_layout_address);
        mapView = findViewById(R.id.a_write_post_map);

        randomKey = SharedManager.read(SharedManager.AUTH_TOKEN,""); // 파베 랜덤키, 내 계정으로 갈수있는
        todayCheck = false;
        tv_complete = findViewById(R.id.btn_complete);
        et_title = findViewById(R.id.a_write_post_et_title);
        et_search_store = findViewById(R.id.a_write_post_et_name);   // 맛집 검색뒤 맛집이름 들어오는 텍스트뷰
        findViewById(R.id.tv_search_store).setOnClickListener(this);  // '맛집 검색' 버튼
        et_menu = findViewById(R.id.a_write_post_et_menu);
        et_content = findViewById(R.id.a_write_post_et_content);
        et_address = findViewById(R.id.a_write_post_et_address);

        findViewById(R.id.btn_back).setOnClickListener(this);
        tv_complete.setOnClickListener(this);

            // postmode : 작성하기
//            tv_complete.setVisibility(View.VISIBLE);
//            setEnabledInputField(true);

        // 사진 여러장 가져오는 부분 코드
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

            //  뒤로가기
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

            // 이미지 버튼 첫번쨰
//            case R.id.iv_image1:
//
//                intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//                //사진을 여러개 선택할수 있도록 한다
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
//                break;

            case R.id.btn_complete:
                // 작성완료

                todayCheck = false;  // 주소만 바꿔서 다시 작성할경우를 위해 다시 false로.

                String strTitle = et_title.getText().toString();
                String strContent = et_content.getText().toString();
                String strMenu = et_menu.getText().toString();
                // check validation of input field
                if ( strTitle  .length() == 0 ||
                        strName  . length() == 0 ||
                        strMenu . length() == 0 ||
                        strContent.length() == 0
                ) {
                    Toast.makeText(this, "비어있는 입력 값이 존재 합니다", Toast.LENGTH_SHORT).show();
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

                        /**  오늘 같은 상점 작성했는지 확인. true면 작성했다는 뜻   **/
                        for(PostModel postModel : todayList)
                        {
                            if(postModel.getAddress().equals(storeAddress)) {todayCheck =  true; break;}
                        }

                        /** 오늘 상점에 대해 최초 작성.   글 작성 가능  **/
                        if (!todayCheck)
                        {
                            // check current login mode
                            int loginType = SharedManager.read(SharedManager.LOGIN_TYPE, -1);
//                if (loginType == -1)
//                    Toast.makeText(this, "로그인 정보가 없습니다\n앱을 재실행하여 로그인 해주세요", Toast.LENGTH_SHORT).show();

                            String strCurrentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                            String strNickName = SharedManager.read(SharedManager.USER_NAME,"");
                            String strAuthToken = SharedManager.read(SharedManager.AUTH_TOKEN,"");
                            String strFcmToken = SharedManager.read(SharedManager.FCM_TOKEN, "");
                            String strPostId = strAuthToken + "_" + strCurrentDate; // (중요 !) 게시글 아이디는 (로그인 토큰 + "_" + 디바이스기준현지시간) 으로 정의한다.
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
                            postModel.setAddress(storeAddress);  // 식당 주소 ('맛집검색'으로 부터 받는 )
                            postModel.setId(strPostId);    // 게시글의 유니크한 ID
                            postModel.setUserId(userId);   // 사용자 로그인 ID
                            postModel.setMenu(strMenu);
                            postModel.setClick("1");   // 조회수
                            postModel.setLike("0");    // 공감수
                            postModel.setPoiPlace(mPoiPlace);

                            // userInfo의 todayList 에 넣을 모델. 상점주소와 날짜만 기록
                            PostModel modelToday = new PostModel();
                            modelToday.setDate(strCurrentDate);
                            modelToday.setAddress(storeAddress);

                            mDatabaseRef.child("postInfo").child(strPostId).setValue(postModel);

                            // 내정보에 오늘 글 작성했음을 확인하는 글 저장
                            mDatabaseRef.child("userInfo").child(randomKey).child("todayList").push().setValue(modelToday);


                            /** 글쓰는 횟수 +1 하고 쉐어드에 저장하는 부분 **/
                            int count =  Integer.parseInt( SharedManager.read(SharedManager.WRITE_COUNT,"") );
                            // 글쓰기 카운트 1회 추가
                            count += 1;
                            SharedManager.write(SharedManager.WRITE_COUNT, String.valueOf(count));

                            final ProgressDialog progressDialog = new ProgressDialog(WritePostAct.this);
                            progressDialog.setTitle("업로드중...");
                            progressDialog.show();

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            String[] picName = new String [6];

                            /** 이미지파일 이름 **/
                            picName[0] = strNickName + '1';
                            picName[1] = strNickName + '2';
                            picName[2] = strNickName + '3';
                            picName[3] = strNickName + '4';
                            picName[4] = strNickName + '5';
                            picName[5] = strNickName + '6';


                            String imgFolderName = strNickName + strCurrentDate; // 유저 닉네임과 일시를 합쳐서 이미지 폴더이름 생성

                            for(int i=0; i<listImages.size(); i++)
                            {


                                /** 여기서 else 문을 만들어서 이미지를 아무것도 선택하지 않을경우를 만들어서
                                 *  listFragment에서 Storage에서 이미지를 가져올떄
                                 */
                                if(listImages.get(i) != null)
                                {

                                    //storage 주소와 폴더 파일명을 지정해 준다.
                                    //        storage.getReferenceFromUrl("gs://arcademy-241eb.appspot.com").child("aca_ad_images/" + pictureName).putFile(filePath)
                                    storage.getReferenceFromUrl("gs://bistro-5bc79.appspot.com").child(imgFolderName).child(picName[i]).putFile(listImages.get(i))
                                            //성공시
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                                {

                                                    /**     이미지 업로드 완료    **/
                                                    progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기


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
                                            //실패시
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                }
                                            })
                                            //진행중
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                    //dialog에 진행률을 퍼센트로 출력해 준다
                                                    progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                                                }
                                            });
                                }
                            } // for문 끝나는 블락
                        }
                        /**  오늘 상점에 대해 이미 작성.   글 작성 불가능   **/
                        else
                        {
                            Toast.makeText(context, "이미 같은 상점에 대한 글을 작성했습니다. 1개의 상점에 대해 하루에 1번만 글을 게시할 수 있습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }});

                break;


                /** 맛집검색 버튼 **/
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
                        et_search_store.setText(name);  // 식당이름 받는 부분

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

    /** startActivityForResult 메소드가 실행될때 실행 **/
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null)
            {     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                listImages.add(imageUri);

                pictureAdapter = new PictureAdapter( getApplicationContext(),listImages);
                rv_images.setAdapter(pictureAdapter);
//                rv_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                pictureAdapter.notifyDataSetChanged();

            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 6){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 6장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            listImages.add(imageUri);  //uri를 list에 담는다.

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
