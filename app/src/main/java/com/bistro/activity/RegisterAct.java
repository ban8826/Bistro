package com.bistro.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.bistro.database.Const;
import com.bistro.database.SharedManager;
import com.bistro.model.UserModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * author hongdroid94
 * summary 회원가입 화면
 */
public class RegisterAct extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;

    private ImageView mIvProfile;
    private EditText mEtEmail, mEtPwd, mEtPwdRe, mEtNickName;
    private Uri mCameraUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);
        setInitialize();
    }

    private void setInitialize() {
        mCameraUri = null;

        // Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bistro");
        mStorage = FirebaseStorage.getInstance();

        mIvProfile = findViewById(R.id.iv_profile);
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtPwdRe = findViewById(R.id.et_pwd_re);
        mEtNickName = findViewById(R.id.et_nick_name);

        mIvProfile.setOnClickListener(this);
        findViewById(R.id.btn_register_complete).setOnClickListener(this);


    }

    // register for activity result
    private final ActivityResultLauncher<Intent> getImageResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if ( result.getData() != null) {
                    mCameraUri = result.getData().getData();
                    Glide.with(this)
                            .load(mCameraUri)
                            .into(mIvProfile);
                }
            });

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_profile:
                // 갤러리에서 이미지 가져오기
                getGalleryImage();
                break;

            case R.id.btn_register_complete:
                // 가입 완료
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strPwdRe = mEtPwdRe.getText().toString();
                String strNickName = mEtNickName.getText().toString();

                // check validation
                if (    strEmail   .length() == 0 ||
                        strPwd     .length() == 0 ||
                        strPwdRe   .length() == 0 ||
                        strNickName.length() == 0) {
                    Toast.makeText(RegisterAct.this, "비어있는 입력 값이 존재 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!strPwd.equals(strPwdRe)) {
                    Toast.makeText(RegisterAct.this, "비밀번호와 확인 값이 다릅니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                // start register
                // 회원가입 진행
                setCompleteRegister(strEmail, strPwd, strNickName);
                break;
        }

    }

    private void getGalleryImage() {
        // get profile image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        getImageResult.launch(intent);
    }


    private void setCompleteRegister(String _strEmail, String _strPwd, String _strNickName) {
        // 회원가입
        mFirebaseAuth.createUserWithEmailAndPassword(_strEmail, _strPwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userToken = task.getResult().getUser().getUid();

                // set profile image to Firebase Storage
                setRegProfileImage(_strEmail, _strNickName, userToken);
            } else if (!task.getException().getMessage().isEmpty()) {
                // 에러 Exception case 중에 이미 Firebase Auth 에 이메일이 존재하는 경우 중복 가입을 막는다.
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "가입 중에 예외가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRegProfileImage(String _strEmail, String _strNickName, String _strUserToken) {

        UserModel userModel = new UserModel();
        userModel.setId(_strEmail);
        userModel.setName(_strNickName);
        userModel.setAuthToken(_strUserToken);

        // upload profile image to firebase storage
        if (mCameraUri != null) {
            // 사진 업로드 데이터가 존재하므로 Firebase Stroage에 업로드 수행 후 회원가입 진행
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Profile Image");
            progressDialog.show();

            // upload firebase storage
            String strFileName = _strUserToken + ".png"; // user auth token
            mStorageRef = mStorage.getReferenceFromUrl(Const.FIREBASE_STORAGE_URL).child("profiles" + File.separator + strFileName);
            mStorageRef.putFile(mCameraUri).addOnSuccessListener(taskSnapshot -> {
                if (taskSnapshot.getTask().isSuccessful()) {
                    mStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        progressDialog.dismiss();
                        // 가입 성공
                        SharedManager.write(SharedManager.PROFILE_STORAGE_PATH, strFileName);
                        userModel.setProfileImgPath(strFileName);

                        // save firebase database
                        mDatabaseRef.child("userInfo").child(_strUserToken).setValue(userModel).addOnSuccessListener(unused -> {
                            // Return to LoginAct
                            Toast.makeText(RegisterAct.this, "회원가입이 완료 되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                }
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "프로필 이미지 업로드에 실패하였습니다\n잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(snapshot -> {
                double progressValue = (double) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                // display current progress value
                progressDialog.setMessage("Uploading " +  (int) progressValue + "% ...");
            });
        } else {
            // 사진 업로드 데이터 없으므로 그냥 회원가입 진행
            userModel.setProfileImgPath(null);

            // save firebase database
            mDatabaseRef.child("userInfo").child(_strUserToken).setValue(userModel).addOnSuccessListener(unused -> {
                // Return to LoginAct
                Toast.makeText(RegisterAct.this, "회원가입이 완료 되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

}