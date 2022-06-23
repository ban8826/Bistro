package com.bistro.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bistro.R;
import com.bistro.database.Const;
import com.bistro.database.SharedManager;
import com.bistro.model.UserModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.usermgmt.response.model.UserAccount;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * summary 회원가입 화면
 */
public class JoinAct extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;
    private Button  btn_chk_name, btn_chk_duplicate;
    private Context context;
    private static final Pattern name_PATTERN = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣]{2,9}$"); // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$"); // 비밀번호 정규식


    private ImageView mIvProfile;
    private EditText mEtEmail, mEtPwd, mEtPwdRe, mEtNickName, mEtHobby;
    private Uri mCameraUri;
    private ArrayList<UserModel> userAccountList;
    private boolean isCheckId = false;                   // 아이디 중복확인 불리언
    private boolean isCheckName = false;                 // 닉네임 중복확인 불리언
    private String validIdTemp = "";                     // 아이디 중복확인 완료 직후 저장될 아이디 값.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);
        setInitialize();
    }

    private void setInitialize() {
        mCameraUri = null;

        context = getApplicationContext();

        // Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("bistro");
        mStorage = FirebaseStorage.getInstance();

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtPwdRe = findViewById(R.id.et_pwd_chk);
        mEtNickName = findViewById(R.id.et_name);
        mEtHobby = findViewById(R.id.et_hobby);

        btn_chk_duplicate = findViewById(R.id.btn_chk_duplicate);
        btn_chk_name = findViewById(R.id.btn_chk_name); // 닉네임(이름)
        btn_chk_duplicate.setOnClickListener(this);
        btn_chk_name.setOnClickListener(this);
        findViewById(R.id.btn_complete).setOnClickListener(this);


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
//            case R.id.iv_profile:
//                // 갤러리에서 이미지 가져오기
//                getGalleryImage();
//                break;

            case R.id.btn_complete:
                // 가입 완료
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strPwdRe = mEtPwdRe.getText().toString();
                String strNickName = mEtNickName.getText().toString();
                String strHobby = mEtHobby.getText().toString();

                // check validation
                if (    strEmail   .length() == 0 ||
                        strPwd     .length() == 0 ||
                        strPwdRe   .length() == 0 ||
                        strNickName.length() == 0 ||
                        strHobby.length() == 0) {
                    Toast.makeText(JoinAct.this, "비어있는 입력 값이 존재 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!strPwd.equals(strPwdRe)) {
                    Toast.makeText(JoinAct.this, "비밀번호와 확인 값이 다릅니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validIdTemp.equals(mEtEmail.getText().toString()))
                { // 중복검사 처리가 끝난 String 값을 임시저장 해놓은 것과 현재 적혀있는 것이 일치하는지 재 확인 필요.
                    Toast.makeText(JoinAct.this, "중복검사를 다시 진행해 주십시오", Toast.LENGTH_SHORT).show();

                    return;
                }

                // start register
                // 회원가입 진행
                setCompleteRegister(strEmail, strPwd, strNickName, strHobby, strPwd);
                break;


            case R.id.btn_chk_duplicate: // 이메일 중복확인

                if (mEtEmail.getText().length() == 0)
                {
                    Toast.makeText(context, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                userAccountList = new ArrayList<>();
                mDatabaseRef.child("userInfo").orderByChild("id").equalTo(mEtEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot item : dataSnapshot.getChildren())
                        {
                            UserModel userData = item.getValue(UserModel.class);
                            userAccountList.add(userData);
                        }

                        if (userAccountList.size() == 0)
                        { // 아이디 중복된 것이 없다는 뜻
                            isCheckId = true;
                            Toast.makeText(context, "사용가능한 아이디입니다", Toast.LENGTH_SHORT).show();

                            validIdTemp = mEtEmail.getText().toString(); // 사용가능한 아이 임시저장. (중복검사 이후 사용자가 수정해버릴 변수 대응)
                        }

                        else
                        { // 아이디이 중복 되었다는 뜻
                            isCheckId = false;
                            Toast.makeText(context, "아이디가 중복된 값이 존재합니다", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
                break;


            case R.id.btn_chk_name :  // 닉네임 중복확인


                if (mEtNickName.getText().length() == 0)
                {
                    Toast.makeText(context, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (!name_PATTERN.matcher(mEtNickName.getText().toString()).matches())
                {
                    Toast.makeText(context, "형식일치안함", Toast.LENGTH_SHORT).show();

                    return;
                }

                userAccountList = new ArrayList<>();
                // 이부분 et_name을 init()에서 name 변수에 담아놓지 말고 여기서 et_name.getText().toString()를 해줘야 한다.
                mDatabaseRef.child("userInfo").orderByChild("name").equalTo(mEtNickName.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userAccount = null;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            if (dataSnapshot.getChildrenCount() != 0)
                            {
                                userAccount = dataSnapshot.getValue(UserModel.class);
                                userAccountList.add(userAccount);
                            }
                        }

                        // 중복된 이름 없음. 정상진행


                        if(userAccountList.size() ==0)
                        {
                            Toast.makeText(context, "사용가능한 닉네임입니다", Toast.LENGTH_SHORT).show();

                            isCheckName = true;
                        }

                        // 중복된 이름 있음.
                        else
                        {
                            Toast.makeText(context, "중복된 닉네임이 존재합니다", Toast.LENGTH_SHORT).show();

                            isCheckName = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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


    private void setCompleteRegister(String _strEmail, String _strPwd, String _strNickName, String _strHobby, String _pwd) {
        // 회원가입
        mFirebaseAuth.createUserWithEmailAndPassword(_strEmail, _strPwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userToken = task.getResult().getUser().getUid();

                // set profile image to Firebase Storage
                setRegProfileImage(_strEmail, _strNickName, userToken, _strHobby, _pwd);
            } else if (!task.getException().getMessage().isEmpty()) {
                // 에러 Exception case 중에 이미 Firebase Auth 에 이메일이 존재하는 경우 중복 가입을 막는다.
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "가입 중에 예외가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRegProfileImage(String _strEmail, String _strNickName, String _strUserToken, String _strHobby, String _pwd) {

        UserModel userModel = new UserModel();
        userModel.setId(_strEmail);
        userModel.setName(_strNickName);
        userModel.setAuthToken(_strUserToken);
        userModel.setHobby(_strHobby);
        userModel.setPwd(_pwd);

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
                            Toast.makeText(JoinAct.this, "회원가입이 완료 되었습니다", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(JoinAct.this, "회원가입이 완료 되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

}