package com.bistro.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bistro.R;
import com.bistro.adapter.PictureAdapter;
import com.bistro.database.SharedManager;
import com.bistro.model.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WritePostAct extends AppCompatActivity implements View.OnClickListener {


    private DatabaseReference
            mDatabaseRef;
    private Context context;

    private TextView
            tv_top,     // 게시글 모드 (작성하기 or 상세보기)
            tv_complete;  // 작성완료 텍스트 버튼

    private EditText
            et_title,   // 게시글 제목
             et_name_of_store,
            et_menu;

    private TextInputEditText
            et_content; // 게시글 내용

    private Uri pictureUri[];
    private ImageView iv_image1, iv_image2, iv_image3, iv_image4, iv_image5, iv_image6, iv_back_arrow
                      , iv_pick_images;
    private RecyclerView rv_images;
    private PictureAdapter pictureAdapter;
    private ArrayList<Uri> listImages;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.act_write_post);
        setInitialize();
    }

    private void setInitialize() {
        SharedManager.init(getApplicationContext());

//        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bistro");
        context = getApplicationContext();

        tv_top = findViewById(R.id.tv_top);
        tv_complete = findViewById(R.id.btn_complete);
        et_title = findViewById(R.id.et_title);
        et_name_of_store = findViewById(R.id.et_store_name);
        et_content = findViewById(R.id.et_content);
        et_menu = findViewById(R.id.et_menu);

        findViewById(R.id.btn_back).setOnClickListener(this);
        tv_complete.setOnClickListener(this);

            // postmode : 작성하기
            tv_complete.setVisibility(View.VISIBLE);
//            setEnabledInputField(true);


        // 사진 여러장 가져오는 부분 코드
        rv_images = findViewById(R.id.rv_images);
        iv_pick_images = findViewById(R.id.iv_pick_image);
        iv_pick_images.setOnClickListener(this);

        listImages = new ArrayList<>();
    }

//    private void setEnabledInputField(boolean _isEnabled) {
//        // 입력필드 활성화 or 비활성화
//        mEtTitle.setEnabled(_isEnabled);
//        mEtContent.setEnabled(_isEnabled);
//        mEtAddress.setEnabled(_isEnabled);
//    }


    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {
            //  뒤로가기
            case R.id.btn_back:
                finish();
                break;

            case R.id.iv_pick_image:

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

                String strTitle = et_title.getText().toString();
                String strName = et_name_of_store.getText().toString();
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
                postModel.setId(strPostId);    // 게시글의 유니크한 ID
                postModel.setUserId(userId);   // 사용자 로그인 ID
                postModel.setMenu(strMenu);
                postModel.setClick("1");   // 조회수
                postModel.setLike("0");    // 공감수

                mDatabaseRef.child("postInfo").child(strPostId).setValue(postModel);

                final ProgressDialog progressDialog = new ProgressDialog(WritePostAct.this);
                progressDialog.setTitle("업로드중...");
                progressDialog.show();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                String picName [] = new String [6];

                /** 이미지파일 이름 **/
                picName[0] = strNickName + '1';
                picName[1] = strNickName + '2';
                picName[2] = strNickName + '3';
                picName[3] = strNickName + '4';
                picName[4] = strNickName + '5';
                picName[5] = strNickName + '6';


                String imgFolderName = strNickName + strCurrentDate; // 유저 닉네임과 일시를 합쳐서 이미지 폴더이름 생성

                for(int i=0; i<listImages.size(); i++) {

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
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                listImages.add(imageUri);

                pictureAdapter = new PictureAdapter( getApplicationContext(),listImages);
                rv_images.setAdapter(pictureAdapter);
                rv_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
                    rv_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    pictureAdapter.notifyDataSetChanged();
                }
            }
        }
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    //ClipData 또는 Uri를 가져온다
//                    pictureUri[0] = data.getData();
//                    Uri uri = data.getData();
//                    ClipData clipData = data.getClipData();
//                    iv_image1.setImageURI(uri);
//                } catch (Exception e) {
//
//                }
//            }
//            else if (resultCode == RESULT_CANCELED) {
//              showToast("사진 선택 취소");
//            }
//        }

//        else if (requestCode == 2) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    //ClipData 또는 Uri를 가져온다
//                    pictureUri[1] = data.getData();
//                    Uri uri = data.getData();
//                    ClipData clipData = data.getClipData();
//                    iv_image2.setImageURI(uri);
//                } catch (Exception e) {
//
//                }
//            }
//            else if (resultCode == RESULT_CANCELED) {
//                showToast("사진 선택 취소");
//            }
//        }
//
//        else if (requestCode == 3) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    //ClipData 또는 Uri를 가져온다
//                    pictureUri[2] = data.getData();
//                    Uri uri = data.getData();
//                    ClipData clipData = data.getClipData();
//                    iv_image3.setImageURI(uri);
//
//                } catch (Exception e) {
//                }
//            }
//            else if (resultCode == RESULT_CANCELED) {
//                showToast("사진 선택 취소");
//            }
//        }
//
//        else if (requestCode == 4) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    //ClipData 또는 Uri를 가져온다
//                    pictureUri[3] = data.getData();
//                    Uri uri = data.getData();
//                    ClipData clipData = data.getClipData();
//                    iv_image4.setImageURI(uri);
//
//                } catch (Exception e) {
//                }
//            }
//            else if (resultCode == RESULT_CANCELED) {
//                showToast("사진 선택 취소");
//            }
//        }
//
//        else if (requestCode == 5) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    //ClipData 또는 Uri를 가져온다
//                    pictureUri[4] = data.getData();
//                    Uri uri = data.getData();
//                    ClipData clipData = data.getClipData();
//                    iv_image5.setImageURI(uri);
//
//                } catch (Exception e) {
//                }
//            }
//            else if (resultCode == RESULT_CANCELED) {
//                showToast("사진 선택 취소");
//            }
//        }
//
//        else if (requestCode == 6) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    //ClipData 또는 Uri를 가져온다
//                    pictureUri[5] = data.getData();
//                    Uri uri = data.getData();
//                    ClipData clipData = data.getClipData();
//                    iv_image6.setImageURI(uri);
//
//                } catch (Exception e) {
//                }
//            }
//            else if (resultCode == RESULT_CANCELED) {
//                showToast("사진 선택 취소");
//            }
//        }
    }

    // 토스트 메소드
    private void showToast(String text)
    {
        View view1 = View.inflate(context, R.layout.toast_layout, null);
        TextView textView = view1.findViewById(R.id.tv_toast);
        textView.setText(text);
        Toast toast = new Toast(context);
        toast.setView(view1);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

}
